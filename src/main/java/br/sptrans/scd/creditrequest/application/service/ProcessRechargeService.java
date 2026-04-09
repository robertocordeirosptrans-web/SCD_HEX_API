package br.sptrans.scd.creditrequest.application.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.creditrequest.application.port.in.ProcessRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.EventoFinanceiroPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.HmPort;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import br.sptrans.scd.shared.cache.InvalidateOrderCache;
import br.sptrans.scd.shared.helper.StatusConsolidationHelper;
import lombok.RequiredArgsConstructor;

/**
 * Implementação do use case de processamento de recarga.
 *
 * <p>Transição do pedido: LIBERADO_PARA_RECARGA(05) → EM_PROCESSO_DE_RECARGA(06).</p>
 * <p>Transição dos itens: LIBERADO_PARA_RECARGA(05) → EM_PROCESSO_DE_RECARGA(06).</p>
 *
 * <p><b>Caminho A — valor efetivo &gt; 0:</b> item vai para EM_PROCESSO_DE_RECARGA(06),
 * DT_ENVIO_HM preenchida, autorização enviada ao HM, histórico com descrição do evento.</p>
 *
 * <p><b>Caminho B — valor efetivo &le; 0:</b> item passa por EM_PROCESSO_DE_RECARGA(06)
 * e imediatamente vai para RECARREGADO(07) com VL_CARREGADO=0, sem integração HM.</p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProcessRechargeService implements ProcessRechargeUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessRechargeService.class);
    private static final String ORIGEM_TRANSICAO = "processar_recarga_scd";
    private static final String DES_RECARREGADO_POR_EVENTO =
            "Indicado como recarregado devido a ocorrencia de eventos financeiros";

    private final CreditRequestPort creditRequestRepository;
    private final CreditRequestItemsPort itemRepository;
    private final HistCreditRequestService historyService;
    private final StatusConsolidationHelper statusConsolidationHelper;
    private final EventoFinanceiroPort eventoFinanceiroPort;
    private final HmPort hmPort;

    // ── processarRecarga ────────────────────────────────────────────────────────

    @Override
    @Transactional
    @InvalidateOrderCache
    public void processarRecarga(ProcessRechargeCommand comando) {
        CreditRequest solicitacao = creditRequestRepository
                .findByCodTipoDocumentoAndIdUsuarioCadastro(
                        comando.codTipoDocumento(), comando.idUsuarioCadastro())
                .orElse(null);

        if (solicitacao == null) {
            log.warn("Solicitação não encontrada para codTipoDocumento={}, idUsuarioCadastro={}",
                    comando.codTipoDocumento(), comando.idUsuarioCadastro());
            return;
        }

        Long numSolicitacao = solicitacao.getNumSolicitacao();
        String codCanal = solicitacao.getCodCanal();
        String numLote = solicitacao.getNumLote();

        log.debug("Processando recarga para solicitação {}/{} e lote {}", numSolicitacao, codCanal, numLote);

        List<Long> numSolicitacaoItens = itemRepository
                .findNumSolicitacaoItemsBySolicitacaoCanalLote(numSolicitacao, codCanal, numLote);
        if (numSolicitacaoItens == null || numSolicitacaoItens.isEmpty()) {
            log.warn("Nenhum item encontrado para solicitação {}/{} e lote {}", numSolicitacao, codCanal, numLote);
            return;
        }

        // Carrega codCanalDistribuicao uma única vez para o batch (performance)
        String codCanalDistribuicao = creditRequestRepository
                .findCodCanalDistribuicao(numSolicitacao, codCanal)
                .orElse(codCanal);

        // Registra a rede HM uma vez por solicitação/canal (cria se não existir)
        hmPort.registrarAutorizacaoRecarga(numSolicitacao, codCanal, codCanalDistribuicao);

        int processados = 0;
        // Itens que entram no histórico do Caminho A (06) ao final do loop
        List<CreditRequestItems> itensCaminhoA = new ArrayList<>();
        Map<Long, String> descricoesCaminhoA = new HashMap<>();

        for (Long numSolicitacaoItem : numSolicitacaoItens) {
            CreditRequestItemsKey key = buildKey(numSolicitacao, numSolicitacaoItem, codCanal);
            Optional<CreditRequestItems> optItem = itemRepository.findById(key);
            if (optItem.isEmpty()) {
                continue;
            }
            CreditRequestItems item = optItem.get();
            if (!SituationCreditRequestItems.LIBERADO_PARA_RECARGA.equals(item.getCodSituacao())) {
                continue;
            }

            // ── Calcula evento financeiro ──────────────────────────────────────
            BigDecimal wValorEventoItem = BigDecimal.ZERO;
            BigDecimal wValorDescItem = BigDecimal.ZERO;
            String wOutrasVias = "0";
            boolean temEvento = "1".equals(item.getFlgEvento());

            if (temEvento) {
                wValorEventoItem = eventoFinanceiroPort.processarLancamento(
                        numSolicitacao, codCanal, numSolicitacaoItem);
                BigDecimal vlItem = item.getVlItem() != null ? item.getVlItem() : BigDecimal.ZERO;
                wOutrasVias = vlItem.compareTo(wValorEventoItem) == 0 ? "1" : "0";
                if (vlItem.add(wValorEventoItem).compareTo(BigDecimal.ZERO) > 0) {
                    wValorDescItem = wValorEventoItem;
                } else {
                    wValorDescItem = vlItem.negate();
                }
            }

            BigDecimal vlItem = item.getVlItem() != null ? item.getVlItem() : BigDecimal.ZERO;
            BigDecimal valorEfetivo = vlItem.add(wValorEventoItem);

            // Popula sqPid se informado
            if (comando.sqPid() != null) {
                item.setSqPid(comando.sqPid());
                item.setDtInicProcesso(LocalDateTime.now());
            }

            if (valorEfetivo.compareTo(BigDecimal.ZERO) > 0) {
                // ── CAMINHO A: envia ao HM ──────────────────────────────────────
                processarCaminhoA(item, temEvento, wValorDescItem, wOutrasVias, valorEfetivo,
                        solicitacao, itensCaminhoA, descricoesCaminhoA);
            } else {
                // ── CAMINHO B: vai direto para RECARREGADO ──────────────────────
                processarCaminhoB(item, numSolicitacao, codCanal);
            }

            processados++;
        }

        // Salva histórico em batch para o Caminho A
        if (!itensCaminhoA.isEmpty()) {
            historyService.saveItemStatusHistoryBatch(itensCaminhoA, ORIGEM_TRANSICAO, descricoesCaminhoA);
        }

        if (processados > 0) {
            // Carrega todos os itens para consolidar status do pedido
            List<CreditRequestItems> todosItens = carregarTodosItens(numSolicitacao, codCanal, numSolicitacaoItens);
            statusConsolidationHelper.consolidarStatusSolicitacao(
                    numSolicitacao, codCanal, todosItens, ORIGEM_TRANSICAO);
        }

        log.debug("Processamento de recarga concluído para solicitação {}/{} — {} itens processados",
                numSolicitacao, codCanal, processados);
    }

    // ── processarItemRecarga ────────────────────────────────────────────────────

    @Transactional
    @InvalidateOrderCache
    @Override
    public void processarItemRecarga(ProcessItemCommand comando) {
        CreditRequest solicitacao = creditRequestRepository
                .findByCodTipoDocumentoAndIdUsuarioCadastro(
                        comando.codTipoDocumento(), comando.idUsuarioCadastro())
                .orElse(null);

        if (solicitacao == null) {
            log.warn("Solicitação não encontrada para codTipoDocumento={}, idUsuarioCadastro={}",
                    comando.codTipoDocumento(), comando.idUsuarioCadastro());
            return;
        }

        Long numSolicitacao = solicitacao.getNumSolicitacao();
        String codCanal = solicitacao.getCodCanal();
        Long numSolicitacaoItem = comando.seqItem() != null ? comando.seqItem().longValue() : null;

        if (numSolicitacaoItem == null) {
            log.warn("seqItem é obrigatório para processarItemRecarga");
            return;
        }

        CreditRequestItemsKey key = buildKey(numSolicitacao, numSolicitacaoItem, codCanal);
        Optional<CreditRequestItems> itemOpt = itemRepository.findById(key);
        if (itemOpt.isEmpty()) {
            log.warn("Item não encontrado: Solicitação={}, Item={}, CodCanal={}",
                    numSolicitacao, numSolicitacaoItem, codCanal);
            return;
        }

        CreditRequestItems item = itemOpt.get();
        if (!SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA.equals(item.getCodSituacao())) {
            log.warn("Item {}/{}/{} não está em EM_PROCESSO_DE_RECARGA, status atual={}",
                    numSolicitacao, numSolicitacaoItem, codCanal, item.getCodSituacao());
            return;
        }

        // Transição → RECARREGADO (07)
        item.setCodSituacao(SituationCreditRequestItems.RECARREGADO);
        if (comando.vlCarregado() != null) {
            item.setVlCarregado(comando.vlCarregado());
        }
        if (comando.codAssinaturaHsm() != null) {
            item.setCodAssinaturaHsm(comando.codAssinaturaHsm());
        }
        item.setDtRetornoHm(LocalDateTime.now());
        item.setDtManutencao(LocalDateTime.now());

        itemRepository.save(item);
        historyService.saveItemStatusHistoryBatch(List.of(item), ORIGEM_TRANSICAO);

        log.info("Item recarga processado — Solicitação={}, Item={}, NovoStatus=RECARREGADO",
                numSolicitacao, numSolicitacaoItem);

        // Consolida status do pedido com todos os itens do lote
        String numLote = solicitacao.getNumLote();
        List<Long> allNumSolicitacaoItens = itemRepository
                .findNumSolicitacaoItemsBySolicitacaoCanalLote(numSolicitacao, codCanal, numLote);
        List<CreditRequestItems> todosItens = carregarTodosItens(numSolicitacao, codCanal, allNumSolicitacaoItens);
        statusConsolidationHelper.consolidarStatusSolicitacao(
                numSolicitacao, codCanal, todosItens, ORIGEM_TRANSICAO);
    }

    // ── helpers privados ────────────────────────────────────────────────────────

    /**
     * Caminho A: valor efetivo &gt; 0 — item marcado como EM_PROCESSO_DE_RECARGA,
     * campos de evento preenchidos e autorização enviada ao HM.
     */
    private void processarCaminhoA(CreditRequestItems item,
            boolean temEvento, BigDecimal wValorDescItem, String wOutrasVias,
            BigDecimal valorEfetivo,
            CreditRequest solicitacao,
            List<CreditRequestItems> itensCaminhoA,
            Map<Long, String> descricoes) {

        item.setCodSituacao(SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA);
        item.setDtEnvioHm(LocalDateTime.now());
        item.setDtManutencao(LocalDateTime.now());

        if (temEvento) {
            item.setVlEvento(wValorDescItem);
            item.setFlgEvento("S");
            item.setFlgOutrasVias(wOutrasVias);
        }

        itemRepository.save(item);

        // Envia autorização ao HM
        boolean liminar = item.getFlgLiminarLoja() != null && item.getFlgLiminarLoja() > 0;
        hmPort.enviarAutorizacaoRecarga(
                item.getId().getNumSolicitacao(),
                item.getId().getNumSolicitacaoItem(),
                item.getId().getCodCanal(),
                item.getNumLogicoCartao(),
                item.getCodAssinaturaHsm(),
                solicitacao.getDtPagtoEconomica(),
                item.getSeqRecarga(),
                valorEfetivo,
                liminar);

        itensCaminhoA.add(item);
        if (temEvento) {
            String descEvento = "Ajuste em decorrencia de Eventos no valor de "
                    + NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(wValorDescItem);
            descricoes.put(item.getId().getNumSolicitacaoItem(), descEvento);
        }

        log.info("Caminho A — Solicitação={}, Item={}, Status=EM_PROCESSO_DE_RECARGA, temEvento={}",
                item.getId().getNumSolicitacao(), item.getId().getNumSolicitacaoItem(), temEvento);
    }

    /**
     * Caminho B: valor efetivo &le; 0 — item passa pelo estado 06 e vai
     * imediatamente para RECARREGADO(07) sem envio ao HM.
     */
    private void processarCaminhoB(CreditRequestItems item, Long numSolicitacao, String codCanal) {
        LocalDateTime agora = LocalDateTime.now();

        // UPDATE 1: grava 06
        item.setCodSituacao(SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA);
        item.setDtEnvioHm(agora);
        item.setDtManutencao(agora);
        itemRepository.save(item);

        // Histórico 06 (sem descrição)
        historyService.saveItemStatusHistoryBatch(List.of(item), ORIGEM_TRANSICAO);

        // UPDATE 2: sobrescreve para 07
        item.setCodSituacao(SituationCreditRequestItems.RECARREGADO);
        item.setVlCarregado(BigDecimal.ZERO);
        item.setDtRecarga(agora);
        item.setDtRetornoHm(agora);
        item.setDtManutencao(agora);
        itemRepository.save(item);

        // Histórico 07 com descrição de evento
        Map<Long, String> desc = Map.of(
                item.getId().getNumSolicitacaoItem(), DES_RECARREGADO_POR_EVENTO);
        historyService.saveItemStatusHistoryBatch(List.of(item), ORIGEM_TRANSICAO, desc);

        log.info("Caminho B — Solicitação={}, Item={}, Status=RECARREGADO (valor zero)",
                numSolicitacao, item.getId().getNumSolicitacaoItem());
    }

    /** Constrói a chave composta de item. */
    private static CreditRequestItemsKey buildKey(Long numSolicitacao, Long numSolicitacaoItem, String codCanal) {
        CreditRequestItemsKey key = new CreditRequestItemsKey();
        key.setNumSolicitacao(numSolicitacao);
        key.setNumSolicitacaoItem(numSolicitacaoItem);
        key.setCodCanal(codCanal);
        return key;
    }

    /** Carrega todos os itens de uma lista de IDs para consolidação de status. */
    private List<CreditRequestItems> carregarTodosItens(Long numSolicitacao, String codCanal,
            List<Long> numSolicitacaoItens) {
        List<CreditRequestItems> resultado = new ArrayList<>(numSolicitacaoItens.size());
        for (Long itemId : numSolicitacaoItens) {
            itemRepository.findById(buildKey(numSolicitacao, itemId, codCanal))
                    .ifPresent(resultado::add);
        }
        return resultado;
    }
}


// /**
//  * Implementação do use case de processamento de recarga.
//  *
//  * <p>
//  * Transição do pedido: LIBERADO_PARA_RECARGA(05) → EM_PROCESSO_DE_RECARGA(06).
//  * Transição dos itens: LIBERADO_PARA_RECARGA(05) →
//  * EM_PROCESSO_DE_RECARGA(06).</p>
//  *
//  * <p>
//  * Itens cujo valor efetivo (vlItem + vlEvento) é ≤ 0 são marcados diretamente
//  * como RECARREGADO(07) com valor zero.</p>
//  */
// @Service
// @Transactional
// @RequiredArgsConstructor
// public class ProcessRechargeService implements ProcessRechargeUseCase {

//     private static final Logger log = LoggerFactory.getLogger(ProcessRechargeService.class);
//     private static final String ORIGEM_TRANSICAO = "processar_recarga_scd";

//     private final CreditRequestPort creditRequestRepository;
//     private final CreditRequestItemsPort itemRepository;
//     private final HistCreditRequestService historyService;
//     private final StatusConsolidationHelper statusConsolidationHelper;


//     @Override
//     @Transactional
//     @InvalidateOrderCache
//     public void processarRecarga(ProcessRechargeCommand comando) {
//         CreditRequest solicitacao = creditRequestRepository
//                 .findByCodTipoDocumentoAndIdUsuarioCadastro(
//                         comando.codTipoDocumento(), comando.idUsuarioCadastro())
//                 .orElse(null);

//         if (solicitacao == null) {
//             log.warn("Solicitação não encontrada para codTipoDocumento={}, idUsuarioCadastro={}",
//                     comando.codTipoDocumento(), comando.idUsuarioCadastro());
//             return;
//         }

//         Long numSolicitacao = solicitacao.getNumSolicitacao();
//         String codCanal = solicitacao.getCodCanal();
//         String numLote = solicitacao.getNumLote();

//         log.debug("Processando recarga para solicitação {}/{} e lote {}", numSolicitacao, codCanal, numLote);

//         List<Long> numSolicitacaoItens = itemRepository.findNumSolicitacaoItemsBySolicitacaoCanalLote(numSolicitacao, codCanal, numLote);
//         if (numSolicitacaoItens == null || numSolicitacaoItens.isEmpty()) {
//             log.warn("Nenhum item encontrado para solicitação {}/{} e lote {}", numSolicitacao, codCanal, numLote);
//             return;
//         }

//         int processados = 0;
//         List<CreditRequestItems> itensProcessados = new java.util.ArrayList<>();
//         for (Long numSolicitacaoItem : numSolicitacaoItens) {
//             CreditRequestItemsKey key = new CreditRequestItemsKey();
//             key.setNumSolicitacao(numSolicitacao);
//             key.setNumSolicitacaoItem(numSolicitacaoItem);
//             key.setCodCanal(codCanal);
//             var optItem = itemRepository.findById(key);
//             if (optItem.isEmpty()) {
//                 continue;
//             }
//             CreditRequestItems item = optItem.get();
//             if (!SituationCreditRequestItems.LIBERADO_PARA_RECARGA.equals(item.getCodSituacao())) {
//                 continue;
//             }

//             BigDecimal vlEvento = item.getVlEvento();
//             BigDecimal vlItem = item.getVlItem() != null ? item.getVlItem() : BigDecimal.ZERO;
//             BigDecimal valorEfetivo = vlItem.add(vlEvento != null ? vlEvento : BigDecimal.ZERO);

//             if (valorEfetivo.compareTo(BigDecimal.ZERO) <= 0) {
//                 // Item com valor zero → marca diretamente como RECARREGADO
//                 item.setCodSituacao(SituationCreditRequestItems.RECARREGADO);
//                 item.setVlCarregado(BigDecimal.ZERO);
//                 item.setDtRetornoHm(LocalDateTime.now());
//             } else {
//                 item.setCodSituacao(SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA);
//                 item.setDtEnvioHm(LocalDateTime.now());
//             }

//             if (comando.sqPid() != null) {
//                 item.setSqPid(comando.sqPid());
//                 item.setDtInicProcesso(LocalDateTime.now());
//             }

//             item.setDtManutencao(LocalDateTime.now());
//             itemRepository.save(item);
//             processados++;
//             itensProcessados.add(item);
//             log.info("Item processado - Solicitação={}, Item={}, NovoStatus={}",
//                     numSolicitacao, item.getId().getNumSolicitacaoItem(), item.getCodSituacao());
//         }

//         if (processados > 0) {
//             historyService.saveItemStatusHistoryBatch(itensProcessados, ORIGEM_TRANSICAO);
//             statusConsolidationHelper.consolidarStatusSolicitacao(numSolicitacao, codCanal, itensProcessados, ORIGEM_TRANSICAO);
//         }

//         log.debug("Processamento de recarga concluído para solicitação {}/{} - {} itens processados",
//                 numSolicitacao, codCanal, processados);

//     }

//     @Transactional
//     @InvalidateOrderCache
//     @Override
//     public void processarItemRecarga(ProcessItemCommand comando) {
//         CreditRequest solicitacao = creditRequestRepository
//                 .findByCodTipoDocumentoAndIdUsuarioCadastro(
//                         comando.codTipoDocumento(), comando.idUsuarioCadastro())
//                 .orElse(null);

//         if (solicitacao == null) {
//             log.warn("Solicitação não encontrada para codTipoDocumento={}, idUsuarioCadastro={}",
//                     comando.codTipoDocumento(), comando.idUsuarioCadastro());
//             return;
//         }

//         Long numSolicitacao = solicitacao.getNumSolicitacao();
//         String codCanal = solicitacao.getCodCanal();
//         Long numSolicitacaoItem = comando.seqItem() != null ? comando.seqItem().longValue() : null;

//         if (numSolicitacaoItem == null) {
//             log.warn("seqItem é obrigatório para processarItemRecarga");
//             return;
//         }

//         CreditRequestItemsKey key = new CreditRequestItemsKey();
//         key.setNumSolicitacao(numSolicitacao);
//         key.setNumSolicitacaoItem(numSolicitacaoItem);
//         key.setCodCanal(codCanal);

//         Optional<CreditRequestItems> itemOpt = itemRepository.findById(key);
//         if (itemOpt.isEmpty()) {
//             log.warn("Item não encontrado: Solicitação={}, Item={}, CodCanal={}",
//                     numSolicitacao, numSolicitacaoItem, codCanal);
//             return;
//         }

//         CreditRequestItems item = itemOpt.get();

//         if (!SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA.equals(item.getCodSituacao())) {
//             log.warn("Item {}/{}/{} não está em EM_PROCESSO_DE_RECARGA, status atual={}",
//                     numSolicitacao, numSolicitacaoItem, codCanal, item.getCodSituacao());
//             return;
//         }

//         item.setCodSituacao(SituationCreditRequestItems.RECARREGADO);
//         if (comando.vlCarregado() != null) {
//             item.setVlCarregado(comando.vlCarregado());
//         }
//         if (comando.codAssinaturaHsm() != null) {
//             item.setCodAssinaturaHsm(comando.codAssinaturaHsm());
//         }
//         item.setDtRetornoHm(LocalDateTime.now());
//         item.setDtManutencao(LocalDateTime.now());

//         itemRepository.save(item);
//         historyService.saveItemStatusHistoryBatch(List.of(item), ORIGEM_TRANSICAO);

//         log.info("Item recarga processado - Solicitação={}, Item={}, NovoStatus=RECARREGADO",
//                 numSolicitacao, numSolicitacaoItem);

//         // Buscar todos os itens da solicitação/canal/lote para consolidação
//         String numLote = solicitacao.getNumLote();
//         List<Long> allNumSolicitacaoItens = itemRepository.findNumSolicitacaoItemsBySolicitacaoCanalLote(numSolicitacao, codCanal, numLote);
//         List<CreditRequestItems> itensProcessados = new java.util.ArrayList<>();
//         for (Long itemIdForConsolidation : allNumSolicitacaoItens) {
//             CreditRequestItemsKey keyForConsolidation = new CreditRequestItemsKey();
//             keyForConsolidation.setNumSolicitacao(numSolicitacao);
//             keyForConsolidation.setNumSolicitacaoItem(itemIdForConsolidation);
//             keyForConsolidation.setCodCanal(codCanal);
//             var optItem = itemRepository.findById(keyForConsolidation);
//             optItem.ifPresent(itensProcessados::add);
//         }
//         statusConsolidationHelper.consolidarStatusSolicitacao(numSolicitacao, codCanal, itensProcessados, ORIGEM_TRANSICAO);
//     }



// }

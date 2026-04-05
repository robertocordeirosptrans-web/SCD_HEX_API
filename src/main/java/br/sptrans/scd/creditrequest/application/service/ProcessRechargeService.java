package br.sptrans.scd.creditrequest.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.application.port.in.ProcessRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import br.sptrans.scd.shared.cache.InvalidateOrderCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Implementação do use case de processamento de recarga.
 *
 * <p>
 * Transição do pedido: LIBERADO_PARA_RECARGA(05) → EM_PROCESSO_DE_RECARGA(06).
 * Transição dos itens: LIBERADO_PARA_RECARGA(05) →
 * EM_PROCESSO_DE_RECARGA(06).</p>
 *
 * <p>
 * Itens cujo valor efetivo (vlItem + vlEvento) é ≤ 0 são marcados diretamente
 * como RECARREGADO(07) com valor zero.</p>
 */
@Service
@RequiredArgsConstructor
public class ProcessRechargeService implements ProcessRechargeUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessRechargeService.class);
    private static final String ORIGEM_TRANSICAO = "processar_recarga_scd";

    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestItemsRepository itemRepository;
    private final HistCreditRequestService historyService;
    private final SituationAscertainedService situationAscertainedService;
    private final CreditRequestMapper creditRequestMapper;

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

        List<Long> numSolicitacaoItens = itemRepository.findNumSolicitacaoItemsBySolicitacaoCanalLote(numSolicitacao, codCanal, numLote);
        if (numSolicitacaoItens == null || numSolicitacaoItens.isEmpty()) {
            log.warn("Nenhum item encontrado para solicitação {}/{} e lote {}", numSolicitacao, codCanal, numLote);
            return;
        }

        int processados = 0;
        List<CreditRequestItemsEJpa> itensProcessados = new java.util.ArrayList<>();
        for (Long numSolicitacaoItem : numSolicitacaoItens) {
            CreditRequestItemsKey key = new CreditRequestItemsKey();
            key.setNumSolicitacao(numSolicitacao);
            key.setNumSolicitacaoItem(numSolicitacaoItem);
            key.setCodCanal(codCanal);
            var optItem = itemRepository.findById(key);
            if (optItem.isEmpty()) {
                continue;
            }
            CreditRequestItems item = optItem.get();
            if (!SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode().equals(item.getCodSituacao())) {
                continue;
            }

            BigDecimal vlEvento = item.getVlEvento();
            BigDecimal vlItem = item.getVlItem() != null ? item.getVlItem() : BigDecimal.ZERO;
            BigDecimal valorEfetivo = vlItem.add(vlEvento != null ? vlEvento : BigDecimal.ZERO);

            if (valorEfetivo.compareTo(BigDecimal.ZERO) <= 0) {
                // Item com valor zero → marca diretamente como RECARREGADO
                item.setCodSituacao(SituationCreditRequestItems.RECARREGADO.getCode());
                item.setVlCarregado(BigDecimal.ZERO);
                item.setDtRetornoHm(LocalDateTime.now());
            } else {
                item.setCodSituacao(SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA.getCode());
                item.setDtEnvioHm(LocalDateTime.now());
            }

            if (comando.sqPid() != null) {
                item.setSqPid(comando.sqPid());
                item.setDtInicProcesso(LocalDateTime.now());
            }

            item.setDtManutencao(LocalDateTime.now());
            itemRepository.save(item);
            historyService.saveItemStatusHistory(item, ORIGEM_TRANSICAO);
            processados++;
            // Adiciona para consolidação
            itensProcessados.add(creditRequestMapper.toEntityItem(item));
            log.info("Item processado - Solicitação={}, Item={}, NovoStatus={}",
                    numSolicitacao, item.getId().getNumSolicitacaoItem(), item.getCodSituacao());
        }

        if (processados > 0) {
            consolidarStatusSolicitacao(numSolicitacao, codCanal, itensProcessados);
        }

        log.debug("Processamento de recarga concluído para solicitação {}/{} - {} itens processados",
                numSolicitacao, codCanal, processados);

    }

    @Transactional
    @InvalidateOrderCache
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

        CreditRequestItemsKey key = new CreditRequestItemsKey();
        key.setNumSolicitacao(numSolicitacao);
        key.setNumSolicitacaoItem(numSolicitacaoItem);
        key.setCodCanal(codCanal);

        Optional<CreditRequestItems> itemOpt = itemRepository.findById(key);
        if (itemOpt.isEmpty()) {
            log.warn("Item não encontrado: Solicitação={}, Item={}, CodCanal={}",
                    numSolicitacao, numSolicitacaoItem, codCanal);
            return;
        }

        CreditRequestItems item = itemOpt.get();

        if (!SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA.getCode()
                .equals(item.getCodSituacao())) {
            log.warn("Item {}/{}/{} não está em EM_PROCESSO_DE_RECARGA, status atual={}",
                    numSolicitacao, numSolicitacaoItem, codCanal, item.getCodSituacao());
            return;
        }

        item.setCodSituacao(SituationCreditRequestItems.RECARREGADO.getCode());
        if (comando.vlCarregado() != null) {
            item.setVlCarregado(comando.vlCarregado());
        }
        if (comando.codAssinaturaHsm() != null) {
            item.setCodAssinaturaHsm(comando.codAssinaturaHsm());
        }
        item.setDtRetornoHm(LocalDateTime.now());
        item.setDtManutencao(LocalDateTime.now());

        itemRepository.save(item);
        historyService.saveItemStatusHistory(item, ORIGEM_TRANSICAO);

        log.info("Item recarga processado - Solicitação={}, Item={}, NovoStatus=RECARREGADO",
                numSolicitacao, numSolicitacaoItem);

        // Buscar todos os itens da solicitação/canal/lote para consolidação
        String numLote = solicitacao.getNumLote();
        List<Long> allNumSolicitacaoItens = itemRepository.findNumSolicitacaoItemsBySolicitacaoCanalLote(numSolicitacao, codCanal, numLote);
        List<CreditRequestItemsEJpa> itensProcessados = new java.util.ArrayList<>();
        for (Long itemIdForConsolidation : allNumSolicitacaoItens) {
            CreditRequestItemsKey keyForConsolidation = new CreditRequestItemsKey();
            keyForConsolidation.setNumSolicitacao(numSolicitacao);
            keyForConsolidation.setNumSolicitacaoItem(itemIdForConsolidation);
            keyForConsolidation.setCodCanal(codCanal);
            var optItem = itemRepository.findById(keyForConsolidation);
            if (optItem.isPresent()) {
                itensProcessados.add(creditRequestMapper.toEntityItem(optItem.get()));
            }
        }
        consolidarStatusSolicitacao(numSolicitacao, codCanal, itensProcessados);
    }

    private void consolidarStatusSolicitacao(Long numSolicitacao, String codCanal,
            List<CreditRequestItemsEJpa> itens) {

        CreditRequest solicitacao = creditRequestRepository
                .findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal)
                .orElse(null);

        if (solicitacao == null) {
            log.warn("Solicitação {}/{} não encontrada para consolidação", numSolicitacao, codCanal);
            return;
        }

        List<String> statusItens = itens.stream()
                .map(CreditRequestItemsEJpa::getCodSituacao)
                .filter(Objects::nonNull)
                .toList();

        String novoStatus = situationAscertainedService.apurarSituacaoPedido(statusItens);

        if (novoStatus != null && !novoStatus.equals(solicitacao.getCodSituacao())) {
            String statusAnterior = solicitacao.getCodSituacao();
            solicitacao.setCodSituacao(novoStatus);
            creditRequestRepository.update(numSolicitacao, codCanal, solicitacao);
            historyService.saveRequestStatusHistory(solicitacao, numSolicitacao, codCanal, ORIGEM_TRANSICAO);

            log.info("Status da solicitação {}/{} consolidado - StatusAnterior={}, NovoStatus={}",
                    numSolicitacao, codCanal, statusAnterior, novoStatus);
        }
    }

}


package br.sptrans.scd.creditrequest.application.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.sptrans.scd.channel.application.port.out.MarketingDistribuitionChannelPersistencePort;
import br.sptrans.scd.creditrequest.application.port.in.ProcessRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRDPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.EventoFinanceiroPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.HmPort;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestRD;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import br.sptrans.scd.product.application.port.out.gateway.LiminarGateway;
import br.sptrans.scd.shared.cache.InvalidateOrderCache;
import br.sptrans.scd.shared.helper.StatusConsolidationHelper;
import lombok.RequiredArgsConstructor;

/**
 * Implementação do use case de processamento de recarga.
 *
 * <p>
 * Transição do pedido: LIBERADO_PARA_RECARGA(05) → EM_PROCESSO_DE_RECARGA(06).
 * </p>
 * <p>
 * Transição dos itens: LIBERADO_PARA_RECARGA(05) → EM_PROCESSO_DE_RECARGA(06).
 * </p>
 *
 * <p>
 * <b>Caminho A — valor efetivo &gt; 0:</b> item vai para
 * EM_PROCESSO_DE_RECARGA(06),
 * DT_ENVIO_HM preenchida, autorização enviada ao HM, histórico com descrição do
 * evento.
 * </p>
 *
 * <p>
 * <b>Caminho B — valor efetivo &le; 0:</b> item passa por
 * EM_PROCESSO_DE_RECARGA(06)
 * e imediatamente vai para RECARREGADO(07) com VL_CARREGADO=0, sem integração
 * HM.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ProcessRechargeService implements ProcessRechargeUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessRechargeService.class);
    private static final String ORIGEM_TRANSICAO = "process_recarga_scd";
    private static final String DES_RECARREGADO_POR_EVENTO = "Indicado como recarregado devido a ocorrencia de eventos financeiros";

    private final CreditRequestItemsPort itemRepository;
    private final MarketingDistribuitionChannelPersistencePort marketingDistribuitionChannelPersistencePort;
    private final CreditRequestRDPort creditRequestRDPort;
    private final HistCreditRequestService historyService;
    private final StatusConsolidationHelper statusConsolidationHelper;
    private final LiminarGateway liminarGateway;
    private final EventoFinanceiroPort eventoFinanceiroPort;
    private final HmPort hmPort;

    @InvalidateOrderCache
    @Override
    public void processarLoteRecarga(List<CreditRequestItems> itens) {

        if (itens.isEmpty()) {
            log.debug("Nenhum item recebido para processamento de lote.");
            return;
        }

        List<CreditRequestItems> processados = new ArrayList<>();
        for (CreditRequestItems item : itens) {
            try {
                String codCanal = item.getCodCanal();
                Long numSolicitacao = item.getSolicitacao() != null ? item.getSolicitacao().getNumSolicitacao() : null;

                // Busca os terminais reais do pedido em SOL_RD_DISTRIBUICOES.
                // Se não houver registros (pedidos legados ou migração parcial), faz fallback
                // para o MarketingDistribuitionChannel como era feito antes.
                List<String> canaisParaRegistrar = resolverCanaisDistribuicao(numSolicitacao, codCanal);

                BigDecimal wValorEventoItem = BigDecimal.ZERO;
                BigDecimal wValorDescItem = BigDecimal.ZERO;
                String wOutrasVias = "0";
                boolean temEvento = "1".equals(item.getFlgEvento());

                if (temEvento) {
                    wValorEventoItem = eventoFinanceiroPort.processarLancamento(
                            numSolicitacao, codCanal, item.getId().getNumSolicitacaoItem());
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

                if (valorEfetivo.compareTo(BigDecimal.ZERO) > 0) {
                    // ── CAMINHO A: envia ao HM ──────────────────────────────────────
                    processarCaminhoA(item, temEvento, wValorDescItem, wOutrasVias, valorEfetivo, canaisParaRegistrar);
                } else {
                    // ── CAMINHO B: vai direto para RECARREGADO ──────────────────────
                    processarCaminhoB(item, numSolicitacao, codCanal);
                }

                item.setDtManutencao(LocalDateTime.now());
                item.setCodSituacao(SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA);

                processados.add(item);
            } catch (Exception e) {
                log.error("Erro ao processar item {}: {}", item.getId(), e.getMessage(), e);
            }
        }

        // Salva histórico em batch
        if (!processados.isEmpty()) {
            historyService.saveItemStatusHistoryBatch(processados, ORIGEM_TRANSICAO);
            // Consolida status dos itens com a solicitação
            for (CreditRequestItems item : processados) {
                if (item.getSolicitacao() != null) {
                    statusConsolidationHelper.consolidarStatusSolicitacao(
                            item.getSolicitacao().getNumSolicitacao(),
                            item.getSolicitacao().getCodCanal(),
                            List.of(item),
                            ORIGEM_TRANSICAO);
                }
            }
        }
    }

    // ── helpers privados ────────────────────────────────────────────────────────

    /**
     * Resolve a lista de terminais de distribuição para um pedido.
     *
     * <p>Consulta primeiro SOL_RD_DISTRIBUICOES. Se a tabela não tiver registros para o pedido
     * (pedidos legados ou recarga anterior à implantação desta feature), faz fallback para
     * {@code MarketingDistribuitionChannel}, garantindo compatibilidade retroativa.</p>
     */
    private List<String> resolverCanaisDistribuicao(Long numSolicitacao, String codCanal) {
        if (numSolicitacao != null) {
            List<CreditRequestRD> distribuicoes =
                    creditRequestRDPort.findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal);
            if (distribuicoes != null && !distribuicoes.isEmpty()) {
                return distribuicoes.stream()
                        .map(CreditRequestRD::getCodCanalDistribuicao)
                        .toList();
            }
        }
        // Fallback: comportamento anterior
        var mktDist = marketingDistribuitionChannelPersistencePort.findByAssocied(codCanal).orElse(null);
        String canalFallback = (mktDist != null && mktDist.getId() != null && mktDist.getId().getCodCanalDistribuicao() != null)
                ? mktDist.getId().getCodCanalDistribuicao()
                : codCanal;
        log.debug("resolverCanaisDistribuicao — sem registros em SOL_RD_DISTRIBUICOES para numSolicitacao={}, usando fallback={}",
                numSolicitacao, canalFallback);
        if (canalFallback == null) {
            log.warn("resolverCanaisDistribuicao — codCanal nulo para numSolicitacao={}, nenhum terminal a registrar", numSolicitacao);
            return List.of();
        }
        return List.of(canalFallback);
    }

    /**
     * Caminho A: valor efetivo &gt; 0 — item marcado como EM_PROCESSO_DE_RECARGA,
     * campos de evento preenchidos e autorização enviada ao HM.
     *
     * <p>Registra cada terminal da lista {@code canaisParaRegistrar} na TB_REDE do HM
     * (idempotente — o HM ignora duplicatas).</p>
     */
    private void processarCaminhoA(CreditRequestItems item,
            boolean temEvento, BigDecimal wValorDescItem, String wOutrasVias,
            BigDecimal valorEfetivo, List<String> canaisParaRegistrar) {

        item.setCodSituacao(SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA);
        item.setDtEnvioHm(LocalDateTime.now());
        item.setDtManutencao(LocalDateTime.now());

        // Registra todos os terminais reais do pedido no HM (TB_REDE)
        for (String terminal : canaisParaRegistrar) {
            hmPort.registrarAutorizacaoRecarga(item.getId().getNumSolicitacao(), item.getId().getCodCanal(), terminal);
        }

        if (temEvento) {
            item.setVlEvento(wValorDescItem);
            item.setFlgEvento("S");
            item.setFlgOutrasVias(wOutrasVias);
        }

        itemRepository.save(item);

        // Envia autorização ao HM (detalhada)
        int liminar = liminarGateway.verificarLiminarEmpresa(item.getId().getNumSolicitacao());
        hmPort.enviarAutorizacaoRecarga(
                item.getId().getNumSolicitacao(),
                item.getId().getNumSolicitacaoItem(),
                item.getId().getCodCanal(),
                item.getNumLogicoCartao(),
                item.getCodAssinaturaHsm(),
                item.getDtPagtoEconomica(),
                item.getSeqRecarga(),
                valorEfetivo,
                liminar);

        // Grava histórico imediatamente, com descrição se houver evento
        if (temEvento && wValorDescItem != null && wValorDescItem.compareTo(BigDecimal.ZERO) != 0) {
            String descEvento = "Ajuste em decorrencia de Eventos no valor de "
                    + NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(wValorDescItem);
            Map<Long, String> descMap = Map.of(item.getId().getNumSolicitacaoItem(), descEvento);
            historyService.saveItemStatusHistoryBatch(List.of(item), ORIGEM_TRANSICAO, descMap);
        } else {
            historyService.saveItemStatusHistoryBatch(List.of(item), ORIGEM_TRANSICAO);
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

}

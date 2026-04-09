package br.sptrans.scd.creditrequest.application.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.application.port.in.ConfirmedRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestPort;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import br.sptrans.scd.shared.helper.StatusConsolidationHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Implementação do use case de confirmação de recarga.
 *
 * <p>Equivalente à rotina {@code ConfirmarRecarga} da package Oracle
 * {@code PCK_MVE_SITUACAOPEDIDO}.</p>
 *
 * <p>Para cada item em {@code EM_PROCESSO_DE_RECARGA (06)} cujo retorno do
 * Hardware Manager já foi registrado ({@code DT_RETORNO_HM} preenchida),
 * atualiza o status para {@code RECARREGADO (07)} e recalcula a situação
 * consolidada do pedido.</p>
 */
@Service
@RequiredArgsConstructor
public class ConfirmedRechargeService implements ConfirmedRechargeUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConfirmedRechargeService.class);
    private static final String ORIGEM_TRANSICAO = "confirmar_recarga_scd";

    private final CreditRequestPort creditRequestRepository;
    private final CreditRequestItemsPort itemRepository;
    private final CreditRequestMapper mapperItens;
    private final HistCreditRequestService historyService;
    private final StatusConsolidationHelper statusConsolidationHelper;



    @Override
    @Transactional
    public void confirmarRecarga(Long numSolicitacao, String codCanal) {
        log.debug("Confirmando recarga para solicitação {}/{}", numSolicitacao, codCanal);

        // Obtém o numLote da solicitação
        CreditRequest solicitacao = creditRequestRepository
                .findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal)
                .orElse(null);
        if (solicitacao == null) {
            log.warn("Solicitação não encontrada para numSolicitacao={}, codCanal={}", numSolicitacao, codCanal);
            return;
        }
        String numLote = solicitacao.getNumLote();
        List<Long> numSolicitacaoItens = itemRepository.findNumSolicitacaoItemsBySolicitacaoCanalLote(numSolicitacao, codCanal, numLote);
        if (numSolicitacaoItens.isEmpty()) {
            log.warn("Nenhum item encontrado para solicitação {}/{} e lote {}", numSolicitacao, codCanal, numLote);
            return;
        }

        int confirmados = 0;
        List<CreditRequestItemsEJpa> itens = new ArrayList<>();
        List<CreditRequestItems> itensDominio = new ArrayList<>();
        for (Long numSolicitacaoItem : numSolicitacaoItens) {
            CreditRequestItemsKey key = new CreditRequestItemsKey();
            key.setNumSolicitacao(numSolicitacao);
            key.setNumSolicitacaoItem(numSolicitacaoItem);
            key.setCodCanal(codCanal);
            var optItem = itemRepository.findById(key);
            if (optItem.isEmpty()) continue;
            CreditRequestItems item = optItem.get();
            if (!SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA.equals(item.getCodSituacao())) {
                continue;
            }
            if (item.getDtRetornoHm() == null) {
                continue;
            }
            String statusAnterior = item.getCodSituacao().getCode();
            item.setCodSituacao(SituationCreditRequestItems.RECARREGADO);
            item.setDtManutencao(java.time.LocalDateTime.now());
            itemRepository.save(item);
            confirmados++;
            itensDominio.add(item);
            // Adiciona para consolidação
            itens.add(mapperItens.toEntityItem(item));
            log.info("Item confirmado - Solicitação={}, Item={}, StatusAnterior={}, NovoStatus={}", numSolicitacao,
                    item.getId().getNumSolicitacaoItem(), statusAnterior,
                    SituationCreditRequestItems.RECARREGADO.getCode());
        }


        if (confirmados > 0) {
            historyService.saveItemStatusHistoryBatch(itensDominio, ORIGEM_TRANSICAO);
            statusConsolidationHelper.consolidarStatusSolicitacao(numSolicitacao, codCanal, itens, ORIGEM_TRANSICAO);
        }

        log.debug("Confirmação de recarga concluída para solicitação {}/{} - {} itens confirmados",
                numSolicitacao, codCanal, confirmados);
    }

 

}

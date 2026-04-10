package br.sptrans.scd.creditrequest.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.application.port.in.ConfirmedRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.HmPort;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
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
    private static final String ORIGEM_TRANSICAO = "confirm_recarga_scd";

    private final CreditRequestItemsPort itemRepository;
    private final HistCreditRequestService historyService;
    private final StatusConsolidationHelper statusConsolidationHelper;
    private final HmPort hmGateway;



    @Override
    @Transactional
    public void confirmarRecarga(CreditRequestItems item) {
        
        Long numSolicitacao = item.getId().getNumSolicitacao();
        String codCanal = item.getId().getCodCanal();
        Long numSolicitacaoItem = item.getId().getNumSolicitacaoItem();


        boolean hmConfirmou = hmGateway.itemConfirmadoPeloHm(numSolicitacao, codCanal, item.getNumLogicoCartao());
        if (!hmConfirmou) {
            log.debug("HM ainda não confirmou item: Solicitação={}, Item={}",
                    numSolicitacao, numSolicitacaoItem);
            return;
        }

        // HM confirmou → atualiza para RECARREGADO
        String statusAnterior = item.getCodSituacao().getCode();
        item.setCodSituacao(SituationCreditRequestItems.RECARREGADO);
        item.setDtRetornoHm(LocalDateTime.now());
        item.setDtManutencao(LocalDateTime.now());
        itemRepository.save(item);
        log.info("Item confirmado - Solicitação={}, Item={}, StatusAnterior={}, NovoStatus={}",
                item.getId().getNumSolicitacao(),
                item.getId().getNumSolicitacaoItem(),
                statusAnterior,
                SituationCreditRequestItems.RECARREGADO.getCode());

        // Registra histórico individual
        historyService.saveItemStatusHistoryBatch(List.of(item), ORIGEM_TRANSICAO);

        // Atualiza status consolidado do pedido
        statusConsolidationHelper.consolidarStatusSolicitacao(numSolicitacao, codCanal, List.of(item), ORIGEM_TRANSICAO);

        log.debug("Confirmação de recarga concluída para solicitação {}/{} item {}", numSolicitacao, codCanal, numSolicitacaoItem);
    }

 

}

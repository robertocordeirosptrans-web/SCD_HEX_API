package br.sptrans.scd.shared.helper;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestPort;
import br.sptrans.scd.creditrequest.application.service.HistCreditRequestService;
import br.sptrans.scd.creditrequest.application.service.SituationAscertainedService;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequest;

@Component
public class StatusConsolidationHelper {
    private static final Logger log = LoggerFactory.getLogger(StatusConsolidationHelper.class);

    private final CreditRequestPort creditRequestRepository;
    private final SituationAscertainedService situationAscertainedService;
    private final HistCreditRequestService historyService;

    public StatusConsolidationHelper(
            CreditRequestPort creditRequestRepository,
            SituationAscertainedService situationAscertainedService,
            HistCreditRequestService historyService
    ) {
        this.creditRequestRepository = creditRequestRepository;
        this.situationAscertainedService = situationAscertainedService;
        this.historyService = historyService;
    }

    /**
     * Consolida o status da solicitação a partir dos status dos itens.
     * @param numSolicitacao Número da solicitação
     * @param codCanal Código do canal
     * @param itens Lista de itens (EJpa)
     * @param origemTransicao Origem da transição para histórico
     */
    public void consolidarStatusSolicitacao(Long numSolicitacao, String codCanal, List<CreditRequestItemsEJpa> itens, String origemTransicao) {
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

        if (novoStatus != null && !novoStatus.equals(solicitacao.getCodSituacao().getCode())) {
            String statusAnterior = solicitacao.getCodSituacao().getCode();
            solicitacao.setCodSituacao(SituationCreditRequest.fromCode(novoStatus));
            creditRequestRepository.update(numSolicitacao, codCanal, solicitacao);
            historyService.saveRequestStatusHistory(solicitacao, numSolicitacao, codCanal, origemTransicao);
            log.info("Status da solicitação {}/{} consolidado - StatusAnterior={}, NovoStatus={}",
                    numSolicitacao, codCanal, statusAnterior, novoStatus);
        }
    }

    /**
     * Apenas apura o novo status consolidado dos itens, sem atualizar entidades.
     * @param itens Lista de itens (EJpa)
     * @param statusAtualSolicitacao Status atual da solicitação
     * @return Novo status ou null se não houver mudança
     */
    public String apurarNovoStatus(List<CreditRequestItemsEJpa> itens, String statusAtualSolicitacao) {
        List<String> statusItens = itens.stream()
                .map(CreditRequestItemsEJpa::getCodSituacao)
                .filter(Objects::nonNull)
                .toList();
        String novoStatus = situationAscertainedService.apurarSituacaoPedido(statusItens);
        if (novoStatus != null && !novoStatus.equals(statusAtualSolicitacao)) {
            return novoStatus;
        }
        return null;
    }
}

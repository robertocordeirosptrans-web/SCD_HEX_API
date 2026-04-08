
package br.sptrans.scd.creditrequest.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.creditrequest.application.port.in.HistCreditRequestManagementUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.HistCreditRequestItemsPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.HistCreditRequestPort;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItems;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestKey;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class HistCreditRequestService implements HistCreditRequestManagementUseCase {

    private final HistCreditRequestItemsPort itemHistoryRepository;
    private final HistCreditRequestPort requestHistoryRepository;
    private final UserResolverHelper userResolverHelper;

    private static final Logger log = LoggerFactory.getLogger(HistCreditRequestService.class);

    @Override
    public List<HistCreditRequestItems> findItemStatusHistory(Long numSolicitacao, Long numSolicitacaoItem,
            String codCanal) {
        return itemHistoryRepository.findLatestByItem(numSolicitacao, numSolicitacaoItem, codCanal);
    }

    @Override
    public List<HistCreditRequest> findRequestStatusHistory(Long numSolicitacao, String codCanal) {
        return requestHistoryRepository.findLatestBySolicitacao(numSolicitacao, codCanal);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveItemStatusHistoryBatch(List<CreditRequestItems> itens, String origemTransicao) {
        List<HistCreditRequestItems> registros = itens.stream()
                .map(item -> montarHistoricoSeNecessario(item, origemTransicao))
                .filter(Objects::nonNull)
                .toList();
        if (!registros.isEmpty()) {
            itemHistoryRepository.saveAll(registros);
            log.info("Batch de históricos de itens salvo: {} registros", registros.size());
        } else {
            log.info("Nenhum histórico novo a salvar no batch (todos já existentes)");
        }
    }

    /**
     * Monta o histórico do item se necessário (se não for duplicado). Retorna null
     * se já existir status igual.
     */
    private HistCreditRequestItems montarHistoricoSeNecessario(CreditRequestItems item, String origemTransicao) {
        List<HistCreditRequestItems> latestHistory = itemHistoryRepository.findLatestByItem(
                item.getId().getNumSolicitacao(),
                item.getId().getNumSolicitacaoItem(),
                item.getId().getCodCanal());
        if (!latestHistory.isEmpty()) {
            HistCreditRequestItems latest = latestHistory.get(0);
            if (item.getCodSituacao().getCode().equals(latest.getCodSituacao())) {
                log.info(
                        "[BATCH] Já existe no histórico - Solicitação: {}, Item: {}, Status: {} - Ignorando duplicado",
                        item.getId().getNumSolicitacao(),
                        item.getId().getNumSolicitacaoItem(),
                        item.getCodSituacao().getCode());
                return null;
            }
        }

        Long nextSeq = itemHistoryRepository.findMaxSeqHistSdis(
                item.getId().getNumSolicitacao(),
                item.getId().getNumSolicitacaoItem(),
                item.getId().getCodCanal()) + 1;

        HistCreditRequestItemsKey historyId = new HistCreditRequestItemsKey();
        historyId.setNumSolicitacao(item.getId().getNumSolicitacao());
        historyId.setNumSolicitacaoItem(item.getId().getNumSolicitacaoItem());
        historyId.setCodCanal(item.getId().getCodCanal());
        historyId.setSeqHistSdis(nextSeq);

        HistCreditRequestItems history = new HistCreditRequestItems();
        history.setId(historyId);
        history.setCodTipoDocumento(item.getCodTipoDocumento());
        history.setCodSituacao(item.getCodSituacao().getCode());
        history.setDtTransicao(LocalDateTime.now());
        history.setIdOrigemTransicao(origemTransicao);
        history.setDtCadastro(item.getDtCadastro());
        history.setDtManutencao(LocalDateTime.now());
        history.setDtPgtoEconomica(item.getDtPagtoEconomica());
        history.setSqPID(item.getSqPid());
        history.setDtInicProcesso(item.getDtInicProcesso());

        User currentUser = userResolverHelper.getCurrentUser();
        if (currentUser != null) {
            history.setIdUsuarioTransicao(currentUser);
        }
        return history;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRequestStatusHistory(CreditRequest request, Long numSolicitacao, String codCanal,
            String origemTransicao) {
        try {
            List<HistCreditRequest> latestHistory = requestHistoryRepository.findLatestBySolicitacao(
                    numSolicitacao, codCanal);

            if (!latestHistory.isEmpty()) {
                HistCreditRequest latest = latestHistory.get(0);
                if (request.getCodSituacao().getCode().equals(latest.getCodSituacao())) {
                    log.info("Já existe no histórico - Solicitação: {}, Status: {} - Ignorando duplicado",
                            numSolicitacao,
                            request.getCodSituacao().getCode());
                    return;
                }

            }

            Long nextSeq = requestHistoryRepository.findMaxSeqHistSdis(
                    numSolicitacao, codCanal) + 1;

            HistCreditRequestKey historyId = new HistCreditRequestKey();
            historyId.setNumSolicitacao(numSolicitacao);
            historyId.setCodCanal(codCanal);
            historyId.setSeqHistSdis(nextSeq);

            HistCreditRequest history = new HistCreditRequest();
            history.setId(historyId);
            history.setCodTipoDocumento(request.getCodTipoDocumento());
            history.setCodSituacao(request.getCodSituacao().getCode());
            history.setDtTransicao(LocalDateTime.now());
            history.setIdOrigemTransicao(origemTransicao);
            history.setDtCadastro(request.getDtCadastro());
            history.setDtManutencao(LocalDateTime.now());
            history.setDtPgtoEconomica(request.getDtPagtoEconomica());
            history.setSqPID(request.getSqPid());
            history.setDtInicProcesso(request.getDtInicProcesso());

            User currentUser = userResolverHelper.getCurrentUser();
            if (currentUser != null) {
                history.setIdUsuarioTransicao(currentUser);
            }

            requestHistoryRepository.save(history);

            log.info("Histórico de solicitação salvo com sucesso - Solicitação: {}, Novo Status: {}, Seq: {}",
                    numSolicitacao, request.getCodSituacao().getCode(), nextSeq);
        } catch (Exception e) {
            log.error("ERRO CRÍTICO: Falha ao salvar histórico de solicitação - Solicitação: {}, Status: {}",
                    numSolicitacao,
                    request.getCodSituacao().getCode(), e);
        }
    }

}

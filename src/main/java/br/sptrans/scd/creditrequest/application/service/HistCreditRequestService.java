package br.sptrans.scd.creditrequest.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.creditrequest.application.port.in.HistCreditRequestManagementUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.HistCreditRequestItemsRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.HistCreditRequestRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItems;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestKey;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistCreditRequestService implements HistCreditRequestManagementUseCase {

    private final HistCreditRequestItemsRepository itemHistoryRepository;
    private final HistCreditRequestRepository requestHistoryRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(HistCreditRequestService.class);

    @Override
    public List<HistCreditRequestItems> findItemStatusHistory(Long numSolicitacao, Long numSolicitacaoItem, String codCanal) {
        return itemHistoryRepository.findLatestByItem(numSolicitacao, numSolicitacaoItem, codCanal);
    }

    @Override
    public List<HistCreditRequest> findRequestStatusHistory(Long numSolicitacao, String codCanal) {
        return requestHistoryRepository.findLatestBySolicitacao(numSolicitacao, codCanal);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveItemStatusHistory(CreditRequestItems item, String origemTransicao) {
        try {
            List<HistCreditRequestItems> latestHistory = itemHistoryRepository.findLatestByItem(
                    item.getId().getNumSolicitacao(),
                    item.getId().getNumSolicitacaoItem(),
                    item.getId().getCodCanal());

            if (!latestHistory.isEmpty()) {
                HistCreditRequestItems latest = latestHistory.get(0);
                if (item.getCodSituacao().equals(latest.getCodSituacao())) {
                    log.info("Status already exists in history - Solicitação: {}, Item: {}, Status: {} - Skipping duplicate",
                            item.getId().getNumSolicitacao(),
                            item.getId().getNumSolicitacaoItem(),
                            item.getCodSituacao());
                    return;
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
            history.setCodSituacao(item.getCodSituacao());
            history.setDtTransicao(LocalDateTime.now());
            history.setIdOrigemTransicao(origemTransicao);
            history.setDtCadastro(item.getDtCadastro());
            history.setDtManutencao(item.getDtManutencao());
            history.setDtPgtoEconomica(item.getDtPagtoEconomica());
            history.setSqPID(item.getSqPid());
            history.setDtInicProcesso(item.getDtInicProcesso());

            User currentUser = getCurrentUser();
            if (currentUser != null) {
                history.setIdUsuarioTransicao(currentUser);
            }

            itemHistoryRepository.save(history);

            log.info("Successfully saved item status history - Solicitação: {}, Item: {}, New Status: {}, Seq: {}",
                    item.getId().getNumSolicitacao(),
                    item.getId().getNumSolicitacaoItem(),
                    item.getCodSituacao(),
                    nextSeq);
        } catch (Exception e) {
            log.error("CRITICAL ERROR: Failed to save item status history for Solicitação: {}, Item: {}, Status: {}",
                    item.getId().getNumSolicitacao(),
                    item.getId().getNumSolicitacaoItem(),
                    item.getCodSituacao(), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRequestStatusHistory(CreditRequest request, Long numSolicitacao, String codCanal, String origemTransicao) {
        try {
            List<HistCreditRequest> latestHistory = requestHistoryRepository.findLatestBySolicitacao(
                    numSolicitacao, codCanal);

            if (!latestHistory.isEmpty()) {
                HistCreditRequest latest = latestHistory.get(0);
                if (request.getCodSituacao().equals(latest.getCodSituacao())) {
                    log.info("Status already exists in history - Solicitação: {}, Status: {} - Skipping duplicate",
                            numSolicitacao,
                            request.getCodSituacao());
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
            history.setCodSituacao(request.getCodSituacao());
            history.setDtTransicao(LocalDateTime.now());
            history.setIdOrigemTransicao(origemTransicao);
            history.setDtCadastro(request.getDtCadastro());
            history.setDtManutencao(request.getDtManutencao());
            history.setDtPgtoEconomica(request.getDtPagtoEconomica());
            history.setSqPID(request.getSqPid());
            history.setDtInicProcesso(request.getDtInicProcesso());

            User currentUser = getCurrentUser();
            if (currentUser != null) {
                history.setIdUsuarioTransicao(currentUser);
            }

            requestHistoryRepository.save(history);

            log.info("Successfully saved solicitation status history - Solicitação: {}, New Status: {}, Seq: {}",
                    numSolicitacao,
                    request.getCodSituacao(),
                    nextSeq);
        } catch (Exception e) {
            log.error("CRITICAL ERROR: Failed to save solicitation status history for Solicitação: {}, Status: {}",
                    numSolicitacao,
                    request.getCodSituacao(), e);
        }
    }

    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof String codLogin) {
                return userRepository.findByCodLogin(codLogin).orElse(null);
            }
        } catch (Exception e) {
            log.debug("Could not get current user from security context", e);
        }
        return null;
    }
}

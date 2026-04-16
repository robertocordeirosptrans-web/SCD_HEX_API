package br.sptrans.scd.creditrequest.application.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestCredit;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse;
import br.sptrans.scd.creditrequest.application.port.out.projection.ProductPeriodReportProjection;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestPort;
import br.sptrans.scd.creditrequest.application.usecases.AlterarStatusCreditRequestCase;
import br.sptrans.scd.creditrequest.application.usecases.CreateCreditRequestCase;
import br.sptrans.scd.creditrequest.application.usecases.PeriodReportCreditCase;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.ActionStatus;
import br.sptrans.scd.creditrequest.domain.enums.SearchMode;
import br.sptrans.scd.shared.cache.InvalidateOrderCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditRequestService implements CreditRequestManagementUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreditRequestService.class);

    private final CreditRequestPort creditRequestRepository;
    private final AlterarStatusCreditRequestCase alterarStatusCase;
    private final CreateCreditRequestCase createCreditRequestCase;
    private final PeriodReportCreditCase periodReportCase;

    // ── Ações de mudança de status ───────────────────────────────────

    @Override
    @Transactional
    @InvalidateOrderCache
    public void block(BlockCommand comando) {
        log.info("Iniciando bloqueio - Entradas: {}", comando.itens().size());
        alterarStatusCase.execute(ActionStatus.BLOQUEAR, comando.itens(), null, null, null, null);
    }

    @Override
    @Transactional
    @InvalidateOrderCache
    public void unblock(UnblockCommand comando) {
        log.info("Iniciando desbloqueio - Entradas: {}", comando.itens().size());
        alterarStatusCase.execute(ActionStatus.DESBLOQUEAR, comando.itens(), null, null, null, null);
    }

    @Override
    @Transactional
    @InvalidateOrderCache
    public void cancel(CancelCommand comando) {
        log.info("Iniciando cancelamento - Entradas: {}", comando.itens().size());
        alterarStatusCase.execute(ActionStatus.CANCELAR, comando.itens(), null, null, null, null);
    }

    @Override
    @Transactional
    @InvalidateOrderCache
    public void pay(PayCommand comando) {
        log.info("Iniciando pagamento - Itens: {}, FormaPagto: {}", comando.itens().size(), comando.codFormaPagto());
        alterarStatusCase.executePay(comando);
    }

    @Override
    @Transactional
    @InvalidateOrderCache
    public void acceptPendingSettlement(AcceptPendingCommand comando) {
        log.info("Iniciando aceite pendente liquidação - Entradas: {}", comando.itens().size());
        alterarStatusCase.execute(ActionStatus.ACEITO_PENDENTE_LIQUIDACAO, comando.itens(), null, null, null,
                comando.dtAceite());
    }

    // ── Relatórios ───────────────────────────────────────────────────────

    @Override
    public List<ProductPeriodReportProjection> generateProductPeriodReport(ProductPeriodReportEntry comand) {
        return periodReportCase.execute(comand.codCanal(), comand.dataInicio(), comand.dataFim(), comand.codProdutos());
    }
 
    // ── Criação ───────────────────────────────────────────────────────

    @Override
    @Transactional
    @InvalidateOrderCache
    public CreateRequestResponse createCreditRequest(CreateRequestCredit request, String idempotencyKey, Long userId) {
        return createCreditRequestCase.execute(request, idempotencyKey, userId);
    }

    // ── Consultas ────────────────────────────────────────────────────

    @Override
    public CreditRequest findById(String codTipoDocumento, Long idUsuarioCadastro) {
        return creditRequestRepository
                .findByCodTipoDocumentoAndIdUsuarioCadastro(codTipoDocumento, idUsuarioCadastro)
                .orElse(null);
    }

    @Override
    public CursorPage<CreditRequest> findAll(SearchCommand comando) {
        SearchMode mode = comando.searchMode() != null
                ? comando.searchMode()
                : SearchMode.OPERATIONAL;

        int limit = mode.getMaxPageSize() + 1;

        List<CreditRequest> results = creditRequestRepository.findWithCursor(
                comando.cursorNumSolicitacao(),
                comando.cursorCodCanal(),
                comando.codCanal(),
                comando.codSituacao(),
                comando.numLote(),
                comando.codFormaPagto(),
                comando.dtInicio(),
                comando.dtFim(),
                comando.dtLiberacaoEfetivaInicio(),
                comando.dtLiberacaoEfetivaFim(),
                comando.dtPagtoEconomicaInicio(),
                comando.dtPagtoEconomicaFim(),
                comando.dtFinanceiraInicio(),
                comando.dtFinanceiraFim(),
                comando.dtAlteracaoInicio(),
                comando.dtAlteracaoFim(),
                comando.vlTotalMin(),
                comando.vlTotalMax(),
                limit);

        boolean hasNext = results.size() > mode.getMaxPageSize();
        List<CreditRequest> content = hasNext
                ? results.subList(0, mode.getMaxPageSize())
                : results;

        String nextCursorNumSol = null;
        String nextCursorCodCanal = null;
        if (hasNext && !content.isEmpty()) {
            CreditRequest last = content.get(content.size() - 1);
            nextCursorNumSol = last.getNumSolicitacao() != null
                    ? last.getNumSolicitacao().toString()
                    : null;
            nextCursorCodCanal = last.getCodCanal();
        }

        return new CursorPage<>(content, content.size(), hasNext,
                nextCursorNumSol, nextCursorCodCanal);
    }


}

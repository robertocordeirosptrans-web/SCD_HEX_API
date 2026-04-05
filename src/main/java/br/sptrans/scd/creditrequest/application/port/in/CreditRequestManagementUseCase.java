package br.sptrans.scd.creditrequest.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestCredit;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SearchMode;

public interface CreditRequestManagementUseCase {
    // ── Ações de mudança de status (usuário) ─────────────────────────

    void block(BlockCommand comando);

    void unblock(UnblockCommand comando);

    void cancel(CancelCommand comando);

    void pay(PayCommand comando);

    void acceptPendingSettlement(AcceptPendingCommand comando);

    // ── Ações de criação ──────────────────────────────────────────────
    CreateRequestResponse createCreditRequest(CreateRequestCredit request, String idempotencyKey, Long userId);

    // ── Consultas ────────────────────────────────────────────────────
    CreditRequest findById(String codTipoDocumento, Long idUsuarioCadastro);

    CursorPage<CreditRequest> findAll(SearchCommand comando);

    // ── Referência a um pedido + canal + seus itens ────────────────
    record OrderItemEntry(
            Long numSolicitacao,
            String codCanal,
            List<Long> numSolicitacaoItems
            ) {

    }

    record BlockCommand(
            List<OrderItemEntry> itens,
            Long idUsuarioTransicao,
            String desOcorrencia
            ) {

    }

    record UnblockCommand(
            List<OrderItemEntry> itens,
            Long idUsuarioTransicao,
            String desOcorrencia
            ) {

    }

    record CancelCommand(
            List<OrderItemEntry> itens,
            Long idUsuarioTransicao,
            String desOcorrencia
            ) {

    }

    record PayItemEntry(
            Long numSolicitacao,
            Long numSolicitacaoItem,
            String codCanal,
            String codProduto,
            String codSituacao,
            Double vlItem,
            Double vlTxadm,
            Double vlTxserv
            ) {

    }

    record PayCommand(
            List<PayItemEntry> itens,
            Long idUsuarioTransicao,
            String codFormaPagto,
            BigDecimal vlPago,
            LocalDateTime dtConfirmaPagto
            ) {

    }

    record AcceptPendingCommand(
            List<OrderItemEntry> itens,
            Long idUsuarioTransicao,
            LocalDateTime dtAceite
            ) {

    }

    record CursorPage<T>(
            List<T> content,
            int size,
            boolean hasNext,
            String nextCursorNumSolicitacao,
            String nextCursorCodCanal
            ) {

    }

    record SearchCommand(
            // ── Cursor ──
            Long cursorNumSolicitacao,
            String cursorCodCanal,
            // ── Filtros ──
            String codCanal,
            String codSituacao,
            String numLote,
            String codFormaPagto,
            LocalDateTime dtInicio,
            LocalDateTime dtFim,
            LocalDateTime dtLiberacaoEfetivaInicio,
            LocalDateTime dtLiberacaoEfetivaFim,
            LocalDateTime dtPagtoEconomicaInicio,
            LocalDateTime dtPagtoEconomicaFim,
            LocalDateTime dtFinanceiraInicio,
            LocalDateTime dtFinanceiraFim,
            LocalDateTime dtAlteracaoInicio,
            LocalDateTime dtAlteracaoFim,
            BigDecimal vlTotalMin,
            BigDecimal vlTotalMax,
            // ── Modo de busca ──
            SearchMode searchMode
            ) {

    }
}

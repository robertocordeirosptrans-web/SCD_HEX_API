package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.domain.CreditRequest;

/**
 * Porta de saída (output port) para persistência de CreditRequest.
 * Implementada pelo adapter JPA (CreditRequestAdapterJpa).
 */
public interface CreditRequestRepository {

    Optional<CreditRequest> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal);

    List<CreditRequest> findByCanalAndSituacao(String codCanal, String codSituacao);

    boolean existsByNumSolicitacao(Long numSolicitacao);

    boolean existsByNumLoteAndCodCanal(String numLote, String codCanal);

    CreditRequest findElegiveisParaLiberacao(String codSituacao, LocalDateTime dtInicio, LocalDateTime dtFim);

    CreditRequest findElegiveisParaProcessamento(String codSituacao);

    CreditRequest findElegiveisParaConfirmacao(String codSituacao);

    void update(Long numSolicitacao, String codCanal, CreditRequest creditRequest);

    Optional<CreditRequest> findByCodTipoDocumentoAndIdUsuarioCadastro(String codTipoDocumento, Long idUsuarioCadastro);

    /**
     * Busca paginada por cursor com filtros dinâmicos.
     */
    List<CreditRequest> findWithCursor(
            Long cursorNumSolicitacao,
            String cursorCodCanal,
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
            int limit);
}

package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.domain.CreditRequest;

public interface CreditRequestPort {
    CreditRequest save(CreditRequest cdr);

    Optional<CreditRequest> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal);

    List<CreditRequest> findByCanalAndSituacao(String codCanal, String codSituacao);

    boolean existsByNumSolicitacao(Long numSolicitacao);

    boolean existsByNumLoteAndCodCanal(String numLote, String codCanal);

    List<CreditRequest> findElegiveisParaLiberacao(String codSituacao, LocalDateTime dtInicio, LocalDateTime dtFim,
            int limit);

    CreditRequest findElegiveisParaProcessamento(String codSituacao);

    List<CreditRequest> findElegiveisParaConfirmacao(String codSituacao, int limit);

    void update(Long numSolicitacao, String codCanal, CreditRequest creditRequest);

    Optional<CreditRequest> findByCodTipoDocumentoAndIdUsuarioCadastro(String codTipoDocumento, Long idUsuarioCadastro);

    /**
     * Busca específica por numSolicitacao (fast path, sem paginação).
     */
    List<CreditRequest> findByNumSolicitacaoSpecific(Long numSolicitacao, String codCanal);

    /**
     * Busca por código de produto com filtros de data.
     */
    List<CreditRequest> findByCodProduto(
            String codProduto,
            String codCanal,
            LocalDateTime dtInicio,
            LocalDateTime dtFim,
            int limit);

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

    /**
     * Retorna o código do canal de distribuição associado à solicitação/canal.
     * Utilizado para integração com HM (registrarAutorizacaoRecarga).
     */
    Optional<String> findCodCanalDistribuicao(Long numSolicitacao, String codCanal);
}

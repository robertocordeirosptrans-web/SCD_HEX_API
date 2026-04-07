
package br.sptrans.scd.creditrequest.adapter.out.jpa.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestEJpaKey;

public interface CreditRequestJpaRepository extends JpaRepository<CreditRequestEJpa, CreditRequestEJpaKey>, JpaSpecificationExecutor<CreditRequestEJpa> {

    @Query(value = "SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s WHERE s.NUM_SOLICITACAO = :numSolicitacao AND s.COD_CANAL = :codCanal", nativeQuery = true)
    Optional<CreditRequestEJpa> findByNumSolicitacaoAndCodCanal(@Param("numSolicitacao") Long numSolicitacao, @Param("codCanal") String codCanal);

    @Query(value = "SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s WHERE s.COD_CANAL = :codCanal AND s.COD_SITUACAO = :codSituacao", nativeQuery = true)
    List<CreditRequestEJpa> findByCanalAndSituacao(@Param("codCanal") String codCanal, @Param("codSituacao") String codSituacao);

    @Query(value = "SELECT COUNT(1) FROM SPTRANSDBA.SOL_DISTRIBUICOES WHERE NUM_SOLICITACAO = :numSolicitacao", nativeQuery = true)
    int countByNumSolicitacao(@Param("numSolicitacao") Long numSolicitacao);

    @Query(value = "SELECT COUNT(1) FROM SPTRANSDBA.SOL_DISTRIBUICOES WHERE NUM_LOTE = :numLote AND COD_CANAL = :codCanal", nativeQuery = true)
    int countByNumLoteAndCodCanal(@Param("numLote") String numLote, @Param("codCanal") String codCanal);

    @Query(value = "SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s WHERE s.COD_SITUACAO = :codSituacao AND s.DT_SOLICITACAO >= :dtInicio AND s.DT_SOLICITACAO <= :dtFim FETCH FIRST :limit ROWS ONLY", nativeQuery = true)
    List<CreditRequestEJpa> findElegiveisParaLiberacao(@Param("codSituacao") String codSituacao, @Param("dtInicio") LocalDateTime dtInicio, @Param("dtFim") LocalDateTime dtFim, @Param("limit") int limit);

    @Query(value = "SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s WHERE s.COD_SITUACAO = :codSituacao", nativeQuery = true)
    List<CreditRequestEJpa> findElegiveisParaProcessamento(@Param("codSituacao") String codSituacao);

    @Query(value = "SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s WHERE s.COD_SITUACAO = :codSituacao FETCH FIRST :limit ROWS ONLY", nativeQuery = true)
    List<CreditRequestEJpa> findElegiveisParaConfirmacao(@Param("codSituacao") String codSituacao, @Param("limit") int limit);

    @Query(value = "SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s WHERE s.NUM_SOLICITACAO = :numSolicitacao AND (:codCanal IS NULL OR s.COD_CANAL = :codCanal)", nativeQuery = true)
    List<CreditRequestEJpa> findByNumSolicitacaoSpecific(@Param("numSolicitacao") Long numSolicitacao, @Param("codCanal") String codCanal);

    @Query(value = "SELECT s.* FROM SPTRANSDBA.SOL_DISTRIBUICOES s INNER JOIN SPTRANSDBA.SOL_DISTRIBUICOES_ITENS i ON s.NUM_SOLICITACAO = i.NUM_SOLICITACAO AND s.COD_CANAL = i.COD_CANAL WHERE i.COD_PRODUTO = :codProduto AND (:codCanal IS NULL OR s.COD_CANAL = :codCanal) AND (:dtInicio IS NULL OR s.DT_SOLICITACAO >= :dtInicio) AND (:dtFim IS NULL OR s.DT_SOLICITACAO <= :dtFim) ORDER BY s.NUM_SOLICITACAO DESC, s.COD_CANAL ASC FETCH FIRST :limit ROWS ONLY", nativeQuery = true)
    List<CreditRequestEJpa> findByCodProduto(@Param("codProduto") String codProduto, @Param("codCanal") String codCanal, @Param("dtInicio") LocalDateTime dtInicio, @Param("dtFim") LocalDateTime dtFim, @Param("limit") int limit);

    @Query(value = "SELECT * FROM SPTRANSDBA.SOL_DISTRIBUICOES s WHERE s.COD_TIPO_DOCUMENTO = :codTipoDocumento AND s.ID_USUARIO_CADASTRO = :idUsuarioCadastro ORDER BY s.DT_SOLICITACAO DESC FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
    Optional<CreditRequestEJpa> findByCodTipoDocumentoAndIdUsuarioCadastro(@Param("codTipoDocumento") String codTipoDocumento, @Param("idUsuarioCadastro") Long idUsuarioCadastro);

    @Query(value = """
    SELECT 
        s.NUM_SOLICITACAO,
        s.COD_CANAL,
        s.ID_USUARIO_CADASTRO,
        s.COD_TIPO_DOCUMENTO,
        s.COD_SITUACAO,
        s.COD_FORMA_PAGTO,
        s.DT_SOLICITACAO,
        s.DT_PREV_LIBERACAO,
        s.DT_ACEITE,
        s.DT_CONFIRMA_PAGTO,
        s.DT_PAGTO_ECONOMICA,
        s.COD_USUARIO_PORTADOR,
        s.DT_LIBERACAO_EFETIVA,
        s.COD_ENDERECO_ENTREGA,
        s.NUM_LOTE,
        s.DT_FINANCEIRA,
        s.VL_TOTAL,
        s.DT_CADASTRO,
        s.FLG_CANC,
        s.DT_MANUTENCAO,
        s.DT_ENVIO_HM,
        s.ID_USUARIO_MANUTENCAO,
        s.FLG_BLOQ
    FROM SPTRANSDBA.SOL_DISTRIBUICOES s
    WHERE (:cursorNumSolicitacao IS NULL OR s.NUM_SOLICITACAO < :cursorNumSolicitacao 
        OR (s.NUM_SOLICITACAO = :cursorNumSolicitacao AND s.COD_CANAL > :cursorCodCanal))
    AND (:codCanal IS NULL OR s.COD_CANAL = :codCanal)
    AND (:codSituacao IS NULL OR s.COD_SITUACAO = :codSituacao)
    AND (:numLote IS NULL OR s.NUM_LOTE = :numLote)
    AND (:codFormaPagto IS NULL OR s.COD_FORMA_PAGTO = :codFormaPagto)
    AND (:dtInicio IS NULL OR s.DT_SOLICITACAO >= :dtInicio)
    AND (:dtFim IS NULL OR s.DT_SOLICITACAO <= :dtFim)
    AND (:dtLiberacaoEfetivaInicio IS NULL OR s.DT_LIBERACAO_EFETIVA >= :dtLiberacaoEfetivaInicio)
    AND (:dtLiberacaoEfetivaFim IS NULL OR s.DT_LIBERACAO_EFETIVA <= :dtLiberacaoEfetivaFim)
    AND (:dtPagtoEconomicaInicio IS NULL OR s.DT_PAGTO_ECONOMICA >= :dtPagtoEconomicaInicio)
    AND (:dtPagtoEconomicaFim IS NULL OR s.DT_PAGTO_ECONOMICA <= :dtPagtoEconomicaFim)
    AND (:dtFinanceiraInicio IS NULL OR s.DT_FINANCEIRA >= :dtFinanceiraInicio)
    AND (:dtFinanceiraFim IS NULL OR s.DT_FINANCEIRA <= :dtFinanceiraFim)
    AND (:dtAlteracaoInicio IS NULL OR s.DT_MANUTENCAO >= :dtAlteracaoInicio)
    AND (:dtAlteracaoFim IS NULL OR s.DT_MANUTENCAO <= :dtAlteracaoFim)
    AND (:vlTotalMin IS NULL OR s.VL_TOTAL >= :vlTotalMin)
    AND (:vlTotalMax IS NULL OR s.VL_TOTAL <= :vlTotalMax)
    ORDER BY s.NUM_SOLICITACAO DESC, s.COD_CANAL ASC
""", nativeQuery = true)
    List<CreditRequestEJpa> findWithCursor(
            @Param("cursorNumSolicitacao") Long cursorNumSolicitacao,
            @Param("cursorCodCanal") String cursorCodCanal,
            @Param("codCanal") String codCanal,
            @Param("codSituacao") String codSituacao,
            @Param("numLote") String numLote,
            @Param("codFormaPagto") String codFormaPagto,
            @Param("dtInicio") LocalDateTime dtInicio,
            @Param("dtFim") LocalDateTime dtFim,
            @Param("dtLiberacaoEfetivaInicio") LocalDateTime dtLiberacaoEfetivaInicio,
            @Param("dtLiberacaoEfetivaFim") LocalDateTime dtLiberacaoEfetivaFim,
            @Param("dtPagtoEconomicaInicio") LocalDateTime dtPagtoEconomicaInicio,
            @Param("dtPagtoEconomicaFim") LocalDateTime dtPagtoEconomicaFim,
            @Param("dtFinanceiraInicio") LocalDateTime dtFinanceiraInicio,
            @Param("dtFinanceiraFim") LocalDateTime dtFinanceiraFim,
            @Param("dtAlteracaoInicio") LocalDateTime dtAlteracaoInicio,
            @Param("dtAlteracaoFim") LocalDateTime dtAlteracaoFim,
            @Param("vlTotalMin") BigDecimal vlTotalMin,
            @Param("vlTotalMax") BigDecimal vlTotalMax,
            Pageable pageable
    );


    

}




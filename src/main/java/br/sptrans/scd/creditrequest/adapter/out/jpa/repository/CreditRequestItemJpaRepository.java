package br.sptrans.scd.creditrequest.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEntity;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEntityKey;
import br.sptrans.scd.creditrequest.application.port.out.projection.ProductPeriodReportProjection;

public interface CreditRequestItemJpaRepository
                extends JpaRepository<CreditRequestItemsEntity, CreditRequestItemsEntityKey>,
                JpaSpecificationExecutor<CreditRequestItemsEntity> {

        @Query("SELECT i FROM CreditRequestItemsEntity i WHERE i.id.codCanal = :codCanal AND i.id.numSolicitacao = :numSolicitacao")
        Page<CreditRequestItemsEntity> findItemsByChannelAndNumSolicitacao(@Param("codCanal") String codCanal,
                        @Param("numSolicitacao") Long numSolicitacao, Pageable pageable);

        /**
         * Busca os primeiros 100 itens de recarga com situação informada.
         * Nota: Oracle não suporta bind variable em FETCH FIRST, por isso o limite é
         * fixo.
         */
        @Query(value = """
                        SELECT i.*
                        FROM SPTRANSDBA.SOL_DISTRIB_ITENS i
                        WHERE i.COD_SITUACAO = :codSituacao
                        FETCH FIRST 100 ROWS ONLY
                        """, nativeQuery = true)
        List<CreditRequestItemsEntity> searchForItensUnlocked(
                        @Param("codSituacao") String codSituacao);

        @Query(value = """
                        SELECT i.*\r
                        FROM SPTRANSDBA.SOL_DISTRIB_ITENS i\r
                        WHERE i.COD_SITUACAO = :codSituacao\r
                        FETCH FIRST 100 ROWS ONLY""", nativeQuery = true)
        List<CreditRequestItemsEntity> searchItemsToBeProcessed(@Param("codSituacao") String codSituacao);

        @Query("SELECT i FROM CreditRequestItemsEntity i WHERE i.id.numSolicitacao = :num AND i.id.codCanal = :canal")
        List<CreditRequestItemsEntity> findAllBySolicitacao(@Param("num") Long num, @Param("canal") String canal);

        /**
         * Busca os primeiros 100 itens de recarga com situação informada.
         * Nota: Oracle não suporta bind variable em FETCH FIRST, por isso o limite é
         * fixo.
         */

        @Query(value = """
                        SELECT i.*\r
                        FROM SPTRANSDBA.SOL_DISTRIB_ITENS i\r
                        WHERE i.COD_SITUACAO = :codSituacao\r
                        FETCH FIRST :limit ROWS ONLY""", nativeQuery = true)
        List<CreditRequestItemsEntity> searchItemsToBeConfirmed(@Param("codSituacao") String codSituacao,
                        @Param("limit") int limit);

        @Query(value = """
                        SELECT
                            i.COD_PRODUTO AS codProduto,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) =  1 THEN 1 END) AS dia01,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) =  2 THEN 1 END) AS dia02,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) =  3 THEN 1 END) AS dia03,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) =  4 THEN 1 END) AS dia04,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) =  5 THEN 1 END) AS dia05,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) =  6 THEN 1 END) AS dia06,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) =  7 THEN 1 END) AS dia07,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) =  8 THEN 1 END) AS dia08,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) =  9 THEN 1 END) AS dia09,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 10 THEN 1 END) AS dia10,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 11 THEN 1 END) AS dia11,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 12 THEN 1 END) AS dia12,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 13 THEN 1 END) AS dia13,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 14 THEN 1 END) AS dia14,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 15 THEN 1 END) AS dia15,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 16 THEN 1 END) AS dia16,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 17 THEN 1 END) AS dia17,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 18 THEN 1 END) AS dia18,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 19 THEN 1 END) AS dia19,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 20 THEN 1 END) AS dia20,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 21 THEN 1 END) AS dia21,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 22 THEN 1 END) AS dia22,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 23 THEN 1 END) AS dia23,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 24 THEN 1 END) AS dia24,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 25 THEN 1 END) AS dia25,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 26 THEN 1 END) AS dia26,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 27 THEN 1 END) AS dia27,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 28 THEN 1 END) AS dia28,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 29 THEN 1 END) AS dia29,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 30 THEN 1 END) AS dia30,
                            COUNT(CASE WHEN EXTRACT(DAY FROM i.DT_CADASTRO) = 31 THEN 1 END) AS dia31,
                            COUNT(*)        AS totalItensPeriodo,
                            SUM(i.VL_ITEM)      AS vlTotalPeriodo,
                            SUM(i.VL_CARREGADO) AS vlPagoPeriodo
                        FROM SPTRANSDBA.SOL_DISTRIB_ITENS i
                        WHERE i.COD_CANAL = :codCanal
                          AND i.DT_CADASTRO >= TO_DATE(:dataInicio, 'YYYY-MM-DD')
                          AND i.DT_CADASTRO <  TO_DATE(:dataFim, 'YYYY-MM-DD') + 1
                          AND i.COD_PRODUTO IN (:codProdutos)
                        GROUP BY i.COD_PRODUTO
                        ORDER BY i.COD_PRODUTO
                        """, nativeQuery = true)
        List<ProductPeriodReportProjection> findProductPeriodReportWithProductFilter(
                        @Param("codCanal") String codCanal,
                        @Param("dataInicio") String dataInicio,
                        @Param("dataFim") String dataFim,
                        @Param("codProdutos") List<String> codProdutos);

        /**
         * Relatório de período por produto (sem filtro de produto).
         */
        @Query(value = """
                           WITH TotaisFinanceiros AS (
                            SELECT
                                I2.COD_PRODUTO,
                                SUM(DISTINCT S2.VL_TOTAL) AS VL_TOTAL_PERIODO,
                                SUM(DISTINCT S2.VL_PAGO) AS VL_PAGO_PERIODO
                            FROM SPTRANSDBA.SOL_DISTRIB_ITENS I2
                            INNER JOIN SPTRANSDBA.SOL_DISTRIBUICOES S2
                                ON S2.NUM_SOLICITACAO = I2.NUM_SOLICITACAO
                                AND S2.COD_CANAL = I2.COD_CANAL
                            WHERE S2.COD_CANAL = :codCanal
                              AND I2.DT_RECARGA BETWEEN TO_DATE(:dataInicio, 'YYYY-MM-DD') AND TO_DATE(:dataFim, 'YYYY-MM-DD')
                            GROUP BY I2.COD_PRODUTO
                        )
                        SELECT
                            I.COD_PRODUTO as codProduto,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 0 THEN 1 ELSE 0 END), 0) AS dia01,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 1 THEN 1 ELSE 0 END), 0) AS dia02,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 2 THEN 1 ELSE 0 END), 0) AS dia03,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 3 THEN 1 ELSE 0 END), 0) AS dia04,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 4 THEN 1 ELSE 0 END), 0) AS dia05,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 5 THEN 1 ELSE 0 END), 0) AS dia06,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 6 THEN 1 ELSE 0 END), 0) AS dia07,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 7 THEN 1 ELSE 0 END), 0) AS dia08,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 8 THEN 1 ELSE 0 END), 0) AS dia09,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 9 THEN 1 ELSE 0 END), 0) AS dia10,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 10 THEN 1 ELSE 0 END), 0) AS dia11,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 11 THEN 1 ELSE 0 END), 0) AS dia12,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 12 THEN 1 ELSE 0 END), 0) AS dia13,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 13 THEN 1 ELSE 0 END), 0) AS dia14,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 14 THEN 1 ELSE 0 END), 0) AS dia15,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 15 THEN 1 ELSE 0 END), 0) AS dia16,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 16 THEN 1 ELSE 0 END), 0) AS dia17,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 17 THEN 1 ELSE 0 END), 0) AS dia18,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 18 THEN 1 ELSE 0 END), 0) AS dia19,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 19 THEN 1 ELSE 0 END), 0) AS dia20,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 20 THEN 1 ELSE 0 END), 0) AS dia21,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 21 THEN 1 ELSE 0 END), 0) AS dia22,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 22 THEN 1 ELSE 0 END), 0) AS dia23,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 23 THEN 1 ELSE 0 END), 0) AS dia24,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 24 THEN 1 ELSE 0 END), 0) AS dia25,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 25 THEN 1 ELSE 0 END), 0) AS dia26,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 26 THEN 1 ELSE 0 END), 0) AS dia27,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 27 THEN 1 ELSE 0 END), 0) AS dia28,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 28 THEN 1 ELSE 0 END), 0) AS dia29,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 29 THEN 1 ELSE 0 END), 0) AS dia30,
                            COALESCE(SUM(CASE WHEN TRUNC(I.DT_RECARGA) = TO_DATE(:dataInicio, 'YYYY-MM-DD') + 30 THEN 1 ELSE 0 END), 0) AS dia31,
                            COUNT(I.NUM_SOLICITACAO_ITEM) AS totalItensPeriodo,
                            COALESCE(MAX(TF.VL_TOTAL_PERIODO), 0) AS vlTotalPeriodo,
                            COALESCE(MAX(TF.VL_PAGO_PERIODO), 0) AS vlPagoPeriodo
                        FROM SPTRANSDBA.SOL_DISTRIB_ITENS I
                        INNER JOIN SPTRANSDBA.SOL_DISTRIBUICOES S
                            ON S.NUM_SOLICITACAO = I.NUM_SOLICITACAO
                            AND S.COD_CANAL = I.COD_CANAL
                        LEFT JOIN TotaisFinanceiros TF
                            ON TF.COD_PRODUTO = I.COD_PRODUTO
                        WHERE S.COD_CANAL = :codCanal
                          AND I.DT_RECARGA BETWEEN TO_DATE(:dataInicio, 'YYYY-MM-DD') AND TO_DATE(:dataFim, 'YYYY-MM-DD')
                        GROUP BY I.COD_PRODUTO
                        ORDER BY I.COD_PRODUTO
                            """, nativeQuery = true)
        List<ProductPeriodReportProjection> findProductPeriodReport(
                        @Param("codCanal") String codCanal,
                        @Param("dataInicio") String dataInicio,
                        @Param("dataFim") String dataFim);

        /**
         * Busca todos os números de solicitação de item para uma solicitação, canal
         * e lote.
         */
        @Query(value = """
                        SELECT i.NUM_SOLICITACAO_ITEM
                        FROM SPTRANSDBA.SOL_DISTRIB_ITENS i
                        JOIN SPTRANSDBA.SOL_DISTRIBUICOES s
                                ON i.NUM_SOLICITACAO = s.NUM_SOLICITACAO AND i.COD_CANAL = s.COD_CANAL
                        WHERE i.NUM_SOLICITACAO = :numSolicitacao
                                AND i.COD_CANAL = :codCanal
                                AND s.NUM_LOTE = :numLote
                        ORDER BY i.NUM_SOLICITACAO_ITEM
                        """, nativeQuery = true)
        List<Long> findNumSolicitacaoItemsBySolicitacaoCanalLote(
                        @Param("numSolicitacao") Long numSolicitacao,
                        @Param("codCanal") String codCanal,
                        @Param("numLote") String numLote);

        @Query(value = """
                        SELECT i.*
                        FROM SPTRANSDBA.SOL_DISTRIB_ITENS i
                        JOIN SPTRANSDBA.SOL_DISTRIBUICOES s
                                ON i.NUM_SOLICITACAO = s.NUM_SOLICITACAO AND i.COD_CANAL = s.COD_CANAL
                        WHERE i.NUM_SOLICITACAO = :numSolicitacao
                                AND i.COD_CANAL = :codCanal
                                AND s.NUM_LOTE = :numLote
                        ORDER BY i.NUM_SOLICITACAO_ITEM
                        """, nativeQuery = true)
        List<CreditRequestItemsEntity> findProcessRechargeService(
                        @Param("numSolicitacao") Long numSolicitacao,
                        @Param("codCanal") String codCanal,
                        @Param("numLote") String numLote);
}

package br.sptrans.scd.creditrequest.adapter.port.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpaKey;
import br.sptrans.scd.creditrequest.application.port.out.projection.ProductPeriodReportProjection;

public interface CreditRequestItemJpaRepository extends JpaRepository<CreditRequestItemsEJpa, CreditRequestItemsEJpaKey>, JpaSpecificationExecutor<CreditRequestItemsEJpa> {

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
    List<CreditRequestItemsEJpa> findProcessRechargeService(
            @Param("numSolicitacao") Long numSolicitacao,
            @Param("codCanal") String codCanal,
            @Param("numLote") String numLote);
}

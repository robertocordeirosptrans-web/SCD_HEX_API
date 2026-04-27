package br.sptrans.scd.product.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.product.adapter.out.persistence.entity.FareEntityJpa;
import br.sptrans.scd.product.application.port.out.query.FareDetailProjection;

public interface FareJpaRepository extends JpaRepository<FareEntityJpa, String>, JpaSpecificationExecutor<FareEntityJpa> {

    @Query(value = """
        SELECT
            t.COD_TARIFA         AS codTarifa,
            t.COD_PRODUTO        AS codProduto,
            p.DES_PRODUTO        AS nomProduto,
            (SELECT pv.COD_VERSAO FROM SPTRANSDBA.PRODUTOS_VERSOES pv
             WHERE pv.COD_PRODUTO = t.COD_PRODUTO
             ORDER BY pv.DT_CADASTRO DESC
             FETCH FIRST 1 ROWS ONLY) AS codVersao,
            t.DT_VIGENCIA_INI    AS dtVigenciaIni,
            t.DT_VIGENCIA_FIM    AS dtVigenciaFim,
            t.DT_CADASTRO        AS dtCadastro,
            t.DT_MANUTENCAO      AS dtManutencao,
            t.DES_TARIFA         AS desTarifa,
            t.ST_TARIFAS         AS stTarifas,
            t.VL_TARIFA          AS vlTarifa,
            t.ID_USUARIO_CADASTRO    AS idUsuarioCadastro,
            u1.COD_LOGIN         AS loginCadastro,
            u1.NOM_USUARIO       AS nomeCadastro,
            t.ID_USUARIO_MANUTENCAO  AS idUsuarioManutencao,
            u2.COD_LOGIN         AS loginManutencao,
            u2.NOM_USUARIO       AS nomeManutencao
        FROM SPTRANSDBA.TARIFAS t
        LEFT JOIN SPTRANSDBA.PRODUTOS p
            ON t.COD_PRODUTO = p.COD_PRODUTO
        LEFT JOIN SPTRANSDBA.USUARIOS u1
            ON t.ID_USUARIO_CADASTRO = u1.ID_USUARIO
        LEFT JOIN SPTRANSDBA.USUARIOS u2
            ON t.ID_USUARIO_MANUTENCAO = u2.ID_USUARIO
        WHERE t.COD_PRODUTO = :codProduto
        ORDER BY t.DT_VIGENCIA_INI DESC
    """, nativeQuery = true)
    List<FareDetailProjection> findDetailByProduct(@Param("codProduto") String codProduto);
}

package br.sptrans.scd.channel.adapter.port.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.channel.adapter.port.out.jpa.entity.SalesChannelEntityJpa;

public interface SalesChannelJpaRepository extends JpaRepository<SalesChannelEntityJpa, String>, JpaSpecificationExecutor<SalesChannelEntityJpa> {

    @Query("SELECT c FROM SalesChannelEntityJpa c WHERE c.codCanal = :codCanal")
    Optional<SalesChannelEntityJpa> findByCodCanal(@Param("codCanal") String codCanal);

    @Query("SELECT COUNT(c) > 0 FROM SalesChannelEntityJpa c WHERE c.codCanal = :codCanal")
    boolean existsByCodCanal(@Param("codCanal") String codCanal);

    @Query("SELECT c FROM SalesChannelEntityJpa c WHERE (:stCanais IS NULL OR c.stCanais = :stCanais) ORDER BY c.codCanal")
    List<SalesChannelEntityJpa> findAllByStCanais(@Param("stCanais") String stCanais);

    @Modifying
    @Transactional
    @Query("""
        UPDATE SalesChannelEntityJpa c SET
            c.codCanalSuperior = :codCanalSuperior,
            c.desCanal = :desCanal,
            c.desRazaoSocial = :desRazaoSocial,
            c.desNomeFantasia = :desNomeFantasia,
            c.vlCaucao = :vlCaucao,
            c.dtInicioCaucao = :dtInicioCaucao,
            c.dtFimCaucao = :dtFimCaucao,
            c.seqNivel = :seqNivel,
            c.flgCriticaNumlote = :flgCriticaNumlote,
            c.flgLimiteDias = :flgLimiteDias,
            c.flgProcessamentoAutomatico = :flgProcessamentoAutomatico,
            c.flgProcessamentoParcial = :flgProcessamentoParcial,
            c.flgSaldoDevedor = :flgSaldoDevedor,
            c.numMinutoIniLibRecarga = :numMinutoIniLibRecarga,
            c.numMinutoFimLibRecarga = :numMinutoFimLibRecarga,
            c.flgEmiteReciboPedido = :flgEmiteReciboPedido,
            c.flgSupercanal = :flgSupercanal,
            c.flgPagtoFuturo = :flgPagtoFuturo,
            c.codClassificacaoPessoa = :codClassificacaoPessoa,
            c.codAtividade = :codAtividade,
            c.dtManutencao = CURRENT_TIMESTAMP,
            c.idUsuarioManutencao = :idUsuarioManutencao
        WHERE c.codCanal = :codCanal
    """)
    int updateSalesChannel(
            @Param("codCanalSuperior") String codCanalSuperior,
            @Param("desCanal") String desCanal,
            @Param("desRazaoSocial") String desRazaoSocial,
            @Param("desNomeFantasia") String desNomeFantasia,
            @Param("vlCaucao") java.math.BigDecimal vlCaucao,
            @Param("dtInicioCaucao") java.time.LocalDate dtInicioCaucao,
            @Param("dtFimCaucao") java.time.LocalDate dtFimCaucao,
            @Param("seqNivel") Integer seqNivel,
            @Param("flgCriticaNumlote") String flgCriticaNumlote,
            @Param("flgLimiteDias") String flgLimiteDias,
            @Param("flgProcessamentoAutomatico") String flgProcessamentoAutomatico,
            @Param("flgProcessamentoParcial") String flgProcessamentoParcial,
            @Param("flgSaldoDevedor") String flgSaldoDevedor,
            @Param("numMinutoIniLibRecarga") Integer numMinutoIniLibRecarga,
            @Param("numMinutoFimLibRecarga") Integer numMinutoFimLibRecarga,
            @Param("flgEmiteReciboPedido") String flgEmiteReciboPedido,
            @Param("flgSupercanal") String flgSupercanal,
            @Param("flgPagtoFuturo") String flgPagtoFuturo,
            @Param("codClassificacaoPessoa") String codClassificacaoPessoa,
            @Param("codAtividade") String codAtividade,
            @Param("idUsuarioManutencao") Long idUsuarioManutencao,
            @Param("codCanal") String codCanal
    );

    @Modifying
    @Transactional
    @Query("""
        UPDATE SalesChannelEntityJpa c SET
            c.stCanais = :stCanais,
            c.dtManutencao = CURRENT_TIMESTAMP,
            c.idUsuarioManutencao = :idUsuarioManutencao
        WHERE c.codCanal = :codCanal
    """)
    int updateStatus(
            @Param("stCanais") String stCanais,
            @Param("idUsuarioManutencao") Long idUsuarioManutencao,
            @Param("codCanal") String codCanal
    );

    // Buscar canais por canal superior
    List<SalesChannelEntityJpa> findByCodCanalSuperior(String codCanalSuperior);

}

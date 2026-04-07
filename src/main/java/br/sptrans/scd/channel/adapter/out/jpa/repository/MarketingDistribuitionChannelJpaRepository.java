package br.sptrans.scd.channel.adapter.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.channel.adapter.out.persistence.entity.MarketingDistribuitionChannelEntityJpa;
import br.sptrans.scd.channel.adapter.out.persistence.entity.MarketingDistribuitionChannelKeyEntityJpa;

public interface MarketingDistribuitionChannelJpaRepository extends JpaRepository<MarketingDistribuitionChannelEntityJpa, MarketingDistribuitionChannelKeyEntityJpa>, JpaSpecificationExecutor<MarketingDistribuitionChannelEntityJpa> {

  @Query("SELECT c FROM MarketingDistribuitionChannelEntityJpa c WHERE c.id.codCanalComercializacao = :codCanalComercializacao AND c.id.codCanalDistribuicao = :codCanalDistribuicao")
  Optional<MarketingDistribuitionChannelEntityJpa> findByCodCanalComercializacaoAndCodCanalDistribuicao(@Param("codCanalComercializacao") String codCanalComercializacao, @Param("codCanalDistribuicao") String codCanalDistribuicao);

  @Query("SELECT c FROM MarketingDistribuitionChannelEntityJpa c")
  List<MarketingDistribuitionChannelEntityJpa> findAllChannels();

  @Query("SELECT c FROM MarketingDistribuitionChannelEntityJpa c WHERE c.id.codCanalComercializacao = :codCanalComercializacao")
  List<MarketingDistribuitionChannelEntityJpa> findByCodCanalComercializacao(@Param("codCanalComercializacao") String codCanalComercializacao);

  @Query("SELECT c FROM MarketingDistribuitionChannelEntityJpa c WHERE c.id.codCanalDistribuicao = :codCanalDistribuicao")
  List<MarketingDistribuitionChannelEntityJpa> findByCodCanalDistribuicao(@Param("codCanalDistribuicao") String codCanalDistribuicao);

  @Query("SELECT COUNT(c) > 0 FROM MarketingDistribuitionChannelEntityJpa c WHERE c.id.codCanalComercializacao = :codCanalComercializacao AND c.id.codCanalDistribuicao = :codCanalDistribuicao")
  boolean existsByCodCanalComercializacaoAndCodCanalDistribuicao(@Param("codCanalComercializacao") String codCanalComercializacao, @Param("codCanalDistribuicao") String codCanalDistribuicao);

  @Query("SELECT c FROM MarketingDistribuitionChannelEntityJpa c WHERE c.id.codCanalComercializacao = :codCanalComercializacao AND c.id.codCanalDistribuicao = :codCanalDistribuicao AND c.codStatus = 'A'")
  List<MarketingDistribuitionChannelEntityJpa> findActiveByCanalDistribuicao(@Param("codCanalComercializacao") String codCanalComercializacao, @Param("codCanalDistribuicao") String codCanalDistribuicao);
 
}

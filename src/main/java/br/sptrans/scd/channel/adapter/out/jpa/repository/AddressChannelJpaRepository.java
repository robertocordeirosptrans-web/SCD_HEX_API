package br.sptrans.scd.channel.adapter.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.channel.adapter.out.persistence.entity.AddressChannelEntityJpa;

public interface AddressChannelJpaRepository extends JpaRepository<AddressChannelEntityJpa, String>, JpaSpecificationExecutor<AddressChannelEntityJpa> {

	@Query("SELECT a FROM AddressChannelEntityJpa a WHERE a.codEndereco = :codEndereco")
	Optional<AddressChannelEntityJpa> findByCodEndereco(@Param("codEndereco") String codEndereco);

	@Query("SELECT COUNT(a) > 0 FROM AddressChannelEntityJpa a WHERE a.codEndereco = :codEndereco")
	boolean existsByCodEndereco(@Param("codEndereco") String codEndereco);

	@Query("SELECT a FROM AddressChannelEntityJpa a WHERE a.codCanal = :codCanal ORDER BY a.codEndereco")
	List<AddressChannelEntityJpa> findAllByCodCanal(@Param("codCanal") String codCanal);

	@Query("SELECT a FROM AddressChannelEntityJpa a WHERE a.codCanal = :codCanal AND (:flgTipoSaida IS NULL OR a.codTipoEndereco = :flgTipoSaida) ORDER BY a.codEndereco")
	Page<AddressChannelEntityJpa> findAllByCodCanal(@Param("codCanal") String codCanal, @Param("flgTipoSaida") String flgTipoSaida, Pageable pageable);

	@Query("SELECT a FROM AddressChannelEntityJpa a ORDER BY a.codEndereco")
	List<AddressChannelEntityJpa> findAllOrderByCodEndereco();

	@Query("SELECT a FROM AddressChannelEntityJpa a")
	Page<AddressChannelEntityJpa> findAllOrderByCodEndereco(Pageable pageable);


}

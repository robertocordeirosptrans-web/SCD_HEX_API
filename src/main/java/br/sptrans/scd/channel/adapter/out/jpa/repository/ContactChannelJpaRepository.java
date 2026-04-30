package br.sptrans.scd.channel.adapter.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.channel.adapter.out.persistence.entity.ContactChannelEntityJpa;

public interface ContactChannelJpaRepository extends JpaRepository<ContactChannelEntityJpa, String>, JpaSpecificationExecutor<ContactChannelEntityJpa> {

	@Query("SELECT c FROM ContactChannelEntityJpa c LEFT JOIN FETCH c.usuarioCadastro LEFT JOIN FETCH c.usuarioManutencao LEFT JOIN FETCH c.canal WHERE c.codContato = :codContato")
	Optional<ContactChannelEntityJpa> findByCodContato(@Param("codContato") String codContato);

	@Query("SELECT COUNT(c) > 0 FROM ContactChannelEntityJpa c WHERE c.codContato = :codContato")
	boolean existsByCodContato(@Param("codContato") String codContato);

	@Query("SELECT DISTINCT c FROM ContactChannelEntityJpa c LEFT JOIN FETCH c.usuarioCadastro LEFT JOIN FETCH c.usuarioManutencao LEFT JOIN FETCH c.canal WHERE c.canal.codCanal = :codCanal ORDER BY c.codContato")
	List<ContactChannelEntityJpa> findAllByCodCanal(@Param("codCanal") String codCanal);

	@Query("SELECT DISTINCT c FROM ContactChannelEntityJpa c LEFT JOIN FETCH c.usuarioCadastro LEFT JOIN FETCH c.usuarioManutencao LEFT JOIN FETCH c.canal WHERE c.canal.codCanal = :codCanal")
	Page<ContactChannelEntityJpa> findAllByCodCanal(@Param("codCanal") String codCanal, Pageable pageable);

	@Query("SELECT c FROM ContactChannelEntityJpa c LEFT JOIN FETCH c.usuarioCadastro LEFT JOIN FETCH c.usuarioManutencao LEFT JOIN FETCH c.canal ORDER BY c.codContato")
	List<ContactChannelEntityJpa> findAllByOrderByCodContato();;

}

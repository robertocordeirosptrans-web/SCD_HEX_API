package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileEntityJpa;

@Repository
public interface ProfileJpaRepository extends JpaRepository<ProfileEntityJpa, String>, JpaSpecificationExecutor<ProfileEntityJpa> {

    @Override
    ProfileEntityJpa save(ProfileEntityJpa pf);

    @Query("SELECT p FROM ProfileEntityJpa p WHERE p.codPerfil = :codPerfil")
    Optional<ProfileEntityJpa> findByCodPerfil(@Param("codPerfil") String codPerfil);

    @Query("SELECT COUNT(p) FROM ProfileEntityJpa p WHERE p.codPerfil = :codPerfil")
    long countByCodPerfil(@Param("codPerfil") String codPerfil);

    @Query("SELECT p FROM ProfileEntityJpa p WHERE (:codStatus IS NULL OR p.codStatus = :codStatus)")
    List<ProfileEntityJpa> findByCodStatus(@Param("codStatus") String codStatus);

    @Query(
        value = "SELECT p FROM ProfileEntityJpa p LEFT JOIN FETCH p.usuarioManutencao WHERE (:codStatus IS NULL OR p.codStatus = :codStatus)",
        countQuery = "SELECT COUNT(p) FROM ProfileEntityJpa p WHERE (:codStatus IS NULL OR p.codStatus = :codStatus)"
    )
    Page<ProfileEntityJpa> findByCodStatus(@Param("codStatus") String codStatus, Pageable pageable);
}

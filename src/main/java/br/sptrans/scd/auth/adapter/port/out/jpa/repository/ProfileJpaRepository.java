package br.sptrans.scd.auth.adapter.port.out.jpa.repository;

import br.sptrans.scd.auth.adapter.port.out.persistence.entity.ProfileEntityJpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
}

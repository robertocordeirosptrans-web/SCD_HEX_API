package br.sptrans.scd.auth.adapter.port.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.port.out.jpa.entity.ProfileEntityJpa;
import br.sptrans.scd.auth.adapter.port.out.jpa.entity.ProfileFunctionalityJpa;
import br.sptrans.scd.auth.adapter.port.out.jpa.entity.ProfileFunctionalityJpaId;

@Repository
public interface ProfileFunctionalityJpaRepository extends JpaRepository<ProfileFunctionalityJpa, ProfileFunctionalityJpaId>, JpaSpecificationExecutor<ProfileFunctionalityJpa> {
    List<ProfileFunctionalityJpa> findByPerfil(ProfileEntityJpa perfil);
}

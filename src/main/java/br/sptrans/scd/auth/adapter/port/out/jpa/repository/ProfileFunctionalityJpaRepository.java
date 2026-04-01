package br.sptrans.scd.auth.adapter.port.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.port.out.persistence.entity.ProfileEntityJpa;
import br.sptrans.scd.auth.adapter.port.out.persistence.entity.ProfileFunctionalityJpa;
import br.sptrans.scd.auth.adapter.port.out.persistence.entity.ProfileFunctionalityJpaId;

@Repository
public interface ProfileFunctionalityJpaRepository extends JpaRepository<ProfileFunctionalityJpa, ProfileFunctionalityJpaId>, JpaSpecificationExecutor<ProfileFunctionalityJpa> {

    List<ProfileFunctionalityJpa> findByPerfil(ProfileEntityJpa perfil);

    @Query("SELECT pf FROM ProfileFunctionalityJpa pf WHERE pf.id.codPerfil = :codPerfil AND pf.id.codSistema = :codSistema AND pf.id.codModulo = :codModulo AND pf.id.codRotina = :codRotina AND pf.id.codFuncionalidade = :codFuncionalidade AND pf.idUsuarioManutencao IS NOT NULL")
    List<ProfileFunctionalityJpa> findActiveByProfileAndFunctionality(
        @Param("codPerfil") String codPerfil,
        @Param("codSistema") String codSistema,
        @Param("codModulo") String codModulo,
        @Param("codRotina") String codRotina,
        @Param("codFuncionalidade") String codFuncionalidade);

    @Modifying
    @Query("UPDATE ProfileFunctionalityJpa pf SET pf.idUsuarioManutencao = :idUsuarioManutencao WHERE pf.id.codPerfil = :codPerfil AND pf.id.codSistema = :codSistema AND pf.id.codModulo = :codModulo AND pf.id.codRotina = :codRotina AND pf.id.codFuncionalidade = :codFuncionalidade")
    void desassociateFunctionality(
        @Param("codPerfil") String codPerfil,
        @Param("codSistema") String codSistema,
        @Param("codModulo") String codModulo,
        @Param("codRotina") String codRotina,
        @Param("codFuncionalidade") String codFuncionalidade,
        @Param("idUsuarioManutencao") Long idUsuarioManutencao);

    @Query("SELECT COUNT(pf) FROM ProfileFunctionalityJpa pf WHERE pf.id.codPerfil = :codPerfil AND pf.id.codSistema = :codSistema AND pf.id.codModulo = :codModulo AND pf.id.codRotina = :codRotina AND pf.id.codFuncionalidade = :codFuncionalidade")
    long countFunctionalityAssociation(
        @Param("codPerfil") String codPerfil,
        @Param("codSistema") String codSistema,
        @Param("codModulo") String codModulo,
        @Param("codRotina") String codRotina,
        @Param("codFuncionalidade") String codFuncionalidade);
}

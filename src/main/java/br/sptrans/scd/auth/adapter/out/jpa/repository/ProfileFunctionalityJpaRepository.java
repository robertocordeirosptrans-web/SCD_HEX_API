
package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileFunctionalityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileFunctionalityJpaId;

@Repository
public interface ProfileFunctionalityJpaRepository
        extends JpaRepository<ProfileFunctionalityJpa, ProfileFunctionalityJpaId>,
        JpaSpecificationExecutor<ProfileFunctionalityJpa> {

    @Query("SELECT pf FROM ProfileFunctionalityJpa pf JOIN FETCH pf.funcionalidade f WHERE pf.perfil = :perfil")
    List<ProfileFunctionalityJpa> findByPerfil(@Param("perfil") ProfileEntityJpa perfil);

    @Query(value = "SELECT pf FROM ProfileFunctionalityJpa pf LEFT JOIN FETCH pf.usuarioManutencao", countQuery = "SELECT COUNT(pf) FROM ProfileFunctionalityJpa pf")
        @Override
        Page<ProfileFunctionalityJpa> findAll(Pageable pageable);

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

    @Query(value = "SELECT pf.COD_SISTEMA as codSistema, pf.COD_MODULO as codModulo, pf.COD_ROTINA as codRotina, pf.COD_FUNCIONALIDADE as codFuncionalidade, f.NOM_FUNCIONALIDADE as nomFuncionalidade, f.DT_MODI as dtModi, f.FLG_EVENTO as flgEvento, f.FLG_MONITORACAO as flgMonitoracao FROM SPTRANSDBA.PERFIL_FUNCIONALIDADES pf LEFT JOIN SPTRANSDBA.FUNCIONALIDADES f ON pf.COD_FUNCIONALIDADE = f.COD_FUNCIONALIDADE WHERE pf.COD_PERFIL = :codPerfil",
            countQuery = "SELECT COUNT(*) FROM SPTRANSDBA.PERFIL_FUNCIONALIDADES pf WHERE pf.COD_PERFIL = :codPerfil",
            nativeQuery = true)
    Page<br.sptrans.scd.auth.adapter.in.rest.dto.ProfileFunctionalityProjectionDTO> findAllProjectedByCodPerfil(@Param("codPerfil") String codPerfil, Pageable pageable);
}

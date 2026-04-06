package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupProfileEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupProfileEntityJpaId;
import br.sptrans.scd.auth.domain.GroupProfile;

@Repository
public interface GroupProfileJpaRepository extends JpaRepository<GroupProfileEntityJpa, GroupProfileEntityJpaId>, JpaSpecificationExecutor<GroupProfileEntityJpa> {

    @Query("SELECT gp FROM GroupProfileEntityJpa gp JOIN FETCH gp.grupo JOIN FETCH gp.perfil")
    List<GroupProfile> findAllGroupProfile();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO SPTRANSDBA.GRUPO_PERFIS (COD_GRUPO, COD_PERFIL, COD_STATUS, ID_USUARIO_MANUTENCAO, DT_MODI) VALUES (:codGrupo, :codPerfil, 'A', :idUsuarioManutencao, CURRENT_DATE)", nativeQuery = true)
    void associateProfileToGroup(@Param("codGrupo") String codGrupo, @Param("codPerfil") String codPerfil, @Param("idUsuarioManutencao") Long idUsuarioManutencao);

    @Modifying
    @Transactional
    @Query(value = "UPDATE SPTRANSDBA.GRUPO_PERFIS SET COD_STATUS = 'I', ID_USUARIO_MANUTENCAO = :idUsuarioManutencao, DT_MODI = CURRENT_DATE WHERE COD_GRUPO = :codGrupo AND COD_PERFIL = :codPerfil", nativeQuery = true)
    void disassociateProfileFromGroup(@Param("codGrupo") String codGrupo, @Param("codPerfil") String codPerfil, @Param("idUsuarioManutencao") Long idUsuarioManutencao);

    @Query(value = "SELECT COUNT(*) FROM SPTRANSDBA.GRUPO_PERFIS WHERE COD_GRUPO = :codGrupo AND COD_PERFIL = :codPerfil AND COD_STATUS = 'A'", nativeQuery = true)
    long countActiveProfileAssociation(@Param("codGrupo") String codGrupo, @Param("codPerfil") String codPerfil);

}

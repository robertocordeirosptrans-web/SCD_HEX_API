package br.sptrans.scd.auth.adapter.port.out.jpa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.adapter.port.out.jpa.entity.GroupProfileEntityJpa;
import br.sptrans.scd.auth.adapter.port.out.jpa.entity.GroupProfileEntityJpaId;



@Repository
public interface GroupProfileJpaRepository extends JpaRepository<GroupProfileEntityJpa, GroupProfileEntityJpaId>, JpaSpecificationExecutor<GroupProfileEntityJpa> {

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

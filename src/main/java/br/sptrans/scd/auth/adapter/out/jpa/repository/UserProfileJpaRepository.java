package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileProjectionDTO;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserProfileJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserProfileJpaId;

@Repository
public interface UserProfileJpaRepository
                extends JpaRepository<UserProfileJpa, UserProfileJpaId>, JpaSpecificationExecutor<UserProfileJpa> {

        @Query("SELECT up FROM UserProfileJpa up INNER JOIN FETCH up.perfil p WHERE up.usuario.idUsuario = :idUsuario AND up.codStatus = :codStatus")
        List<UserProfileJpa> findByUsuarioIdUsuarioAndCodStatus(@Param("idUsuario") Long idUsuario,
                        @Param("codStatus") String codStatus);

        /**
         * Carrega numa única query: perfis ativos do usuário + suas funcionalidades.
         * Evita N+1: substitui o loop perfil-por-perfil em
         * carregarFuncionalidadesEfetivas.
         */
        @Query("""
                        SELECT DISTINCT up FROM UserProfileJpa up
                        JOIN FETCH up.perfil p
                        JOIN FETCH p.perfilFuncionalidades pf
                        JOIN FETCH pf.funcionalidade f
                        WHERE up.usuario.idUsuario = :idUsuario
                        AND up.codStatus = 'A'
                        AND p.codStatus = 'A'
                        """)
        List<UserProfileJpa> findActiveWithFunctionalities(@Param("idUsuario") Long idUsuario);

        @Query("SELECT COUNT(up) FROM UserProfileJpa up WHERE up.id.codPerfil = :codPerfil AND up.codStatus = 'A'")
        long countActiveUsersByProfile(@Param("codPerfil") String codPerfil);

        @Query("SELECT up FROM UserProfileJpa up ORDER BY up.id.idUsuario, up.id.codPerfil")
        List<UserProfileJpa> listAllUserProfiles();

        @Query("SELECT up FROM UserProfileJpa up ORDER BY up.id.idUsuario, up.id.codPerfil")
        Page<UserProfileJpa> listAllUserProfiles(Pageable pageable);

        // @Query("SELECT up FROM UserProfileJpa up WHERE up.id.codPerfil = :codPerfil ORDER BY up.id.idUsuario")
        // Page<UserProfileJpa> findByIdCodPerfil(@Param("codPerfil") String codPerfil, Pageable pageable);

        @Query(value = "SELECT u.COD_LOGIN as codLogin, u.NOM_USUARIO as nomUsuario, u.NOM_DEPARTAMENTO as nomDepartamento, u.NOM_EMAIL as nomEmail, u.COD_STATUS as codStatus FROM SPTRANSDBA.USUARIOS u LEFT JOIN SPTRANSDBA.USUARIO_PERFIS gu ON u.ID_USUARIO = gu.ID_USUARIO WHERE gu.COD_PERFIL = :codPerfil", countQuery = "SELECT COUNT(*) FROM SPTRANSDBA.USUARIOS u LEFT JOIN SPTRANSDBA.USUARIO_PERFIS gu ON u.ID_USUARIO = gu.ID_USUARIO WHERE gu.COD_PERFIL = :codPerfil", nativeQuery = true)
        Page<UserProfileProjectionDTO> findAllProjectedByCodPerfil(
                        @Param("codPerfil") String codPerfil, Pageable pageable);

        @Query("SELECT up FROM UserProfileJpa up JOIN FETCH up.perfil p WHERE up.id.idUsuario = :idUsuario ORDER BY up.id.codPerfil")
        Page<UserProfileJpa> findByIdUsuario(@Param("idUsuario") Long idUsuario, Pageable pageable);

}

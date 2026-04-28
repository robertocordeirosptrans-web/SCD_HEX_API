
package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupUserEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupUserEntityJpaId;


public interface GroupUserJpaRepository extends JpaRepository<GroupUserEntityJpa, GroupUserEntityJpaId> {

    /**
     * Carrega grupos ativos do usuário com perfis e suas funcionalidades.
     * Fonte #2 de carregarFuncionalidadesEfetivas:
     * GRUPO_USUARIOS → GRUPO_PERFIS → PERFIL_FUNCIONALIDADES.
     */
    @Query("""
            SELECT DISTINCT gu FROM GroupUserEntityJpa gu
            JOIN FETCH gu.grupo g
            JOIN FETCH g.grupoPerfis gp
            JOIN FETCH gp.perfil p
            JOIN FETCH p.perfilFuncionalidades pf
            JOIN FETCH pf.funcionalidade f
            WHERE gu.id.idUsuario = :idUsuario
            AND gu.codStatus = 'A'
            AND g.codStatus = 'A'
            AND gp.codStatus = 'A'
            AND p.codStatus = 'A'
            """)
    List<GroupUserEntityJpa> findActiveWithFunctionalities(@Param("idUsuario") Long idUsuario);
    @Query(value = "SELECT u.COD_GRUPO as codGrupo, g.NOM_GRUPO as nomGrupo, u.DT_MODI as dtModi, u.COD_STATUS as codStatus FROM SPTRANSDBA.GRUPO_USUARIOS u LEFT JOIN SPTRANSDBA.GRUPOS g ON g.COD_GRUPO = u.COD_GRUPO WHERE u.ID_USUARIO = :idUsuario", 
           countQuery = "SELECT COUNT(*) FROM SPTRANSDBA.GRUPO_USUARIOS u WHERE u.ID_USUARIO = :idUsuario", nativeQuery = true)
    Page<GroupCustomProjection> findGroupsByUserId(@Param("idUsuario") Long idUsuario, Pageable pageable);

    @Query("SELECT COUNT(gu) FROM GroupUserEntityJpa gu WHERE gu.id.codGrupo = :codGrupo AND gu.codStatus = 'A'")
    long countActiveUsersByGroup(@Param("codGrupo") String codGrupo);

    @Query(value = "SELECT gu FROM GroupUserEntityJpa gu LEFT JOIN FETCH gu.usuario LEFT JOIN FETCH gu.usuarioManutencao ORDER BY gu.id.codGrupo, gu.id.idUsuario", countQuery = "SELECT COUNT(gu) FROM GroupUserEntityJpa gu")
    Page<GroupUserEntityJpa> findAllGroupUsers(Pageable pageable);

    @Query(value = "SELECT gu FROM GroupUserEntityJpa gu LEFT JOIN FETCH gu.usuario LEFT JOIN FETCH gu.usuarioManutencao WHERE gu.id.idUsuario = :idUsuario ORDER BY gu.id.codGrupo", countQuery = "SELECT COUNT(gu) FROM GroupUserEntityJpa gu WHERE gu.id.idUsuario = :idUsuario")
    Page<GroupUserEntityJpa> findByGroupsUser(@Param("idUsuario") Long idUsuario, Pageable pageable);

    @Query(value = "SELECT gu FROM GroupUserEntityJpa gu LEFT JOIN FETCH gu.usuario LEFT JOIN FETCH gu.usuarioManutencao WHERE gu.id.codGrupo = :codGrupo ORDER BY gu.id.idUsuario", countQuery = "SELECT COUNT(gu) FROM GroupUserEntityJpa gu WHERE gu.id.codGrupo = :codGrupo")
    Page<GroupUserEntityJpa> findByGroupCode(@Param("codGrupo") String codGrupo, Pageable pageable);

    @Query(value = "SELECT u.COD_LOGIN as codLogin, u.NOM_USUARIO as nomUsuario, u.NOM_DEPARTAMENTO as nomDepartamento, u.NOM_EMAIL as nomEmail, u.COD_STATUS as codStatus "
            +
            "FROM SPTRANSDBA.USUARIOS u " +
            "LEFT JOIN SPTRANSDBA.GRUPO_USUARIOS gu ON u.ID_USUARIO = gu.ID_USUARIO " +
            "WHERE gu.COD_GRUPO = :codGrupo", countQuery = "SELECT COUNT(*) FROM SPTRANSDBA.USUARIOS u LEFT JOIN SPTRANSDBA.GRUPO_USUARIOS gu ON u.ID_USUARIO = gu.ID_USUARIO WHERE gu.COD_GRUPO = :codGrupo", nativeQuery = true)
    Page<GroupUserCustomProjection> findCustomUsersByGroup(@Param("codGrupo") String codGrupo, Pageable pageable);

}

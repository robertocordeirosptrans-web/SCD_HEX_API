package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserFunctionalityEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserFunctionalityEntityJpaId;

@Repository
public interface UserFunctionalityJpaRepository
        extends JpaRepository<UserFunctionalityEntityJpa, UserFunctionalityEntityJpaId> {

    /**
     * Carrega as funcionalidades diretas ativas do usuário (USUARIO_FUNCIONALIDADES).
     * Fonte #3 de carregarFuncionalidadesEfetivas.
     */
    @Query("""
            SELECT DISTINCT uf FROM UserFunctionalityEntityJpa uf
            JOIN FETCH uf.funcionalidade f
            WHERE uf.id.idUsuario = :idUsuario
            AND uf.codStatusUsuFun = 'A'
            """)
    List<UserFunctionalityEntityJpa> findActiveByUsuario(@Param("idUsuario") Long idUsuario);
}

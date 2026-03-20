package br.sptrans.scd.auth.adapter.port.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.port.out.jpa.entity.UserProfileJpa;
import br.sptrans.scd.auth.adapter.port.out.jpa.entity.UserProfileJpaId;

@Repository
public interface UserProfileJpaRepository extends JpaRepository<UserProfileJpa, UserProfileJpaId>, JpaSpecificationExecutor<UserProfileJpa> {

        @Query("SELECT up FROM UserProfileJpa up INNER JOIN FETCH up.perfil p WHERE up.usuario.idUsuario = :idUsuario AND up.codStatus = :codStatus")
        List<UserProfileJpa> findByUsuarioIdUsuarioAndCodStatus(@Param("idUsuario") Long idUsuario, @Param("codStatus") String codStatus);

        @Query("SELECT COUNT(up) FROM UserProfileJpa up WHERE up.id.codPerfil = :codPerfil AND up.codStatus = 'A'")
        long countActiveUsersByProfile(@Param("codPerfil") String codPerfil);

        @Query("SELECT up FROM UserProfileJpa up ORDER BY up.id.idUsuario, up.id.codPerfil")
        List<UserProfileJpa> listAllUserProfiles();
}

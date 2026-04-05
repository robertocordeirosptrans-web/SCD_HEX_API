package br.sptrans.scd.auth.adapter.port.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.adapter.port.out.persistence.entity.UserEntityJpa;

public interface UserRepositoryJpa extends JpaRepository<UserEntityJpa, Long>, JpaSpecificationExecutor<UserEntityJpa>  {
    // Verifica existência por login
    boolean existsByCodLogin(String codLogin);

    // Busca todos por status
    @Query("SELECT u FROM UserEntityJpa u WHERE (:codStatus IS NULL OR u.codStatus = :codStatus)")
    List<UserEntityJpa> findAllByCodStatus(@Param("codStatus") String codStatus);

    // Atualiza tentativas e status
    @Modifying
    @Transactional
    @Query("UPDATE UserEntityJpa u SET u.numTentativasFalha = :numTentativas, u.codStatus = :codStatus WHERE u.idUsuario = :idUsuario")
    void atualizarTentativasEStatus(@Param("idUsuario") Long idUsuario, @Param("numTentativas") int numTentativas, @Param("codStatus") String codStatus);

    // Atualiza último acesso
    @Modifying
    @Transactional
    @Query("UPDATE UserEntityJpa u SET u.dtUltimoAcesso = CURRENT_TIMESTAMP WHERE u.idUsuario = :idUsuario")
    void atualizarUltimoAcesso(@Param("idUsuario") Long idUsuario);

    // Atualiza status
    @Modifying
    @Transactional
    @Query("UPDATE UserEntityJpa u SET u.codStatus = :codStatus WHERE u.idUsuario = :idUsuario")
    void updateStatus(@Param("idUsuario") Long idUsuario, @Param("codStatus") String codStatus);

    // Atualiza senha
    @Modifying
    @Transactional
    @Query("UPDATE UserEntityJpa u SET u.codSenha = :newPasswordHash, u.oldSenha = :oldPasswordHash, u.dtExpiraSenha = :expiryDate WHERE u.idUsuario = :idUsuario")
    void updatePassword(@Param("idUsuario") Long idUsuario, @Param("newPasswordHash") String newPasswordHash, @Param("oldPasswordHash") String oldPasswordHash, @Param("expiryDate") java.time.LocalDateTime expiryDate);

    // Reseta tentativas e status
    @Modifying
    @Transactional
    @Query("UPDATE UserEntityJpa u SET u.numTentativasFalha = 0, u.codStatus = :codStatus WHERE u.idUsuario = :idUsuario")
    void resetAttemptsAndStatus(@Param("idUsuario") Long idUsuario, @Param("codStatus") String codStatus);

    // Atualiza jornada de acesso
    @Modifying
    @Transactional
    @Query("UPDATE UserEntityJpa u SET u.numDiasSemanasPermitidos = :diasPermitidos, u.dt_jornada_ini = :jornadaIni, u.dt_jornada_fim = :jornadaFim WHERE u.idUsuario = :idUsuario")
    void updateAccessSchedule(@Param("idUsuario") Long idUsuario, @Param("diasPermitidos") String diasPermitidos, @Param("jornadaIni") java.util.Date jornadaIni, @Param("jornadaFim") java.util.Date jornadaFim);

    /**
     * Busca usuário por login.
     */
    @Query("SELECT u FROM UserEntityJpa u WHERE u.codLogin = :codLogin")
    Optional<UserEntityJpa> findByCodLogin(@Param("codLogin") String codLogin);

    /**
     * Busca usuário por email.
     */
    Optional<UserEntityJpa> findByNomEmail(String nomEmail);

    Optional<UserEntityJpa> findByCodCpf(String codCpf);

}

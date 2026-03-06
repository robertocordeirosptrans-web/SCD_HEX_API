package br.sptrans.scd.auth.application.port.out;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.User;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByCodLogin(String codLogin);

    Optional<User> findByNomEmail(String nomEmail);

    void atualizarTentativasEStatus(Long idUsuario, int numTentativas, String codStatus);

    void atualizarUltimoAcesso(Long idUsuario);

    // ── CRUD ──────────────────────────────────────────────────────────────────
    /**
     * Insere novo usuário na tabela USUARIOS.
     */
    void insert(User usuario);

    /**
     * Atualiza campos cadastrais do usuário (exceto COD_LOGIN e COD_SENHA).
     */
    void update(User usuario);

    /**
     * Atualiza COD_STATUS e DT_MANUTENCAO.
     */
    void updateStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao);

    /**
     * Atualiza COD_SENHA, OLD_SENHA e DT_EXPIRA_SENHA.
     */
    void updatePassword(Long idUsuario, String newPasswordHash, String oldPasswordHash, LocalDateTime expiryDate);

    /**
     * Reseta NUM_TENTATIVAS para 0 e aplica novo status.
     */
    void resetAttemptsAndStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao);

    /**
     * Atualiza jornada de acesso (NUM_DIAS_SEMANAS_PERMITIDOS, DT_JORNADA_INI,
     * DT_JORNADA_FIM).
     */
    void updateAccessSchedule(Long idUsuario, String diasPermitidos, Date jornadaIni, Date jornadaFim, Long idUsuarioManutencao);

    /**
     * Carrega as funcionalidades efetivas do usuário combinando três fontes: 1.
     * PERFIL_FUNCIONALIDADES onde USUARIO_PERFIS.COD_STATUS = 'Ativo' 2.
     * PERFIL_FUNCIONALIDADES via GRUPO_PERFIS onde GRUPO_USUARIOS.COD_STATUS =
     * 'Ativo' 3. USUARIO_FUNCIONALIDADES diretas com COD_STATUS_USU_FUN =
     * 'Ativo'
     *
     * Implementado no adaptador JPA com JOIN otimizado.
     */
    Set<Functionality> carregarFuncionalidadesEfetivas(Long idUsuario);

    // ── Verificações ──────────────────────────────────────────────────────────
    boolean existsByLogin(String codLogin);

    List<User> findAll(String codStatus);

    /**
     * Verifica se o usuário possui sessão ativa (DT_ULTIMO_ACESSO > now - 30
     * min). Usado antes de inativar para evitar interrupção abrupta de sessão.
     */
    boolean hasActiveSession(Long idUsuario);

}

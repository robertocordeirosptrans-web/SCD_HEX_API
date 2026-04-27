package br.sptrans.scd.auth.application.port.in;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.auth.domain.User;

/**
 * Porta de Entrada — casos de uso de gestão administrativa de usuários.
 *
 * Separado do AuthUseCase por propósito diferente: - AuthUseCase → fluxo
 * público (login, recuperação de senha) - UserManagementUseCase → operações
 * internas de administração (CRUD, vínculos)
 *
 * Regras gerais: - COD_LOGIN é único e imutável após criação - Senha inicial
 * gerada pelo sistema; usuário obrigado a trocar no primeiro acesso -
 * Inativação bloqueada se usuário possui sessão ativa (última sessão < 30 min)
 * - Toda alteração registra ID_USUARIO_MANUTENCAO + DT_MANUTENCAO
 */
public interface UserManagementUseCase {
    // ── CRUD principal ────────────────────────────────────────────────────────

    /**
     * Cria novo usuário. Gera senha temporária MD5; define DT_EXPIRA_SENHA =
     * now() para forçar troca imediata.
     */
    User createUser(CreateUserCommand command);

    /**
     * Atualiza dados cadastrais (nome, e-mail, CPF, RG, jornada). COD_LOGIN é
     * imutável — qualquer tentativa de alteração é ignorada.
     */
    User updateUser(UpdateUserCommand command);

    /**
     * Inativa o usuário (COD_STATUS = 'I'). Bloqueado se houver sessão ativa
     * nos últimos 30 minutos.
     */
    void deactivateUser(StatusChangeCommand command);

    /**
     * Reativa usuário inativo (COD_STATUS = 'A'). Reseta NUM_TENTATIVAS para 0.
     */
    void reactivateUser(StatusChangeCommand command);

    /**
     * Desbloqueia usuário bloqueado por excesso de tentativas (COD_STATUS = 'B'
     * → 'A'). Reseta NUM_TENTATIVAS para 0. Operação exclusiva do administrador
     * — diferente do desbloqueio automático via recuperação de senha.
     */
    void unblockUser(StatusChangeCommand command);

    /**
     * Bloqueia um usuário ativo (COD_STATUS = 'A' → 'B'). Operação
     * administrativa que impede o login até desbloqueio.
     */
    void blockUser(StatusChangeCommand command);

    /**
     * Redefinição administrativa de senha. Gera nova senha temporária e define
     * DT_EXPIRA_SENHA = now() para forçar troca no próximo login. Não exige
     * token — o administrador que executa a operação.
     */
    String adminResetPassword(AdminResetPasswordCommand command);

    // ── Jornada de acesso ─────────────────────────────────────────────────────
    /**
     * Atualiza as configurações de jornada do usuário:
     * NUM_DIAS_SEMANAS_PERMITIDOS, DT_JORNADA_INI e DT_JORNADA_FIM.
     */
    void updateAccessSchedule(UpdateScheduleCommand command);

    // ── Vínculos de perfil ────────────────────────────────────────────────────
    /**
     * Vincula um ou mais perfis diretamente ao usuário (USUARIO_PERFIS).
     * Idempotente: se o vínculo existe mas está inativo, reativa.
     */
    // void assignProfiles(AssignProfilesCommand command);
    /**
     * Remove vínculo de perfil do usuário (COD_STATUS = 'I' em USUARIO_PERFIS).
     * Não deleta — mantém histórico.
     */
    // void removeProfile(RemoveProfileCommand command);
    // ── Vínculos de grupo ─────────────────────────────────────────────────────
    /**
     * Vincula usuário a um grupo (GRUPO_USUARIOS). Idempotente: se o vínculo
     * existe mas está inativo, reativa.
     */
    // void assignGroup(AssignGroupCommand command);
    /**
     * Remove vínculo de grupo do usuário (COD_STATUS = 'I' em GRUPO_USUARIOS).
     */
    // void removeGroup(RemoveGroupCommand command);
    // ── Consultas ─────────────────────────────────────────────────────────────

    /**
     * Lista usuários com paginação, filtros e ordenação.
     */
    List<User> listUsersPaginated(UserFilterRequest filtro, int page, int size, String sortBy, String sortDir);

    Page<User> listUsersPaginated(Specification<UserEntityJpa> spec, Pageable pageable);

    /**
     * Conta total de usuários com os filtros aplicados.
     */
    long countUsers(UserFilterRequest filtro);

    /**
     * Busca usuário por ID com perfis, grupos e funcionalidades efetivas
     * carregados.
     */
    User findById(Long idUsuario);

    // ═════════════════════════════════════════════════════════════════════════
    // Comandos
    // ═════════════════════════════════════════════════════════════════════════
    record CreateUserCommand(
            String codLogin,
            String nomUsuario,
            String nomEmail,
            String codCpf,
            String codRg,
            String numDiasSemanasPermitidos, // ex: "2,3,4,5,6"
            LocalDateTime dtJornadaIni,
            LocalDateTime dtJornadaFim,
            Long idUsuarioLogado) {

    }

    record UpdateUserCommand(
            Long idUsuario,
            String nomUsuario,
            String nomEmail,
            String codCpf,
            String codRg,
            Long idUsuarioLogado) {

    }

    record StatusChangeCommand(
            Long idUsuario,
            Long idUsuarioLogado) {

    }

    record AdminResetPasswordCommand(
            Long idUsuario,
            Long idUsuarioLogado) {

    }

    record UpdateScheduleCommand(
            Long idUsuario,
            String numDiasSemanasPermitidos,
            Date dtJornadaIni,
            Date dtJornadaFim,
            Long idUsuarioLogado) {

    }

    record AssignProfilesCommand(
            Long idUsuario,
            Set<String> codsPerfil,
            Long idUsuarioLogado) {

    }

    record RemoveProfileCommand(
            Long idUsuario,
            String codPerfil,
            Long idUsuarioLogado) {

    }

    record AssignGroupCommand(
            Long idUsuario,
            String codGrupo,
            Long idUsuarioLogado) {

    }

    record RemoveGroupCommand(
            Long idUsuario,
            String codGrupo,
            Long idUsuarioLogado) {

    }

    record UserFilterRequest(
            String nomUsuario,
            String nomEmail,
            String codPerfil,
            String codStatus) {

    }

    // ═════════════════════════════════════════════════════════════════════════
    // Exceção de domínio
    // ═════════════════════════════════════════════════════════════════════════
    class UserManagementException extends RuntimeException {

        private final ErrorType errorType;

        public UserManagementException(ErrorType errorType, String message) {
            super(message);
            this.errorType = errorType;
        }

        public ErrorType getErrorType() {
            return errorType;
        }
    }

    enum ErrorType {
        LOGIN_ALREADY_EXISTS,
        USER_NOT_FOUND,
        PROFILE_NOT_FOUND,
        GROUP_NOT_FOUND,
        ALREADY_ACTIVE,
        ALREADY_INACTIVE,
        NOT_BLOCKED,
        ACTIVE_SESSION, // inativação bloqueada por sessão ativa
        PROFILE_ALREADY_ASSIGNED,
        PROFILE_NOT_ASSIGNED,
        GROUP_ALREADY_ASSIGNED,
        GROUP_NOT_ASSIGNED
    }

}

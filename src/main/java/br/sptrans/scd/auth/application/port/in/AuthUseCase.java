package br.sptrans.scd.auth.application.port.in;

import java.util.Set;

import br.sptrans.scd.auth.domain.User;

/**
 * Porta de Entrada (Input Port) do módulo de autenticação. Define o contrato
 * dos casos de uso da US001 — implementado por AuthService. Chamado
 * exclusivamente pelos adaptadores de entrada (AuthController, FilterJwt).
 */
public interface AuthUseCase {

    /**
     * US001 — Controle de Acesso e Autenticação Segura.
     *
     * Regras: - Senha validada com MD5 - Bloqueia após 3 tentativas; lança
     * exceção com mensagem específica - Conta bloqueada/inativa: exceção com
     * mensagem distinta - Valida jornada de acesso (DT_JORNADA_INI/FIM e
     * NUM_DIAS_SEMANAS_PERMITIDOS) - Sucesso: reseta tentativas, persiste
     * último acesso, retorna usuário com permissões
     *
     * @return Usuario com permissões carregadas
     * @throws AuthenticationException se credenciais inválidas, conta
     * bloqueada, fora de jornada
     */
    User autenticar(AuthComand comand);

    /**
     * US001 — Carregar permissões do usuário autenticado. Une: USUARIO_PERFIS +
     * GRUPO_PERFIS + USUARIO_FUNCIONALIDADES (diretas). Filtra: somente
     * entidades com status = 'Ativo'.
     *
     * @return conjunto de funcionalidades efetivas do usuário
     */

    UserContext loadUserContext(String codLogin);

    /**
     * US001 — Solicitar redefinição de senha. Regras: - Verifica NOM_EMAIL na
     * tabela USUARIOS antes de qualquer ação - Gera token UUID único; persiste
     * em password_reset_tokens com TTL - Invalida token anterior se existir -
     * Dispara e-mail SMTP apenas para e-mails cadastrados
     *
     * @throws AuthenticationException se e-mail não encontrado
     */
    void recoveryResetPassword(ResetRequestComand comando);

    /**
     * US001 — Redefinir senha com token válido. Regras: - Valida token: não
     * expirado, não utilizado - Compara MD5 com OLD_SENHA para impedir
     * reutilização - Regras de complexidade: maiúscula + minúscula + especial +
     * número + sem sequências - Marca token como used=true após redefinição
     *
     * @throws AuthenticationException se token inválido, expirado ou senha
     * rejeitada
     */
    void resetPassword(ResetPasswordComand comando);

    /**
     * Trocar senha do usuário autenticado.
     * 
     * Fluxo: O usuário já está autenticado (token JWT no header) e deseja
     * trocar sua própria senha. Requisitos:
     * 
     * 1. Recebe no header o token de autenticação (Authorization: Bearer <token>)
     * 2. Decodifica o token para capturar o userId
     * 3. Busca o usuário no banco de dados
     * 4. Lê a coluna codSenha (hash da senha atual)
     * 5. Compara a senha atual fornecida com o hash armazenado usando PasswordHashUtil
     * 6. Se válida, gera novo hash de acordo com o tipo detectado
     * 7. Atualiza codSenha com o novo hash
     * 8. Move o hash anterior para oldSenha
     * 9. Atualiza dtModi com timestamp atual
     * 10. Atualiza dtExpiraSenha com data futura de 3 meses
     * 11. Invalida todas as sessões ativas do usuário (logout em todos os dispositivos)
     *
     * @param comando contém userId (extraído do token), senhaAtual e novaSenha
     * @throws AuthenticationFailedException se senha atual incorreta
     * @throws ValidationException se nova senha não atende política
     * @throws ResourceNotFoundException se usuário não encontrado
     */
    void changePassword(ChangePasswordCommand comando);

    /**
     * Invalida o cache de permissões (perfis e funcionalidades) do usuário.
     * Deve ser chamado após logout para garantir que o próximo login
     * recarregue as permissões atualizadas do banco de dados.
     */
    void evictUserPermissionsCache(Long idUsuario);

    /**
     * US001 — Renovar tokens a partir de um refresh token válido.
     * Valida o refresh token, verifica se o usuário ainda está ativo,
     * cria nova sessão e retorna novo par de tokens (access + refresh).
     *
     * @return par de tokens renovados
     * @throws AuthenticationException se o refresh token for inválido/expirado
     *                                 ou o usuário estiver bloqueado/inativo
     */
    TokenPair refreshToken(RefreshTokenComand comand);

    record TokenPair(String accessToken, String refreshToken) {}

    record RefreshTokenComand(String refreshToken, String ip, String userAgent) {}

    record UserContext(Long id, String name, Set<String> roles, Set<String> permissions, Set<String> groups) {

    }

    record AuthComand(String codLogin, String senha) {

    }

    record ResetRequestComand(String email) {

    }

    record ResetPasswordComand(String token, String novaSenha) {

    }

    /**
     * Comando para trocar senha do usuário autenticado.
     * 
     * @param idUsuario ID do usuário extraído do token JWT
     * @param senhaAtual senha atual em texto plano (para validação)
     * @param novaSenha nova senha em texto plano (será hashada com BCrypt)
     */
    record ChangePasswordCommand(Long idUsuario, String senhaAtual, String novaSenha) {

    }

    // ── Exceção de domínio ───────────────────────────────────────────────────
    class AuthenticationException extends RuntimeException {

        private final AthenticationErrorType tipo;

        public AuthenticationException(AthenticationErrorType tipo, String mensagem) {
            super(mensagem);
            this.tipo = tipo;
        }

        public AthenticationErrorType getTipo() {
            return tipo;
        }
    }

    enum AthenticationErrorType {
        CREDENCIAIS_INVALIDAS,
        CONTA_BLOQUEADA,
        CONTA_INATIVA,
        FORA_DA_JORNADA,
        EMAIL_NAO_ENCONTRADO,
        TOKEN_INVALIDO,
        TOKEN_EXPIRADO,
        SENHA_REUTILIZADA,
        SENHA_FRACA
    }
}

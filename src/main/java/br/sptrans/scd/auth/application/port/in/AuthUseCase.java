package br.sptrans.scd.auth.application.port.in;

import java.util.Set;

import br.sptrans.scd.auth.domain.Functionality;
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
    Set<Functionality> loadPermissions(Long idUsuario);

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

    record UserContext(Long id, String name, Set<String> roles, Set<String> permissions, Set<String> groups) {

    }

    record AuthComand(String codLogin, String senha) {

    }

    record ResetRequestComand(String email) {

    }

    record ResetPasswordComand(String token, String novaSenha) {

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

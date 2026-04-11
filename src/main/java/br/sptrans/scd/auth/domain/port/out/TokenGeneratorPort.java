package br.sptrans.scd.auth.domain.port.out;

import br.sptrans.scd.auth.domain.User;

/**
 * Output Port — Abstração de geração e validação de tokens JWT.
 * Permite trocar mecanismo de token sem alterar o domínio.
 */
public interface TokenGeneratorPort {
    /**
     * Gera um token JWT de acesso sem session_id (compatibilidade legada).
     */
    String generate(User user);

    /**
     * Gera um token JWT de acesso com session_id embutido no payload.
     */
    String generate(User user, String sessionId);

    /**
     * Gera um token JWT de refresh.
     */
    String generateRefresh(User user);

    /**
     * Valida o token e retorna o login (subject) se válido.
     * Retorna null ou string vazia se o token for inválido/expirado.
     */
    String validateAndGetSubject(String token);
}

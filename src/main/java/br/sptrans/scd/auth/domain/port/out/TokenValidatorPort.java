package br.sptrans.scd.auth.domain.port.out;

/**
 * Output Port — Abstração de validação de token.
 * O filtro chama este port; o adapter JWT implementa.
 *
 * Isso permite trocar JWT por outro mecanismo
 * sem alterar o filtro nem o domínio.
 */
public interface TokenValidatorPort {

    /**
     * Valida o token e retorna o login (subject) se válido.
     * Retorna null ou string vazia se o token for inválido/expirado.
     */
    String validateAndGetSubject(String token);

    /**
     * Extrai o session_id do payload do token sem revalidar a assinatura.
     * Retorna null se o claim não estiver presente.
     */
    String extractSessionId(String token);
}
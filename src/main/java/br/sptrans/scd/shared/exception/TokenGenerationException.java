package br.sptrans.scd.shared.exception;

/**
 * Exceção final para erro na geração de token.
 * Lançada quando ocorrem falhas na criação de um novo token JWT.
 * Exemplos: configuração inválida do secret, algoritmo não suportado, etc.
 */
public final class TokenGenerationException extends TokenException {

    public TokenGenerationException(String message) {
        super(message, "TOKEN_GENERATION_FAILED");
    }

    public TokenGenerationException(String message, Throwable cause) {
        super(message, "TOKEN_GENERATION_FAILED", cause);
    }
}

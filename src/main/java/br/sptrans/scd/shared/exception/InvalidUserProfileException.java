package br.sptrans.scd.shared.exception;

/**
 * Exceção final para perfil de usuário inválido.
 * Lançada quando um perfil de usuário está em estado inválido ou inconsistente.
 * Exemplos: perfil expirado, perfil sem funcionalidades, perfil sem acesso.
 */
public final class InvalidUserProfileException extends BusinessException {

    public InvalidUserProfileException(String message) {
        super(message, "INVALID_USER_PROFILE");
    }

    public InvalidUserProfileException(String message, Throwable cause) {
        super(message, "INVALID_USER_PROFILE", cause);
    }
}

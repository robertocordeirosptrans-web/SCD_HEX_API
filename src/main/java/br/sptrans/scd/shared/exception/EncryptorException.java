package br.sptrans.scd.shared.exception;

/**
 * Exceção final para erros de criptografia.
 * Lançada quando operações de criptografia ou descriptografia falham.
 * Exemplos: erro ao criptografar/descriptografar dados, key inválida, algoritmo não suportado, etc.
 */
public final class EncryptorException extends RuntimeException {

    private final String errorCode;

    public EncryptorException(String mensagem) {
        super(mensagem);
        this.errorCode = "ENCRYPTION_ERROR";
    }

    public EncryptorException(String mensagem, String errorCode) {
        super(mensagem);
        this.errorCode = errorCode;
    }

    public EncryptorException(String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.errorCode = "ENCRYPTION_ERROR";
    }

    public EncryptorException(String mensagem, String errorCode, Throwable causa) {
        super(mensagem, causa);
        this.errorCode = errorCode;
    }

    public EncryptorException(Throwable causa) {
        super(causa);
        this.errorCode = "ENCRYPTION_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}

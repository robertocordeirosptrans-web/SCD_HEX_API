package br.sptrans.scd.shared.exception;

public class EncryptorException extends Exception {

    public EncryptorException(String mensagem) {
        super(mensagem);
    }

    public EncryptorException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public EncryptorException(Throwable causa) {
        super(causa);
    }

}

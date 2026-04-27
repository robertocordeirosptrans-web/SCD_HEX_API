package br.sptrans.scd.shared.idempotency;

import org.springframework.http.HttpStatus;

import br.sptrans.scd.shared.exception.ModuleException;

/**
 * Lançada quando uma requisição com a mesma {@code Idempotency-Key} está em
 * conflito com um registro existente — situações de concorrência (PROCESSING ativo)
 * ou payload divergente para uma chave já executada.
 *
 * <p>Resulta em resposta HTTP {@code 409 Conflict}.</p>
 */
public class IdempotencyConflictException extends RuntimeException implements ModuleException {

    private static final String ERROR_CODE = "IDEMPOTENCY_CONFLICT";

    private final String idempotencyKey;

    public IdempotencyConflictException(String idempotencyKey, String message) {
        super(message);
        this.idempotencyKey = idempotencyKey;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}

package br.sptrans.scd.shared.exception;

import org.springframework.http.HttpStatus;

public interface ModuleException {

    HttpStatus getHttpStatus();

    String getErrorCode();

    String getMessage();
}

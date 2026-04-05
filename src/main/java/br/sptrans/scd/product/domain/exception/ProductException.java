package br.sptrans.scd.product.domain.exception;

import org.springframework.http.HttpStatus;

import br.sptrans.scd.product.domain.enums.ProductErrorType;
import br.sptrans.scd.shared.exception.ModuleException;

/**
 * Exceção de domínio do módulo de Produto. Carrega o tipo de erro para que as
 * camadas superiores possam traduzir para o código HTTP/mensagem adequados.
 */
public class ProductException extends RuntimeException implements ModuleException {

    private final ProductErrorType errorType;

    public ProductException(ProductErrorType errorType) {
        super(errorType.getDescription());
        this.errorType = errorType;
    }

    public ProductException(ProductErrorType errorType, Throwable cause) {
        super(errorType.getDescription(), cause);
        this.errorType = errorType;
    }

    public ProductErrorType getErrorType() {
        return errorType;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return errorType.getHttpStatus();
    }

    @Override
    public String getErrorCode() {
        String name = errorType.name();
        if (name.contains("NOT_FOUND")) {
            return "PRODUCT_NOT_FOUND";
        } else if (name.contains("ALREADY_EXISTS") || name.contains("CODE_ALREADY_EXISTS")) {
            return "PRODUCT_DUPLICATE";
        } else if (name.contains("ALREADY_ACTIVE") || name.contains("ALREADY_INACTIVE")) {
            return "PRODUCT_INVALID_STATE";
        } else if (name.contains("CONFLICT")) {
            return "PRODUCT_CONFLICT";
        } else if (name.contains("INVALID")) {
            return "PRODUCT_VALIDATION_ERROR";
        }
        return "PRODUCT_ERROR";
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

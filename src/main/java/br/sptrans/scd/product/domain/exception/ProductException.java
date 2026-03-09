package br.sptrans.scd.product.domain.exception;

import br.sptrans.scd.product.domain.enums.ProductErrorType;

/**
 * Exceção de domínio do módulo de Produto. Carrega o tipo de erro para que as
 * camadas superiores possam traduzir para o código HTTP/mensagem adequados.
 */
public class ProductException extends RuntimeException {

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
}

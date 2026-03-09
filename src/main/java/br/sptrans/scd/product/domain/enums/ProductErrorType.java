package br.sptrans.scd.product.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductErrorType {
    PRODUCT_NOT_FOUND("Produto não encontrado."),
    CODE_ALREADY_EXISTS("Código de produto já cadastrado."),
    PRODUCT_ALREADY_ACTIVE("Produto já está ativo."),
    PRODUCT_ALREADY_INACTIVE("Produto já está inativo."),
    PRODUCT_WITHOUT_VERSION("Produto sem versão configurada não pode ser ativado."),
    VERSION_NOT_FOUND("Versão de produto não encontrada."),
    VERSION_INVALID_DATE("Data de início da nova versão deve ser futura.");

    private final String description;

    public static ProductErrorType fromCode(String description) {
        for (ProductErrorType type : values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de erro desconhecido: " + description);
    }
}

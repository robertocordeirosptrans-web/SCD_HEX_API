package br.sptrans.scd.product.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ProductStatus {
    ACTIVE("A", "Ativo"),
    BLOCKED("B", "Bloqueado"),
    INACTIVE("I", "Inativo");

    private final String code;
    private final String description;

    public static ProductStatus fromCode(String code) {
        for (ProductStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de status desconhecido: " + code);
    }
}

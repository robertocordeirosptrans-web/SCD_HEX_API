package br.sptrans.scd.product.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ProductVersionStatus {
    ACTIVE("A", "Ativo"),
    BLOCKED("B", "Bloqueado"),
    INACTIVE("I", "Inativo");

    private final String code;
    private final String description;

    public static ProductVersionStatus fromCode(String code) {
        for (ProductVersionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de status desconhecido: " + code);
    }
}

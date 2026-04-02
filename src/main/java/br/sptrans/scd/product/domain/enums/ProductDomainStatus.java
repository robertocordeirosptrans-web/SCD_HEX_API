package br.sptrans.scd.product.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Status generalizado para entidades de domínio do produto:
 * Family, Species, Technology, ProductType, Modality.
 */
@Getter
@AllArgsConstructor
public enum ProductDomainStatus {

    ACTIVE("A", "Ativo"),
    INACTIVE("I", "Inativo");

    private final String code;
    private final String description;

    public static ProductDomainStatus fromCode(String code) {
        for (ProductDomainStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de status desconhecido: " + code);
    }
}

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
    VERSION_INVALID_DATE("Data de início da nova versão deve ser futura."),
    FARE_NOT_FOUND("Tarifa não encontrada."),
    FARE_VALIDITY_CONFLICT("Conflito de vigência com outra tarifa existente."),
    FEE_NOT_FOUND("Taxa não encontrada."),

    // Family
    FAMILY_NOT_FOUND("Família não encontrada."),
    FAMILY_CODE_ALREADY_EXISTS("Código de família já cadastrado."),
    FAMILY_ALREADY_ACTIVE("Família já está ativa."),
    FAMILY_ALREADY_INACTIVE("Família já está inativa."),

    // Modality
    MODALITY_NOT_FOUND("Modalidade não encontrada."),
    MODALITY_CODE_ALREADY_EXISTS("Código de modalidade já cadastrado."),
    MODALITY_ALREADY_ACTIVE("Modalidade já está ativa."),
    MODALITY_ALREADY_INACTIVE("Modalidade já está inativa."),

    // Species
    SPECIES_NOT_FOUND("Espécie não encontrada."),
    SPECIES_CODE_ALREADY_EXISTS("Código de espécie já cadastrado."),
    SPECIES_ALREADY_ACTIVE("Espécie já está ativa."),
    SPECIES_ALREADY_INACTIVE("Espécie já está inativa."),

    // ProductsType
    PRODUCTS_TYPE_NOT_FOUND("Tipo de produto não encontrado."),
    PRODUCTS_TYPE_CODE_ALREADY_EXISTS("Código de tipo de produto já cadastrado."),
    PRODUCTS_TYPE_ALREADY_ACTIVE("Tipo de produto já está ativo."),
    PRODUCTS_TYPE_ALREADY_INACTIVE("Tipo de produto já está inativo."),

    // Technology
    TECHNOLOGY_NOT_FOUND("Tecnologia não encontrada."),
    TECHNOLOGY_CODE_ALREADY_EXISTS("Código de tecnologia já cadastrado."),
    TECHNOLOGY_ALREADY_ACTIVE("Tecnologia já está ativa."),
    TECHNOLOGY_ALREADY_INACTIVE("Tecnologia já está inativa.");

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

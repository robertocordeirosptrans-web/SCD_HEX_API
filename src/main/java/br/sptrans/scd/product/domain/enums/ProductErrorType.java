package br.sptrans.scd.product.domain.enums;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductErrorType {
    PRODUCT_NOT_FOUND("Produto não encontrado.", HttpStatus.NOT_FOUND),
    PRODUCT_TYPE_NOT_FOUND("Tipo de produto não encontrado.", HttpStatus.NOT_FOUND),
    CODE_ALREADY_EXISTS("Código de produto já cadastrado.", HttpStatus.CONFLICT),
    PRODUCT_ALREADY_ACTIVE("Produto já está ativo.", HttpStatus.CONFLICT),
    PRODUCT_ALREADY_INACTIVE("Produto já está inativo.", HttpStatus.CONFLICT),
    PRODUCT_WITHOUT_VERSION("Produto sem versão configurada não pode ser ativado.", HttpStatus.UNPROCESSABLE_ENTITY),
    VERSION_NOT_FOUND("Versão de produto não encontrada.", HttpStatus.NOT_FOUND),
    VERSION_INVALID_DATE("Data de início da nova versão deve ser futura.", HttpStatus.UNPROCESSABLE_ENTITY),
    FARE_NOT_FOUND("Tarifa não encontrada.", HttpStatus.NOT_FOUND),
    FARE_VALIDITY_CONFLICT("Conflito de vigência com outra tarifa existente.", HttpStatus.CONFLICT),
    FEE_NOT_FOUND("Taxa não encontrada.", HttpStatus.NOT_FOUND),
    // Family
    FAMILY_NOT_FOUND("Família não encontrada.", HttpStatus.NOT_FOUND),
    FAMILY_CODE_ALREADY_EXISTS("Código de família já cadastrado.", HttpStatus.CONFLICT),
    FAMILY_ALREADY_ACTIVE("Família já está ativa.", HttpStatus.CONFLICT),
    FAMILY_ALREADY_INACTIVE("Família já está inativa.", HttpStatus.CONFLICT),
    // Modality
    MODALITY_NOT_FOUND("Modalidade não encontrada.", HttpStatus.NOT_FOUND),
    MODALITY_CODE_ALREADY_EXISTS("Código de modalidade já cadastrado.", HttpStatus.CONFLICT),
    MODALITY_ALREADY_ACTIVE("Modalidade já está ativa.", HttpStatus.CONFLICT),
    MODALITY_ALREADY_INACTIVE("Modalidade já está inativa.", HttpStatus.CONFLICT),
    // Species
    SPECIES_NOT_FOUND("Espécie não encontrada.", HttpStatus.NOT_FOUND),
    SPECIES_CODE_ALREADY_EXISTS("Código de espécie já cadastrado.", HttpStatus.CONFLICT),
    SPECIES_ALREADY_ACTIVE("Espécie já está ativa.", HttpStatus.CONFLICT),
    SPECIES_ALREADY_INACTIVE("Espécie já está inativa.", HttpStatus.CONFLICT),
    // ProductsType
    PRODUCTS_TYPE_NOT_FOUND("Tipo de produto não encontrado.", HttpStatus.NOT_FOUND),
    PRODUCTS_TYPE_CODE_ALREADY_EXISTS("Código de tipo de produto já cadastrado.", HttpStatus.CONFLICT),
    PRODUCTS_TYPE_ALREADY_ACTIVE("Tipo de produto já está ativo.", HttpStatus.CONFLICT),
    PRODUCTS_TYPE_ALREADY_INACTIVE("Tipo de produto já está inativo.", HttpStatus.CONFLICT),
    // Technology
    TECHNOLOGY_NOT_FOUND("Tecnologia não encontrada.", HttpStatus.NOT_FOUND),
    TECHNOLOGY_CODE_ALREADY_EXISTS("Código de tecnologia já cadastrado.", HttpStatus.CONFLICT),
    TECHNOLOGY_ALREADY_ACTIVE("Tecnologia já está ativa.", HttpStatus.CONFLICT),
    TECHNOLOGY_ALREADY_INACTIVE("Tecnologia já está inativa.", HttpStatus.CONFLICT);

    private final String description;
    private final HttpStatus httpStatus;

    public static ProductErrorType fromCode(String description) {
        for (ProductErrorType type : values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de erro desconhecido: " + description);
    }
}

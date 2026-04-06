package br.sptrans.scd.channel.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Status generalizado para entidades de domínio do produto:
 * Family, Species, Technology, ProductType, Modality.
 */
@Getter
@AllArgsConstructor
public enum ChannelDomainStatus {

    ACTIVE("A", "Ativo"),
    INACTIVE("I", "Inativo");

    private final String code;
    private final String description;

    public static ChannelDomainStatus fromCode(String code) {
        for (ChannelDomainStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de status desconhecido: " + code);
    }
}
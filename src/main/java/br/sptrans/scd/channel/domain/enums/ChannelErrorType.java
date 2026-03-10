package br.sptrans.scd.channel.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelErrorType {

    // TypesActivity
    TYPES_ACTIVITY_NOT_FOUND("Tipo de atividade não encontrado."),
    TYPES_ACTIVITY_CODE_ALREADY_EXISTS("Código de tipo de atividade já cadastrado."),
    TYPES_ACTIVITY_ALREADY_ACTIVE("Tipo de atividade já está ativo."),
    TYPES_ACTIVITY_ALREADY_INACTIVE("Tipo de atividade já está inativo."),

    // SalesChannel
    SALES_CHANNEL_NOT_FOUND("Canal de vendas não encontrado."),
    SALES_CHANNEL_CODE_ALREADY_EXISTS("Código de canal de vendas já cadastrado."),
    SALES_CHANNEL_ALREADY_ACTIVE("Canal de vendas já está ativo."),
    SALES_CHANNEL_ALREADY_INACTIVE("Canal de vendas já está inativo."),

    // ContactChannel
    CONTACT_CHANNEL_NOT_FOUND("Contato do canal não encontrado."),
    CONTACT_CHANNEL_CODE_ALREADY_EXISTS("Código de contato já cadastrado."),

    // AddressChannel
    ADDRESS_CHANNEL_NOT_FOUND("Endereço do canal não encontrado."),
    ADDRESS_CHANNEL_CODE_ALREADY_EXISTS("Código de endereço já cadastrado.");

    private final String description;
}

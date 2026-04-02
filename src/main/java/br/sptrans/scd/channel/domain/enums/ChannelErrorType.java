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
    ADDRESS_CHANNEL_CODE_ALREADY_EXISTS("Código de endereço já cadastrado."),

    // AgreementValidity
    AGREEMENT_VALIDITY_NOT_FOUND("Vigência de convênio não encontrada."),
    AGREEMENT_VALIDITY_ALREADY_EXISTS("Vigência de convênio já cadastrada para este canal e produto."),

    // MarketingDistribuitionChannel
    MARKETING_CHANNEL_NOT_FOUND("Canal de comercialização/distribuição não encontrado."),
    MARKETING_CHANNEL_ALREADY_EXISTS("Canal de comercialização/distribuição já cadastrado."),

    // ProductChannel
    PRODUCT_CHANNEL_NOT_FOUND("Canal de produto não encontrado."),
    PRODUCT_CHANNEL_ALREADY_EXISTS("Canal de produto já cadastrado para este canal e produto."),

    // RechargeLimit
    RECHARGE_LIMIT_NOT_FOUND("Limite de recarga não encontrado."),
    RECHARGE_LIMIT_ALREADY_EXISTS("Limite de recarga já cadastrado para este canal e produto."),

    // User
    USER_NOT_FOUND("Usuário não encontrado.");

    private final String description;
}

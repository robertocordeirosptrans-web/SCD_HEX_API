package br.sptrans.scd.channel.domain.enums;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelErrorType {

    // TypesActivity
    TYPES_ACTIVITY_NOT_FOUND("Tipo de atividade não encontrado.", HttpStatus.NOT_FOUND),
    TYPES_ACTIVITY_CODE_ALREADY_EXISTS("Código de tipo de atividade já cadastrado.", HttpStatus.CONFLICT),
    TYPES_ACTIVITY_ALREADY_ACTIVE("Tipo de atividade já está ativo.", HttpStatus.CONFLICT),
    TYPES_ACTIVITY_ALREADY_INACTIVE("Tipo de atividade já está inativo.", HttpStatus.CONFLICT),

    // SalesChannel
    SALES_CHANNEL_NOT_FOUND("Canal de vendas não encontrado.", HttpStatus.NOT_FOUND),
    SALES_CHANNEL_CODE_ALREADY_EXISTS("Código de canal de vendas já cadastrado.", HttpStatus.CONFLICT),
    SALES_CHANNEL_ALREADY_ACTIVE("Canal de vendas já está ativo.", HttpStatus.CONFLICT),
    SALES_CHANNEL_ALREADY_INACTIVE("Canal de vendas já está inativo.", HttpStatus.CONFLICT),

    // ContactChannel
    CONTACT_CHANNEL_NOT_FOUND("Contato do canal não encontrado.", HttpStatus.NOT_FOUND),
    CONTACT_CHANNEL_CODE_ALREADY_EXISTS("Código de contato já cadastrado.", HttpStatus.CONFLICT),

    // AddressChannel
    ADDRESS_CHANNEL_NOT_FOUND("Endereço do canal não encontrado.", HttpStatus.NOT_FOUND),
    ADDRESS_CHANNEL_CODE_ALREADY_EXISTS("Código de endereço já cadastrado.", HttpStatus.CONFLICT),

    // AgreementValidity
    AGREEMENT_VALIDITY_NOT_FOUND("Vigência de convênio não encontrada.", HttpStatus.NOT_FOUND),
    AGREEMENT_VALIDITY_ALREADY_EXISTS("Vigência de convênio já cadastrada para este canal e produto.", HttpStatus.CONFLICT),

    // MarketingDistribuitionChannel
    MARKETING_CHANNEL_NOT_FOUND("Canal de comercialização/distribuição não encontrado.", HttpStatus.NOT_FOUND),
    MARKETING_CHANNEL_ALREADY_EXISTS("Canal de comercialização/distribuição já cadastrado.", HttpStatus.CONFLICT),

    // ProductChannel
    PRODUCT_CHANNEL_NOT_FOUND("Canal de produto não encontrado.", HttpStatus.NOT_FOUND),
    PRODUCT_CHANNEL_ALREADY_EXISTS("Canal de produto já cadastrado para este canal e produto.", HttpStatus.CONFLICT),

    // RechargeLimit
    RECHARGE_LIMIT_NOT_FOUND("Limite de recarga não encontrado.", HttpStatus.NOT_FOUND),
    RECHARGE_LIMIT_ALREADY_EXISTS("Limite de recarga já cadastrado para este canal e produto.", HttpStatus.CONFLICT),
    RECHARGE_LIMIT_NOT_VIGENTE("Não há limite de recarga vigente parametrizado para este canal e produto.", HttpStatus.NOT_FOUND),

    // User
    USER_NOT_FOUND("Usuário não encontrado.", HttpStatus.NOT_FOUND);

    private final String description;
    private final HttpStatus httpStatus;
}

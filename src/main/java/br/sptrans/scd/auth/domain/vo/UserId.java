package br.sptrans.scd.auth.domain.vo;

import lombok.Value;

/**
 * Value Object imutável que encapsula o identificador do usuário.
 */
@Value
public class UserId {

    Long valor;

    public UserId(Long valor) {
        if (valor != null && valor <= 0) {
            throw new IllegalArgumentException("UserId inválido: deve ser maior que zero");
        }
        this.valor = valor;
    }
}

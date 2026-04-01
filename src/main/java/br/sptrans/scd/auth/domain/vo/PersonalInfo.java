package br.sptrans.scd.auth.domain.vo;

import lombok.Value;

@Value
public class PersonalInfo {

    private final String name;
    private final String address;
    private final String cpf;
    private final String rg;
    private final String email;
    private final String phone;
}

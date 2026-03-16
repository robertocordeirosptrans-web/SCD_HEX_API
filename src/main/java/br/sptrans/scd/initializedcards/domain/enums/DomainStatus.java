package br.sptrans.scd.initializedcards.domain.enums;

public enum DomainStatus {
    ACTIVE("A", "Ativo"),
    INACTIVE("I", "Inativo");

    private final String code;
    private final String description;

    DomainStatus(String 
    code,
    String description
        

    ) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean canLogin() {
        return this == ACTIVE;
    }


}

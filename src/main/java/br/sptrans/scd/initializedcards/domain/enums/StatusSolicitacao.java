package br.sptrans.scd.initializedcards.domain.enums;

public enum StatusSolicitacao {
    ATIVA("A", "Ativa"),
    CANCELADA("C", "Cancelada");

    private final String code;
    private final String description;

    StatusSolicitacao(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static StatusSolicitacao fromCode(String code) {
        for (StatusSolicitacao s : values()) {
            if (s.code.equals(code)) return s;
        }
        throw new IllegalArgumentException("Status desconhecido: " + code);
    }
}

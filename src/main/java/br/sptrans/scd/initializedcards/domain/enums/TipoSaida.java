package br.sptrans.scd.initializedcards.domain.enums;

public enum TipoSaida {
    RETIRADA("R", "Retirada"),
    ENTREGA("E", "Entrega");

    private final String code;
    private final String description;

    TipoSaida(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static TipoSaida fromCode(String code) {
        for (TipoSaida t : values()) {
            if (t.code.equals(code)) return t;
        }
        throw new IllegalArgumentException("TipoSaida desconhecido: " + code);
    }
}

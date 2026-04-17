package br.sptrans.scd.initializedcards.domain.enums;

public enum FaseSolicitacao {
    CADASTRADA("C", "Cadastrada"),
    APROVADA("A", "Aprovada"),
    ASSOCIADA("S", "Associada ao Lote SCP"),
    GERADA("G", "Arquivo Gerado"),
    RETIRADA("R", "Retirada/Entregue"),
    FINALIZADA("F", "Finalizada");

    private final String code;
    private final String description;

    FaseSolicitacao(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static FaseSolicitacao fromCode(String code) {
        for (FaseSolicitacao f : values()) {
            if (f.code.equals(code)) return f;
        }
        throw new IllegalArgumentException("Fase desconhecida: " + code);
    }
}

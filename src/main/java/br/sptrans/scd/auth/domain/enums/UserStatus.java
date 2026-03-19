package br.sptrans.scd.auth.domain.enums;

/**
 * Enum para status de usuário. Substitui Strings "A", "B", "I" por valores
 * type-safe (FASE 3).
 */
public enum UserStatus {

    ACTIVE("A", "Ativo"),
    BLOCKED("B", "Bloqueado"),
    INACTIVE("I", "Inativo");

    private final String code;
    private final String description;

    UserStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public static UserStatus valueOfCode(String code) {
        if (code == null) {
            return null;
        }
        for (UserStatus status : values()) {
            if (status.getCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public boolean canLogin() {
        return this == ACTIVE;
    }

    public boolean isBlocked() {
        return this == BLOCKED;
    }
}

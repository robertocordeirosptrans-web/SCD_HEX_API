package br.sptrans.scd.auth.domain.vo;

import lombok.Builder;
import lombok.Value;

/**
 * Value Object imutável que representa a política de complexidade de senha.
 * Configurável via {@link #strict()} ou {@link #moderate()}.
 */
@Value
@Builder
public class PasswordPolicy {

    int minLength;
    boolean requireUppercase;
    boolean requireLowercase;
    boolean requireNumber;
    boolean requireSpecial;
    boolean blockSequential;

    /**
     * Política estrita: mínimo 8 chars, maiúscula, minúscula, número, especial, sem sequências.
     */
    public static PasswordPolicy strict() {
        return PasswordPolicy.builder()
                .minLength(8)
                .requireUppercase(true)
                .requireLowercase(true)
                .requireNumber(true)
                .requireSpecial(true)
                .blockSequential(true)
                .build();
    }

    /**
     * Política moderada: mínimo 6 chars, sem demais restrições — adequada para dev/teste.
     */
    public static PasswordPolicy moderate() {
        return PasswordPolicy.builder()
                .minLength(6)
                .requireUppercase(false)
                .requireLowercase(false)
                .requireNumber(false)
                .requireSpecial(false)
                .blockSequential(false)
                .build();
    }
}

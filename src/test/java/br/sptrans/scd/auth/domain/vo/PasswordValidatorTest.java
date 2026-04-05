package br.sptrans.scd.auth.domain.vo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.sptrans.scd.auth.application.port.in.PasswordValidator;
import br.sptrans.scd.auth.application.service.StrictPasswordValidator;
import br.sptrans.scd.shared.security.PasswordHashUtil;

public class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StrictPasswordValidator();
    }

    @Test
    @DisplayName("✓ Deve aceitar senha com todos os requisitos")
    void shouldAcceptValidPassword() {
        String validPassword = "Admin@Passw0rd!";

        PasswordValidationResult result = validator.validate(validPassword, null);

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("✗ Deve rejeitar senha vazia")
    void shouldRejectEmptyPassword() {
        PasswordValidationResult result = validator.validate("", null);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("mínimo")));
    }

    @Test
    @DisplayName("✗ Deve rejeitar senha curta")
    void shouldRejectShortPassword() {
        String shortPassword = "Abc@1";

        PasswordValidationResult result = validator.validate(shortPassword, null);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("mínimo")));
    }

    @Test
    @DisplayName("✗ Deve rejeitar sem maiúscula")
    void shouldRejectWithoutUppercase() {
        String noUppercase = "mypassw0rd@1234";

        PasswordValidationResult result = validator.validate(noUppercase, null);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("maiúscula")));
    }

    @Test
    @DisplayName("✗ Deve rejeitar sem número")
    void shouldRejectWithoutNumbers() {
        String noNumbers = "MyPassw@rd";

        PasswordValidationResult result = validator.validate(noNumbers, null);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("número")));
    }

    @Test
    @DisplayName("✗ Deve rejeitar reutilização de senha")
    void shouldRejectPasswordReuse() {
        String oldPassword = "password123";
        String oldPasswordHash = PasswordHashUtil.hashBcrypt(oldPassword);

        PasswordValidationResult result = validator.validate(oldPassword, oldPasswordHash);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("igual")));
    }
}

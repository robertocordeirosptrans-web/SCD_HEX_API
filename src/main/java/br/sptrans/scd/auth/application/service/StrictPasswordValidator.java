package br.sptrans.scd.auth.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.PasswordValidator;
import br.sptrans.scd.auth.domain.vo.PasswordPolicy;
import br.sptrans.scd.auth.domain.vo.PasswordValidationResult;
import br.sptrans.scd.shared.security.PasswordHashUtil;

/**
 * Validador estrito de senha.
 * <p>
 * Aplica todas as regras da {@link PasswordPolicy#strict()}:
 * comprimento mínimo, maiúscula, minúscula, número, caractere especial,
 * bloqueio de sequências óbvias e rejeição de repetição da senha atual.
 */
@Primary
@Component("strictPasswordValidator")
public class StrictPasswordValidator implements PasswordValidator {

    private static final Pattern TEM_MAIUSCULA = Pattern.compile(".*[A-Z].*");
    private static final Pattern TEM_MINUSCULA = Pattern.compile(".*[a-z].*");
    private static final Pattern TEM_NUMERO    = Pattern.compile(".*\\d.*");
    private static final Pattern TEM_ESPECIAL  = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    private static final Pattern TEM_SEQUENCIAL = Pattern.compile(
            ".*(012|123|234|345|456|567|678|789|890"
            + "|abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz"
            + "|ABC|BCD|CDE|DEF|EFG|FGH|GHI|HIJ|IJK|JKL|KLM|LMN|MNO|NOP|OPQ|PQR|QRS|RST|STU|TUV|UVW|VWX|WXY|XYZ).*"
    );

    private final PasswordPolicy policy;

    public StrictPasswordValidator() {
        this.policy = PasswordPolicy.strict();
    }

    @Override
    public PasswordValidationResult validate(String password, String oldPasswordHash) {
        List<String> errors = new ArrayList<>();
        String safe = password != null ? password : "";

        if (password == null || password.length() < policy.getMinLength()) {
            errors.add("A senha deve ter no mínimo " + policy.getMinLength() + " caracteres.");
        }
        if (policy.isRequireUppercase() && !TEM_MAIUSCULA.matcher(safe).matches()) {
            errors.add("A senha deve conter ao menos uma letra maiúscula.");
        }
        if (policy.isRequireLowercase() && !TEM_MINUSCULA.matcher(safe).matches()) {
            errors.add("A senha deve conter ao menos uma letra minúscula.");
        }
        if (policy.isRequireNumber() && !TEM_NUMERO.matcher(safe).matches()) {
            errors.add("A senha deve conter ao menos um número.");
        }
        if (policy.isRequireSpecial() && !TEM_ESPECIAL.matcher(safe).matches()) {
            errors.add("A senha deve conter ao menos um caractere especial (!@#$% etc.).");
        }
        if (policy.isBlockSequential() && TEM_SEQUENCIAL.matcher(safe.toLowerCase()).matches()) {
            errors.add("A senha não pode conter sequências óbvias (abc, 123 etc.).");
        }
        if (oldPasswordHash != null && !safe.isEmpty()
                && PasswordHashUtil.verificar(password, oldPasswordHash)) {
            errors.add("A nova senha não pode ser igual à senha atual.");
        }

        return errors.isEmpty()
                ? PasswordValidationResult.success()
                : PasswordValidationResult.failure(errors);
    }
}

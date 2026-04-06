package br.sptrans.scd.shared.helper;

/**
 * Utilitário para sanitização de dados sensíveis em logs de produção.
 * Garante que PII (informações pessoais identificáveis) e credenciais
 * nunca sejam expostos em texto claro nos logs.
 */
public final class LogSanitizer {

    private LogSanitizer() {
    }

    /**
     * Mascara e-mail mantendo apenas o primeiro caractere e o domínio.
     * Exemplo: "roberto@sptrans.com.br" → "r***@sptrans.com.br"
     */
    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "***";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    /**
     * Mascara CPF mantendo apenas os 3 últimos dígitos.
     * Exemplo: "123.456.789-00" → "***.***.***-00" (ou sem pontos: "***00")
     */
    public static String maskCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return "***";
        }
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() < 3) {
            return "***";
        }
        return "***" + digits.substring(digits.length() - 3);
    }

    /**
     * Mascara login/username mantendo apenas os 2 primeiros caracteres.
     * Exemplo: "roberto.cordeiro" → "ro***"
     */
    public static String maskLogin(String login) {
        if (login == null || login.isBlank()) {
            return "***";
        }
        if (login.length() <= 2) {
            return login.charAt(0) + "***";
        }
        return login.substring(0, 2) + "***";
    }

    /**
     * Mascara telefone mantendo apenas os 4 últimos dígitos.
     * Exemplo: "11999887766" → "***7766"
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return "***";
        }
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() <= 4) {
            return "***";
        }
        return "***" + digits.substring(digits.length() - 4);
    }

    /**
     * Mascara documento genérico (RG, CNPJ, etc.) mantendo apenas os 3 últimos caracteres.
     */
    public static String maskDocument(String document) {
        if (document == null || document.isBlank()) {
            return "***";
        }
        if (document.length() <= 3) {
            return "***";
        }
        return "***" + document.substring(document.length() - 3);
    }
}

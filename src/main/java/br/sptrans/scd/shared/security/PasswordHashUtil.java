package br.sptrans.scd.shared.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Utilitário centralizado para detecção e verificação de algoritmos de hash de senha.
 * <p>
 * Algoritmos suportados (detecção baseada no formato do hash armazenado):
 * <ul>
 *   <li><b>BCrypt</b>: hash armazenado começa com {@code $2a$}, {@code $2b$} ou {@code $2y$}</li>
 *   <li><b>SHA-256</b>: hash armazenado possui 64 caracteres hexadecimais</li>
 *   <li><b>MD5</b>: hash armazenado possui 32 caracteres hexadecimais</li>
 * </ul>
 * Senhas novas devem sempre ser armazenadas com BCrypt via {@link #hashBcrypt(String)}.
 */
public final class PasswordHashUtil {

    private static final Pattern PADRAO_BCRYPT   = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    private static final Pattern PADRAO_HEX_SHA256 = Pattern.compile("^[a-fA-F0-9]{64}$");
    private static final Pattern PADRAO_HEX_MD5    = Pattern.compile("^[a-fA-F0-9]{32}$");

    private PasswordHashUtil() {}

    public enum TipoHash {
        BCRYPT, SHA256, MD5, DESCONHECIDO
    }

    /**
     * Detecta o algoritmo de hash com base no formato do hash armazenado no banco.
     */
    public static TipoHash detectarTipoHash(String hashArmazenado) {
        if (hashArmazenado == null || hashArmazenado.isBlank()) {
            return TipoHash.DESCONHECIDO;
        }
        if (PADRAO_BCRYPT.matcher(hashArmazenado).matches()) {
            return TipoHash.BCRYPT;
        }
        if (PADRAO_HEX_SHA256.matcher(hashArmazenado).matches()) {
            return TipoHash.SHA256;
        }
        if (PADRAO_HEX_MD5.matcher(hashArmazenado).matches()) {
            return TipoHash.MD5;
        }
        return TipoHash.DESCONHECIDO;
    }

    /**
     * Verifica se a senha recebida corresponde ao hash armazenado.
     * <p>
     * O tipo de criptografia é detectado automaticamente a partir do hash armazenado:
     * <ul>
     *   <li>BCrypt: usa {@code BCrypt.checkpw} (cliente envia texto plano)</li>
     *   <li>SHA-256: cliente envia hash SHA-256 hex de 64 chars para comparação direta</li>
     *   <li>MD5: cliente envia hash MD5 hex de 32 chars para comparação direta</li>
     * </ul>
     *
     * @return {@code true} se a senha corresponde ao hash armazenado
     */
    public static boolean verificar(String senhaRecebida, String hashArmazenado) {
        if (senhaRecebida == null || hashArmazenado == null) {
            return false;
        }
        TipoHash tipo = detectarTipoHash(hashArmazenado.trim());
        return switch (tipo) {
            case BCRYPT  -> BCrypt.checkpw(senhaRecebida, hashArmazenado.trim());
            case SHA256  -> PADRAO_HEX_SHA256.matcher(senhaRecebida).matches()
                             && senhaRecebida.equalsIgnoreCase(hashArmazenado.trim());
            case MD5     -> PADRAO_HEX_MD5.matcher(senhaRecebida).matches()
                             && senhaRecebida.equalsIgnoreCase(hashArmazenado.trim());
            case DESCONHECIDO -> false;
        };
    }

    /**
     * Gera um hash BCrypt com fator de custo 12 para armazenamento seguro de nova senha.
     *
     * @param senhaPlana senha em texto plano
     * @return hash BCrypt pronto para persistência
     */
    public static String hashBcrypt(String senhaPlana) {
        return BCrypt.hashpw(senhaPlana, BCrypt.gensalt(12));
    }

    /**
     * Gera um hash SHA-256 em hexadecimal para a senha fornecida.
     *
     * @param senhaPlana senha em texto plano
     * @return hash SHA-256 em 64 caracteres hexadecimais minúsculos
     */
    public static String hashSha256(String senhaPlana) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(senhaPlana.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 não disponível na JVM", e);
        }
    }
}

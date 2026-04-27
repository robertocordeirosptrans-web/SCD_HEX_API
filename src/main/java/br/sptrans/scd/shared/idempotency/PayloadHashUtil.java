package br.sptrans.scd.shared.idempotency;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitário para geração de hash SHA-256 do payload de uma requisição.
 *
 * <p>Produz uma string hexadecimal de 64 caracteres que identifica univocamente
 * o conteúdo da requisição. Usado pelo mecanismo de idempotência para detectar
 * payloads divergentes associados à mesma {@code Idempotency-Key}.</p>
 */
public final class PayloadHashUtil {

    private PayloadHashUtil() {
    }

    /**
     * Calcula o SHA-256 do conteúdo informado e retorna a representação hexadecimal.
     *
     * @param content conteúdo a ser hasheado (serialização JSON do payload)
     * @return hash SHA-256 em hexadecimal (64 chars, letras minúsculas)
     */
    public static String hash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 é garantido pela JVM — nunca deve ocorrer
            throw new IllegalStateException("Algoritmo SHA-256 não disponível", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

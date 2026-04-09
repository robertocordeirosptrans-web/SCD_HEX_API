package br.sptrans.scd.shared.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.sptrans.scd.shared.exception.EncryptorException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Criptografia {
 
    private StandardPBEStringEncryptor encryptor;
    
    @Value("${crypto.encryption.password:defaultKey123}") // Mova para variável de ambiente!
    private String encryptionPassword;
    
    @PostConstruct
    public void init() {
        this.encryptor = new StandardPBEStringEncryptor();
        this.encryptor.setPassword(encryptionPassword);
        this.encryptor.setAlgorithm("PBEWithMD5AndDES");
    }

    public String encripta(String texto) throws EncryptorException {
        try {
            return encryptor.encrypt(texto);
        } catch (Exception e) {
            log.error("Erro ao criptografar texto", e);
            throw new EncryptorException("Conversao invalida: " + e.getMessage());
        }
    }

    public String decripta(String texto) throws EncryptorException {
        try {
            return encryptor.decrypt(texto);
        } catch (Exception e) {
            log.error("Erro ao descriptografar texto", e);
            throw new EncryptorException("Conversao invalida: " + e.getMessage());
        }
    }

    /**
     * Método MD5 mantido para compatibilidade com sistemas legados
     * Retorna string no formato "byte1;byte2;byte3;..." como o original
     * @deprecated Use apenas para sistemas legados. Para novas implementações, o recomendavel é usar Argon2 ou BCrypt via Spring Security.
     */
    @Deprecated
    public static String getMD5(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(s.getBytes());
            
            // Mantém o formato original (bytes como string com ponto e vírgula)
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                if (i > 0) result.append(";");
                result.append(digest[i]); // Isso gera números negativos, mas mantém compatibilidade
            }
            return result.toString();
            
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 não disponível", e);
            return "";
        }
    }
    
    /**
     * Alternativa: MD5 em formato hexadecimal (mais comum)
     * Útil se os sistemas legados conseguirem migrar
     */
    public static String getMD5Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(s.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 não disponível", e);
            return "";
        }
    }
}
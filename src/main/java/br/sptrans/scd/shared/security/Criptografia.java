package br.sptrans.scd.shared.security;


import java.security.MessageDigest;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.stereotype.Component;

import br.sptrans.scd.shared.exception.EncryptorException;
import lombok.NoArgsConstructor;




@Component
@NoArgsConstructor
public class Criptografia {

 
    private static StandardPBEStringEncryptor encryptor;

    public static String encripta(String texto) throws EncryptorException {
        try {
            return encryptor.encrypt(texto);
        } catch (Exception e) {
            throw new EncryptorException("Conversao invalida");
        }
    }

    public static String decripta(String texto) throws EncryptorException {
        try {
            return encryptor.decrypt(texto);
        } catch (Exception e) {
            throw new EncryptorException("Conversao invalida");
        }
    }

    // private static String bytesToHex(byte[] hash) {
    //     StringBuffer hexString = new StringBuffer();
    //     for (int i = 0; i < hash.length; i++) {
    //         String hex = Integer.toHexString(0xff & hash[i]);
    //         if (hex.length() == 1) {
    //             hexString.append('0');
    //         }
    //         hexString.append(hex);
    //     }
    //     return hexString.toString();
    // }

    @SuppressWarnings("UseSpecificCatch")
    public static String getMD5(String s) {
        try {
            byte[] bbb = s.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bbb);
            byte[] digest = md.digest();
            s = "";
            for (int i = 0; i < 16; i++) {
                s += String.valueOf(digest[i]) + ";";
            }
            // mudar quando alterar tabela
            // return s.substring(0,15);
            return s;
        } catch (Exception e) {
            return "";
        }
    }

}

package com.jobportal.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import java.util.Base64;

public class UrlSafeEncryptorUtil {
    private static final StandardPBEStringEncryptor encryptor ;

    static {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("password");
        encryptor.setAlgorithm("PBEWithMD5AndDES");
    }

    public static String encrypt(String input) {
        byte[] encryptedBytes = encryptor.encrypt(input).getBytes();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
    }

    public static String decrypt(String input) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(input);
        String encrypted = new String(decodedBytes);
        return encryptor.decrypt(encrypted);
    }


}
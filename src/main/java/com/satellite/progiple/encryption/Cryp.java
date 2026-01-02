package com.satellite.progiple.encryption;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Cryp implements ICoder {
    @Override
    public String encode(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    @Override
    public String decode(String value) {
        byte[] decoded = Base64.getDecoder().decode(value);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}

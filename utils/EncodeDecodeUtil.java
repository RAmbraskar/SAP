package com.ea.utils;

import java.util.Base64;

public class EncodeDecodeUtil {

    private EncodeDecodeUtil() {
    }

    public static String base64Encrypt(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String base64Decrypt(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }

    public static void main(String[] args) {
        System.out.println(base64Encrypt("c0794efcb3dcc5d19205dafb69a4a51b4a005760962b114e6a84450748abcfcd"));
    }
}

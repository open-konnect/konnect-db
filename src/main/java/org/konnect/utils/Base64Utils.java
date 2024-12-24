package org.konnect.utils;

import java.util.Base64;

public class Base64Utils {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    public static String encode(String input) {
        return ENCODER.encodeToString(input.getBytes());
    }

    public static String decode(String input) {
        return new String(DECODER.decode(input));
    }
}

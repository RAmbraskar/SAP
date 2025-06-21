package com.ea.utils;

import org.apache.commons.lang3.RandomStringUtils;
import java.util.UUID;

public class RandomUtil {

    private RandomUtil() {
    }

    public static long generateRandomNumberLong(int length) {
        return Long.parseLong(RandomStringUtils.randomNumeric(length));
    }

    public static String generateUUIDFromHexId() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 20).toUpperCase();
    }
    public static String randomUUID(){
        return UUID.randomUUID().toString();
    }
}

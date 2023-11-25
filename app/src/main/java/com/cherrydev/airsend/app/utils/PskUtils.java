package com.cherrydev.airsend.app.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class PskUtils {
    public static String getRandomPsk() {
        return RandomStringUtils.random(5, true, true);
    }
}

package com.cherrydev.airsend.app.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class AirSendStringUtils {
    public static String getRandomAlphaNumbericString(int length) {
        return RandomStringUtils.random(length, true, true); // todo move to utils/core-lib

    }

}

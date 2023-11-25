package com.cherrydev.airsend.app.utils;

public class PskUtils {
    public static String getRandomPsk(){
        return AirSendStringUtils.getRandomAlphaNumbericString(5);
    }
}

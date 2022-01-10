package com.cherrydev.airsend.core;

public class Constants {
    public enum Protocol {

        TLSv13("TLSv1.3"),
        TLSv12("TLSv1.2");

        private String name;

        Protocol(String name) {
            this.name = name;
        }
    }

    public static final String protocol = Protocol.TLSv12.name;
    public static final String localhostAddress = "127.0.0.1";


    public static final String OPEN = "\u0002\u0003";
    public static final String CLOSE = "\u0004\u0003";
    public static final String EOF = "\u0003";
}

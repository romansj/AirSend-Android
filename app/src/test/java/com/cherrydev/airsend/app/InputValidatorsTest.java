package com.cherrydev.airsend.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cherrydev.airsend.app.utils.InputValidators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class InputValidatorsTest {

    public static Stream<Arguments> provideIPs() {
        return Stream.of(
                Arguments.of("192.168.1.100", true),
                Arguments.of("10.0.2.16", true),
                Arguments.of("127.0.0.1", true),
                Arguments.of("198.51.100.0", true),
                Arguments.of("203.0.113.255", true),
                Arguments.of("a", false),
                Arguments.of("1", false),
                Arguments.of("192.0.2.0", false), // fails
                Arguments.of("0.42.42.42", false), // fails
                Arguments.of("\nt\n", false)
        );
    }

    public static Stream<Arguments> providePorts() {
        return Stream.of(
                Arguments.of("38899", true),
                Arguments.of("65535", true),
                Arguments.of("65536", false),
                Arguments.of("10.0.2.16", false),
                Arguments.of("0", false),
                Arguments.of("2e2", false),
                Arguments.of("e", false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideIPs")
    void validateIP(String IPText, boolean isValid) {
        var validationResult = InputValidators.validateIP(IPText);
        assertEquals(isValid, validationResult);
    }


    @ParameterizedTest
    @MethodSource("providePorts")
    void validatePort(String portText, boolean isValid) {
        var validationResult = InputValidators.validatePort(portText);
        assertEquals(isValid, validationResult);
    }
}
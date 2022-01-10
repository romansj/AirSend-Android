package com.cherrydev.airsend.core.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cherrydev.airsend.app.service.ClientManagerOwnerProperties;
import com.cherrydev.airsend.core.ClientMessage;
import com.cherrydev.airsend.core.Constants;
import com.cherrydev.airsend.core.MessageType;
import com.cherrydev.airsend.core.ReceivedConverter;
import com.google.common.truth.Truth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ClientMessageTest {

    public static Stream<Arguments> provideMessages() {
        return Stream.of(
                Arguments.of(new ClientMessage("ipv4", 0, "Hello!"), "Hello!"), // basic
                Arguments.of(new ClientMessage("ipv4", 0, "Good evening\n!"), "Good evening\n!"), // unintended removal of line breaks
                Arguments.of(new ClientMessage("ipv4", 0, "Good\n\nevening!"), "Good\n\nevening!"), // unintended removal of double line breaks
                Arguments.of(new ClientMessage("ipv4", 0, "āēīū ķļņžčšģ"), "āēīū ķļņžčšģ") // LV
        );
    }

    public static Stream<Arguments> provideMessagesForTerminator() {
        ClientManagerOwnerProperties op = new ClientManagerOwnerProperties(0, "unit", "T");

        return Stream.of(
                // regular message matches protocol - ends with EOF character
                Arguments.of(new ClientMessage("ipv4", 0, "Hello!"), "Hello!" + Constants.EOF),

                // trim should remove empty characters
                Arguments.of(new ClientMessage("ipv4", 0, "Hello!" + Constants.EOF), "Hello!" + Constants.EOF),

                // connection contains params
                Arguments.of(new ClientMessage("ipv4", 0, op.getPort() + "," + op.getClientType() + "," + op.getName(), MessageType.CONNECT), "0,T,unit" + Constants.OPEN),

                // disconnect ends with CLOSE EOF
                Arguments.of(new ClientMessage("ipv4", 0, MessageType.DISCONNECT), Constants.CLOSE),

                // message ends with EOF with full constructor
                Arguments.of(new ClientMessage("ipv4", 0, null, "Test", MessageType.MESSAGE, ""), "Test" + Constants.EOF)
        );
    }

    @ParameterizedTest
    @MethodSource("provideMessages")
    public void messageContentIntegrity(ClientMessage message, String expected) {
        String actual = message.getUserMessage();
        assertEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("provideMessagesForTerminator")
    public void terminatorIntegrity(ClientMessage message, String expected) {
        String actual = message.getMessage();
        assertEquals(expected, actual);
    }


    @Test
    void typeIsConnection() {
        String expected = "" + Constants.OPEN;
        ClientMessage message = new ClientMessage("ipv4", 0, MessageType.CONNECT);
        String actual = message.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void typeIsDisconnection() {
        String expected = "" + Constants.CLOSE;
        ClientMessage message = new ClientMessage("ipv4", 0, MessageType.DISCONNECT);
        String actual = message.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void typeIsMessage() {
        String expected = "Hello!" + Constants.EOF;
        ClientMessage message = new ClientMessage("ipv4", 0, "Hello!");
        String actual = message.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void messageContentFidelityFromString() {
        ClientMessage message = new ClientMessage("ipv4", 0, "Hello!");
        var received = ReceivedConverter.fromReceived(message.getMessage(), "ipv4", 0);

        Truth.assertThat(received.getUserMessage()).isEqualTo(message.getUserMessage());
    }

    @Test
    void messageTypeFidelityFromString() {
        ClientMessage message = new ClientMessage("ipv4", 0, "Hello!");
        var received = ReceivedConverter.fromReceived(message.getMessage(), "ipv4", 0);

        Truth.assertThat(received.getType()).isEqualTo(message.getType());
    }


}
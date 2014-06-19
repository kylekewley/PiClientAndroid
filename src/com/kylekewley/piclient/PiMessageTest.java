package com.kylekewley.piclient;

import com.google.protobuf.GeneratedMessageLite;
import com.kylekewley.piclient.protocolbuffers.ParseErrorProto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PiMessageTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testConstructor() throws Exception{

        PiMessageCallbacks callbacks = new PiMessageCallbacks() {
            @Override
            public void serverReturnedData(byte[] data, PiMessage message) {

            }

            @Override
            public void serverRepliedWithMessage(GeneratedMessageLite response, PiMessage sentMessage) {

            }

            @Override
            public void serverSuccessfullyParsedMessage(PiMessage message) {

            }

            @Override
            public void serverReturnedErrorForMessage(ParseErrorProto.ParseError parseError, PiMessage message) {

            }
        };
    }
}
package com.kylekewley.piclient;

import com.google.protobuf.GeneratedMessageLite;
import com.kylekewley.piclient.protocolbuffers.ParseErrorProto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.debugger.ExceptionCatchMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Stack;

public class PiClientTest implements PiClientCallbacks {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 10002;

    private long sendStartTime;

    private PiClient piClient;



    @Before
    public void setUp() throws Exception {
        piClient = new PiClient("localhost", 10002, this);
    }

    @After
    public void tearDown() throws Exception {
        piClient.close();
    }

    @Test
    public void testMessage() throws Exception {
        byte[] data = new byte[1024 * 512];
        PiMessage message = new PiMessage(0, data);
        message.setMessageCallbacks(new PiMessageCallbacks() {
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
                System.out.println("Server had trouble parsing message");
            }
        });

        sendStartTime = System.currentTimeMillis();
        piClient.sendMessage(message);
        Thread.sleep(5000);
    }




    /*
    PiClientCallbacks
     */


    //By default, we will just log the updates
    @Override
    public void clientConnectedToHost(PiClient piClient) {
        System.out.println("Connected to host");
    }

    @Override
    public void clientTryingConnectionToHost(PiClient piClient) {
        System.out.println("Trying to connect...");
    }

    @Override
    public void clientDisconnectedFromHost(PiClient piClient) {
        System.out.println("Disconnected from host.");
    }

    @Override
    public void clientConnectionTimedOut(PiClient piClient) {
        System.out.println("Connection to host timed out. Try to reconnect.");
    }

    @Override
    public void clientRaisedError(PiClient piClient, @NotNull ClientErrorCode error) {
        System.out.println(error.getErrorMessage());
    }

    @Override
    public void clientRaisedError(PiClient piClient, Exception error) {
        System.out.print("Failed with exception: " + error.getClass().toString() + " : ");
        System.out.println(error.getMessage());
    }

}
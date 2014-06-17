package com.kylekewley.piclient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Stack;

public class PiClientTest implements PiClientCallbacks {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 10002;

    private PiClient piClient;



    @Before
    public void setUp() throws Exception {
        piClient = new PiClient(this);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConnectToPiServer() throws Exception {
        System.out.println("Testing connection");
        piClient.connectToPiServer(DEFAULT_HOST, DEFAULT_PORT);
    }

    @Test
    public void testReconnectToPiServer() throws Exception {
        piClient.reconnectToPiServer();

    }

    @Test
    public void testClose() throws Exception {

    }

    @Test
    public void testSendMessage() throws Exception {

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
import com.kylekewley.piclient.*;
import com.kylekewley.piclient.PiClientCallbacks;

import com.kylekewley.piclient.protocolbuffers.ParseError;
import com.kylekewley.piclient.protocolbuffers.Ping;
import com.squareup.wire.Message;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PiClientTest implements PiClientCallbacks {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 10002;

    private long sendStartTime;

    private PiClient piClient;
    private Thread mainThread;


    @Before
    public void setUp() throws Exception {
        piClient = new PiClient("localhost", 10002, this);
        mainThread = Thread.currentThread();

    }

    @After
    public void tearDown() throws Exception {
        piClient.close();
    }

    @Test
    public void testMessage() throws Exception {

        Ping ping = new Ping.Builder().message("Hello World!").build();
        PiMessage message1 = new PiMessage(1, ping);
        message1.setMessageCallbacks(new PiMessageCallbacks() {
            @Override
            public void serverReturnedData(byte[] data, PiMessage message) {

            }

            @Override
            public void serverRepliedWithMessage(Message response, PiMessage sentMessage) {
                System.out.println("Parsed message 1");
            }

            @Override
            public void serverSuccessfullyParsedMessage(PiMessage message) {

            }

            @Override
            public void serverReturnedErrorForMessage(ParseError parseError, PiMessage message) {

            }
        });
        PiMessage message2 = new PiMessage(1, ping);
        message2.setMessageCallbacks(new PiMessageCallbacks(Ping.class) {
            @Override
            public void serverReturnedData(byte[] data, PiMessage message) {

            }

            @Override
            public void serverRepliedWithMessage(Message response, PiMessage sentMessage) {
                Ping ping = (Ping)response;
                System.out.println(ping.message);

                mainThread.interrupt();
                long sendEndTime = System.currentTimeMillis();
                System.out.println("Server parsed message in " + (sendEndTime - sendStartTime) + " milliseconds.");
                piClient.close();

            }

            @Override
            public void serverSuccessfullyParsedMessage(PiMessage message) {
            }

            @Override
            public void serverReturnedErrorForMessage(ParseError parseError, PiMessage message) {
                System.out.println("Server had trouble parsing message");
            }
        });

        sendStartTime = System.currentTimeMillis();
        piClient.sendMessage(message1);
        piClient.sendMessage(message2);
        while (!Thread.interrupted());
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
        mainThread.interrupt();
    }

    @Override
    public void clientRaisedError(PiClient piClient, @NotNull ClientErrorCode error) {
        System.out.println(error.getErrorMessage());
        mainThread.interrupt();
    }

    @Override
    public void clientRaisedError(PiClient piClient, @NotNull Exception error) {
        System.out.print("Failed with exception: " + error.getClass().toString() + " : ");
        System.out.println(error.getMessage());
        mainThread.interrupt();
    }

}
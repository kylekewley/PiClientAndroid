import com.kylekewley.piclient.*;
import com.kylekewley.piclient.protocolbuffers.ParseError;
import com.kylekewley.piclient.protocolbuffers.Ping;
import com.squareup.wire.Message;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

public class PiMessageTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testBufferedOutputStream() throws Exception {
        final Ping ping = new Ping((int)(System.currentTimeMillis()/1000), "Hello World!");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        final boolean[] messageParsed = {false};

        PiMessage message = new PiMessage(0, ping);
        message.writeToOutputStream(outputStream);
        message.setMessageCallbacks(new PiMessageCallbacks(Ping.class) {
            @Override
            public void serverReturnedData(byte[] data, PiMessage message) {
                Assert.fail("Server should have returned a message");
            }

            @Override
            public void serverRepliedWithMessage(Message response, PiMessage sentMessage) {
                Assert.assertEquals("Sent message didn't equal received message", response, ping);
                messageParsed[0] = true;
                System.out.println("Server replied successfully");
            }

            @Override
            public void serverSuccessfullyParsedMessage(PiMessage message) {
                Assert.fail("Server should have returned a message");
            }

            @Override
            public void serverReturnedErrorForMessage(ParseError parseError, PiMessage message) {
                Assert.fail("Server should have returned a message");
            }
        });

        PiParser p = new PiParser();
        PiServerManager manager = new PiServerManager(p);
        ArrayList<PiMessage> messages = new ArrayList<PiMessage>();
        messages.add(message);

        manager.serverSentMessage(ByteBuffer.wrap(outputStream.toByteArray()), messages);


        Assert.assertTrue("Message wasn't parsed successfully", messageParsed[0]);
    }
}
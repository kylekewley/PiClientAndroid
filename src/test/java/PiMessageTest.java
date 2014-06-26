import com.kylekewley.piclient.PiMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.OutputStream;
import java.net.Socket;

public class PiMessageTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testBufferedOutputStream() throws Exception {
        //50mb data
        byte[] data = new byte[50 * 1024 * 1024];

        Socket s = new Socket("localhost", 10002);
        while (!s.isConnected());

        OutputStream stream = s.getOutputStream();


        PiMessage message = new PiMessage(0, data);
        message.writeToOutputStream(stream);

        stream.close();
        s.close();
    }
}
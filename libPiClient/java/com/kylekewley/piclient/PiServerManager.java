package com.kylekewley.piclient;

import com.google.protobuf.InvalidProtocolBufferException;
import com.kylekewley.piclient.protocolbuffers.ParseErrorProto;
import com.kylekewley.piclient.protocolbuffers.PiHeaderProto;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Kyle Kewley on 6/19/14.
 *
 * This class keeps track of divided messages sent from the server.
 * If a message gets split up into multiple socket read calls, this class
 * will put them all together and call a parse method when we get a full message.
 */
public class PiServerManager {

    /**
     * Keeps track of which part of the message we are reading in.
     */
    enum MessageStatus {
        MESSAGE_STATUS_NONE,
        MESSAGE_STATUS_PARTIAL_HEADER,
        MESSAGE_STATUS_PARTIAL_MESSAGE
    }

    /*
    Instance Variables
     */

    ///Variable to keep track of which part of the message we are reading.
    private MessageStatus messageStatus = MessageStatus.MESSAGE_STATUS_NONE;

    ///Used to store the headerLength after enough data is parsed.
    private int headerLength;

    ///Used for parsing the headerLength prefix
    private ByteBuffer headerLengthBuffer = ByteBuffer.allocate(PiMessage.HEADER_PREFIX_SIZE);

    ///Used to store the PiHeader from the message.
    private PiHeaderProto.PiHeader piHeader;

    ///Used to store the message data after we find out it's length.
    private byte[] messageData;

    ///The number of bytes actually stored for the header
    private int currentHeaderLength;

    ///The number of bytes stored for the actual message
    private int currentMessageLength;

    ///The PiParser that will handle complete messages
    private PiParser piParser;


    /**
     * Initialize the PiServerManager with a PiParser.
     */
    public PiServerManager(PiParser piParser) {
        this.piParser = piParser;
    }

    private PiServerManager() {}


    /**
     * Called when the the socket has data to read.
     *
     * @param message       The message from the socket.
     * @param sentMessages  The list of messages that have been sent to the server.
     *
     * @return  true if the message was parsed successfully, false if there was an error parsing out a header.
     */
    public boolean serverSentMessage(ByteBuffer message, ArrayList<PiMessage> sentMessages) {
        if (messageStatus == MessageStatus.MESSAGE_STATUS_NONE) {
            //Parse the headerLength
            while (headerLengthBuffer.hasRemaining() &&
                    message.hasRemaining()) {
                headerLengthBuffer.put(message.get());
            }

            if (!headerLengthBuffer.hasRemaining()) {
                //Parse out the header
                headerLengthBuffer.flip();
                if (PiMessage.HEADER_PREFIX_SIZE == 1) {
                    //Byte
                    headerLength = headerLengthBuffer.get();
                }else if (PiMessage.HEADER_PREFIX_SIZE == 2) {
                    //Short
                    headerLength = headerLengthBuffer.getShort();
                }else if (PiMessage.HEADER_PREFIX_SIZE == 4) {
                    //Int
                    headerLength = headerLengthBuffer.getInt();
                }

                messageStatus = MessageStatus.MESSAGE_STATUS_PARTIAL_HEADER;
            }
        }

        if (messageStatus == MessageStatus.MESSAGE_STATUS_PARTIAL_HEADER) {
            if (messageData == null) {
                messageData = new byte[headerLength];
                currentHeaderLength = 0;

            }

            //Now merge stuff
            int copyLength = headerLength - currentHeaderLength;

            if (copyLength > message.remaining())
                copyLength = message.remaining();

            message.get(messageData, currentHeaderLength, copyLength);

            currentHeaderLength += copyLength;

            if (currentHeaderLength == headerLength) {
                //Got the full header
                try {
                    piHeader = PiHeaderProto.PiHeader.newBuilder().mergeFrom(messageData).build();
                } catch (InvalidProtocolBufferException e) {
                    return false;
                }
                messageData = null;
                messageStatus = MessageStatus.MESSAGE_STATUS_PARTIAL_MESSAGE;
            }
        }

        if (messageStatus == MessageStatus.MESSAGE_STATUS_PARTIAL_MESSAGE) {
            if (messageData == null) {
                messageData = new byte[piHeader.getMessageLength()];
                currentMessageLength = 0;
            }

            int copyLength = piHeader.getMessageLength() - currentMessageLength;

            if (copyLength > message.remaining())
                copyLength = message.remaining();

            message.get(messageData, currentMessageLength, copyLength);

            currentMessageLength += copyLength;

            if (currentMessageLength == piHeader.getMessageLength()) {
                //Got the full message
                messageStatus = MessageStatus.MESSAGE_STATUS_NONE;

                PiMessage previousMessage = null;
                for (PiMessage piMessage : sentMessages) {
                    if (piMessage.getMessageId() == piHeader.getMessageID()) {
                        previousMessage = piMessage;
                    }
                }

                if (previousMessage != null) {
                    piParser.parseData(messageData, piHeader);
                }else {
                    piParser.parseData(messageData, piHeader, previousMessage);
                }

                headerLengthBuffer = ByteBuffer.allocate(PiMessage.HEADER_PREFIX_SIZE);
                messageData = null;
            }
        }

        return true;
    }
}

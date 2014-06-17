package com.kylekewley.piclient;

import com.google.protobuf.MessageLite;
import com.kylekewley.piclient.protocolbuffers.PiHeaderProto;

import java.io.BufferedOutputStream;

/**
 * Created by Kyle Kewley on 6/14/14.
 *
 * A PiMessage makes a copy of the data it is initialized with. The actual message
 * is immutable, but portions of the header can be modified.
 */

public class PiMessage {

    ///Keeps track of which message IDs we have used
    private static int currentMessageId = 0;

    ///The header for the PiMessage
    private PiHeaderProto.PiHeader.Builder piHeader;

    ///The data that the message will send
    private byte[] messageData;

    ///The number of bytes already sent using a write operation
    private int totalBytesSent;

    ///The class that will handle message errors and server replies
    private PiMessageCallbacks messageCallbacks;




    /*
    Static Methods
     */


    /**
     * @return  A unique message ID
     */
    public static int getUniqueMessageId() {
        return ++currentMessageId;
    }


    /*
    Constructors
     */


    /**
     * Don't implement this constructor. A PiMessage must have some sort of parserID
     * associated with it.
     */
    private PiMessage() {}


    /**
     * Create a new PiMessage with the given parser ID and message.
     *
     * @param parserId  The ID set for the server side parser able to handle the message.
     * @param message   The message that will be sent to the server.
     */
    PiMessage(int parserId, MessageLite message) {

    }


    /**
     * Create a new PiMessage with the given parser ID and binary data.
     *
     * @param parserId  The ID set for the server side parser able to handle the data.
     * @param data      The data to send to the server.
     */
    PiMessage(int parserId, byte[] data) {

    }


    /**
     * Create a new PiMessage with the given parser ID and no data.
     * This will just send the header to the server. This can be used
     * for a parser that doesn't need any data and just needs to be executed.
     *
     * @param parserId  The ID set for the server side parser able to handle the data.
     */
    PiMessage(int parserId) {

    }

    /*
    Public Methods
     */


    /**
     * Write as many bytes as possible from the current position to the outputStream.
     *
     * @param outputStream  The stream to write the data to.
     *
     * @return  The number of bytes written to the stream.
     */
    public int writeToOutputStream(BufferedOutputStream outputStream) {
        //TODO: implement method
        return 0;
    }


    /**
     * Reset the write position. Any write call will now write data
     * from the beginning of the PiMessage.
     */
    public void resetWriteLocation() {

    }

    /**
     * @return  The number of bytes sent.
     */
    public int getTotalBytesSent() {
        return totalBytesSent;
    }

    /**
     * @return  The total number of bytes needed to write the full PiMessage.
     */
    public int serializedSize() {
        return 0;
    }


    /**
     * @param messageCallbacks  The object that will handle message callbacks and server responses.
     */
    public void setMessageCallbacks(PiMessageCallbacks messageCallbacks) {
        this.messageCallbacks = messageCallbacks;
    }

    /**
     * @return  The object that will handle message callbacks and server responses.
     */
    public PiMessageCallbacks getMessageCallbacks() {
        return messageCallbacks;
    }









    /*
    Private Methods
     */
}
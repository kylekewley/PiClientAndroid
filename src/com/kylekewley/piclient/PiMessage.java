package com.kylekewley.piclient;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.kylekewley.piclient.protocolbuffers.PiHeaderProto;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by Kyle Kewley on 6/14/14.
 *
 * A PiMessage makes a copy of the data it is initialized with. The actual message
 * is immutable, but portions of the header can be modified.
 */

public class PiMessage {

    /*
    Useful Constants
     */

    ///The size of the header length prefix in bytes.
    static final int HEADER_PREFIX_SIZE = 2;


    /*
    Member Variables
     */

    ///Keeps track of which message IDs we have used.
    private static int currentMessageId = 0;

    ///The header for the PiMessage
    private PiHeaderProto.PiHeader.Builder piHeader;

    ///The data that the message will send
    private byte[] messageData;

    ///The class that will handle message errors and server replies
    private PiMessageCallbacks messageCallbacks;

    ///The ByteBuffer used to store and send data to the server
    private ByteBuffer byteBuffer;

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
        piHeader = PiHeaderProto.PiHeader.newBuilder();
        piHeader.setParserID(parserId);
        piHeader.setMessageLength(message.getSerializedSize());
        piHeader.setSuccessResponse(true);
        piHeader.setMessageID(getUniqueMessageId());

        messageData = message.toByteArray();
    }

    /**
     * Create a new PiMessage with the given parser ID and binary data.
     *
     * @param parserId  The ID set for the server side parser able to handle the data.
     * @param data      The data to send to the server.
     */
    PiMessage(int parserId, byte[] data) {
        piHeader = PiHeaderProto.PiHeader.newBuilder();
        piHeader.setParserID(parserId);
        piHeader.setMessageLength(data.length);
        piHeader.setSuccessResponse(true);
        piHeader.setMessageID(getUniqueMessageId());

        messageData = data;
    }

    /**
     * Create a new PiMessage with the given parser ID and no data.
     * This will just send the header to the server. This can be used
     * for a parser that doesn't need any data and just needs to be executed.
     *
     * @param parserId  The ID set for the server side parser able to handle the data.
     */
    PiMessage(int parserId) {
        piHeader = PiHeaderProto.PiHeader.newBuilder();
        piHeader.setParserID(parserId);
        piHeader.setMessageLength(0);
        piHeader.setSuccessResponse(true);
        piHeader.setMessageID(getUniqueMessageId());

    }


    /*
    Public Methods
     */


    /**
     * Write the message to the outputStream.
     *
     * @param outputStream  The stream to write the data to.
     *
     */
    public void writeToOutputStream(OutputStream outputStream) throws IOException {
        PiHeaderProto.PiHeader header = piHeader.build();

        //Write the header prefix
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeShort(header.getSerializedSize());

        //Write the header
        header.writeTo(outputStream);

        //Write the data
        outputStream.write(messageData);
    }

    /**
     * This will return the same ByteBuffer through multiple calls.
     * This means that resetByteBuffer() must be called after any changes
     * are made to the PiMessage, or if the ByteBuffer needs to point to the beginning
     * of the data again.
     *
     * @return A ByteBuffer object with the message data or null if there was an error while creating the ByteBuffer
     */
    public ByteBuffer getByteBuffer() {
        if (byteBuffer == null)
            byteBuffer = getByteBufferHelper();
        return byteBuffer;
    }

    /**
     * @return  The total number of bytes needed to write the full PiMessage.
     */
    public int serializedSize() {
        return HEADER_PREFIX_SIZE + piHeader.build().getSerializedSize() + messageData.length;
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

    /**
     * @return A new ByteBuffer with the message data or null if there was an error creating the byte buffer.
     */
    private ByteBuffer getByteBufferHelper() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(serializedSize());
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        PiHeaderProto.PiHeader header = piHeader.build();

        try {
            dataOutputStream.writeShort((short)header.getSerializedSize());
            header.writeTo(dataOutputStream);
            dataOutputStream.write(messageData);



            //Get the ByteBuffer from the outputStream
            ByteBuffer byteBuffer = ByteBuffer.wrap(outputStream.toByteArray());

            dataOutputStream.close();
            outputStream.close();

            return byteBuffer;
        }catch (IOException e) {
            return null;
        }
    }
}
package com.kylekewley.piclient;


import com.kylekewley.piclient.protocolbuffers.PiHeader;
import com.squareup.wire.Message;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

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

    static final long HEADER_FLAG_ERROR = 1L << 0;
    /*
    Member Variables
     */

    ///Keeps track of which message IDs we have used.
    private static int currentMessageId = 0;

    ///The header for the PiMessage
    private PiHeader piHeader;

    ///The data that the message will send
    private byte[] messageData;

    ///The class that will handle message errors and server replies
    private PiMessageCallbacks messageCallbacks;

    ///The ByteBuffer used to store and send data to the server
    @Nullable
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
    public PiMessage(int parserId, @NotNull Message message) {
        piHeader = new PiHeader.Builder()
                .parserID(parserId)
                .messageLength(message.getSerializedSize())
                .successResponse(true)
                .messageID(getUniqueMessageId()).build();

        messageData = message.toByteArray();
    }

    /**
     * Create a new PiMessage with the given parser ID and binary data.
     *
     * @param parserId  The ID set for the server side parser able to handle the data.
     * @param data      The data to send to the server.
     */
    public PiMessage(int parserId, @NotNull byte[] data) {
        piHeader = new PiHeader.Builder()
                .parserID(parserId)
                .messageLength(data.length)
                .successResponse(true)
                .messageID(getUniqueMessageId()).build();

        messageData = data;
    }

    /**
     * Create a new PiMessage with the given parser ID and no data.
     * This will just send the header to the server. This can be used
     * for a parser that doesn't need any data and just needs to be executed.
     *
     * @param parserId  The ID set for the server side parser able to handle the data.
     */
    public PiMessage(int parserId) {
        piHeader = new PiHeader.Builder()
                .parserID(parserId)
                .messageLength(0)
                .successResponse(true)
                .messageID(getUniqueMessageId()).build();
        messageData = new byte[0];
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
    public void writeToOutputStream(@NotNull OutputStream outputStream) throws IOException {

        //Write the header prefix
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeShort(piHeader.getSerializedSize());

        //Write the header
        outputStream.write(piHeader.toByteArray());

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
    @Nullable
    public ByteBuffer getByteBuffer() {
        if (byteBuffer == null)
            byteBuffer = getByteBufferHelper();
        return byteBuffer;
    }


    /**
     * @return  The message ID.
     */
    public int getMessageId() {
        return piHeader.messageID;
    }


    /**
     * @return  The total number of bytes needed to write the full PiMessage.
     */
    public int serializedSize() {

        return HEADER_PREFIX_SIZE + piHeader.getSerializedSize() + messageData.length;
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
    @Nullable
    private ByteBuffer getByteBufferHelper() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(serializedSize());
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        try {
            //Write the header length prefix
            dataOutputStream.writeShort((short) piHeader.getSerializedSize());

            //Write the header
            dataOutputStream.write(piHeader.toByteArray());

            //Write the message
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PiMessage) {
            PiMessage message = (PiMessage)obj;

            return this.piHeader.equals(message.piHeader) && Arrays.equals(this.messageData, message.messageData);
        }
        return false;
    }
}
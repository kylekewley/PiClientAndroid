package com.kylekewley.piclient;

import com.google.protobuf.InvalidProtocolBufferException;
import com.kylekewley.piclient.protocolbuffers.ParseErrorProto;
import com.kylekewley.piclient.protocolbuffers.PiHeaderProto;

/**
 * Created by Kyle Kewley on 6/23/14.
 */
public class PiParser {

    /**
     * Parse a full message with the given piHeader that is not replying to any sent message.
     *
     * @param messageData   The messageData to parse.
     * @param piHeader      The piHeader from the data.
     */
    public void parseData(byte[] messageData, PiHeaderProto.PiHeader piHeader) {

    }

    /**
     * Parse a full message with the given PiHeader that is a reply to the previousMessage.
     *
     * @param messageData       The messageData to parse.
     * @param piHeader          The piHeader from the data.
     * @param previousMessage   The message the data is replying to.
     */
    public void parseData(byte[] messageData, PiHeaderProto.PiHeader piHeader, PiMessage previousMessage) {
        com.google.protobuf.GeneratedMessageLite.Builder builder = previousMessage.getMessageCallbacks().getBuilder();

        if (piHeader.getMessageLength() == 0) {
            //Just a header reply
            previousMessage.getMessageCallbacks().serverSuccessfullyParsedMessage(previousMessage);
        }else {
            //There is data with it
            if ((piHeader.getFlags() & PiMessage.HEADER_FLAG_ERROR) != 0) {
                try {
                    ParseErrorProto.ParseError error = ParseErrorProto.ParseError.newBuilder().mergeFrom(messageData).build();
                    previousMessage.getMessageCallbacks().serverReturnedErrorForMessage(error, previousMessage);
                } catch (InvalidProtocolBufferException e) {
                    //Parser didn't work, just reply with the data
                    previousMessage.getMessageCallbacks().serverReturnedData(messageData, previousMessage);
                }
            } else if (builder != null) {
                try {
                    builder.clear();
                    builder.mergeFrom(messageData);

                    previousMessage.getMessageCallbacks().serverRepliedWithMessage(builder.build(), previousMessage);
                } catch (InvalidProtocolBufferException e) {
                    //Parser didn't work, just reply with the data
                    previousMessage.getMessageCallbacks().serverReturnedData(messageData, previousMessage);
                }
            }else {
                //Just reply with the data
                previousMessage.getMessageCallbacks().serverReturnedData(messageData, previousMessage);
            }
        }

    }

}

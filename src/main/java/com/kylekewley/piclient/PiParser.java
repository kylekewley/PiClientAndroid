package com.kylekewley.piclient;

import com.google.protobuf.InvalidProtocolBufferException;
import com.kylekewley.piclient.CustomParserWrapper;
import com.kylekewley.piclient.protocolbuffers.ParseErrorProto;
import com.kylekewley.piclient.protocolbuffers.PiHeaderProto;

import java.util.TreeSet;
/**
 * Created by Kyle Kewley on 6/23/14.
 */
public class PiParser {

    ///Set of all registered parsers
    private TreeSet<CustomParserWrapper> parsers = new TreeSet<CustomParserWrapper>();


    /**
     * Parse a full message with the given piHeader that is not replying to any sent message.
     *
     * @param messageData   The messageData to parse.
     * @param piHeader      The piHeader from the data.
     */
    public void parseData(byte[] messageData, PiHeaderProto.PiHeader piHeader) {
        CustomParserWrapper tmpWrapper = new CustomParserWrapper(piHeader.getParserID());

        CustomParserWrapper parserWrapper = findParserWrapper(tmpWrapper);

        if (parserWrapper != null) {
            try {
                parserWrapper.getParser().parse(messageData);
            }catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
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


    /**
     *Register a custom parser for a given parserID range.
     *All incoming messages with a PiHeader.parserID in the given range
     *will be passed along to the given custom parser to parse.
     *No intersecting ranges are allowed.
     *
     * @param customParser  The parser that will be registered.
     *
     *@return   true if the range is unique, false otherwise
     */
    public boolean registerParserForId(CustomParserWrapper customParser) {
        if (customParser.getParser() == null)
            return false;

        return parsers.add(customParser);
    }


    /**
     * Iterates through the parsers set looking for a parser that is equal to parserWrapper.
     *
     * @param parserWrapper     The CustomParserWrapper to search for.
     * @return  The equal CustomParserWrapper from the parsers set, or null if not found.
     */
    private CustomParserWrapper findParserWrapper(CustomParserWrapper parserWrapper) {
        if (parsers == null)
            return null;

        for (CustomParserWrapper compareWrapper : parsers) {
            if (compareWrapper.equals(parserWrapper))
                return compareWrapper;
        }

        return null;
    }

}

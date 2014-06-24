package com.kylekewley.piclient;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

/**
 * Created by Kyle Kewley on 6/24/14.
 */
public abstract class CustomBufferParser implements CustomParser {
    private MessageLite.Builder parserBuilder;

    public CustomBufferParser() {
        parserBuilder = getParserBuilder();
    }

    abstract void parse(com.google.protobuf.MessageLite message);

    abstract MessageLite.Builder getParserBuilder();

    @Override
    public void parse(byte[] data) {
        parserBuilder.clear();
        
        try {
            parserBuilder.mergeFrom(data);
            parse(parserBuilder.build());
        }catch (InvalidProtocolBufferException e) {
            System.err.println(e.getMessage());
        }

    }
}

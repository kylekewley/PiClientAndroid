package com.kylekewley.piclient;

import com.squareup.wire.Message;
import com.squareup.wire.Wire;

import java.io.IOException;

/**
 * Created by Kyle Kewley on 6/24/14.
 */
public abstract class CustomBufferParser<T extends Message> implements CustomParser {
    private Class<T> messageClass;

    public CustomBufferParser(Class<T> messageClass) {
        this.messageClass = messageClass;
    }

    public abstract void parse(T message);

    @Override
    public void parse(byte[] data) {

        Wire wire = MessageWire.getInstance();
        try {
            parse(wire.parseFrom(data, messageClass));
        }catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }
}

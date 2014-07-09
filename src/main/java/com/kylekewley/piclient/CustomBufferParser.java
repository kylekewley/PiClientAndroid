package com.kylekewley.piclient;

import com.squareup.wire.Message;
import com.squareup.wire.Wire;

import java.io.IOException;

/**
 * Created by Kyle Kewley on 6/24/14.
 */
public abstract class CustomBufferParser<T extends Message> implements CustomParser {
    private T message;

    public CustomBufferParser() {}

    public abstract void parse(T message);

    @Override
    public void parse(byte[] data) {

        Wire wire = MessageWire.getInstance();
        try {
            message = (T)wire.parseFrom(data, message.getClass());

            parse(message);
        }catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }
}

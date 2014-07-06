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

    abstract void parse(Message message);

    @Override
    public void parse(byte[] data) {
        System.out.println("Class of T: " + message.getClass());
        Wire wire = new Wire();
        try {
            message = (T)MessageWire.getInstance().parseFrom(data, message.getClass());

            parse(message);
        }catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }
}

package com.kylekewley.piclient;

import com.squareup.wire.Wire;

/**
 * Created by kylekewley on 7/5/14.
 */
public class MessageWire {
    private static Wire ourInstance = new Wire();

    public static Wire getInstance() {
        return ourInstance;
    }

    private MessageWire() {
    }
}

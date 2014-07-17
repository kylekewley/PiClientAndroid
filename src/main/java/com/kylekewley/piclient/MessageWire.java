package com.kylekewley.piclient;

import com.squareup.wire.Wire;

import org.jetbrains.annotations.NotNull;

/**
 * Created by kylekewley on 7/5/14.
 */
public class MessageWire {
    private static final Wire ourInstance = new Wire();

    @NotNull
    public static Wire getInstance() {
        return ourInstance;
    }

    private MessageWire() {
    }
}

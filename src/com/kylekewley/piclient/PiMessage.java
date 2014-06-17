package com.kylekewley.piclient;

/**
 * Created by Kyle Kewley on 6/14/14.
 */

public class PiMessage {

    ///Keeps track of which message IDs we have used
    private static int currentMessageId = 0;

    /*
    Static Methods
     */


    /**
     * @return  A unique message ID
     */
    public static int getUniqueMessageId() {
        return ++currentMessageId;
    }
}
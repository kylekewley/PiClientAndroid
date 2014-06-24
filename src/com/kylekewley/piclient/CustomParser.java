package com.kylekewley.piclient;

/**
 * Created by Kyle Kewley on 6/24/14.
 */
public interface CustomParser {

    /**
     * Parse the given input data
     *
     * @param data  The data to parse.
     */
    public void parse(byte[] data);
}

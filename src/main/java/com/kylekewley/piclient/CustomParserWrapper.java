package com.kylekewley.piclient;

/**
 * Created by Kyle Kewley on 6/24/14.
 */
public class CustomParserWrapper implements Comparable{

    ///The parser to register
    private CustomParser parser;

    ///The start value for the parserID
    private final int startValue;

    ///The end value for the parserID
    private final int endValue;

    /**
     * Create a CustomParserWrapper with the given parser, startParserID and endParserID.
     *
     * @param parser        The parser to use for messages in the given parserID range.
     * @param startValue    The start value for the parserID.
     * @param endValue      The end value for the parserID.
     */
    CustomParserWrapper(CustomParser parser, int startValue, int endValue) {
        this.parser = parser;
        this.startValue = startValue;
        this.endValue = endValue;
    }

    /**
     * Create a CustomParserWrapper with a single parseID.
     *
     * @param parser    The parser to use for messages in the given parserID.
     * @param parseId   The ID that tells which incoming messages the parser can handle.
     */
    CustomParserWrapper(CustomParser parser, int parseId) {
        this.parser = parser;
        this.startValue = parseId;
        this.endValue = parseId;
    }

    /**
     *Creates a dummy parser wrapper with a single ID.
     *This will not be allowed to be registered with the PiParser because there is nothing
     *to parse the data. It is used for searching for a custom parser.
     *
     * @param parseId   The single ID value that will be used in the search.
     */
    CustomParserWrapper(int parseId) {
        this.startValue = parseId;
        this.endValue = parseId;
    }

    /*
    Getter methods
     */

    public CustomParser getParser() {
        return parser;
    }

    public int getStartValue() {
        return startValue;
    }

    public int getEndValue() {
        return endValue;
    }

    /*
    Comparing CustomParserWrappers
     */


    @Override
    public boolean equals(Object obj) {
        return compareTo(obj) == 0;
    }


    @Override
    public int compareTo(Object o) {
        if (o instanceof CustomParserWrapper) {
            CustomParserWrapper tmp = (CustomParserWrapper)o;

            if (endValue < tmp.startValue)
                return -1;
            else if (startValue > tmp.endValue)
                return 1;
            else
                return 0;
        }

        return 1;
    }
}

package com.kylekewley.piclient;

import com.kylekewley.piclient.protocolbuffers.ParseError;
import com.squareup.wire.Message;

/**
 * Created by Kyle Kewley on 6/17/14.
 *
 * This is an abstract class used to define custom callbacks for a PiMessage.
 * If the server replies to a PiMessage, the response will be sent to one of the
 * methods from its PiMessageCallbacks object. The builder variable will be used
 * to convert the response data to a protocol buffer message. 
 */
public abstract class PiMessageCallbacks {

    private Class<? extends Message> messageClass;


    /**
     * The default constructor. All data sent back from the server will be passed to the serverReturnedData method.
     *
     */
    public PiMessageCallbacks() {

    }


    /**
     * Initialize the PiMessageCallbacks object with the given builder.
     * A null builder will just send a byte array back.
     *
     * @param messageClass The messageClass used to parse the server reply.
     */
    public PiMessageCallbacks(Class<? extends Message> messageClass) {
        this.messageClass = messageClass;
    }


    /**
     * @param messageClass The messageClass used to parse the server reply.
     */
    public void setMessageClass(Class<? extends Message> messageClass) {
        this.messageClass = messageClass;
    }

    /**
     * @return  The builder used to parse the server reply, or null.
     */
    public Class<? extends Message> getMessageClass() {
        return messageClass;
    }

    /**
     * Called if this.builder is null and the server sends a reply message.
     *
     * @param data      The binary data from the server.
     * @param message   The PiMessage object that is being replied to.
     */
    public abstract void serverReturnedData(byte[] data, PiMessage message);


    /**
     * Called if this.buffer is not null and we are able to parse a reply from the server.
     *
     * @param response      The message that the server replied with.
     * @param sentMessage   The PiMessage object that is being replied to.
     */
    public abstract void serverRepliedWithMessage(Message response, PiMessage sentMessage);


    /**
     * Called if the server sends back an empty reply message.
     *
     * @param message   The PiMessage object that is being replied to.
     */
    public abstract void serverSuccessfullyParsedMessage(PiMessage message);


    /**
     * Called if the server sends an error message in response to the send PiMessage.
     *
     * @param parseError    The error message sent from the server.
     * @param message       The PiMessage object that is being replied to.
     */
    public abstract void serverReturnedErrorForMessage(ParseError parseError, PiMessage message);
}

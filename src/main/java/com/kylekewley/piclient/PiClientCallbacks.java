package com.kylekewley.piclient;

/**
 * Created by Kyle Kewley on 6/13/14.
 */
public interface PiClientCallbacks {
    enum ClientErrorCode {
        DISCONNECTED_CLIENT ("Error: The client is disconnected and unable to send data."),
        INVALID_PORT ("Error: The port number must be between 0 and 65535."),
        INVALID_HOSTNAME ("Error: The PiClient was unable to resolve the hostname"),
        SECURITY_EXCEPTION ("Error: The PiClient was unable to resolve the hostname because security manager is present and permission to resolve the host name is denied."),
        UNKNOWN_CONNECTION_ERROR ("Error: An unknown problem occurred while trying to connect. Please check your hostname and IP address."),
        CONNECTION_TIMEOUT ("Error: The connection timed out while trying to connect to the PiServer."),
        CONNECTION_REFUSED ("Error: Unable to connect. Please check that the server is running on the specified port and IP address."),
        UNABLE_TO_SEND_MESSAGE ("Error: The PiClient was unable to send the message."),
        SOCKET_CONFIGURATION_ERROR ("Error: The socket is not configured properly and cannot connect to the server."),
        UNABLE_TO_READ_MESSAGE ("Error: Unable to read the message from the server.");

        private final String errorMessage;

        ClientErrorCode(String errorMessage) {
            this.errorMessage= errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

    }


    /**
     * This method is called when the client successfully connects to the PiServer host.
     *
     * @param piClient  The client that made the successful connection
     */
    void clientConnectedToHost(PiClient piClient);

    /**
     * Called when the client attempts to connect to the PiServer.
     *
     * @param piClient  The client trying to make the connection.
     */
    void clientTryingConnectionToHost(PiClient piClient);

    /**
     * Called when the client successfully disconnects from the host. The PiClient
     * object can now be safely destroyed.
     *
     * @param piClient  The now disconnected PiClient object
     */
    void clientDisconnectedFromHost(PiClient piClient);

    /**
     * Called if the piClient is unable to connect to the host in the allotted timeout.
     * The piClient is no longer trying to make a connection to the host.
     *
     * @param piClient  The client that was unable to make a connection.
     */
    void clientConnectionTimedOut(PiClient piClient);

    /**
     * Called if there is an error sent by the server. As of now, this method is not being
     * used because parsing errors are sent back and handled by the PiMessage object that caused them.
     *
     * @param piClient      The client that raised the error.
     * @param error         The error code associated with the error.
     */
    void clientRaisedError(PiClient piClient, ClientErrorCode error);


    /**
     * Called if there is an exception raised where we don't know how to deal with it.
     * This is more of a debugging tool and will only be called for exceptions that I don't understand.
     * Hopefully after testing, I won't have to use this callback.
     *
     * @param piClient      The client that raised the error.
     * @param error         The Exception that was raised.
     */
    void clientRaisedError(PiClient piClient, Exception error);
}

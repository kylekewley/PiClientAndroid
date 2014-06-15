package com.kylekewley.piserver;

import com.kylekewley.piserver.PiClient;

import java.net.InetAddress;

/**
 * Created by Kyle Kewley on 6/13/14.
 */
public interface PiClientCallbacks {
    enum ClientErrorCode {
        DISCONNECTED_CLIENT
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
     * @param piClient      The client that raised the error
     * @param errorNumber   The error number associated with the error
     * @param errorMessage  The human readable error message
     */
    void clientRaisedError(PiClient piClient, ClientErrorCode errorNumber, String errorMessage);
}

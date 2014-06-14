package com.kylekewley.piserver;

import com.kylekewley.piserver.PiClient;

import java.net.InetAddress;

/**
 * Created by Kyle Kewley on 6/13/14.
 */
public interface PiClientCallbacks {
    void clientConnectedToHost(PiClient piClient);
    void clientTryingConnectionToHost(PiClient piClient);
    void clientDisconnectedFromHost(PiClient piClient);
    void clientConnectionTimedOut(PiClient piClient);
    void clientRaised(PiClient piClient, String errorMessage);
}

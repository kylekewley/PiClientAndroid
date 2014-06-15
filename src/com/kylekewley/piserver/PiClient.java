package com.kylekewley.piserver;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Kyle Kewley on 6/11/14.
 *
 * The PiClient class is used to send and receive data from a PiServer on a remote machine.
 *
 * This class is designed to be used with a user interface. Rather than throwing exceptions,
 * the class sends error messages and connection status updates to the clientCallbacks object.
 * This will make it easier for an app using this class to prompt the user on exceptional
 * circumstances rather than forcing the user of this class to surround everything with try catch blocks.
 *
 */
public class PiClient {

    /*
    Class Constants
     */

    ///The default timeout for connecting to a socket in milliseconds
    private static final int DEFAULT_TIMEOUT = 5000;

    /*
    Class Data Members
     */

    ///The socket that will be used to connect to the PiServer.
    private Socket socket;

    ///The class that gets the client status updates and error messages.
    private PiClientCallbacks clientCallbacks;

    ///The timeout (in milliseconds) used to try connecting the socket.
    ///A value of zero is equal to no timeout.
    private int connectionTimeout = DEFAULT_TIMEOUT;


    /*
    Class Constructors
     */


    /**
     * Creates an unconnected PiClient. The socket must be connected later.
     * using the connectToServer() method.
     */
    PiClient() {
        //TODO: implement constructor
    }

    /**
     * Creates an unconnected PiClient with the given object to handle callback functions.
     *
     * @param clientCallbacks   The object that will handle error messages and status updates.
     */
    PiClient(PiClientCallbacks clientCallbacks) {
        //TODO: implement constructor
    }

    /**
     * Creates a PiClient and connects it to the specified IP address on the given port.
     *
     * @param address           The IP address.
     * @param port              The port number.
     * @param clientCallbacks   The object that will handle error messages and status updates.
     */
    PiClient(InetAddress address, int port, PiClientCallbacks clientCallbacks) {
        //TODO: implement constructor
    }

    /**
     * Creates a PiClient and connects it to the host on the given port.
     *
     * @param hostName          The name of the host.
     * @param port              The port number.
     * @param clientCallbacks   The object that will handle error messages and status updates.
     */
    PiClient(String hostName, int port, PiClientCallbacks clientCallbacks) {
        //TODO: implement constructor
    }


    /*
    Getter and Setter Methods
     */


    /**
     * @return  The PiClientCallbacks object for the PiClient,
     * or null if the PiClientCallbacks object was not set.
     */
    public PiClientCallbacks getClientCallbacks() {
        return clientCallbacks;
    }

    /**
     * @param clientCallbacks   The object that will handle error messages and status updates.
     */
    public void setClientCallbacks(PiClientCallbacks clientCallbacks) {
        this.clientCallbacks = clientCallbacks;
    }


    /**
     * @return  The number of milliseconds until the client times out while trying to connect.
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @param connectionTimeout The number of milliseconds until the client times out while trying to connect.
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }


    /**
     * @return  true if the PiClient is currently connected to the server.
     */
    public boolean isConnected() {
        //TODO: Test method
        if (socket == null)
            return false;
        if (socket.isClosed())
            return false;
        return socket.isConnected();
    }


    /*
    Public Class Methods
     */

    /*
        Connecting and Disconnecting
     */


    /**
     * Connects the PiClient to the specified host on the given port.
     * If the PiClient is already connected to a PiServer, the PiClient will
     * disconnect from the server and connect to the new host.
     *
     * @param hostName  The IP address.
     * @param port      The port number.
     */
    public void connectToPiServer(String hostName, int port) {
        //TODO: implement method
    }

    /**
     * Connects the PiClient to the specified IP address on the given port.
     * If the PiClient is already connected to a PiServer, the PiClient will
     * disconnect from the server and connect to the new host.
     * @param host      The name of the host.
     * @param port      The port number.
     */
    public void connectToPiServer(InetAddress host, int port) {
        //TODO: implement method
    }


    /**
     * Disconnect and connect back to the host the PiClient was previously connected to.
     */
    public void reconnectToPiServer() {
        //TODO: implement method
    }


    /**
     * Closes the connection to the PiServer. The PiClient can be reconnected
     * by calling any of the connectToPiServer methods.
     */
    public void close() {
        //TODO: implement method
    }


    /*
        Sending Data
     */


    /**
     * Sends the PiMessage object to the PiServer.
     * If the PiClient is not connected, the DISCONNECTED_CLIENT error
     * will be sent to the clientCallbacks object.
     * @param message
     */
    public void sendMessage(PiMessage message) {
        //TODO: implement method
    }


    /**
     * Adds the PiClient to the group on the PiServer.
     * If the PiClient is not connected, the DISCONNECTED_CLIENT error
     * will be sent to the clientCallbacks object.
     *
     * @param groupName The group name to add the client to.
     */
    public void addToGroup(String groupName) {
        //TODO: implement method
    }

    /**
     * Removes the PiClient from the group on the PiServer.
     * If the PiClient is not connected, the DISCONNECTED_CLIENT error
     * will be sent to the clientCallbacks object.
     *
     * @param groupName The group name to remove the client from.
     */
    public void removeFromGroup(String groupName) {
        //TODO: implement method
    }


    /*
    Private Methods
     */


    /**
     * Adds the PiClient to the group on the PiServer.
     *
     * @param groupName The group name to add the client to.
     */
    private void addToGroupHelper(String groupName) {
        //TODO: implement method
    }

    /**
     * Removes the PiClient from the group on the PiServer.
     *
     * @param groupName The group name to remove the client from.
     */
    private void removeFromGroupHelper(String groupName) {
        //TODO: implement method
    }

}

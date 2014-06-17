package com.kylekewley.piclient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

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
 * I want to make this class as easy to use as possible, so all method calls defined in the PiClient class
 * will be non-blocking.
 */
public class PiClient implements PiClientCallbacks {

    /*
    Class Constants
     */

    ///The default timeout for connecting to a socket in milliseconds
    private static final int DEFAULT_TIMEOUT = 5000;

    ///The highest number a port can connect to
    private static final long MAX_PORT = 65535;

    ///The default timeout for joining a thread in milliseconds
    private static final int DEFAULT_THREAD_TIMEOUT = 1000;

    /*
    Class Data Members
     */

    ///The class that gets the client status updates and error messages.
    private PiClientCallbacks clientCallbacks = this;

    ///The timeout (in milliseconds) used to try connecting the socket.
    ///A value of zero is equal to no timeout.
    private int connectionTimeout = DEFAULT_TIMEOUT;


    ///The PiClientHelper running on a secondary thread.
    @Nullable
    private PiClientHelper clientHelper;

    ///The Thread object that is running the PiClientHelper.
    @Nullable
    private Thread clientHelperThread;


    ///The name of the host or IP address to connect to.
    private String hostName;

    ///The port number to connect to on the remote host.
    private int port;


    /*
    Class Constructors
     */


    /**
     * Creates an unconnected PiClient. The socket must be connected later.
     * using the connectToServer() method.
     */
    PiClient() {

    }

    /**
     * Creates an unconnected PiClient with the given object to handle callback functions.
     *
     * @param clientCallbacks   The object that will handle error messages and status updates.
     */
    PiClient(PiClientCallbacks clientCallbacks) {
        this.clientCallbacks = clientCallbacks;
    }


    /**
     * Creates a PiClient and connects it to the host on the given port.
     *
     * @param hostName          The name of the host.
     * @param port              The port number.
     * @param clientCallbacks   The object that will handle error messages and status updates.
     */
    PiClient(String hostName, int port, PiClientCallbacks clientCallbacks) {
        this.hostName = hostName;
        this.port = port;
        this.clientCallbacks = clientCallbacks;

        connectToPiServer(hostName, port);
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
        if (clientHelper == null)
            return false;
        return clientHelper.isConnected();
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
     * If the port is less than zero or greater than 65535, ClientErrorCode.INVALID_PORT
     * is sent to the clientCallbacks.
     *
     * If the hostName is null or the hostName can't be resolved, ClientErrorCode.INVALID_HOSTNAME
     * is sent to the clientCallbacks.
     *
     * If a security manager is present and permission to resolve the host name is denied,
     * ClientErrorCode.SECURITY_EXCEPTION is sent to clientCallbacks.
     *
     * If there is any other error while trying to create the InetSocketAddress,
     * ClientErrorCode.UNKNOWN_CONNECTION_ERROR is sent to the clientCallbacks.
     *
     *
     * @param hostName  The IP address.
     * @param port      The port number.
     */
    public void connectToPiServer(@Nullable String hostName, int port) {

        //Make sure the port is valid
        if (port <= 0 || port > MAX_PORT) {
            //Invalid port
            clientCallbacks.clientRaisedError(this, ClientErrorCode.INVALID_PORT);

            return;
        }
        //Make sure the host is not null
        if (hostName == null) {
            clientCallbacks.clientRaisedError(this, ClientErrorCode.INVALID_HOSTNAME);

            return;
        }

        //Data is fine as far as we can tell. Create and run the new thread.
        clientHelper = new PiClientHelper(hostName, port);
        clientHelperThread = new Thread(clientHelper);
        clientHelperThread.start();

    }


    /**
     * Disconnect and connect back to the host the PiClient was previously connected to.
     */
    public void reconnectToPiServer() {
        close();
        connectToPiServer(this.hostName, this.port);
    }


    /**
     * Closes the connection to the PiServer. The PiClient can be reconnected
     * by calling any of the connectToPiServer methods.
     */
    public void close() {
        if (clientHelper != null && clientHelper.isConnected()) {
            clientHelper.close();

            try {
                clientHelperThread.join(DEFAULT_THREAD_TIMEOUT);
            }catch (Exception e) {
                System.out.println("Exception while joining client helper thread: " + e.getMessage());
            }

        }
    }


    /*
        Sending Data
     */


    /**
     * Sends the PiMessage object to the PiServer.
     * If the PiClient is not connected, the DISCONNECTED_CLIENT error
     * will be sent to the clientCallbacks object.
     *
     * @param message   The message to send to the server.
     */
    public void sendMessage(PiMessage message) {
        clientHelper.sendMessage(message);
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


    /*
    PiClientCallbacks Methods
     */

    //By default, we will just log the updates
    @Override
    public void clientConnectedToHost(PiClient piClient) {
        if (clientHelper != null)
            System.out.println("Connected to host: " + clientHelper.getHostName() +
                    " on port: " + clientHelper.getPort());
    }

    @Override
    public void clientTryingConnectionToHost(PiClient piClient) {
        System.out.println("Trying to connect...");
    }

    @Override
    public void clientDisconnectedFromHost(PiClient piClient) {
        System.out.println("Disconnected from host.");
    }

    @Override
    public void clientConnectionTimedOut(PiClient piClient) {
        System.out.println("Connection to host timed out. Try to reconnect.");
    }

    @Override
    public void clientRaisedError(PiClient piClient, @NotNull ClientErrorCode error) {
        System.out.println(error.getErrorMessage());
    }

    @Override
    public void clientRaisedError(PiClient piClient, @NotNull Exception error) {
        System.out.print(error.getMessage());
    }

    /*
    Inner Class Helper
     */

    private class PiClientHelper implements Runnable {

        ///The socket that will be used to connect to the PiServer.
        private Socket socket;

        ///The queue of messages for the PiClient to send to the server
        @NotNull
        private ConcurrentLinkedQueue<PiMessage> messageQueue = new ConcurrentLinkedQueue<PiMessage>();

        ///The list that holds sent messages waiting for a reply
        @NotNull
        private ArrayList<PiMessage> sentMessages = new ArrayList<PiMessage>();

        /*
        Constructors
         */

        /**
         * Create a new PiClientHelper that will connect to the given port and hostName
         *
         * @param hostName      The IP address or host name to connect to.
         * @param port          The port number.
         */
        PiClientHelper(String hostName, int port) {
            PiClient.this.port = port;
            PiClient.this.hostName = hostName;
        }


        /*
        Overridden Methods
         */
        @Override
        public void run() {
            //We have a valid hostName and port as far as we can tell
            boolean connected = connectToPiServer(PiClient.this.hostName, PiClient.this.port);

            if (connected) {
                //Send and receive data
                waitForData();
            }

        }


        /*
        Public Methods.
         */


        /*
        Getters and Setters
         */


        /**
         * @return  The host name string, or null if not set.
         */
        public String getHostName() {
            return hostName;
        }

        /**
         * @return  The port number, or zero if not set.
         */
        public int getPort() {
            return port;
        }


        /**
         * @return  true if the PiClientHelper is currently connected to a PiServer
         */
        public boolean isConnected() {
            if (socket == null || socket.isClosed())
                return false;

            return socket.isConnected();
        }


        /*
        Sending Messages
         */


        public void sendMessage(PiMessage message) {
            if (clientHelperThread.isInterrupted() || !isConnected()) {
                //We are closed
                clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.DISCONNECTED_CLIENT);
                return;
            }

            messageQueue.add(message);
        }


        /*
        Private Methods
         */


        /**
         * Adds the PiClient to the group on the PiServer.
         *
         * @param groupName The group name to add the client to.
         */
        private void addToGroup(String groupName) {
            //TODO: implement method
        }

        /**
         * Removes the PiClient from the group on the PiServer.
         *
         * @param groupName The group name to remove the client from.
         */
        private void removeFromGroup(String groupName) {
            //TODO: implement method
        }


        /**
         * Connects to the PiServer on the given host and port. This method blocks the thread
         *
         * @param hostName  The The IP address or host name to connect to.
         * @param port      The port number.
         *
         * @return  true if the connection was made, false if there was an error
         */
        private boolean connectToPiServer(String hostName, int port) {
            InetSocketAddress address = resolveSocketAddress(hostName, port);

            if (address == null) {
                //Error resolving the host, nothing else to do.
                return false;
            }

            //address is not null, connect a socket.
            socket = new Socket();

            //Now connect it with the specified timeout
            try {
                socket.connect(address, connectionTimeout);

                //We made the connection.
                clientCallbacks.clientConnectedToHost(PiClient.this);
                return true;

            } catch (SocketTimeoutException e) {
                clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.CONNECTION_TIMEOUT);
            } catch (ConnectException e) {
                clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.CONNECTION_REFUSED);
            } catch (Exception e) {

                //Catch IOException, IllegalBlockingModeException, and IllegalArgumentException because
                //I don't know exactly what they mean. I expect that IllegalArgumentException will never get called
                //because the address should be valid by now.
                clientCallbacks.clientRaisedError(PiClient.this, e);
            }

            return false;
        }


        /**
         * Generates an InetSocketAddress from the hostName and port.
         * If the method returns null, the clientCallbacks object will already
         * have been notified of the specific error.
         *
         * @param hostName  The IP address or host name to connect to.
         * @param port      The port number.
         *
         * @return      A resolved InetSocketAddress, or null if there was a problem creating the socket address.
         */
        private InetSocketAddress resolveSocketAddress(String hostName, int port) {
            try {
                InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);

                //Check if the hostname was resolved
                if (socketAddress.isUnresolved()) {
                    //Couldn't find the host
                    clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.INVALID_HOSTNAME);
                }else {
                    return socketAddress;
                }

            } catch (SecurityException e) {
                clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.SECURITY_EXCEPTION);
            } catch (Exception e) {
                clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.UNKNOWN_CONNECTION_ERROR);
            }

            //Something went wrong
            return null;
        }


        /**
         * Loop indefinitely while checking for data and sending messages to the PiServer.
         * This method should only be called after the socket is connected.
         */
        private void waitForData() {
            while (!Thread.currentThread().isInterrupted()) {

            }
        }


        private void close() {
            if (isConnected() && socket != null) {
                clientHelperThread.interrupt();

                try {
                    socket.close();
                }catch (Exception e) {
                    System.out.print("Error closing socket: " + e.getMessage());
                }


                messageQueue.clear();
                sentMessages.clear();

                //TODO: Reset the ServerManager once we create the class.

                //The main thread should now be able to call join()
            }
        }
    }
}
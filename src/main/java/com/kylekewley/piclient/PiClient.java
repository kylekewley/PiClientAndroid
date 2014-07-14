package com.kylekewley.piclient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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

    ///The number of bytes to allocate for incoming messages
    private static final int BUFFER_SIZE = 16 * 1024; //16kb

    /*
    Class Data Members
     */

    ///The class that gets the client status updates and error messages.
    private PiClientCallbacks clientCallbacks = this;

    ///The timeout (in milliseconds) used to try connecting the socket.
    ///A value of zero is equal to no timeout.
    private int connectionTimeout = DEFAULT_TIMEOUT;


    ///The PiClientHelper running on a secondary thread.
    private PiClientHelper clientHelper;

    ///The Thread object that is running the PiClientHelper.
    private Thread clientHelperThread;


    ///The name of the host or IP address to connect to.
    private String hostName;

    ///The port number to connect to on the remote host.
    private int port;

    ///The PiParser for the client instance.
    private PiParser piParser = new PiParser();

    ///The PiServerManager for this client instance
    private PiServerManager serverManager = new PiServerManager(piParser);

    /*
    Class Constructors
     */


    /**
     * Creates an unconnected PiClient. The socket must be connected later.
     * using the connectToServer() method.
     */
    public PiClient() {

    }

    /**
     * Creates an unconnected PiClient with the given object to handle callback functions.
     *
     * @param clientCallbacks   The object that will handle error messages and status updates.
     */
    public PiClient(PiClientCallbacks clientCallbacks) {
        this.clientCallbacks = clientCallbacks;
    }


    /**
     * Creates a PiClient and connects it to the host on the given port.
     *
     * @param hostName          The name of the host.
     * @param port              The port number.
     * @param clientCallbacks   The object that will handle error messages and status updates.
     */
    public PiClient(String hostName, int port, PiClientCallbacks clientCallbacks) {
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
     * @return  A not-null piParser object used for parsing incoming data.
     */
    public PiParser getPiParser() {
        return piParser;
    }

    /**
     * @return  true if the PiClient is currently connected to the server.
     */
    public boolean isConnected() {
        return clientHelper != null && clientHelper.isConnected();
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
    public void connectToPiServer(String hostName, int port) {

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
        if (clientHelper != null && clientHelper.isConnected() && clientHelperThread != null) {
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
        if (clientHelper != null && message != null)
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
    public void clientRaisedError(PiClient piClient, ClientErrorCode error) {
        System.out.println(error.getErrorMessage());
    }

    @Override
    public void clientRaisedError(PiClient piClient, Exception error) {
        System.out.print(error.getMessage());
    }

    /*
    Inner Class Helper
     */

    private class PiClientHelper implements Runnable {

        ///The socket that will be used to connect to the PiServer.
        private SocketChannel socket;

        ///The buffer to use for incoming data
        private ByteBuffer inBuffer;

        ///The queue of messages for the PiClient to send to the server
        private ConcurrentLinkedQueue<PiMessage> messageQueue = new ConcurrentLinkedQueue<PiMessage>();

        ///The list that holds sent messages waiting for a reply
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

            inBuffer = ByteBuffer.allocate(BUFFER_SIZE);

            //The client will connect to the server asynchronously when the run() method is called
        }


        /*
        Overridden Methods
         */
        @Override
        public void run() {
            //We have a valid hostName and port as far as we can tell
            boolean connected = connectToPiServer(PiClient.this.hostName, PiClient.this.port);

            if (connected) {
                if (waitForConnectionToFinish()) {
                    //The connection is established
                    waitForData();
                }
            }
            //If we exit run(), the thread was interrupted or the socket threw an error.

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
            return socket != null && socket.isConnected();
        }


        /*
        Sending Messages
         */


        /**
         * Add a message to the messageQueue.
         * If the socket is not connected, the message will be
         * stored and sent as soon as the connection is made.
         *
         * @param message   The message to send to the server.
         */
        public void sendMessage(PiMessage message) {
            if (clientHelperThread.isInterrupted()) {
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

            //Now connect it with the specified timeout
            try {
                //
                //Configure the socket channel
                //
                socket = SocketChannel.open();

                //Set non-blocking mode
                socket.configureBlocking(false);

                socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                //Connect
                socket.connect(address);

                //The socket isn't actually connected until socket.finishConnect() returns true
                //But we didn't have any errors so return true
                return true;

            } catch (SocketTimeoutException e) {
                clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.CONNECTION_TIMEOUT);
            } catch (ConnectException e) {
                clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.CONNECTION_REFUSED);
            } catch (IOException e) {
                clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.UNKNOWN_CONNECTION_ERROR);
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
         * This function blocks until either:
         * - Thread.interrupted() returns true
         * - The socket finishes establishing a connection
         * - The socket throws an error while establishing a connection
         *
         * This should be called after socket.connect() has been called and socket is set to non-blocking.
         *
         * @return true if the socket is opened, false if there was an error waiting for the connection to establish.
         */
        private boolean waitForConnectionToFinish() {
            if (socket == null || !socket.isConnectionPending()) {
                //Socket isn't ready to connect
                clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.SOCKET_CONFIGURATION_ERROR);
                return false;
            }
            try {
                //Loop until connected or interrupted
                while (!socket.finishConnect() && !Thread.interrupted());
            } catch (Exception e) {
                clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.UNKNOWN_CONNECTION_ERROR);
                return false;
            }

            return socket.isConnected();
        }

        /**
         * Loop indefinitely while checking for data and sending messages to the PiServer.
         * This method should only be called after the socket is connected.
         */
        private void waitForData() {
            while (!Thread.interrupted() && isConnected()) {
                if (messageQueue.size() > 0) {
                    //Send the message
                    PiMessage message = messageQueue.peek();

                    ByteBuffer byteBuffer = message.getByteBuffer();

                    if (byteBuffer == null) {
                        //Error creating the ByteBuffer
                        clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.UNABLE_TO_SEND_MESSAGE);
                        //Remove the message
                        messageQueue.remove();
                    }else {
                        try {
                            //Send the message
                            socket.write(byteBuffer);
                            if (!byteBuffer.hasRemaining()) {
                                //Remove it from the message queue, add to sentMessages.
                                sentMessages.add(messageQueue.remove());
                            }
                        }catch (IOException e) {
                            //Error sending the ByteBuffer
                            clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.UNABLE_TO_SEND_MESSAGE);
                            //Remove the message
                            messageQueue.remove();
                        }
                    }
                }

                try {
                    if (socket.read(inBuffer) > 0) {
                        inBuffer.flip();
                        boolean status = serverManager.serverSentMessage(inBuffer, sentMessages);
                        inBuffer.clear();

                        if (status == false) {
                            clientCallbacks.clientRaisedError(PiClient.this, ClientErrorCode.UNABLE_TO_READ_MESSAGE);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading data...");
                }
            }
        }


        private void close() {
            if (isConnected() && socket != null) {
                try {
                    socket.close();
                }catch (Exception e) {
                    System.err.print("Error closing socket: " + e.getMessage());
                }


                messageQueue.clear();
                sentMessages.clear();

                serverManager = new PiServerManager(piParser);
                //The main thread should now be able to call join()
            }
        }
    }
}

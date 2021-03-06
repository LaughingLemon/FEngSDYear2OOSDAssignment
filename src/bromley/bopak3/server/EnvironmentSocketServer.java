package bromley.bopak3.server;
//Multi-threaded client\server utilising java sockets

import bromley.bopak3.common.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentSocketServer extends Thread implements EnvironmentSocketServerInterface {

    public static final int PORT = 8090;

    //flag to enable safe(-ish) shutdown
    private boolean shutThisDown = false;

    //use a collection to keep track of server connections
    private List<EnvironmentSocketThread> socketList = new ArrayList<EnvironmentSocketThread>();

    //enables the socket to be closed outside of run
    public EnvironmentServerSocket serverSocket;

    private EnvironmentSocketFactory socketFactory;

    public EnvironmentSocketServer(EnvironmentSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    private EnvironmentSocketEvent messageHandler;

    public void setMessageHandler(EnvironmentSocketEvent messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void run() {
        try {
            serverSocket = socketFactory.createServerSocket(PORT);
            try {
                while(!shutThisDown) {
                    // Blocks until a connection occurs
                    //the only way to un-block this
                    //seems to be to close the server socket
                    EnvironmentSocket socket = serverSocket.accept();
                    EnvironmentSocketThread socketThread = socketFactory.createSocketThread(socket);
                    //wire the socket event to this class's event handler
                    socketThread.setMessageHandler(messageHandler);
                    //connect to the client
                    socketThread.connect();
                    if(socketThread.isConnected())
                        socketList.add(socketThread);
                }
            } finally {
                serverSocket.close();
            }
        } catch(IOException e) {
            //ignore any exception
        }
    } //run

    public void shutDown() {
        //this doesn't shut down the socket,
        //but prevents another one being opened
        shutThisDown = true;
        try {
            //... but this does!
            serverSocket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    } //shutdown

    public void sendMessage(String message) {
        //for each of the server connections, send a message
        for(EnvironmentSocketThread socketThread : socketList) {
            socketThread.sendMessage(message);
        }
    }

}

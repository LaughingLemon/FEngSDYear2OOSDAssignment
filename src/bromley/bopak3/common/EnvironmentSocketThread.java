package bromley.bopak3.common;

//import various IO and network classes

import java.io.*;


//this class wrappers a network socket in a thread
//together with the input and output streams
//all nicely encapsulated
public class EnvironmentSocketThread extends Thread {

    //network socket
    private EnvironmentSocket socket;
    //network socket
    private EnvironmentSocketFactory socketFactory;
    //readers and writer used by the socket
    private BufferedReader in;
    private PrintWriter out;
    //status of socket
    private boolean connected = false;

    //getter and setter
    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    //allows the communication of messages to other classes
    private EnvironmentSocketEvent messageHandler;

    //the client of this class sets the message handler using this function
    public void setMessageHandler(EnvironmentSocketEvent messageHandler) {
        this.messageHandler = messageHandler;
    }

    private synchronized void fireMessageHandler(String message) {
        //if the message handler has been set...
        if(messageHandler != null)
            //send the message via the message handler and the event class
            messageHandler.messageReceived(new EnvironmentSocketMessage(this, message));
    }

    private void createReaderWriter() {
        //create the input stream reader from the connected socket
        try {
            this.in = new BufferedReader(
                    new InputStreamReader(this.socket.getInputStream())
            );
            //create the output stream writer from the connected socket
            this.out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(this.socket.getOutputStream())
                    ),
                    true
            );
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        if(socket != null && socket.isConnected()) {
            //this assumes that the socket is already connected
            //create the interfaces
            createReaderWriter();
            //kick off the thread
            start(); // Calls run()
            setConnected(true);
        } else
            setConnected(false);
    }

    public void connect(String host, int port) {
        //if the socket is not connected...
        if(socket == null)
            //try five times
            for(int i = 0; i < 5; i++) {
                try {
                    //establish a connection.
                    socket = socketFactory.createNewSocket(host, port);
                    //if connection is successful
                    break;
                } catch(IOException e) {
                    try {
                        //wait a second
                        System.out.println(e.getMessage());
                        Thread.sleep(1000);
                    } catch(InterruptedException e1) {
                        //don't bother
                    }
                }
            }
        //create the interfaces and start the thread
        connect();
    }

    public EnvironmentSocketThread(EnvironmentSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    public EnvironmentSocketThread(EnvironmentSocket socket,
                                   EnvironmentSocketFactory socketFactory) {
        //assign the local socket
        this.socket = socket;
        this.socketFactory = socketFactory;
    }

    public void run() {
        try {
            //until the client says END
            while(true) {
                //read in a line from the input stream
                String str = in.readLine();
                //until the END text is sent
                if(str.equals("END"))
                    break;
                //send message to the client class
                fireMessageHandler(str);
            }
        } catch(IOException e) {
            //ignore any exceptions
        } finally {
            try {
                //close the socket
                socket.close();
            } catch(IOException e) {
                //ignore any exceptions
            }
        }
    }

    public void sendMessage(String message) {
        //send the message to the receiver
        //if the output stream has been set up...
        if(this.out != null) {
            //send the message
            System.out.println("EnvironmentSocketThread.sendMessage: " + message);
            this.out.println(message);
        }
    }

}

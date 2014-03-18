package bromley.bopak3.common;
//Created by Shaun

//import various IO and network classes

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;


//this class wrappers a network socket in a thread
//together with the input and output streams
//all nicely encapsulated
public class EnvironmentSocketThread extends Thread {

    //network socket
    private Socket socket;
    //readers and writter used by the socket
    private BufferedReader in;
    private PrintWriter out;

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

    private void createReaderWriter() throws IOException {
        //create the input stream reader from the connected socket
        this.in = new BufferedReader(
                new InputStreamReader(this.socket.getInputStream())
        );
        //create the output stream writer from the connected socket
        this.out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(this.socket.getOutputStream())
                ),
                true);
    }

    public void connect() throws IOException {
        //this assumes that the socket is already connected
        //create the interfaces
        createReaderWriter();
        //kick off the thread
        start(); // Calls run()
    }

    public void connect(String host, int port) throws IOException {
        //if the socket is not connected...
        if(!socket.isConnected())
            //establish a connection. Any problems are sent back to the
            //caller via the exception
            socket.connect(new InetSocketAddress(host, port));
        //create the interfaces and start the thread
        connect();
    }

    public EnvironmentSocketThread(Socket s) {
        //assign the local socket
        this.socket = s;
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
        if(this.out != null)
            //send the message
            this.out.println(message);
    }

}

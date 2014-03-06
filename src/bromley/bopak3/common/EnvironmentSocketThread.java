package bromley.bopak3.common;
//Created by Shaun

import java.io.*;
import java.net.Socket;

public class EnvironmentSocketThread extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    //allows the communication of messages to other classes
    private EnvironmentSocketEvent messageHandler;

    public void setMessageHandler(EnvironmentSocketEvent messageHandler) {
        this.messageHandler = messageHandler;
    }

    private synchronized void fireMessageHandler(String message) {
        if(messageHandler != null)
            messageHandler.messageReceived(new EnvironmentSocketMessage(this, message));
    }

    public EnvironmentSocketThread(Socket s) throws IOException {
        //assign the local socket
        this.socket = s;
        //set up the input and output streams
        this.in = new BufferedReader(
                new InputStreamReader(this.socket.getInputStream())
        );
        this.out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(this.socket.getOutputStream())
                ),
                true);
        start(); // Calls run()
    }

    public void run() {
        try {
            //until the client says END
            while(true) {
                //read in a line from the input stream
                String str = in.readLine();
                if(str.equals("END"))
                    break;
                //send message to who's listening
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
        this.out.println(message);
    }

}

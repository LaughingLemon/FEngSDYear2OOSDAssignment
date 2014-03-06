package test;
//Created by Shaun

import bromley.bopak3.common.EnvironmentSocketEvent;
import bromley.bopak3.common.EnvironmentSocketMessage;
import bromley.bopak3.common.EnvironmentSocketThread;
import bromley.bopak3.server.EnvironmentSocketServer;
import bromley.bopak3.server.EnvironmentSocketServerInterface;

import java.io.IOException;
import java.net.Socket;

public class EnvironmentSocketServerTest {

    private static void testServerReceive() {
        System.out.println("testServerReceive start");

        EnvironmentSocketServerInterface server = new EnvironmentSocketServer();
        server.setMessageHandler(new EnvironmentSocketEvent() {
            public void messageReceived(EnvironmentSocketMessage e) {
                System.out.println("Message received: " + e.getMessage());
            }
        });
        server.start();

        try {
            Socket s = new Socket("localhost", EnvironmentSocketServer.PORT);
            try {
                EnvironmentSocketThread client = new EnvironmentSocketThread(s);
                client.sendMessage("This should be read by the server");
                client.sendMessage("END");
            } finally {
                s.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        server.shutDown();
        System.out.println("testServerReceive end");
    }

    public static void main(String[] args) {
        System.out.println("EnvironmentSocketServerTest start");
        testServerReceive();
        System.out.println("EnvironmentSocketServerTest end");
    }
}

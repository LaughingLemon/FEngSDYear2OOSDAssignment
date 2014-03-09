package test;

import bromley.bopak3.common.EnvironmentSocketEvent;
import bromley.bopak3.common.EnvironmentSocketMessage;
import bromley.bopak3.common.EnvironmentTemperature;
import bromley.bopak3.server.*;

import java.io.StringReader;

//Created by Shaun
public class EnvironmentServerTest {

    private static void delay(long time) {
        //simple time delay function
        try {
            Thread.sleep(time * 1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    //mock class which simulates the real thing, but allows testing
    private static class MockEnvironmentSocketServer implements EnvironmentSocketServerInterface {

        //allows the test to send commands to the server
        private EnvironmentSocketEvent messageHandler;

        public void receiveMessage(String message) {
            if(messageHandler != null)
                messageHandler.messageReceived(new EnvironmentSocketMessage(this, message));
        }

        public void setMessageHandler(EnvironmentSocketEvent messageHandler) {
            this.messageHandler = messageHandler;
        }

        //when the server uses these commands, it prints out
        public void shutDown() {
            System.out.println("shutDown");
        }

        public void start() {
            System.out.println("start");
        }

        public void sendMessage(String message) {
            System.out.println("sendMessage: " + message);
        }
    }

    //test to see that, given test data, the server outputs
    //the messages to the TCP/IP link, represented by
    //the mock object
    private static void testServerMessages() {
        System.out.println("testServerMessages start");
        //represents the data file
        StringReader sr = new StringReader("Time,Temp,Solar,Wind\n" +
                "00:00:00,-142.5,9600,4.35\n" +
                "00:01:00,-150.6,0,10.39\n" +
                "00:02:00,-155.6,0,46.0\n" +
                "00:03:00,-160.3,0,257.8");
        //represents the TCP/IP link
        MockEnvironmentSocketServer mss = new MockEnvironmentSocketServer();
        EnvironmentDataReaderInterface dr = new EnvironmentDataReader();
        dr.loadDataSource(sr);
        EnvironmentCalculations calculations = new EnvironmentCalculations();
        EnvironmentServer server = new EnvironmentServer(dr, mss, calculations);
        //set the required temperature and the initial temperature
        server.setRequestedIndoorTemperature(EnvironmentTemperature.celsiusToFahrenheit(20.0));
        server.setIndoorTemperature(EnvironmentTemperature.celsiusToFahrenheit(20.0));
        server.startClock();
        for(int i = 0; i < 5; i++) {
            outputServer(server);
            delay(60);
        }
        outputServer(server);
        //shut everything down
        server.stopClock();
        System.out.println("testServerMessages end");
    }

    private static void testClientMessages() {
        System.out.println("testClientMessages start");
        MockEnvironmentSocketServer mss = new MockEnvironmentSocketServer();
        //doesn't need the data reader
        EnvironmentServer server = new EnvironmentServer(null, mss, null);
        //the default is off
        System.out.println("server control setting: " + server.isTempControlSwitchedOn());
        System.out.println("server indoor temp: " + server.getIndoorTemperature());
        //the command goes in here...
        mss.receiveMessage("ON,23.0");
        //...and should come out here
        System.out.println("server control setting: " + server.isTempControlSwitchedOn());
        System.out.println("server indoor temp: " + server.getIndoorTemperature());
        //and switch it off again
        mss.receiveMessage("OFF");
        //the temperature should be the same as before
        System.out.println("server control setting: " + server.isTempControlSwitchedOn());
        System.out.println("server indoor temp: " + server.getIndoorTemperature());
        System.out.println("testClientMessages end");
    }

    private static void outputServer(EnvironmentServer server) {
        //simple output function to save typing
        System.out.println("TimeStamp: " + server.getCurrentTime());
        System.out.println("Outdoor Temperature: " + server.getOutdoorTemperature());
        System.out.println("Solar Panel Output: " + server.getSolarPanelOutput());
        System.out.println("Wind Turbine Output: " + server.getWindTurbineOutput());
    }

    public static void main(String[] args) {
        System.out.println("EnvironmentServerTest start");
        testClientMessages();
        testServerMessages();
        System.out.println("EnvironmentServerTest end");
    }
}

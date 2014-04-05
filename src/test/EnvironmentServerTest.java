import bromley.bopak3.common.EnvironmentSocketEvent;
import bromley.bopak3.common.EnvironmentSocketMessage;
import bromley.bopak3.common.EnvironmentTemperature;
import bromley.bopak3.common.EnvironmentTime;
import bromley.bopak3.server.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

//Created by Shaun
@RunWith(JUnit4.class)
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
            if(this.messageHandler != null)
                this.messageHandler.messageReceived(new EnvironmentSocketMessage(this, message));
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
    @Test
    public void testServerMessages() {
        //represents the data file
        StringReader sr = new StringReader("Time,Temp,Solar,Wind\n" +
                "00:00:00,-142.5,9600,4.35\n" +
                "00:01:00,-150.6,0,10.39\n" +
                "00:02:00,-155.6,0,46.0\n" +
                "00:03:00,-160.3,0,257.8");
        //represents the TCP/IP link
        MockEnvironmentSocketServer socketServer = spy(new MockEnvironmentSocketServer());
        EnvironmentDataReaderInterface dr = new EnvironmentDataReader();
        dr.loadDataSource(sr);
        EnvironmentCalculations calculations = new EnvironmentCalculations();
        EnvironmentServer server = new EnvironmentServer(dr, socketServer, calculations, 5);
        //set the required temperature and the initial temperature
        server.setRequestedIndoorTemperature(EnvironmentTemperature.celsiusToFahrenheit(20.0));
        server.setIndoorTemperature(EnvironmentTemperature.celsiusToFahrenheit(20.0));
        server.startClock();
        delay(10);
        verify(socketServer).sendMessage("OFF,00:00:00,68.0,-142.5,1712.53,9604.35,OFF");
        verify(socketServer).sendMessage("OFF,00:01:00,67.79,-150.6,1776.71,10.39,OFF");
        verify(socketServer).sendMessage("OFF,00:02:00,67.57,-155.6,1815.59,46.0,OFF");
        server.setTempControlSwitchedOn(true);
        delay(10);
        verify(socketServer).sendMessage("ON,00:03:00,67.34,-160.3,1852.0,257.8,OFF");
        verify(socketServer).sendMessage("ON,00:00:00,67.14,-142.5,1705.58,9604.35,OFF");
        //shut everything down
        server.stopClock();
        verify(socketServer).shutDown();
    }

    @Test
    public void testClientMessages() {
        MockEnvironmentSocketServer socketServer = spy(new MockEnvironmentSocketServer());
        //doesn't need the data reader
        EnvironmentServer server = new EnvironmentServer(null, socketServer, null);
        server.setCurrentTime(EnvironmentTime.stringToTime("00:00:00"));
        //the default is off
        assertEquals(false, server.isTempControlSwitchedOn());
        assertEquals(0.00, server.getRequestedIndoorTemperature(), 0.001);
        assertEquals(false, server.isWindowTintOn());
        //the command goes in here...
        socketServer.receiveMessage("ON,23.0,OFF");
        verify(socketServer).sendMessage("ON,00:00:00,0.0,0.0,0.0,0.0,OFF");
        //...and should come out here
        assertEquals(true, server.isTempControlSwitchedOn());
        assertEquals(23.00, server.getRequestedIndoorTemperature(), 0.001);
        assertEquals(false, server.isWindowTintOn());
        //and switch it off again
        socketServer.receiveMessage("OFF,0.0,ON");
        verify(socketServer).sendMessage("OFF,00:00:00,0.0,0.0,0.0,0.0,ON");
        //the temperature should be the same as before
        assertEquals(false, server.isTempControlSwitchedOn());
        assertEquals(23.00, server.getRequestedIndoorTemperature(), 0.001);
        assertEquals(true, server.isWindowTintOn());
    }

}

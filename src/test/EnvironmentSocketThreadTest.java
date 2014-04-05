import bromley.bopak3.common.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.BufferedReader;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class EnvironmentSocketThreadTest {

    @Test
    public void testIsConnected() throws Exception {
        EnvironmentSocketFactory socketFactory = mock(EnvironmentSocketFactory.class);
        EnvironmentSocket socket = mock(EnvironmentSocket.class);
        when(socket.isConnected()).thenReturn(true);
        when(socketFactory.createNewSocket("temp", 10)).thenReturn(socket);
        PrintWriter printWriter = mock(PrintWriter.class);
        BufferedReader bufferedReader = mock(BufferedReader.class);
        when(socket.getBufferedReader()).thenReturn(bufferedReader);
        when(socket.getPrintWriter()).thenReturn(printWriter);

        EnvironmentSocketThread socketThread = new EnvironmentSocketThread(socketFactory);
        socketThread.connect("temp", 10);

        verify(socketFactory).createNewSocket("temp", 10);
        verify(socket).isConnected();
        verify(socket).getBufferedReader();
        verify(socket).getPrintWriter();

        assertTrue(socketThread.isConnected());
    }

    @Test
    public void testSendMessage() throws Exception {
        EnvironmentSocketFactory socketFactory = mock(EnvironmentSocketFactory.class);
        EnvironmentSocket socket = mock(EnvironmentSocket.class);
        when(socket.isConnected()).thenReturn(true);
        when(socketFactory.createNewSocket("temp", 10)).thenReturn(socket);
        PrintWriter printWriter = mock(PrintWriter.class);
        BufferedReader bufferedReader = mock(BufferedReader.class);
        when(socket.getBufferedReader()).thenReturn(bufferedReader);
        when(socket.getPrintWriter()).thenReturn(printWriter);

        EnvironmentSocketThread socketThread = new EnvironmentSocketThread(socketFactory);
        socketThread.connect("temp", 10);

        socketThread.sendMessage("Test Message");

        verify(printWriter).println("Test Message");
    }

    private String testMessageReceived;

    private static void delay(long time) {
        //simple time delay function
        try {
            Thread.sleep(time * 1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReceiveMessage() throws Exception {
        EnvironmentSocketFactory socketFactory = mock(EnvironmentSocketFactory.class);
        EnvironmentSocket socket = mock(EnvironmentSocket.class);
        when(socket.isConnected()).thenReturn(true);
        when(socketFactory.createNewSocket("temp", 10)).thenReturn(socket);
        PrintWriter printWriter = mock(PrintWriter.class);
        BufferedReader bufferedReader = mock(BufferedReader.class);
        when(socket.getBufferedReader()).thenReturn(bufferedReader);
        when(socket.getPrintWriter()).thenReturn(printWriter);

        when(bufferedReader.readLine()).thenReturn("Test Message");

        EnvironmentSocketThread socketThread = new EnvironmentSocketThread(socketFactory);
        socketThread.setMessageHandler(new EnvironmentSocketEvent() {
            @Override
            public void messageReceived(EnvironmentSocketMessage e) {
                testMessageReceived = e.getMessage();
            }
        });
        socketThread.connect("temp", 10);

        delay(5);

        assertEquals("Test Message", testMessageReceived);
    }

} 

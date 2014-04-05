import bromley.bopak3.common.*;
import bromley.bopak3.server.EnvironmentSocketServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class EnvironmentSocketServerTest {

    private static void delay(long time) {
        //simple time delay function
        try {
            Thread.sleep(time * 1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testServerReceive() throws Exception {
        EnvironmentSocketFactory socketFactory = mock(EnvironmentSocketFactory.class);
        EnvironmentServerSocket serverSocket = mock(EnvironmentServerSocket.class);
        EnvironmentSocket socket = mock(EnvironmentSocket.class);
        when(serverSocket.accept())
                .thenReturn(socket)
                .thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocation) throws InterruptedException {
                        Thread.sleep(50000);
                        return "";
                    }
                });
        when(socketFactory.createServerSocket(EnvironmentSocketServer.PORT)).thenReturn(serverSocket);
        EnvironmentSocketThread socketThread = mock(EnvironmentSocketThread.class);
        when(socketThread.isConnected()).thenReturn(true);
        when(socketFactory.createSocketThread(socket)).thenReturn(socketThread);

        EnvironmentSocketEvent socketEvent = new EnvironmentSocketEvent() {
            public void messageReceived(EnvironmentSocketMessage e) {
            }
        };

        EnvironmentSocketServer server = new EnvironmentSocketServer(socketFactory);
        server.setMessageHandler(socketEvent);
        server.start();

        verify(socketFactory).createServerSocket(EnvironmentSocketServer.PORT);
        verify(serverSocket, atLeastOnce()).accept();
        verify(socketFactory).createSocketThread(socket);
        verify(socketThread).setMessageHandler(socketEvent);
        verify(socketThread).connect();
        verify(socketThread).isConnected();

        server.sendMessage("Test message");

        verify(socketThread).sendMessage("Test message");

        server.shutDown();

        verify(serverSocket).close();
    }

}

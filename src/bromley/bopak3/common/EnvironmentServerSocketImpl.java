package bromley.bopak3.common;
//Created by Shaun

import java.io.IOException;
import java.net.ServerSocket;

public class EnvironmentServerSocketImpl implements EnvironmentServerSocket {

    private ServerSocket serverSocket;
    private EnvironmentSocketFactoryImpl socketFactory;

    public EnvironmentServerSocketImpl(ServerSocket serverSocket,
                                       EnvironmentSocketFactoryImpl socketFactory) {
        this.serverSocket = serverSocket;
        this.socketFactory = socketFactory;
    }

    public EnvironmentSocket accept() throws IOException {
        return socketFactory.wrapperSocket(serverSocket.accept());
    }

    public void close() throws IOException {

    }
}

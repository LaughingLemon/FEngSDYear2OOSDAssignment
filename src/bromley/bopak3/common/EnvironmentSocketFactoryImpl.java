package bromley.bopak3.common;
//Created by Shaun

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//a little factory class that makes sockets and/or wrappers them
public class EnvironmentSocketFactoryImpl implements EnvironmentSocketFactory {
    //create a new socket interface from the host and post
    public EnvironmentSocket createNewSocket(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        return wrapperSocket(socket);
    }

    //create a server socket wrapped in an interface
    public EnvironmentServerSocket createServerSocket(int port) throws IOException {
        return new EnvironmentServerSocketImpl(new ServerSocket(port), this);
    }

    //wrapper an existing socket
    public EnvironmentSocket wrapperSocket(Socket socket) {
        return new EnvironmentSocketImpl(socket);
    }
}

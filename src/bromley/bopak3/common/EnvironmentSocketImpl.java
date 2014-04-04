package bromley.bopak3.common;
//Created by Shaun

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

//an interface wrapper around a Socket object
public class EnvironmentSocketImpl implements EnvironmentSocket {

    private Socket socket;

    public EnvironmentSocketImpl(Socket socket) {
        this.socket = socket;
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public void close() throws IOException {
        socket.close();
    }
}

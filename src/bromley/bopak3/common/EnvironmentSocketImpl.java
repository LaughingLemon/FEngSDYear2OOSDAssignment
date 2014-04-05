package bromley.bopak3.common;
//Created by Shaun

import java.io.*;
import java.net.Socket;

//an interface wrapper around a Socket object
public class EnvironmentSocketImpl implements EnvironmentSocket {

    private Socket socket;

    public EnvironmentSocketImpl(Socket socket) {
        this.socket = socket;
    }

    @Override
    public BufferedReader getBufferedReader() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public PrintWriter getPrintWriter() throws IOException {
        return new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())
                ),
                true
        );
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public void close() throws IOException {
        socket.close();
    }
}

package bromley.bopak3.common;
//Created by Shaun

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public interface EnvironmentSocket {
    public BufferedReader getBufferedReader() throws IOException;

    public PrintWriter getPrintWriter() throws IOException;

    public boolean isConnected();

    public void close() throws IOException;
}

package bromley.bopak3.common;
//Created by Shaun

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface EnvironmentSocket {
    public InputStream getInputStream() throws IOException;

    public OutputStream getOutputStream() throws IOException;

    public boolean isConnected();

    public void close() throws IOException;
}

package bromley.bopak3.common;
//Created by Shaun

import bromley.bopak3.common.EnvironmentSocket;

import java.io.IOException;

public interface EnvironmentServerSocket {
    public EnvironmentSocket accept() throws IOException;

    public void close() throws IOException;
}

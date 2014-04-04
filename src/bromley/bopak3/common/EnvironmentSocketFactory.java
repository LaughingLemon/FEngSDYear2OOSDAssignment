package bromley.bopak3.common;
//Created by Shaun

import java.io.IOException;

public interface EnvironmentSocketFactory {
    public EnvironmentSocket createNewSocket(String host, int port) throws IOException;

    public EnvironmentServerSocket createServerSocket(int port) throws IOException;
}

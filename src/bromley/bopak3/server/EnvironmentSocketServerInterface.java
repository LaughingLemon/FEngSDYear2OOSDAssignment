package bromley.bopak3.server;
//Created by Shaun

import bromley.bopak3.common.EnvironmentSocketEvent;

public interface EnvironmentSocketServerInterface {
    void setMessageHandler(EnvironmentSocketEvent messageHandler);

    void shutDown();

    void start();

    void sendMessage(String message);
}

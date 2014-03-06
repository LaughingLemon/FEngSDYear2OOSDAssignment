package bromley.bopak3.common;
//Created by Shaun

import java.util.EventObject;

//acts as a kind of message carrier between classes
public class EnvironmentSocketMessage extends EventObject {

    private String message;

    public String getMessage() {
        return message;
    }

    public EnvironmentSocketMessage(Object source, String message) {
        super(source);
        this.message = message;
    }
}

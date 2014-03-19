package bromley.bopak3.client;

import bromley.bopak3.common.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

public class EnvironmentClientDisplay extends Frame {

    //environment variables
    private Date currentTime;
    private double indoorTemperature = 0.0;
    private double outdoorTemperature = 0.0;
    private double powerGenerated = 0.0;
    private double powerConsumed = 0.0;
    private boolean controlOn = true;
    private double requiredTemperature = EnvironmentTemperature.celsiusToFahrenheit(25.0);
    private boolean windowTintOn = false;

    //getters and setters for the above
    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

    public double getIndoorTemperature() {
        return indoorTemperature;
    }

    public void setIndoorTemperature(double indoorTemperature) {
        this.indoorTemperature = indoorTemperature;
    }

    public double getOutdoorTemperature() {
        return outdoorTemperature;
    }

    public void setOutdoorTemperature(double outdoorTemperature) {
        this.outdoorTemperature = outdoorTemperature;
    }

    public double getPowerGenerated() {
        return powerGenerated;
    }

    public void setPowerGenerated(double powerGenerated) {
        this.powerGenerated = powerGenerated;
    }

    public double getPowerConsumed() {
        return powerConsumed;
    }

    public void setPowerConsumed(double powerConsumed) {
        this.powerConsumed = powerConsumed;
    }

    public boolean isControlOn() {
        return controlOn;
    }

    public void setControlOn(boolean controlOn) {
        this.controlOn = controlOn;
    }

    public double getRequiredTemperature() {
        return requiredTemperature;
    }

    public void setRequiredTemperature(double requiredTemperature) {
        this.requiredTemperature = requiredTemperature;
    }

    public boolean isWindowTintOn() {
        return windowTintOn;
    }

    public void setWindowTintOn(boolean windowTintOn) {
        this.windowTintOn = windowTintOn;
    }

    //visual setup
    protected void setUpWindow() {
        //to be overridden by descendants
    }

    //Class to handle exit
    private class ExitHandler extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    //allows the client to connect to the server
    private EnvironmentSocketThread socketThread;

    protected void updateDisplay() {
        //overidden in descendants
    }

    public void sendMessage() {
        //send a message to the server
        socketThread.sendMessage((isControlOn() ? "ON" : "OFF") + "," +
                getRequiredTemperature() + "," +
                (isWindowTintOn() ? "ON" : "OFF"));
    }

    //constructor
    public EnvironmentClientDisplay(EnvironmentSocketThread socketThread) {
        this.socketThread = socketThread;
        //create a message handler to handle messages from the server
        this.socketThread.setMessageHandler(new EnvironmentSocketEvent() {
            //when the message is received from the server, parse it and
            //set the various values
            public void messageReceived(EnvironmentSocketMessage e) {
                String[] environmentValues = e.getMessage().split(",");
                setControlOn(environmentValues[0].equals("ON"));
                setCurrentTime(EnvironmentTime.stringToTime(environmentValues[1]));
                setIndoorTemperature(Double.parseDouble(environmentValues[2]));
                setOutdoorTemperature(Double.parseDouble(environmentValues[3]));
                setPowerConsumed(Double.parseDouble(environmentValues[4]));
                setPowerGenerated(Double.parseDouble(environmentValues[5]));
                setWindowTintOn(environmentValues[6].equals("ON"));
                //update the display with the new values
                updateDisplay();
            }
        });

        //set up the components on the window
        setUpWindow();

        //create a nice exit handler
        this.addWindowListener(new ExitHandler());

        //make the form visible
        setVisible(true);
    }

}
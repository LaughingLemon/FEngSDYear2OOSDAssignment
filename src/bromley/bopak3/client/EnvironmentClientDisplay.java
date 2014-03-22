package bromley.bopak3.client;

import bromley.bopak3.common.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

public class EnvironmentClientDisplay extends Frame {

    //labels for different degrees
    protected static final String CELSIUS_FAHRENHEIT_LABEL = "Celsius\\Fahrenheit";
    //environment variables
    private Date currentTime = EnvironmentTime.stringToTime("00:00:00");
    private double indoorTemperature = 0.0;
    private double outdoorTemperature = 0.0;
    private double powerGenerated = 0.0;
    private double powerConsumed = 0.0;
    private boolean controlOn = true;
    private double requiredTemperature = EnvironmentTemperature.celsiusToFahrenheit(25.0);
    private boolean windowTintOn = false;
    private boolean inCelsius = false;

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

    public boolean isInCelsius() {
        return inCelsius;
    }

    public void setInCelsius(boolean inCelsius) {
        this.inCelsius = inCelsius;
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

    public EnvironmentSocketThread getSocketThread() {
        return socketThread;
    }

    //this is a utility function to replace repeated lines of code
    protected Component addComponentToPanel(Component component, Font font, int x, int y, int width, int height, Panel panel) {
        //set the font
        component.setFont(font);
        //set the position and size
        component.setBounds(x, y, width, height);
        //add the component to the panel
        panel.add(component);
        //return the component, if it needs a reference
        //this will need to be down cast for specific component types
        return component;
    }

    protected void updateDisplay() {
        //overridden in descendants
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
package bromley.bopak3.server;

import bromley.bopak3.common.EnvironmentSocketEvent;
import bromley.bopak3.common.EnvironmentSocketMessage;
import bromley.bopak3.common.EnvironmentTemperature;
import bromley.bopak3.common.EnvironmentTime;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//import java.io.InputStream;
//import java.io.InputStreamReader;

public class EnvironmentServer {

    //used to read the data file
    EnvironmentDataReaderInterface dataReader;
    //used to do environment calculations
    EnvironmentCalculations calculations;

    //current environment data
    private Date currentTime;
    private double outdoorTemperature = 0.0;
    private double indoorTemperature = 0.0;
    private double solarPanelOutput = 0.0;
    private double windTurbineOutput = 0.0;
    private boolean tempControlSwitchedOn = false;
    private boolean windowTintOn = false;

    private double powerConsumption = 0.0;
    private double temperatureLoss = 0.0;
    private double requestedIndoorTemperature = 0.0;

    private int timeInterval = 60;

    //getters and setters
    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

    public double getOutdoorTemperature() {
        return outdoorTemperature;
    }

    public void setOutdoorTemperature(double outdoorTemperature) {
        this.outdoorTemperature = outdoorTemperature;
    }

    public double getIndoorTemperature() {
        return indoorTemperature;
    }

    public void setIndoorTemperature(double indoorTemperature) {
        this.indoorTemperature = indoorTemperature;
    }

    public double getSolarPanelOutput() {
        return solarPanelOutput;
    }

    public void setSolarPanelOutput(double solarPanelOutput) {
        this.solarPanelOutput = solarPanelOutput;
    }

    public double getWindTurbineOutput() {
        return windTurbineOutput;
    }

    public void setWindTurbineOutput(double windTurbineOutput) {
        this.windTurbineOutput = windTurbineOutput;
    }

    private String environmentDataAsString() {
        //utility function to package the environment data as a string
        //for sending via the network socket
        DecimalFormat decimalFormat = new DecimalFormat("0.0#");
        return (isTempControlSwitchedOn() ? "ON" : "OFF") + "," +
                EnvironmentTime.timeToString(getCurrentTime()) + "," +
                decimalFormat.format(getIndoorTemperature()) + "," +
                decimalFormat.format(getOutdoorTemperature()) + "," +
                decimalFormat.format(getPowerConsumption()) + "," +
                decimalFormat.format(getTotalPowerProduction()) + "," +
                (isWindowTintOn() ? "ON" : "OFF");
    }

    public boolean isTempControlSwitchedOn() {
        return tempControlSwitchedOn;
    }

    public void setTempControlSwitchedOn(boolean tempControlSwitchedOn) {
        this.tempControlSwitchedOn = tempControlSwitchedOn;
    }

    public double getPowerConsumption() {
        return powerConsumption;
    }

    public void setPowerConsumption(double powerConsumption) {
        this.powerConsumption = powerConsumption;
    }

    public double getTemperatureLoss() {
        return temperatureLoss;
    }

    public void setTemperatureLoss(double temperatureLoss) {
        this.temperatureLoss = temperatureLoss;
    }

    public double getRequestedIndoorTemperature() {
        return requestedIndoorTemperature;
    }

    public void setRequestedIndoorTemperature(double requestedIndoorTemperature) {
        this.requestedIndoorTemperature = requestedIndoorTemperature;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public boolean isWindowTintOn() {
        return windowTintOn;
    }

    public void setWindowTintOn(boolean windowTintOn) {
        this.windowTintOn = windowTintOn;
    }

    //handler object for messages from the clients
    private EnvironmentSocketEvent messageHandler = new EnvironmentSocketEvent() {
        public void messageReceived(EnvironmentSocketMessage e) {
            //split the message into two parts (if there are...)
            String[] messages = e.getMessage().split(",");
            //the first bit indicates whether to switch the temperature control
            //on or off
            setTempControlSwitchedOn(messages[0].equals("ON"));
            //and if it's on...
            if(isTempControlSwitchedOn())
                //set the temperature level
                setRequestedIndoorTemperature(Double.parseDouble(messages[1]));
            //set the window tint
            setWindowTintOn(messages[2].equals("ON"));
            //echo back the current environment variables
            socketServer.sendMessage(environmentDataAsString());
        }
    };

    //constructor with dependency injected objects
    public EnvironmentServer(EnvironmentDataReaderInterface dataReader,
                             EnvironmentSocketServerInterface socketServer,
                             EnvironmentCalculations calculations) {
        //call the other constructor with a minute interval
        this(dataReader, socketServer, calculations, 60);
    }

    //constructor with dependency injected objects
    public EnvironmentServer(EnvironmentDataReaderInterface dataReader,
                             EnvironmentSocketServerInterface socketServer,
                             EnvironmentCalculations calculations,
                             int timeInterval) {
        this.dataReader = dataReader;
        this.socketServer = socketServer;
        //message handler to be able to set the temperature
        //and switch on
        this.socketServer.setMessageHandler(messageHandler);
        this.calculations = calculations;
        //set the time interval
        setTimeInterval(timeInterval);
    }

    //manages network connections
    private EnvironmentSocketServerInterface socketServer;

    //clock to regularly update values
    Timer clock = new Timer();

    //interval is a minute
    private static final int SECOND_INTERVAL = 1000;
    //delay is zero
    private static final int CLOCK_DELAY = 0;

    //task run by the timer (effectively a thread)
    private class DataChange extends TimerTask {
        public void run() {
            //get the values from the reader
            EnvironmentData dataObj = dataReader.getNextEnvironmentData();
            //set the object values to the new ones
            setCurrentTime(dataObj.getTimeStamp());
            setOutdoorTemperature(dataObj.getOutdoorTemperature());
            setSolarPanelOutput(dataObj.getSolarPanelOutput());
            setWindTurbineOutput(dataObj.getWindTurbineOutput());
            //the new indoor temperature is the old minus the temperature loss
            setIndoorTemperature(getIndoorTemperature() - getTemperatureLoss());
            //calculate the environment statistics
            calculateEnvironmentStats();
            //send the data via the socket
            socketServer.sendMessage(environmentDataAsString());
        }
    }

    //utility function that just sums the power generators
    private double getTotalPowerProduction() {
        return getSolarPanelOutput() + getWindTurbineOutput();
    }

    private void calculateEnvironmentStats() {
        //first figure out the heat loss i.e. power consumption
        setPowerConsumption(calculations.heatLoss(EnvironmentTemperature.fahrenheitToCelsius(getOutdoorTemperature()),
                EnvironmentTemperature.fahrenheitToCelsius(getIndoorTemperature())));
        //if the current temperature is greater or equal to that that set
        //(or the temperature control is switched off)...
        if(getIndoorTemperature() >= getRequestedIndoorTemperature() || !isTempControlSwitchedOn())
            //...there's no heat, but there will still be loss
            setTemperatureLoss(calculations.temperatureDropMinute(getPowerConsumption()));
        else
            //...otherwise the loss is the difference between input and output
            setTemperatureLoss(calculations.temperatureDropMinute(getPowerConsumption() - getTotalPowerProduction()));
    }

    public void startClock() {
        //kicks off the network socket process
        socketServer.start();
        //kicks off the clock process
        clock.schedule(new DataChange(), CLOCK_DELAY, getTimeInterval() * SECOND_INTERVAL);
        //slight delay to allow the data to be read
        slightSleep(250);
    }

    private void slightSleep(long time) {
        //sleep for a short while
        try {
            Thread.sleep(time);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopClock() {
        //stops it
        clock.cancel();
        //and shuts down the socket server
        socketServer.shutDown();
    }

    public static void main(String[] args) {
        //load the data file
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(".\\data\\EnvironmentData.csv");
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        //InputStream resourceStream = EnvironmentServer.class.getResourceAsStream("/data/EnvironmentData.csv");
        //Reader fileReader = new InputStreamReader(resourceStream);
        //create the network server
        EnvironmentSocketServer socketServer = new EnvironmentSocketServer();
        //create the data reader...
        EnvironmentDataReader dataReader = new EnvironmentDataReader();
        //...and load the file
        dataReader.loadDataSource(fileReader);
        //sets the data reader to read the PST time data
        dataReader.getEnvironmentDataAtTime(EnvironmentTime.timeInEnvironment(EnvironmentTime.getCurrentTime()));
        //create the calculations object
        EnvironmentCalculations calculations = new EnvironmentCalculations();
        //create the server
        EnvironmentServer server = new EnvironmentServer(dataReader, socketServer, calculations);
        //set the requested temperature and the actual temperature
        server.setRequestedIndoorTemperature(EnvironmentTemperature.celsiusToFahrenheit(20.0));
        server.setIndoorTemperature(EnvironmentTemperature.celsiusToFahrenheit(20.0));
        //make sure the control switched on
        server.setTempControlSwitchedOn(true);
        //kick off the clock to read in the data
        server.startClock();
    }

}
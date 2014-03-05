package bromley.bopak3.server;

import java.util.Date;

public class EnvironmentData {
    //simple storage class representing a line of data
    //in the environment data file

    private Date timeStamp;
    private double outdoorTemperature;
    private double solarPanelOutput;
    private double windTurbineOutput;

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getOutdoorTemperature() {
        return outdoorTemperature;
    }

    public void setOutdoorTemperature(double outdoorTemperature) {
        this.outdoorTemperature = outdoorTemperature;
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
}

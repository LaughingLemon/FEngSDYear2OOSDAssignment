package bromley.bopak3.server;
//Created by Shaun

import static java.lang.Math.abs;

public class EnvironmentCalculations {

    private double wallArea = 0.0;
    private double windowArea = 0.0;
    private double volume = 0.0;

    //default constructor with default values
    public EnvironmentCalculations() {
        this.windowArea = 40.0;
        this.wallArea = 360.0 - this.wallArea;
        this.volume = 400.0;
    }

    //constructor with other values
    public EnvironmentCalculations(double totalArea, double windowArea, double volume) {
        this.windowArea = windowArea;
        this.wallArea = totalArea - this.windowArea;
        this.volume = volume;
    }

    private static final double U_VALUE_WINDOW = 0.0448;
    private static final double U_VALUE_WALL = 0.0357;
    private static final double SPECIFIC_HEAT_AIR = 1210.0;

    private static final int SECONDS_IN_MINUTE = 60;

    //calculate the temperature drop for a volume of air
    //and heat loss
    public double temperatureDrop(double heatLoss, double seconds) {
        return (heatLoss * seconds) / (SPECIFIC_HEAT_AIR * volume);
    }

    //as above but for a minute
    public double temperatureDropMinute(double heatLoss) {
        return temperatureDrop(heatLoss, SECONDS_IN_MINUTE);
    }

    //calculate the heat loss given a temperature difference
    public double heatLoss(double outdoorTemp, double indoorTemp) {
        return abs(indoorTemp - outdoorTemp) * ((wallArea * U_VALUE_WALL) + (windowArea * U_VALUE_WINDOW));
    }

}

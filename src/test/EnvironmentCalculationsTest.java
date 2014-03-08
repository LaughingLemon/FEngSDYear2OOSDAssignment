package test;
//Created by Shaun

import bromley.bopak3.common.EnvironmentTemperature;
import bromley.bopak3.server.EnvironmentCalculations;

public class EnvironmentCalculationsTest {

    private static void testHeatLossCalculations() {
        System.out.println("testHeatLossCalculations start");
        EnvironmentCalculations calculations = new EnvironmentCalculations();
        System.out.println("Temperature drop: " + calculations.temperatureDropMinute(calculations.heatLoss(EnvironmentTemperature.fahrenheitToCelsius(-142), 25.0)));
        System.out.println("Temperature drop: " + calculations.temperatureDrop(calculations.heatLoss(EnvironmentTemperature.fahrenheitToCelsius(-142), 25.0), 80 * 60));
        calculations = new EnvironmentCalculations(500, 100, 1000);
        System.out.println("Temperature drop: " + calculations.temperatureDropMinute(calculations.heatLoss(-100.0, 20.0)));
        System.out.println("testHeatLossCalculations end");
    }

    public static void main(String[] args) {
        System.out.println("EnvironmentCalculationsTest start");
        testHeatLossCalculations();
        System.out.println("EnvironmentCalculationsTest end");
    }

}

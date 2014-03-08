package test;
//Created by Shaun

import bromley.bopak3.common.EnvironmentTemperature;

public class EnvironmentTemperatureTest {

    private static void testConversions() {
        System.out.println("testConversions start");
        System.out.println("Temp: " + EnvironmentTemperature.celsiusToFahrenheit(EnvironmentTemperature.fahrenheitToCelsius(10)));
        System.out.println("Temp: " + EnvironmentTemperature.fahrenheitToCelsius(EnvironmentTemperature.celsiusToFahrenheit(10)));
        System.out.println("testConversions end");
    }

    public static void main(String[] args) {
        System.out.println("EnvironmentTemperatureTest start");
        testConversions();
        System.out.println("EnvironmentTemperatureTest end");
    }

}

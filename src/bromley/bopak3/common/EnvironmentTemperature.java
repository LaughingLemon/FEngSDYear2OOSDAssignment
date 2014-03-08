package bromley.bopak3.common;
//Created by Shaun

public class EnvironmentTemperature {
    //simple conversion
    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }

    public static double celsiusToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }
}

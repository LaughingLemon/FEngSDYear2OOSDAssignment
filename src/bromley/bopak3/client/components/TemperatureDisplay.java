package bromley.bopak3.client.components;

import bromley.bopak3.common.EnvironmentTemperature;

import java.awt.*;

import static java.lang.Math.abs;

//Panel with LED's on it to show the temperature with
//convenient function to set the display
public class TemperatureDisplay extends Panel {

    public static final String DEGREES_C = "\u00b0C";
    public static final String DEGREES_F = "\u00b0F";

    private LEDPanel minus;
    private LEDPanel hundreds;
    private LEDPanel tens;
    private LEDPanel units;

    private Label degreeLabel;

    private boolean displayInCelsius = false;

    public boolean isDisplayInCelsius() {
        return displayInCelsius;
    }

    public void setDisplayInCelsius(boolean displayInCelsius) {
        this.displayInCelsius = displayInCelsius;
    }

    public TemperatureDisplay() {
        //sets the layout
        setLayout(LEDPanel.createLayout(5));
        minus = new LEDPanel();
        add(minus);
        hundreds = new LEDPanel();
        add(hundreds);
        tens = new LEDPanel();
        add(tens);
        units = new LEDPanel();
        add(units);
        //create a label
        degreeLabel = new Label(DEGREES_F);
        degreeLabel.setFont(new Font("Sans-serif", Font.BOLD, 24));
        add(degreeLabel);
    }

    public void displayTemperature(Double temp) {
        degreeLabel.setText(isDisplayInCelsius() ? DEGREES_C : DEGREES_F);
        temp = isDisplayInCelsius() ? EnvironmentTemperature.fahrenheitToCelsius(temp) : temp;
        minus.setDisplayNumber(temp < 0 ? -1 : 10);
        hundreds.setDisplayNumber((int) (abs(temp) / 100.0));
        tens.setDisplayNumber((int) ((abs(temp) / 10.0) % 10));
        units.setDisplayNumber((int) (abs(temp) % 10));
    }
}

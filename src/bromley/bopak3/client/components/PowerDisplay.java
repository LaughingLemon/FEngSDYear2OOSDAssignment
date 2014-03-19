package bromley.bopak3.client.components;
//Created by Shaun

import java.awt.*;

import static java.lang.Math.abs;

//Panel with LED's on it to show the temperature with
//convenient function to set the display
public class PowerDisplay extends Panel {

    //segments
    private LEDPanel thousands;
    private LEDPanel hundreds;
    private LEDPanel tens;
    private LEDPanel units;

    public PowerDisplay() {
        //set the layout
        setLayout(LEDPanel.createLayout(5));
        //create the LEDs
        thousands = new LEDPanel();
        add(thousands);
        hundreds = new LEDPanel();
        add(hundreds);
        tens = new LEDPanel();
        add(tens);
        units = new LEDPanel();
        add(units);
        //create a label
        Label powerLabel = new Label("Watts");
        powerLabel.setFont(new Font("Sans-serif", Font.BOLD, 24));
        add(powerLabel);
    }

    public void displayPower(Double power) {
        //divide the power up into distinct digits for display
        thousands.setDisplayNumber((int) (abs(power) / 1000.0));
        hundreds.setDisplayNumber((int) ((abs(power) / 100.0) % 1000));
        tens.setDisplayNumber((int) ((abs(power) / 10.0) % 100));
        units.setDisplayNumber((int) (abs(power) % 10));
    }
}

package bromley.bopak3.client;

import bromley.bopak3.client.components.*;
import bromley.bopak3.common.EnvironmentSocketThread;
import bromley.bopak3.server.EnvironmentSocketServer;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class EnvironmentClientMaster extends EnvironmentClientDisplay {

    private ToggleSwitch onOffSwitch;
    private TimeDisplay currentTimeDisplay;
    private TemperatureDisplay indoorTemperatureDisplay;
    private TemperatureDisplay outdoorTemperatureDisplay;
    private PowerDisplay powerConsumptionDisplay;
    private PowerDisplay powerGenerationDisplay;
    private Label onLabel;
    private Label offLabel;

    private static final Font STANDARD_FONT = new Font("Sans-serif", Font.BOLD, 24);

    private static Panel createTextPanel(Panel panel, String text) {
        //create a container Panel
        Panel container = new Panel();
        container.setLayout(null);
        //create a label
        Label label = new Label(text);
        label.setFont(STANDARD_FONT);
        container.add(label);
        //set the size and location (width ten pixels per character)
        int labelWidth = Math.max(text.length() * 25, panel.getWidth());
        label.setBounds(0, 0, labelWidth, 25);

        container.add(panel);
        panel.setLocation(0, label.getHeight() + 20);

        int containerWidth = Math.max(panel.getWidth(), label.getWidth());
        container.setSize(containerWidth, 20 + panel.getHeight() + label.getHeight() + 20);

        return container;
    }

    protected void setUpWindow() {
        setLayout(new GridLayout(3, 2, 10, 10));

        add(createTextPanel(createSwitchPanel(), "Temperature Control"));

        final int LED_WIDTH = 75;
        final int LED_HEIGHT = 100;

        currentTimeDisplay = new TimeDisplay();
        currentTimeDisplay.setSize(LED_WIDTH * 4, LED_HEIGHT);
        add(createTextPanel(currentTimeDisplay, "Current Time"));

        indoorTemperatureDisplay = new TemperatureDisplay();
        indoorTemperatureDisplay.setSize(LED_WIDTH * 5, LED_HEIGHT);
        add(createTextPanel(indoorTemperatureDisplay, "Indoor Temperature"));

        outdoorTemperatureDisplay = new TemperatureDisplay();
        outdoorTemperatureDisplay.setSize(LED_WIDTH * 5, LED_HEIGHT);
        add(createTextPanel(outdoorTemperatureDisplay, "Outdoor Temperature"));

        powerConsumptionDisplay = new PowerDisplay();
        powerConsumptionDisplay.setSize(LED_WIDTH * 5, LED_HEIGHT);
        add(createTextPanel(powerConsumptionDisplay, "Power Consumption"));

        powerGenerationDisplay = new PowerDisplay();
        powerGenerationDisplay.setSize(LED_WIDTH * 5, LED_HEIGHT);
        add(createTextPanel(powerGenerationDisplay, "Power Generation"));

        setSize(800, 500);
    }

    private Panel createSwitchPanel() {
        Panel switchPanel = new Panel(null);
        onOffSwitch = new ToggleSwitch();
        onOffSwitch.addSwitchListener(new SwitchListener() {
            public void switchChange(SwitchEvent e) {
                setControlOn(e.isSwitchedOn());
                sendMessage();
            }
        });
        onOffSwitch.setBounds(0, 0, 120, 120);
        switchPanel.add(onOffSwitch);

        onLabel = new Label();
        onLabel.setText("ON");
        onLabel.setFont(STANDARD_FONT);
        switchPanel.add(onLabel);
        onLabel.setBounds(onOffSwitch.getWidth() + 10, 20, 50, 50);
        offLabel = new Label();
        offLabel.setText("OFF");
        offLabel.setFont(STANDARD_FONT);
        switchPanel.add(offLabel);
        offLabel.setBounds(onLabel.getX(), 70, 50, 50);
        switchPanel.setSize(onOffSwitch.getWidth() + 10 + offLabel.getWidth(), onOffSwitch.getHeight());
        return switchPanel;
    }

    protected void updateDisplay() {
        onLabel.setForeground(isControlOn() ? Color.green : Color.black);
        offLabel.setForeground(isControlOn() ? Color.black : Color.red);
        currentTimeDisplay.displayTime(getCurrentTime());
        indoorTemperatureDisplay.displayTemperature(getIndoorTemperature());
        outdoorTemperatureDisplay.displayTemperature(getOutdoorTemperature());
        powerConsumptionDisplay.displayPower(getPowerConsumed());
        powerGenerationDisplay.displayPower(getPowerGenerated());
    }

    public EnvironmentClientMaster(EnvironmentSocketThread socketThread) {
        super(socketThread);
    }

    public static void main(String[] args) {
        EnvironmentSocketThread socketThread = null;
        try {
            Socket clientSocket = new Socket("localhost", EnvironmentSocketServer.PORT);
            socketThread = new EnvironmentSocketThread(clientSocket);
            socketThread.connect();
        } catch(IOException e) {
            e.printStackTrace();
        }
        EnvironmentClientMaster clientMaster = new EnvironmentClientMaster(socketThread);
        clientMaster.sendMessage();
    }

}
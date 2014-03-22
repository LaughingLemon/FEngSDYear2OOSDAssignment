package bromley.bopak3.client;

//project and component imports

import bromley.bopak3.client.components.*;
import bromley.bopak3.common.EnvironmentSocketThread;
import bromley.bopak3.server.EnvironmentSocketServer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//standard library imports

//extends the base class where the networking management is
//and the display variables are stored
public class EnvironmentClientMaster extends EnvironmentClientDisplay {

    private static final int LED_WIDTH = 50;
    private static final int LED_HEIGHT = 75;
    //display components, Panel derivatives mostly
    private ToggleSwitch controlOnOffSwitch;
    private TimeDisplay currentTimeDisplay;
    private TemperatureDisplay indoorTemperatureDisplay;
    private TemperatureDisplay outdoorTemperatureDisplay;
    private PowerDisplay powerConsumptionDisplay;
    private PowerDisplay powerGenerationDisplay;
    private Label controlOnLabel;
    private Label controlOffLabel;
    private ToggleSwitch windowTintOnOffSwitch;
    private Label windowTintOnLabel;
    private Label windowTintOffLabel;
    private TemperatureDisplay temperatureSettings;
    private Label degreesLabel;
    private Label connectionLabel;

    //font defined centrally
    private static final Font STANDARD_FONT = new Font("Sans-serif", Font.BOLD, 24);

    //utility method to create a panel to contain a component
    //with a little label above it
    private static Panel createTextPanel(Component component, String headerText) {
        //create a container Panel
        Panel container = new Panel();
        container.setLayout(null);
        //create a label
        Label label = new Label(headerText);
        label.setFont(STANDARD_FONT);
        container.add(label);
        //set the size and location of the label (either 
        //25 * number of characters, or the width of the panel, whichever is bigger)
        int labelWidth = Math.max(headerText.length() * 25, component.getWidth());
        label.setBounds(0, 0, labelWidth, 25);

        //add the component contained, and set the size and location
        container.add(component);
        component.setLocation(0, label.getHeight() + 20);

        //figure out the size of the container, either
        //the width of the label or the width of the component
        int containerWidth = Math.max(component.getWidth(), label.getWidth());
        //the height is combination of all the component heights and a border of 20
        container.setSize(containerWidth, 20 + component.getHeight() + label.getHeight() + 20);

        //return the container with the components
        return container;
    }

    protected void setUpWindow() {
        setLayout(new GridLayout(3, 3, 5, 5));

        //add the control switch panel
        add(createTextPanel(createControlPanel(), "Temperature Control"));
        //add the window tint control panel
        add(createTextPanel(createWindowTintPanel(), "Window Tint"));

        //add the time display panel
        currentTimeDisplay = new TimeDisplay();
        currentTimeDisplay.setSize(LED_WIDTH * 4 + 10, LED_HEIGHT);
        add(createTextPanel(currentTimeDisplay, "Current Time"));

        //add temperature setting panel
        add(createTextPanel(createTemperatureSettings(), "Temperature Control"));

        //add indoor temperature display
        indoorTemperatureDisplay = new TemperatureDisplay();
        indoorTemperatureDisplay.setSize(LED_WIDTH * 5 + 10, LED_HEIGHT);
        add(createTextPanel(indoorTemperatureDisplay, "Indoor Temperature"));

        //add outdoor temperature display
        outdoorTemperatureDisplay = new TemperatureDisplay();
        outdoorTemperatureDisplay.setSize(LED_WIDTH * 5 + 10, LED_HEIGHT);
        add(createTextPanel(outdoorTemperatureDisplay, "Outdoor Temperature"));

        //add power consumption display
        powerConsumptionDisplay = new PowerDisplay();
        powerConsumptionDisplay.setSize(LED_WIDTH * 5 + 10, LED_HEIGHT);
        add(createTextPanel(powerConsumptionDisplay, "Power Consumption"));

        //add power generation display
        powerGenerationDisplay = new PowerDisplay();
        powerGenerationDisplay.setSize(LED_WIDTH * 5 + 10, LED_HEIGHT);
        add(createTextPanel(powerGenerationDisplay, "Power Generation"));

        //add the configuration options
        add(createTextPanel(createOptionsPanel(), "Configuration"));

        setSize(900, 500);
    }

    private Panel createOptionsPanel() {
        //details various configuration options
        Panel optionsPanel = new Panel(null);

        //sets either celsius or fahrenheit
        Button degreesSetting = (Button) addComponentToPanel(new Button(CELSIUS_FAHRENHEIT_LABEL),
                STANDARD_FONT, 0, 0, 250, 35, optionsPanel);
        degreesSetting.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //change the internal field
                setInCelsius(!isInCelsius());
                //set the label to illustrate
                degreesLabel.setText(isInCelsius() ? TemperatureDisplay.DEGREES_C : TemperatureDisplay.DEGREES_F);
                //set the various temperature displays.
                //Conversion is handled internally
                indoorTemperatureDisplay.setDisplayInCelsius(isInCelsius());
                outdoorTemperatureDisplay.setDisplayInCelsius(isInCelsius());
                temperatureSettings.setDisplayInCelsius(isInCelsius());
                //and update everything
                updateDisplay();
            }
        });

        //a little label to show what the degree type is
        degreesLabel = new Label(TemperatureDisplay.DEGREES_F);
        degreesLabel = (Label) addComponentToPanel(degreesLabel, STANDARD_FONT,
                0, degreesSetting.getHeight() + 5, 50, 50, optionsPanel);

        //something to indicate whether we're connected to the server
        connectionLabel = new Label("Disconnected");
        connectionLabel = (Label) addComponentToPanel(connectionLabel, STANDARD_FONT,
                degreesLabel.getWidth() + 5, degreesLabel.getY(), 200, 50, optionsPanel);

        //set the height and width according to the contained components
        optionsPanel.setSize(Math.max(degreesSetting.getWidth(),
                connectionLabel.getX() + connectionLabel.getWidth()),
                degreesLabel.getY() + degreesLabel.getHeight());

        //and return the panel
        return optionsPanel;
    }

    private Panel createTemperatureSettings() {
        //contains controls to change the temperature
        Panel temperatureControlPanel = new Panel(null);

        //use a temperature display
        temperatureSettings = (TemperatureDisplay) addComponentToPanel(new TemperatureDisplay(), getFont(),
                0, 0, LED_WIDTH * 4 + 10, LED_HEIGHT, temperatureControlPanel);

        //a set of buttons to increase the temperature
        Button upButton = (Button) addComponentToPanel(new Button("UP"), getFont(),
                temperatureSettings.getWidth(), 0, 50, 25, temperatureControlPanel);
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setRequiredTemperature(getRequiredTemperature() + 1);
                updateDisplay();
            }
        });

        //...decrease the temperature
        Button downButton = (Button) addComponentToPanel(new Button("DOWN"), getFont(),
                upButton.getX(), upButton.getHeight(), 50, 25, temperatureControlPanel);
        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setRequiredTemperature(getRequiredTemperature() - 1);
                updateDisplay();
            }
        });

        //...and send the information to the server
        Button setButton = (Button) addComponentToPanel(new Button("SET"), getFont(),
                downButton.getX(), downButton.getY() + downButton.getHeight(), 50, 25, temperatureControlPanel);
        setButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        //and set the size depending on the components
        temperatureControlPanel.setSize(upButton.getX() + upButton.getWidth(),
                Math.max(temperatureSettings.getHeight(), setButton.getY() + setButton.getHeight()));

        //return the panel for display
        return temperatureControlPanel;
    }

    private Panel createWindowTintPanel() {
        //create the switch components
        windowTintOnOffSwitch = new ToggleSwitch();
        windowTintOnLabel = new Label();
        windowTintOffLabel = new Label();
        //create the panel with the components on it
        Panel windowTintPanel = createSwitchPanel(windowTintOnOffSwitch, windowTintOnLabel, windowTintOffLabel);
        //add a listener to be able to change the setting
        windowTintOnOffSwitch.addSwitchListener(new SwitchListener() {
            public void switchChange(SwitchEvent e) {
                //set the internal field
                setWindowTintOn(e.isSwitchedOn());
                //send the details to the server
                sendMessage();
            }
        });
        //and return the panel for display
        return windowTintPanel;
    }

    private Panel createControlPanel() {
        //as the window tint
        controlOnOffSwitch = new ToggleSwitch();
        controlOnLabel = new Label();
        controlOffLabel = new Label();
        Panel controlPanel = createSwitchPanel(controlOnOffSwitch, controlOnLabel, controlOffLabel);
        controlOnOffSwitch.addSwitchListener(new SwitchListener() {
            public void switchChange(SwitchEvent e) {
                setControlOn(e.isSwitchedOn());
                sendMessage();
            }
        });
        return controlPanel;
    }

    //because the switches are similar, use this method to
    //size and position them
    private Panel createSwitchPanel(ToggleSwitch toggleSwitch, Label onLabel, Label offLabel) {
        Panel switchPanel = new Panel(null);
        toggleSwitch.setBounds(10, 0, 75, 75);
        switchPanel.add(toggleSwitch);

        final int TEXT_WIDTH = 50;
        final int TEXT_HEIGHT = 25;

        onLabel.setText("ON");
        onLabel.setFont(STANDARD_FONT);
        switchPanel.add(onLabel);
        onLabel.setBounds(toggleSwitch.getWidth() + 10, toggleSwitch.getY(), TEXT_WIDTH, TEXT_HEIGHT);

        offLabel.setText("OFF");
        offLabel.setFont(STANDARD_FONT);
        switchPanel.add(offLabel);
        offLabel.setBounds(onLabel.getX(), toggleSwitch.getY() + toggleSwitch.getHeight() - TEXT_HEIGHT, TEXT_WIDTH, TEXT_HEIGHT);

        switchPanel.setSize(toggleSwitch.getWidth() + 10 + onLabel.getWidth(), toggleSwitch.getHeight());

        return switchPanel;
    }

    protected void updateDisplay() {
        //indicate whether the server is connected
        connectionLabel.setText(getSocketThread().isConnected() ? "Connected" : "Disconnected");
        connectionLabel.setForeground(getSocketThread().isConnected() ? Color.green : Color.red);
        //...update the temperature controls
        controlOnLabel.setForeground(isControlOn() ? Color.green : Color.black);
        controlOffLabel.setForeground(!isControlOn() ? Color.red : Color.black);
        //...the time
        currentTimeDisplay.displayTime(getCurrentTime());
        //...the indoor and outdoor temperatures
        indoorTemperatureDisplay.displayTemperature(getIndoorTemperature());
        outdoorTemperatureDisplay.displayTemperature(getOutdoorTemperature());
        //...the power consumption and generation
        powerConsumptionDisplay.displayPower(getPowerConsumed());
        powerGenerationDisplay.displayPower(getPowerGenerated());
        //...the window tint
        windowTintOnLabel.setForeground(isWindowTintOn() ? Color.green : Color.black);
        windowTintOffLabel.setForeground(!isWindowTintOn() ? Color.red : Color.black);
        //...and temperature settings
        temperatureSettings.displayTemperature(getRequiredTemperature());
    }

    public EnvironmentClientMaster(EnvironmentSocketThread socketThread) {
        super(socketThread);
    }

    public static void main(String[] args) {
        //create a socket thread component to connect to the server
        EnvironmentSocketThread socketThread = new EnvironmentSocketThread();
        //should work unless the server is down
        socketThread.connect("localhost", EnvironmentSocketServer.PORT);
        //create the master control panel object
        EnvironmentClientMaster clientMaster = new EnvironmentClientMaster(socketThread);
        //send a message
        clientMaster.sendMessage();
        //...and update the display (in case it's disconnected)
        clientMaster.updateDisplay();
    }

}
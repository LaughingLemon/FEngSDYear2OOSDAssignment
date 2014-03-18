package bromley.bopak3.client;

import bromley.bopak3.common.EnvironmentSocketThread;
import bromley.bopak3.common.EnvironmentTime;
import bromley.bopak3.server.EnvironmentSocketServer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;

public class EnvironmentClientRemote extends EnvironmentClientDisplay {

    //these are the display objects
    //simple labels and buttons, partly
    //chosen to contrast with the master display
    //nice, though
    private Button onOffSwitch;
    private Label currentTimeDisplay;
    private Label indoorTemperature;
    private Label outdoorTemperature;
    private Label powerConsumptionDisplay;
    private Label powerGenerationDisplay;
    private Label onLabel;
    private Label offLabel;
    private Button windowTintOnSwitch;
    private Label windowTintOnLabel;
    private Label windowTintOffLabel;
    private Label temperatureRequired;

    //constructor
    public EnvironmentClientRemote(EnvironmentSocketThread socketThread) {
        super(socketThread);
    }

    //these objects can all be declared as static as used as such
    //fonts used in the application
    private static final Font SMALLER_FONT = new Font("Sans-serif", Font.BOLD, 18);
    private static final Font STANDARD_FONT = new Font("Sans-serif", Font.BOLD, 24);
    private static final Font BIG_FONT = new Font("Sans-serif", Font.BOLD, 36);
    //for formatting numbers
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");

    //declared here to enable the "cards" to be "turned"
    //by the buttons at the head of the form
    private CardLayout cardLayout;
    private Panel cardPanel;

    protected void setUpWindow() {
        //using BorderLayout for the whole Frame
        //can be useful, if used carefully
        //(better than GridLayout)
        setLayout(new BorderLayout());

        //add the button panel to the top
        add(createButtonPanel(), BorderLayout.NORTH);

        //create the card layout panel. CardLayout allows you to pack
        //a large number of components into a small display size
        cardLayout = new CardLayout();
        cardPanel = new Panel(cardLayout);

        //add a control panel
        cardPanel.add(createOnOffPanel(), "On Off Panel");
        //add the temperature display panel
        cardPanel.add(createTemperaturePanel(), "Temperature");
        //add the power production\consumption panel
        cardPanel.add(createPowerPanel(), "Power Panel");

        //finally, add the card layout to the middle
        //this expands the panel to fill out the
        //rest of the form
        add(cardPanel, BorderLayout.CENTER);

        //and set the size to something like a mobile
        //phone
        setSize(250, 400);
    }

    private Panel createTemperaturePanel() {
        //setting the layout to null allows the components to
        //bet set precisely using setBounds (or setLocation)
        Panel temperaturePanel = new Panel(null);

        //actual indoor temperature
        //informative label
        Label indoorTemperatureHeaderLabel = new Label("Indoor Temperature");
        indoorTemperatureHeaderLabel.setFont(SMALLER_FONT);
        indoorTemperatureHeaderLabel.setBounds(10, 10, 250, 30);
        temperaturePanel.add(indoorTemperatureHeaderLabel);
        //and the temperature display
        indoorTemperature = new Label("-000");
        indoorTemperature.setFont(BIG_FONT);
        indoorTemperature.setBounds(10, 40, 75, 50);
        temperaturePanel.add(indoorTemperature);
        //the degree label. Changed when using Celsius
        Label indoorTemperatureDegrees = new Label("°F");
        indoorTemperatureDegrees.setFont(STANDARD_FONT);
        indoorTemperatureDegrees.setBounds(100, 50, 30, 35);
        temperaturePanel.add(indoorTemperatureDegrees);

        //outdoor temperature display
        //nice label
        Label outdoorTemperatureHeaderLabel = new Label("Outdoor Temperature");
        outdoorTemperatureHeaderLabel.setFont(SMALLER_FONT);
        outdoorTemperatureHeaderLabel.setBounds(10, 100, 200, 30);
        temperaturePanel.add(outdoorTemperatureHeaderLabel);
        //and the display itself
        outdoorTemperature = new Label("-000");
        outdoorTemperature.setFont(BIG_FONT);
        outdoorTemperature.setBounds(10, 130, 75, 50);
        temperaturePanel.add(outdoorTemperature);
        //and the degree type label
        Label outdoorTemperatureDegrees = new Label("°F");
        outdoorTemperatureDegrees.setFont(STANDARD_FONT);
        outdoorTemperatureDegrees.setBounds(100, 140, 30, 35);
        temperaturePanel.add(outdoorTemperatureDegrees);

        //required temperature display
        //header label
        Label temperatureRequiredHeader = new Label("Set Temperature");
        temperatureRequiredHeader.setFont(SMALLER_FONT);
        temperatureRequiredHeader.setBounds(10, 190, 150, 30);
        temperaturePanel.add(temperatureRequiredHeader);
        //display itself
        temperatureRequired = new Label("00");
        temperatureRequired.setFont(BIG_FONT);
        temperatureRequired.setBounds(10, 220, 45, 50);
        temperaturePanel.add(temperatureRequired);
        //button for increasing the set temperature
        Button temperatureUp = new Button("UP");
        temperatureUp.setFont(SMALLER_FONT);
        temperatureUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //when the button is clicked, decrement the temperature
                setRequiredTemperature(getRequiredTemperature() + 1);
                //set the required temperature display
                temperatureRequired.setText(DECIMAL_FORMAT.format(getRequiredTemperature()));
            }
        });
        temperatureUp.setBounds(70, 220, 80, 30);
        temperaturePanel.add(temperatureUp);
        //button for decreasing the set temperature
        Button temperatureDown = new Button("DOWN");
        temperatureDown.setFont(SMALLER_FONT);
        temperatureDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //when the button is clicked, decrement the temperature
                setRequiredTemperature(getRequiredTemperature() - 1);
                //set the required temperature display
                temperatureRequired.setText(DECIMAL_FORMAT.format(getRequiredTemperature()));
            }
        });
        temperatureDown.setBounds(70, 250, 80, 30);
        temperaturePanel.add(temperatureDown);
        //button to send the temperature data to the server
        Button temperatureSet = new Button("SET");
        temperatureSet.setFont(STANDARD_FONT);
        temperatureSet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //uses the base class message sender function
                sendMessage();
            }
        });
        temperatureSet.setBounds(150, 220, 75, 60);
        temperaturePanel.add(temperatureSet);

        //and return the nicely laid out panel
        return temperaturePanel;
    }

    private Panel createButtonPanel() {
        //buttons that go at the to of the form
        //to allow the cards to be changed

        //yeah, GridLayout. Use in moderation (about the only useful
        //place for it)
        Panel buttonPanel = new Panel(new GridLayout(1, 2));

        //back button
        Button prevButton = new Button("Back");
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //go backwards through the card layout
                cardLayout.previous(cardPanel);
            }
        });
        buttonPanel.add(prevButton);
        //forwards button
        Button nextButton = new Button("Next");
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //go forwards through the card layout
                cardLayout.next(cardPanel);
            }
        });
        buttonPanel.add(nextButton);

        //and return the nice button panel
        return buttonPanel;
    }

    private Panel createOnOffPanel() {
        //contains most of the environment controls
        Panel onOffPanel = new Panel(null);

        Label temperatureControlLabel = new Label("Temperature Control");
        temperatureControlLabel.setFont(SMALLER_FONT);
        temperatureControlLabel.setBounds(10, 10, 190, 30);
        onOffPanel.add(temperatureControlLabel);

        onOffSwitch = new Button("On\\Off");
        onOffSwitch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setControlOn(!isControlOn());
                sendMessage();
            }
        });
        onOffSwitch.setFont(STANDARD_FONT);
        onOffSwitch.setBounds(10, 40, 110, 110);
        onOffPanel.add(onOffSwitch);

        onLabel = new Label("ON");
        onLabel.setFont(BIG_FONT);
        onLabel.setBounds(130, 40, 60, 50);
        onOffPanel.add(onLabel);

        offLabel = new Label("OFF");
        offLabel.setFont(BIG_FONT);
        offLabel.setBounds(130, 100, 75, 50);
        onOffPanel.add(offLabel);

        Label windowTintLabel = new Label("Window Tint");
        windowTintLabel.setFont(SMALLER_FONT);
        windowTintLabel.setBounds(10, 170, 190, 30);
        onOffPanel.add(windowTintLabel);

        windowTintOnSwitch = new Button("On\\Off");
        windowTintOnSwitch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setWindowTintOn(!isWindowTintOn());
                sendMessage();
            }
        });
        windowTintOnSwitch.setFont(STANDARD_FONT);
        windowTintOnSwitch.setBounds(10, 200, 110, 110);
        onOffPanel.add(windowTintOnSwitch);

        windowTintOnLabel = new Label("ON");
        windowTintOnLabel.setFont(BIG_FONT);
        windowTintOnLabel.setBounds(130, 200, 60, 50);
        onOffPanel.add(windowTintOnLabel);

        windowTintOffLabel = new Label("OFF");
        windowTintOffLabel.setFont(BIG_FONT);
        windowTintOffLabel.setBounds(130, 260, 75, 50);
        onOffPanel.add(windowTintOffLabel);

        return onOffPanel;
    }

    private Panel createPowerPanel() {
        Panel powerPanel = new Panel(null);

        Label powerConsumptionHeaderLabel = new Label("Power Consumption");
        powerConsumptionHeaderLabel.setFont(SMALLER_FONT);
        powerConsumptionHeaderLabel.setBounds(20, 130, 185, 30);
        powerPanel.add(powerConsumptionHeaderLabel);

        powerConsumptionDisplay = new Label("9000");
        powerConsumptionDisplay.setFont(BIG_FONT);
        powerConsumptionDisplay.setBounds(20, 60, 85, 50);
        powerPanel.add(powerConsumptionDisplay);

        Label powerConsumptionDisplayLabel = new Label("Watts");
        powerConsumptionDisplayLabel.setFont(STANDARD_FONT);
        powerConsumptionDisplayLabel.setBounds(110, 70, 70, 35);
        powerPanel.add(powerConsumptionDisplayLabel);

        Label powerGenerationHeaderLabel = new Label("Power Generation");
        powerGenerationHeaderLabel.setFont(SMALLER_FONT);
        powerGenerationHeaderLabel.setBounds(20, 30, 165, 30);
        powerPanel.add(powerGenerationHeaderLabel);

        powerGenerationDisplay = new Label("9000");
        powerGenerationDisplay.setFont(BIG_FONT);
        powerGenerationDisplay.setBounds(20, 170, 85, 50);
        powerPanel.add(powerGenerationDisplay);

        Label powerGenerationDisplayLabel = new Label("Watts");
        powerGenerationDisplayLabel.setFont(STANDARD_FONT);
        powerGenerationDisplayLabel.setBounds(110, 180, 70, 35);
        powerPanel.add(powerGenerationDisplayLabel);

        Label environmentTimeLabel = new Label("Environment Time");
        environmentTimeLabel.setFont(SMALLER_FONT);
        environmentTimeLabel.setBounds(20, 240, 180, 30);
        powerPanel.add(environmentTimeLabel);

        currentTimeDisplay = new Label("23:59:59");
        currentTimeDisplay.setFont(BIG_FONT);
        currentTimeDisplay.setBounds(20, 270, 150, 50);
        powerPanel.add(currentTimeDisplay);

        return powerPanel;
    }

    protected void updateDisplay() {
        onLabel.setForeground(isControlOn() ? Color.green : Color.black);
        offLabel.setForeground(isControlOn() ? Color.black : Color.red);
        windowTintOnLabel.setForeground(isWindowTintOn() ? Color.green : Color.black);
        windowTintOffLabel.setForeground(isWindowTintOn() ? Color.black : Color.red);

        powerConsumptionDisplay.setText(DECIMAL_FORMAT.format(getPowerConsumed()));
        powerGenerationDisplay.setText(DECIMAL_FORMAT.format(getPowerGenerated()));
        currentTimeDisplay.setText(EnvironmentTime.timeToString(getCurrentTime()));

        outdoorTemperature.setText(DECIMAL_FORMAT.format(getOutdoorTemperature()));
        indoorTemperature.setText(DECIMAL_FORMAT.format(getIndoorTemperature()));
        temperatureRequired.setText(DECIMAL_FORMAT.format(getRequiredTemperature()));
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
        EnvironmentClientRemote clientRemote = new EnvironmentClientRemote(socketThread);
        clientRemote.sendMessage();
    }

}
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

    //this is a utility function to replace repeated lines of code
    private Component addComponentToPanel(Component component, Font font, int x, int y, int width, int height, Panel panel) {
        //set the font
        component.setFont(font);
        //set the position and size
        component.setBounds(x, y, width, height);
        //add the component to the panel
        panel.add(component);
        //return the component, if it needs a reference
        //this will need to be down cast for specific component types
        return component;
    }

    private Panel createTemperaturePanel() {
        //setting the layout to null allows the components to
        //be set precisely using setBounds (or setLocation)
        Panel temperaturePanel = new Panel(null);

        //actual indoor temperature
        //informative label
        addComponentToPanel(new Label("Indoor Temperature"), SMALLER_FONT, 10, 10, 250, 30, temperaturePanel);
        //and the temperature display. note the down casting to a label
        indoorTemperature = (Label) addComponentToPanel(new Label("-000"), BIG_FONT, 10, 40, 75, 50, temperaturePanel);
        //the degree label. Changed when using Celsius
        addComponentToPanel(new Label("°F"), STANDARD_FONT, 100, 50, 30, 35, temperaturePanel);

        //outdoor temperature display
        //nice label
        addComponentToPanel(new Label("Outdoor Temperature"), SMALLER_FONT, 10, 100, 200, 30, temperaturePanel);
        //and the display itself
        outdoorTemperature = (Label) addComponentToPanel(new Label("-000"), BIG_FONT, 10, 130, 75, 50, temperaturePanel);
        //and the degree type label
        addComponentToPanel(new Label("°F"), STANDARD_FONT, 100, 140, 30, 35, temperaturePanel);

        //required temperature display
        //header label
        addComponentToPanel(new Label("Set Temperature"), SMALLER_FONT, 10, 190, 150, 30, temperaturePanel);
        //display itself
        temperatureRequired = (Label) addComponentToPanel(new Label("00"), BIG_FONT, 10, 220, 45, 50, temperaturePanel);
        //button for increasing the set temperature. note the down casting to a button
        Button temperatureUp = (Button) addComponentToPanel(new Button("UP"), SMALLER_FONT, 70, 220, 80, 30, temperaturePanel);
        temperatureUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //when the button is clicked, increment the temperature
                setRequiredTemperature(getRequiredTemperature() + 1);
                //set the required temperature display
                temperatureRequired.setText(DECIMAL_FORMAT.format(getRequiredTemperature()));
            }
        });
        temperaturePanel.add(temperatureUp);
        //button for decreasing the set temperature
        Button temperatureDown = (Button) addComponentToPanel(new Button("DOWN"), SMALLER_FONT, 70, 250, 80, 30, temperaturePanel);
        temperatureDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //when the button is clicked, decrement the temperature
                setRequiredTemperature(getRequiredTemperature() - 1);
                //set the required temperature display
                temperatureRequired.setText(DECIMAL_FORMAT.format(getRequiredTemperature()));
            }
        });
        //button to send the temperature data to the server
        Button temperatureSet = (Button) addComponentToPanel(new Button("SET"), STANDARD_FONT, 150, 220, 75, 60, temperaturePanel);
        temperatureSet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //uses the base class message sender function
                sendMessage();
            }
        });

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

        //create a nice label
        addComponentToPanel(new Label("Temperature Control"), SMALLER_FONT, 10, 10, 190, 30, onOffPanel);

        //add the temperature control on\off button
        onOffSwitch = (Button) addComponentToPanel(new Button("On\\Off"), STANDARD_FONT, 10, 40, 110, 110, onOffPanel);
        onOffSwitch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setControlOn(!isControlOn());
                sendMessage();
            }
        });

        //add the feedback labels, indicating whether the setting is on or off
        onLabel = (Label) addComponentToPanel(new Label("ON"), BIG_FONT, 130, 40, 60, 50, onOffPanel);
        offLabel = (Label) addComponentToPanel(new Label("OFF"), BIG_FONT, 130, 100, 75, 50, onOffPanel);

        //create a nice label for the windows tint
        addComponentToPanel(new Label("Window Tint"), SMALLER_FONT, 10, 170, 190, 30, onOffPanel);

        //add the control button for the tint
        windowTintOnSwitch = (Button) addComponentToPanel(new Button("On\\Off"), STANDARD_FONT, 10, 200, 110, 110, onOffPanel);
        windowTintOnSwitch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setWindowTintOn(!isWindowTintOn());
                sendMessage();
            }
        });

        //add feedback labels
        windowTintOnLabel = (Label) addComponentToPanel(new Label("ON"), BIG_FONT, 130, 200, 60, 50, onOffPanel);
        windowTintOffLabel = (Label) addComponentToPanel(new Label("OFF"), BIG_FONT, 130, 260, 75, 50, onOffPanel);

        //return the panel to the card layout
        return onOffPanel;
    }

    private Panel createPowerPanel() {
        //contains the power display and the environment time
        Panel powerPanel = new Panel(null);

        //a nice label
        addComponentToPanel(new Label("Power Consumption"), SMALLER_FONT, 20, 130, 185, 30, powerPanel);
        //the power consumption display
        powerConsumptionDisplay = (Label) addComponentToPanel(new Label("9000"), BIG_FONT, 20, 60, 85, 50, powerPanel);
        //indicate that this is in watts
        addComponentToPanel(new Label("Watts"), STANDARD_FONT, 110, 70, 70, 35, powerPanel);
        //another helpful label
        addComponentToPanel(new Label("Power Generation"), SMALLER_FONT, 20, 30, 165, 30, powerPanel);
        //the power generation display
        powerGenerationDisplay = (Label) addComponentToPanel(new Label("9000"), BIG_FONT, 20, 170, 85, 50, powerPanel);
        //also in watts
        addComponentToPanel(new Label("Watts"), STANDARD_FONT, 110, 180, 70, 35, powerPanel);
        //yet another helpful label
        addComponentToPanel(new Label("Environment Time"), SMALLER_FONT, 20, 240, 180, 30, powerPanel);
        //the current time in the environment
        currentTimeDisplay = (Label) addComponentToPanel(new Label("23:59:59"), BIG_FONT, 20, 270, 150, 50, powerPanel);
        //and return the panel for display in the card layout
        return powerPanel;
    }

    protected void updateDisplay() {
        //this is where the information from the server is displayed in the screen.
        //note that this is called in a synchronized function
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
package bromley.bopak3.client.components;

import laughing.lemon.components.LEDPanel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//Panel with LED's on it to show the time with
//convenient function to set the display
public class TimeDisplay extends LEDDisplayContainer {

    private LEDPanel minute10;
    private LEDPanel minute1;
    private LEDPanel hour10;
    private LEDPanel hour1;

    public TimeDisplay() {
        //sets the layout
        setLayout(createLayout(4));
        //creates the digits
        hour10 = new LEDPanel();
        hour10.setSize(50, 100);
        add(hour10);
        hour1 = new LEDPanel();
        hour1.setSize(50, 100);
        add(hour1);
        minute10 = new LEDPanel();
        minute10.setSize(50, 100);
        add(minute10);
        minute1 = new LEDPanel();
        minute1.setSize(50, 100);
        add(minute1);

        setSize(4 * 50, 100);
    }

    private static Calendar calendar = new GregorianCalendar();

    public void displayTime(Date time) {
        calendar.setTime(time);
        //parse the time into hours and minutes
        minute10.setDisplayNumber(calendar.get(Calendar.MINUTE) / 10);
        minute1.setDisplayNumber(calendar.get(Calendar.MINUTE) % 10);
        hour10.setDisplayNumber(calendar.get(Calendar.HOUR_OF_DAY) / 10);
        hour1.setDisplayNumber(calendar.get(Calendar.HOUR_OF_DAY) % 10);
    }
}

package bromley.bopak3.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EnvironmentTime {
    //simple wrapper around the SimpleDateFormat to just be able to parse
    //time

    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    public Date stringToTime(String timeString) throws ParseException {
        return formatter.parse(timeString);
    }

    public String timeToString(Date time) {
        return formatter.format(time);
    }

}

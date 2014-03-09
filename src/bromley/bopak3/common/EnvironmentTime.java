package bromley.bopak3.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class EnvironmentTime {
    //simple wrapper around the SimpleDateFormat to just be able to parse
    //and format time

    private static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    public static Date stringToTime(String timeString) {
        Date returnDate = null;
        try {
            returnDate = formatter.parse(timeString);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    public static String timeToString(Date time) {
        return formatter.format(time);
    }

    private static Calendar calendar = new GregorianCalendar();

    private static Date toNearestWholeMinute(Date d) {
        //from http://stackoverflow.com/a/8233614
        //set the calendar's time
        calendar.setTime(d);

        //if the time is more than 30 secs, add a minute
        if(calendar.get(Calendar.SECOND) >= 30)
            calendar.add(Calendar.MINUTE, 1);

        //lop off the seconds
        calendar.set(Calendar.SECOND, 0);

        //return the new time
        return calendar.getTime();
    }

    public static Date getCurrentTime() {
        //get the current time and round it to the
        //nearest minute
        return toNearestWholeMinute(new Date());
    }

    public static String timeInEnvironment(Date time) {
        //save the current time zone
        TimeZone timeZone = formatter.getTimeZone();
        //change the time zone to Pacific Standard Time (U.S.)
        formatter.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
        //convert the time to a string, which will be in PST
        String timeStr = formatter.format(time);
        //reset the time zone
        formatter.setTimeZone(timeZone);
        //and return the time as a string
        return timeStr;
    }

}

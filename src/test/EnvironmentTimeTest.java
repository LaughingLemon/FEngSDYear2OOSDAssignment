import bromley.bopak3.common.EnvironmentTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * EnvironmentTime Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Mar 30, 2014</pre>
 */

@RunWith(JUnit4.class)
public class EnvironmentTimeTest {

    /**
     * Method: stringToTime(String timeString)
     */
    @Test
    public void testStringToTime() throws Exception {
        Calendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0);
        //simple test to make sure that the time can be parsed and converted back
        assertEquals("Dates not the same", calendar.getTime(), EnvironmentTime.stringToTime("00:00:00"));
        calendar = new GregorianCalendar(1970, 0, 1, 23, 59);
        assertEquals("Dates not the same", calendar.getTime(), EnvironmentTime.stringToTime("23:59:00"));
    }

    /**
     * Method: timeToString(Date time)
     */
    @Test
    public void testTimeToString() throws Exception {
        Calendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0);
        //simple test to make sure that the time can be parsed and converted back
        assertEquals("Dates not the same", "00:00:00", EnvironmentTime.timeToString(calendar.getTime()));
        calendar = new GregorianCalendar(1970, 0, 1, 23, 59);
        assertEquals("Dates not the same", "23:59:00", EnvironmentTime.timeToString(calendar.getTime()));
    }

    /**
     * Method: timeInEnvironment(Date time)
     */
    @Test
    public void testTimeInEnvironment() throws Exception {
        Calendar calendar = new GregorianCalendar(1970, 0, 0, 9, 0);
        assertEquals("Dates not the same", "00:00:00", EnvironmentTime.timeInEnvironment(calendar.getTime()));
    }

} 

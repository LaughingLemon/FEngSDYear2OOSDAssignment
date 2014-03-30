import bromley.bopak3.server.EnvironmentData;
import bromley.bopak3.server.EnvironmentDataReader;
import bromley.bopak3.server.EnvironmentDataReaderInterface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.StringReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * EnvironmentDataReader Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Mar 30, 2014</pre>
 */

@RunWith(JUnit4.class)
public class EnvironmentDataReaderTest {

    /**
     * Method: loadDataSource(Reader dataReader)
     */
    @Test
    public void testReadSingleRecord() throws Exception {
        StringReader sr = new StringReader("Time,Temp,Solar,Wind\n" +
                "00:00:00,-142.5953,9600,4.354906");
        EnvironmentDataReaderInterface dr = new EnvironmentDataReader();
        dr.loadDataSource(sr);

        Calendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);

        EnvironmentData dataObj;
        //should be the data for 00:00:00
        dataObj = dr.getNextEnvironmentData();
        assertEquals("Wrong timestamp", calendar.getTime(), dataObj.getTimeStamp());
        assertEquals("Outdoor temperature wrong", -142.59, dataObj.getOutdoorTemperature(), 0.01);
        assertEquals("Solar Panel output wrong", 9600.0, dataObj.getSolarPanelOutput(), 0.01);
        assertEquals("Wind Turbine output wrong", 4.35, dataObj.getWindTurbineOutput(), 0.01);
        //should also be the data for 00:00:00
        dataObj = dr.getNextEnvironmentData();
        assertEquals("Wrong timestamp", calendar.getTime(), dataObj.getTimeStamp());
        assertEquals("Outdoor temperature wrong", -142.59, dataObj.getOutdoorTemperature(), 0.01);
        assertEquals("Solar Panel output wrong", 9600.0, dataObj.getSolarPanelOutput(), 0.01);
        assertEquals("Wind Turbine output wrong", 4.35, dataObj.getWindTurbineOutput(), 0.01);
    }

    /**
     * Method: getEnvironmentData()
     */
    @Test
    public void testReadMultipleRecord() throws Exception {
        StringReader sr = new StringReader("Time,Temp,Solar,Wind\n" +
                "00:00:00,-142.5953,9600,4.354906\n" +
                "00:01:00,-142.5981,9600,257.802553");
        EnvironmentDataReaderInterface dr = new EnvironmentDataReader();
        dr.loadDataSource(sr);

        Calendar calendar;
        calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);

        EnvironmentData dataObj;
        //should be the data for 00:00:00
        dataObj = dr.getNextEnvironmentData();
        assertEquals("Wrong timestamp", calendar.getTime(), dataObj.getTimeStamp());
        assertEquals("Outdoor temperature wrong", -142.5953, dataObj.getOutdoorTemperature(), 0.0001);
        assertEquals("Solar Panel output wrong", 9600.0, dataObj.getSolarPanelOutput(), 0.01);
        assertEquals("Wind Turbine output wrong", 4.35, dataObj.getWindTurbineOutput(), 0.01);
        //should be the data for 00:01:00
        calendar = new GregorianCalendar(1970, 0, 1, 0, 1, 0);
        dataObj = dr.getNextEnvironmentData();
        assertEquals("Wrong timestamp", calendar.getTime(), dataObj.getTimeStamp());
        assertEquals("Outdoor temperature wrong", -142.5981, dataObj.getOutdoorTemperature(), 0.0001);
        assertEquals("Solar Panel output wrong", 9600.0, dataObj.getSolarPanelOutput(), 0.01);
        assertEquals("Wind Turbine output wrong", 257.8, dataObj.getWindTurbineOutput(), 0.01);
        //should be the data for 00:00:00 again
        dataObj = dr.getNextEnvironmentData();
        calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
        assertEquals("Wrong timestamp", calendar.getTime(), dataObj.getTimeStamp());
        assertEquals("Outdoor temperature wrong", -142.5953, dataObj.getOutdoorTemperature(), 0.0001);
        assertEquals("Solar Panel output wrong", 9600.0, dataObj.getSolarPanelOutput(), 0.01);
        assertEquals("Wind Turbine output wrong", 4.35, dataObj.getWindTurbineOutput(), 0.01);
    }

    /**
     * Method: getNextEnvironmentData()
     */
    @Test
    public void testGetNextEnvironmentData() throws Exception {
        StringReader sr = new StringReader("Time,Temp,Solar,Wind\n" +
                "00:00:00,-142.5953,9600,4.354906\n" +
                "00:01:00,-142.5981,9600,257.802553\n" +
                "00:02:00,-142.6047,9600,28.508035\n" +
                "00:03:00,-142.613,9600,34.62416");
        EnvironmentDataReaderInterface dr = new EnvironmentDataReader();
        dr.loadDataSource(sr);

        Calendar calendar;
        calendar = new GregorianCalendar(1970, 0, 1, 0, 2, 0);

        EnvironmentData dataObj;
        //should be the data for 00:02:00
        dataObj = dr.getEnvironmentDataAtTime("00:02:00");
        assertEquals("Wrong timestamp", calendar.getTime(), dataObj.getTimeStamp());
        assertEquals("Outdoor temperature wrong", -142.6047, dataObj.getOutdoorTemperature(), 0.0001);
        assertEquals("Solar Panel output wrong", 9600.0, dataObj.getSolarPanelOutput(), 0.01);
        assertEquals("Wind Turbine output wrong", 28.5, dataObj.getWindTurbineOutput(), 0.01);
        //should be the data for 00:03:00
        calendar = new GregorianCalendar(1970, 0, 1, 0, 3, 0);
        dataObj = dr.getNextEnvironmentData();
        assertEquals("Wrong timestamp", calendar.getTime(), dataObj.getTimeStamp());
        assertEquals("Outdoor temperature wrong", -142.613, dataObj.getOutdoorTemperature(), 0.001);
        assertEquals("Solar Panel output wrong", 9600.0, dataObj.getSolarPanelOutput(), 0.01);
        assertEquals("Wind Turbine output wrong", 34.62, dataObj.getWindTurbineOutput(), 0.01);
        //should be the data for 00:00:00 again
        calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
        dataObj = dr.getNextEnvironmentData();
        assertEquals("Wrong timestamp", calendar.getTime(), dataObj.getTimeStamp());
        assertEquals("Outdoor temperature wrong", -142.5953, dataObj.getOutdoorTemperature(), 0.0001);
        assertEquals("Solar Panel output wrong", 9600.0, dataObj.getSolarPanelOutput(), 0.01);
        assertEquals("Wind Turbine output wrong", 4.35, dataObj.getWindTurbineOutput(), 0.01);
    }

}

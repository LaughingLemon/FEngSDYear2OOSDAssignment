package test;

import bromley.bopak3.server.EnvironmentData;
import bromley.bopak3.server.EnvironmentDataReader;

import java.io.StringReader;
import java.text.ParseException;

public class EnvironmentDataReaderTest {

    private static void testReadSingleRecord() {
        System.out.println("testReadSingleRecord start");

        StringReader sr = new StringReader("Time,Temp,Solar,Wind\n" +
                "00:00:00,-142.5953,9600,4.354906");
        EnvironmentDataReader dr = new EnvironmentDataReader();
        dr.loadDataSource(sr);

        EnvironmentData dataObj;
        //should be the data for 00:00:00
        dataObj = dr.getNextEnvironmentData();
        outputEnvironmentData(dataObj);
        //should also be the data for 00:00:00
        dataObj = dr.getNextEnvironmentData();
        outputEnvironmentData(dataObj);

        System.out.println("testReadSingleRecord end");
    }

    private static void testReadMultipleRecord() {
        System.out.println("testReadMultipleRecord start");

        StringReader sr = new StringReader("Time,Temp,Solar,Wind\n" +
                "00:00:00,-142.5953,9600,4.354906\n" +
                "00:01:00,-142.5981,9600,257.802553");
        EnvironmentDataReader dr = new EnvironmentDataReader();
        dr.loadDataSource(sr);

        EnvironmentData dataObj;
        //should be the data for 00:00:00
        dataObj = dr.getNextEnvironmentData();
        outputEnvironmentData(dataObj);
        //should be the data for 00:01:00
        dataObj = dr.getNextEnvironmentData();
        outputEnvironmentData(dataObj);
        //should be the data for 00:00:00 again
        dataObj = dr.getNextEnvironmentData();
        outputEnvironmentData(dataObj);

        System.out.println("testReadMultipleRecord end");
    }

    private static void testReadSpecificRecord() {
        System.out.println("testReadSpecificRecord start");

        StringReader sr = new StringReader("Time,Temp,Solar,Wind\n" +
                "00:00:00,-142.5953,9600,4.354906\n" +
                "00:01:00,-142.5981,9600,257.802553\n" +
                "00:02:00,-142.6047,9600,28.508035\n" +
                "00:03:00,-142.613,9600,34.62416");
        EnvironmentDataReader dr = new EnvironmentDataReader();
        dr.loadDataSource(sr);

        EnvironmentData dataObj;
        //should be the data for 00:02:00
        try {
            dataObj = dr.getEnvironmentDataAtTime("00:02:00");
            outputEnvironmentData(dataObj);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        //should be the data for 00:03:00
        dataObj = dr.getNextEnvironmentData();
        outputEnvironmentData(dataObj);
        //should be the data for 00:00:00 again
        dataObj = dr.getNextEnvironmentData();
        outputEnvironmentData(dataObj);

        System.out.println("testReadSpecificRecord end");
    }

    private static void outputEnvironmentData(EnvironmentData dataObj) {
        //simple output function to save typing
        System.out.println("TimeStamp: " + dataObj.getTimeStamp());
        System.out.println("Outdoor Temperature: " + dataObj.getOutdoorTemperature());
        System.out.println("Solar Panel Output: " + dataObj.getSolarPanelOutput());
        System.out.println("Wind Turbine Output: " + dataObj.getWindTurbineOutput());
    }

    public static void main(String[] args) {
        System.out.println("EnvironmentDataReaderTest start");
        testReadSingleRecord();
        testReadMultipleRecord();
        testReadSpecificRecord();
        System.out.println("EnvironmentDataReaderTest end");
    }

}

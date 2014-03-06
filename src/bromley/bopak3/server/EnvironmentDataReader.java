package bromley.bopak3.server;

import bromley.bopak3.common.EnvironmentTime;

import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class EnvironmentDataReader implements EnvironmentDataReaderInterface {
    //list to keep collection of data from file
    private List<EnvironmentData> dataList = new ArrayList<EnvironmentData>();

    //current data item in list
    private int currentIndex = -1;

    public void loadDataSource(Reader dataReader) {
        //use a Scanner class to parse file data
        Scanner fileScanner = new Scanner(dataReader);
        //end of line or comma as delimiter
        fileScanner.useDelimiter("[,\n]");
        //if there is a next line, skip it
        if(fileScanner.hasNextLine())
            fileScanner.nextLine();
        //clear the list
        dataList.clear();
        //... and set the index
        currentIndex = -1;
        //while there's something to parse...
        while(fileScanner.hasNext()) {
            //create an new data object
            EnvironmentData dataObj = new EnvironmentData();
            try {
                //set the various values
                dataObj.setTimeStamp(EnvironmentTime.stringToTime(fileScanner.next()));
                dataObj.setOutdoorTemperature(fileScanner.nextDouble());
                dataObj.setSolarPanelOutput(fileScanner.nextDouble());
                dataObj.setWindTurbineOutput(fileScanner.nextDouble());
            } catch(ParseException e) {
                //in case we can't parse the date
                e.printStackTrace();
            }
            //add the object to the list
            dataList.add(dataObj);
        }
    }

    public EnvironmentData getNextEnvironmentData() {
        //if we're not at the end of the list, get the next one
        if(currentIndex < dataList.size() - 1) {
            currentIndex++;
        } else {
            //...and if we are, go back to the beginning
            currentIndex = 0;
        }
        //return the saved object
        return dataList.get(currentIndex);
    }

    public EnvironmentData getEnvironmentDataAtTime(Date time) {
        //start at the beginning
        currentIndex = -1;
        //go through all the objects
        for(EnvironmentData dataObj : dataList) {
            currentIndex++;
            //until we find the one for that time
            if(dataObj.getTimeStamp().equals(time)) {
                return dataObj;
            }
        }
        //if we can't find the right one, return null
        return null;
    }

    public EnvironmentData getEnvironmentDataAtTime(String timeString) {
        Date time = null;
        try {
            //convert string to time
            time = EnvironmentTime.stringToTime(timeString);
        } catch(ParseException e) {
            //trap the exception, if it happens
            e.printStackTrace();
        }
        //as above, but using a string input
        return getEnvironmentDataAtTime(time);
    }

}

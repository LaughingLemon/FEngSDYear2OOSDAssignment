package bromley.bopak3.server;

import bromley.bopak3.common.EnvironmentTime;

import java.io.Reader;
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
            //set the various values
            dataObj.setTimeStamp(EnvironmentTime.stringToTime(fileScanner.next()));
            dataObj.setOutdoorTemperature(fileScanner.nextDouble());
            dataObj.setSolarPanelOutput(fileScanner.nextDouble());
            dataObj.setWindTurbineOutput(fileScanner.nextDouble());
            //add the object to the list
            dataList.add(dataObj);
        }
    }


    public EnvironmentData getEnvironmentData() {
        //return the saved object
        return dataList.get(currentIndex);
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
        return getEnvironmentData();
    }

    public EnvironmentData getEnvironmentDataAtTime(Date time) {
        //start at the beginning
        currentIndex = -1;
        //go through all the objects
        for(EnvironmentData dataObj : dataList) {
            currentIndex++;
            //until we find the one for that time
            if(dataObj.getTimeStamp().equals(time)) {
                return getEnvironmentData();
            }
        }
        //if we can't find the right one, return null
        return null;
    }

    public EnvironmentData getEnvironmentDataAtTime(String timeString) {
        //convert string to time
        Date time = EnvironmentTime.stringToTime(timeString);
        //as above, but using a string input
        return getEnvironmentDataAtTime(time);
    }

}

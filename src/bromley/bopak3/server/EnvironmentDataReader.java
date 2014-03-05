package bromley.bopak3.server;

import bromley.bopak3.common.EnvironmentTime;

import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class EnvironmentDataReader implements EnvironmentDataReaderInterface {

    private List<EnvironmentData> dataList = new ArrayList<EnvironmentData>();
    private EnvironmentTime timeParser = new EnvironmentTime();

    private int currentIndex = -1;

    @Override
    public void loadDataSource(Reader dataReader) {
        Scanner fileScanner = new Scanner(dataReader);
        fileScanner.useDelimiter("[,\n]");
        if(fileScanner.hasNextLine())
            fileScanner.nextLine();
        dataList.clear();
        currentIndex = -1;
        while(fileScanner.hasNext()) {
            EnvironmentData dataObj = new EnvironmentData();
            try {
                dataObj.setTimeStamp(timeParser.stringToTime(fileScanner.next()));
                dataObj.setOutdoorTemperature(fileScanner.nextDouble());
                dataObj.setSolarPanelOutput(fileScanner.nextDouble());
                dataObj.setWindTurbineOutput(fileScanner.nextDouble());
            } catch(ParseException e) {
                e.printStackTrace();
            }
            dataList.add(dataObj);
        }
    }

    @Override
    public EnvironmentData getNextEnvironmentData() {
        if(currentIndex < dataList.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        return dataList.get(currentIndex);
    }

    @Override
    public EnvironmentData getEnvironmentDataAtTime(Date time) {
        currentIndex = -1;
        for(EnvironmentData dataObj: dataList) {
            currentIndex++;
            if(dataObj.getTimeStamp().equals(time)) {
                return dataObj;
            }
        }
        return null;
    }

    @Override
    public EnvironmentData getEnvironmentDataAtTime(String timString) throws ParseException {
       return getEnvironmentDataAtTime(timeParser.stringToTime(timString));
    }

}

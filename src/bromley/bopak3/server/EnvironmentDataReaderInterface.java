package bromley.bopak3.server;

import java.io.Reader;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by Shaun on 05/03/14.
 */
public interface EnvironmentDataReaderInterface {
    void loadDataSource(Reader dataReader);

    EnvironmentData getNextEnvironmentData();

    EnvironmentData getEnvironmentDataAtTime(Date time);

    EnvironmentData getEnvironmentDataAtTime(String timString) throws ParseException;
}

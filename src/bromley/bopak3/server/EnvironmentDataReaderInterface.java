package bromley.bopak3.server;

import java.io.Reader;
import java.util.Date;

//interface for the data reader (allows better unit testing)
public interface EnvironmentDataReaderInterface {
    void loadDataSource(Reader dataReader);

    EnvironmentData getNextEnvironmentData();

    EnvironmentData getEnvironmentDataAtTime(Date time);

    EnvironmentData getEnvironmentDataAtTime(String timString);
}

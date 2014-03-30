import bromley.bopak3.common.EnvironmentTemperature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

/**
 * EnvironmentTemperature Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Mar 30, 2014</pre>
 */

@RunWith(JUnit4.class)
public class EnvironmentTemperatureTest {

    /**
     * Method: fahrenheitToCelsius(double fahrenheit)
     */
    @Test
    public void testFahrenheitToCelsius() throws Exception {
        assertEquals(-12.22, EnvironmentTemperature.fahrenheitToCelsius(10), 0.2);
    }

    /**
     * Method: celsiusToFahrenheit(double celsius)
     */
    @Test
    public void testCelsiusToFahrenheit() throws Exception {
        assertEquals(50.0, EnvironmentTemperature.celsiusToFahrenheit(10), 0.2);
    }

} 

//Created by Shaun

import bromley.bopak3.common.EnvironmentTemperature;
import bromley.bopak3.server.EnvironmentCalculations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class EnvironmentCalculationsTest {

    @Test
    public void testHeatLossCalculations() throws Exception {
        EnvironmentCalculations calculations = new EnvironmentCalculations();
        assertEquals("Temperature drop in one minute", 0.2208,
                calculations.temperatureDropMinute(
                        calculations.heatLoss(
                                EnvironmentTemperature.fahrenheitToCelsius(-142),
                                25.0)
                ),
                0.0001
        );

        assertEquals("Temperature drop over 80 minutes", 17.669,
                calculations.temperatureDrop(
                        calculations.heatLoss(
                                EnvironmentTemperature.fahrenheitToCelsius(-142),
                                25.0),
                        80 * 60
                ),
                0.001
        );

        calculations = new EnvironmentCalculations(500, 100, 1000);
        assertEquals("Temperature drop w. different areas\\volume", 0.1116,
                calculations.temperatureDropMinute(
                        calculations.heatLoss(-100.0, 20.0)
                ),
                0.0001
        );
    }
}

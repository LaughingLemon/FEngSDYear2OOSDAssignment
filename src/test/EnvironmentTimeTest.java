package test;

import bromley.bopak3.common.EnvironmentTime;

import java.text.ParseException;

public class EnvironmentTimeTest {

    private static void testParseFromToString() {
        System.out.println("testParseFromToString start");

        //simple test to make sure that the time can be parsed and converted back
        try {
            System.out.println(EnvironmentTime.timeToString(EnvironmentTime.stringToTime("00:00:00")));
            System.out.println(EnvironmentTime.timeToString(EnvironmentTime.stringToTime("23:59:00")));
        } catch(ParseException e) {
            e.printStackTrace();
        }

        System.out.println("testParseFromToString end");
    }

    private static void testGetCurrentPST() {
        System.out.println("testGetCurrentPST start");
        System.out.println("Time: " + EnvironmentTime.timeInPST(EnvironmentTime.getCurrentTime()));
        System.out.println("testGetCurrentPST end");
    }

    public static void main(String[] args) {
        System.out.println("EnvironmentTimeTest start");
        testParseFromToString();
        testGetCurrentPST();
        System.out.println("EnvironmentTimeTest end");
    }

}

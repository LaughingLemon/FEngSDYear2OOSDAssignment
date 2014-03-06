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

    public static void main(String[] args) {
        System.out.println("EnvironmentTimeTest start");
        testParseFromToString();
        System.out.println("EnvironmentTimeTest end");
    }

}

package io.github.mxvc.utils;



import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Util {

    public static Date getNextTime(){

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
        Calendar now = Calendar.getInstance(timeZone);
        System.out.println("当前时区" + timeZone.getDisplayName());



        Location location = new Location("26.628081035604723", "106.65109092554405");

        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, timeZone);
        Calendar officialSunrise = calculator.getOfficialSunriseCalendarForDate(now);

        return officialSunrise.getTime();
    }




}
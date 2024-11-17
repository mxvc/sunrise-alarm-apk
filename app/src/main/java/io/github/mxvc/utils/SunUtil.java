package io.github.mxvc.utils;



import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cn.hutool.core.date.DateUtil;
import io.github.mxvc.BuildConfig;

public class SunUtil {



    public static Date getNextTime(Location location){
        if (BuildConfig.DEBUG) {
            return DateUtil.offsetMinute(new Date(), 1);
        }
        TimeZone timeZone = TimeZone.getDefault();
        Calendar now = Calendar.getInstance(timeZone);
        System.out.println("当前时区" + timeZone.getDisplayName());


        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, timeZone);
        Calendar sunrise = calculator.getCivilSunriseCalendarForDate(now);

        if(sunrise.getTime().getTime() < now.getTime().getTime()){
            Calendar tomorrow = DateUtil.tomorrow().toCalendar();
            sunrise = calculator.getCivilSunriseCalendarForDate(tomorrow);
        }


        return sunrise.getTime();
    }



}

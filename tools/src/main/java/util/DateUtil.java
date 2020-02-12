package util;

/*
import api.TimeZoneAPI;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
*/

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Modified by Yong Wu on 24.08.2018
 * Removed functions related to timezone
 */


/**
 * Created by felixboenke on 01.06.17.
 * Util class that formates the date into to the correct date format for the location analytics API
 * For getting the correct Timezone the google maps timeZone API is used
 * doc: https://developers.google.com/maps/documentation/timezone/intro?hl=de
 * Expected format for start-date with specific hour: yyyy-MM-dd'T'HH:00
 * Expected format for end-date with specific hour: yyyy-MM-dd'T'HH:00
 * Expected format for start-date: yyyy-MM-dd
 */
public class DateUtil {

    public static DateTimeFormatter dateTimeFormatterExact = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:EEEE");
    public static DateTimeFormatter dateTimeFormatterHour = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00");
    public static DateTimeFormatter dateTimeOnlyHour = DateTimeFormatter.ofPattern("HH:00");
    public static DateTimeFormatter dateTimeFormatterMonthDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter dateTimeFormatterMonth = DateTimeFormatter.ofPattern("MM");
    public static DateTimeFormatter dateTimeFormatterWeekDay = DateTimeFormatter.ofPattern("EEEE");
    public static DateTimeFormatter dateTimeFormatTaxiAPI = DateTimeFormatter.ofPattern("2016-01-dd'T'HH:00");


    public static String formatDate(ZonedDateTime date, DateTimeFormatter formatter) {
        return date.format(formatter);
    }

    public static long getCurrentUnixTimeStamp() {
        return Instant.now().getEpochSecond();
    }
    /*
    public static String getTimeZondeIdFromLatLon(double lat, double lon) {
        String response = getTimeZoneFromLocation(lat, lon);
        JsonObject timeZone = new Gson().fromJson(response, JsonObject.class);
        return timeZone.get("timeZoneId").getAsString();
    }

    public static String getDateFromLatLon(double lat, double lon, DateTimeFormatter formatter) {
        ZoneId zoneId = getZoneIdFromString(getTimeZondeIdFromLatLon(lat, lon));
        return formatDate(getZoneDateTimeFromId(zoneId), formatter);
    }

    public static LocalDateTime getLocalDateFromZoneIdAndTimestamp(ZoneId id, String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {

            Date currentDate = format.parse(date);
            return LocalDateTime.ofInstant(currentDate.toInstant(), id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ZonedDateTime getZonedDateFromLatLon(double lat, double lon) {
        ZoneId zoneId = getZoneIdFromString(getTimeZondeIdFromLatLon(lat, lon));
        return getZoneDateTimeFromId(zoneId);
    }

    private static String getTimeZoneFromLocation(double lat, double lon) {
        return TimeZoneAPI.getTimeZoneFromLatLon(lat, lon);
    }

    private static ZoneId getZoneIdFromString(String id) {
        return ZoneId.of(id);
    }

    private static ZonedDateTime getZoneDateTimeFromId(ZoneId id) {
        return ZonedDateTime.now(id);
    }

    public static ZoneId getZoneIdFromLatLon(double lat, double lon) {
        return getZoneIdFromString(getTimeZondeIdFromLatLon(lat, lon));
    }
    */
    public static double calculateTimeDifference(long firstTimeStamp, long secondTimeStamp, TimeFormat format) {
        long difference = Math.abs(firstTimeStamp - secondTimeStamp);
        switch (format) {
            case SECONDS:
                return difference;
            case MINUTES:
                return difference / 60.0;
            case HOURS:
                return difference / 60.0 / 60.0;
            case DAYS:
                return difference / 60.0 / 60.0 / 24.0;
            default:
                return difference;
        }
    }

    public static long convertDateStringToTimeStamp(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd:HH:mm:ss");
        try {
            Date dateTime = formatter.parse(date);
            return dateTime.toInstant().getEpochSecond();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }
    /*
    public static ZonedDateTime getLocalDateTimeFromZoneAndTimeStamp(ZoneId id, long timeStamp) {
        return Instant.ofEpochSecond(timeStamp).atZone(id);
    }

    public static void main(String[] args) {
        ZonedDateTime date = DateUtil.getZonedDateFromLatLon(40.78366788073222, -73.97549996076525);
        date = date.plusHours(1);
        System.out.println(date);
    }
    */
}

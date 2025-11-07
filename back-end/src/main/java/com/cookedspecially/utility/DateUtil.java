package com.cookedspecially.utility;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Ankit on 1/10/2016.
 */
public class DateUtil {

    public static Date convertStringDateToGMTDate(String dateTimeString, String format, String inputDateTimeZone) throws ParseException {

        TimeZone inputTimeZone = TimeZone.getTimeZone(inputDateTimeZone);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(inputTimeZone);
        Date dateInInputTimeZone = sdf.parse(dateTimeString);

        Calendar cal = new GregorianCalendar(inputTimeZone);
        cal.setTime(dateInInputTimeZone);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        return cal.getTime();
    }

    public static Date convertDateToGMTDate(Date inputDate, String inputDateTimeZone) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(inputDateTimeZone));
        cal.setTime(inputDate);

        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        return cal.getTime();
    }

    public static Date getStartOfDay(Date date, String timeZone) {
        Calendar c = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    public static Date getEndOfDay(Date date, String timeZone) {
        Calendar c = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);

        return c.getTime();
    }

    public static Date nowInGMT() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        return cal.getTime();
    }

    public static Date nowInSpecifiedTimeZone(String timeZone) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
        return cal.getTime();
    }
    public static Date yesterdaySpecifiedTimeZone(String timeZone) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
        cal.add(cal.DATE, -1);
        return cal.getTime();
    }

    
    public static Date getLastDayDate(String timeZone){
    	Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
        return cal.getTime();
    }
    
    public static Date addToDate(Date date, int numDays, int numMonths, int numYears, String timeZone) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, numDays);
        cal.add(Calendar.MONTH, numMonths);
        cal.add(Calendar.YEAR, numYears);
        return cal.getTime();
    }

    public static Date addToTime(Date date, int numHours, int numMins, int numSeconds, String timeZone) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, numHours);
        cal.add(Calendar.MINUTE, numMins);
        cal.add(Calendar.SECOND, numSeconds);
        return cal.getTime();
    }
    public static Timestamp getCurrentTimestampInGMT(){
    	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		return Timestamp.valueOf(fmt.format(timestamp));
    }
    public static Timestamp convertTimeInMilliSecToTimestampInGMT(long timeInMilliSec){
    	Timestamp timestamp = new Timestamp(timeInMilliSec);
	    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		return Timestamp.valueOf(fmt.format(timestamp));
    }
    public static Timestamp convertTimeInMilliSecToTimestampInIST(long timeInMilliSec){
    	Timestamp timestamp = new Timestamp(timeInMilliSec);
	    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    fmt.setTimeZone(TimeZone.getTimeZone("IST"));
		return Timestamp.valueOf(fmt.format(timestamp));
    }
    public static Date getMonthStartEndDate(String filter, String timeZone, String date, String format, boolean startEnd) throws ParseException{
    	Calendar cal=GregorianCalendar.getInstance(TimeZone.getTimeZone(timeZone));
    	if(!StringUtility.isNullOrEmpty(date) && !StringUtility.isNullOrEmpty(format)){
    		SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
            cal.setTime(sdf.parse(date));
    	}else
    		startEnd=true;
    	 int dateCount = cal.getActualMinimum(Calendar.DATE);
    	 if("END".equalsIgnoreCase(filter))
    		 dateCount = cal.getActualMaximum(Calendar.DATE);
    	 if(startEnd)
    	   cal.set(Calendar.DATE, dateCount);
    	 if("END".equalsIgnoreCase(filter)){
    		 cal.set(Calendar.HOUR_OF_DAY, 23);
    		 cal.set(Calendar.MINUTE, 59);
    		 cal.set(Calendar.SECOND, 59);	 
    	 }else{    		
    	 cal.set(Calendar.HOUR_OF_DAY, 0);
		 cal.set(Calendar.MINUTE, 0);
		 cal.set(Calendar.SECOND, 0);}
    	 cal.getTime();
    	 cal.setTimeZone(TimeZone.getTimeZone("GMT"));
         return cal.getTime(); 
    }

    public static Timestamp convertDateStringIntoTimeStamp(String date, String timeZone) {
        Timestamp timestamp = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            Date parsedDate = dateFormat.parse(date);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
        }
        return timestamp;
    }

    public static Timestamp convertDateStringIntoTimeStampGMT(String date, String format, String timeZone) {
        Timestamp timestamp = null;
        if(timeZone == null)
        	timeZone="GMT";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            Date parsedDate = dateFormat.parse(date);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
        }
        return convertTimeInMilliSecToTimestampInGMT(timestamp.getTime());
    }

}
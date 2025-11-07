/**
 * 
 */
package com.cookedspecially.enums;
import java.util.HashSet;

/**
 * @author shashank
 *
 */
public enum WeekDayFlags {

	SUNDAY(0x01, "Sunday", 0, "Sun"),
	MONDAY(0x02, "Monday", 1, "Mon"),
	TUESDAY(0x04, "Tuesday", 2, "Tue"),
	WEDNESDAY(0x08, "Wednesday", 3, "Wed"),
	THURSDAY(0x10, "Thursday", 4, "Thu"),
	FRIDAY(0x20, "Friday", 5, "Fri"),
	SATURDAY(0x40, "Saturday", 6, "Sat");
	
	private int weekdayFlagValue;
	private String weekdayName;
	private int index;
	private String weekdayCode;
	
	WeekDayFlags(int weekdayFlagValue, String weekdayName, int index, String weekdayCode) {
		this.weekdayFlagValue = weekdayFlagValue;
		this.weekdayName = weekdayName;
		this.index = index;
		this.weekdayCode = weekdayCode;
	}
	
	public int getWeekdayFlagValue() {
		return this.weekdayFlagValue;
	}
	
	public String toString() {
		return this.getWeekdayName();
	}
	
	public String getWeekdayName() {
		return weekdayName;
	}
	
	public static HashSet<String> getWeekDayFlags(int daysVal) {
		HashSet<String> weekdayFlags = new HashSet<String>();
        for(WeekDayFlags weekdayFlag : WeekDayFlags.values()) {
        	int weekdayFlagValue = weekdayFlag.getWeekdayFlagValue();
            if ( ( weekdayFlagValue&daysVal ) == weekdayFlagValue ) {
               weekdayFlags.add(weekdayFlag.getWeekdayName());
            }
        }
        return weekdayFlags;
	}

	public static int getWeekDayVal(HashSet<String> weekdayFlags) {
		int weekdayVal = 0;
		for(WeekDayFlags weekdayFlag : WeekDayFlags.values()) {
        	String weekdayName = weekdayFlag.getWeekdayName();
            if ( weekdayFlags.contains(weekdayName) ) {
               weekdayVal |= weekdayFlag.getWeekdayFlagValue();
            }
        }
		return weekdayVal;
	}
	
	public static boolean[] getWeekDayFlagsArr(int daysVal) {
		boolean[] weekdayFlagsArr = new boolean[7];
        for(WeekDayFlags weekdayFlag : WeekDayFlags.values()) {
        	int weekdayFlagValue = weekdayFlag.getWeekdayFlagValue();
        	weekdayFlagsArr[weekdayFlag.getIndex()] = (weekdayFlagValue & daysVal) == 0? false : true;
        }
        return weekdayFlagsArr;
	}
	
	public static int getWeekDayVal(boolean[] weekdayFlags) {
		int weekdayVal = 0;
		for(WeekDayFlags weekdayFlag : WeekDayFlags.values()) {
	    	int weekdayIndex = weekdayFlag.getIndex();
	        if ( weekdayFlags[weekdayIndex] ) {
	           weekdayVal |= weekdayFlag.getWeekdayFlagValue();
	        }
	    }
		return weekdayVal;
	}
	
	public int getIndex() {
		return index;
	}

	public String getWeekdayCode() {
		return weekdayCode;
	}
}

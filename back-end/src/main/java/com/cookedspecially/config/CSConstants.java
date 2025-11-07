/**
 * 
 */
package com.cookedspecially.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author sagarwal , rahul 
 *
 */
public class CSConstants {

	public static String USERNAME = "username";
	public static String PASSWORD = "password";
	public static String TOKEN = "token";
	public static String COMMA = ",";
	public static List<String> currencyList = Arrays.asList("Australia Dollar", "Canada Dollars", "Denars", "Euro",  "Francs", "HongKong Dollars", "Indian Rupee", "Lire", "Nairas", "Pounds", "Rials", "Ringgits", "Rupiahs", "Shillings", "Switzerland Francs", "Taiwan Dollars", "US Dollar", "Yen");
	public static List<String> timeZoneIds = Arrays.asList("Asia/Calcutta", "CST", "EST", "GMT", "PST", "UTC");
	public static Map<Integer, String>  minDeliveryTime;
	static {
        Map<Integer, String> aMap = new TreeMap<Integer, String>();
        aMap.put(15, "00:15");
        aMap.put(30, "00:30");
        aMap.put(45, "00:45");
        aMap.put(60, "01:00");
        aMap.put(75, "01:15");
        aMap.put(90, "01:30");
        aMap.put(105,"01:45");
        aMap.put(120,"02:00");
        aMap.put(135,"02:15");
        aMap.put(150,"02:30");
        aMap.put(165,"02:45");
        aMap.put(180,"03:00");
        minDeliveryTime = Collections.unmodifiableMap(aMap);
    }
	
	public static Map<Integer, String>   deliveryTimeInterval;
	static {
        Map<Integer, String> aMap = new 	TreeMap<Integer, String>();
        aMap.put(15,"00:15");
        aMap.put(30,"00:30");
        aMap.put(45,"00:45");
        aMap.put(60,"01:00");
        aMap.put(75,"01:15");
        aMap.put(90,"01:30");
        aMap.put(105,"01:45");
        aMap.put(120,"02:00");
        aMap.put(135,"02:15");
        aMap.put(150,"02:30");
        aMap.put(165,"02:45");
        aMap.put(180,"03:00");
        deliveryTimeInterval = Collections.unmodifiableMap(aMap);
    }
	public static List<String> openFlag = Arrays.asList("Always Open",  "Open During Normal Hours" , "Closed");
	
}

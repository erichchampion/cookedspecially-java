package com.cookedspecially.dto.zomato.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScheduleTimeSlots {

	//hh:mm:ss 24 hr format time Eg: 10:00:00
	public String start_time;
	public String end_time;
}

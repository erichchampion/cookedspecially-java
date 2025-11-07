package com.cookedspecially.dto.zomato.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CategorySchedules {
	
	public String schedule_name;
	public Integer schedule_day;
	public ScheduleTimeSlots[] schedule_time_slots;
}

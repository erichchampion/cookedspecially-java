package com.cookedspecially.dto.zomato.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Groups {
	
	public Integer group_id;
	public String group_name;
	public String group_description;
	public String group_minimum;
	public String group_maximum;
	public Integer group_is_active;
	public Items[] items;
	
	

}

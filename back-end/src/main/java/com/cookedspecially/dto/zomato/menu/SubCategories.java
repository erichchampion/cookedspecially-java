package com.cookedspecially.dto.zomato.menu;

import java.util.List;

import com.cookedspecially.dto.zomato.common.Items;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SubCategories {

	public Integer subcategory_id;
	public String subcategory_name;
	public String subcategory_description;
	public Integer subcategory_is_active;
	public String subcategory_image_url;
	public Object[] subcategory_tags;
	public List<Items> items;
}

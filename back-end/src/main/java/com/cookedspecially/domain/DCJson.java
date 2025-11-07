package com.cookedspecially.domain;

import java.io.Serializable;

import com.cookedspecially.dto.DiscountDTO;
import com.cookedspecially.enums.check.AdditionalCategories;
import com.cookedspecially.enums.restaurant.ChargesType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class DCJson implements Serializable {
	private static final long serialVersionUID = 1L;
	public long id;
	public long dcId;
	public String name;
	public AdditionalCategories category;
	public ChargesType type;
	public float value;
	
	public DCJson(DiscountDTO dto){
		this.dcId = dto.id;
		//this.id = dto.dcId;
		this.name = dto.name;
		this.type = dto.type;
		this.value = dto.value;
		this.category=dto.category;
	}
	
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public DCJson(){
		
	}
	public long getDcId() {
		return dcId;
	}

	public void setDcId(long dcId) {
		this.dcId = dcId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public AdditionalCategories getCategory() {
		return category;
	}
	public void setCategory(AdditionalCategories category) {
		this.category = category;
	}
	public ChargesType getType() {
		return type;
	}
	public void setType(ChargesType type) {
		this.type = type;
	}
}

/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author shashank , rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonDish implements Serializable {
    private static final long serialVersionUID = 1L;
    
	public int id;
	public String name;
	public float price;
	public int quantity;
	public String dishType;
	public String instructions;
	public Integer dishSizeId;
	public List<JsonAddOn> addOns;
	
	//we are using this bean only for email invoices
	public String dishSize;
	
	public String getDishSize() {
		return dishSize;
	}
	public void setDishSize(String dishSize) {
		this.dishSize = dishSize;
	}
	public Integer getDishSizeId() {
		return dishSizeId;
	}
	public void setDishSizeId(Integer dishSizeId) {
		this.dishSizeId = dishSizeId;
	}
	public List<JsonAddOn> getAddOns() {
		return addOns;
	}
	public void setAddOns(List<JsonAddOn> addOns) {
		this.addOns = addOns;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getQuantity() {
		if (quantity == 0)
			return 1;
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getDishType() {
		return dishType;
	}
	public void setDishType(String dishType) {
		this.dishType = dishType;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions= instructions;
	}
	
	
}

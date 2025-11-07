package com.cookedspecially.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonAddOn implements Serializable {
    private static final long serialVersionUID = 1L;
    
	public int id;
	public String name;
	public float price;
	public String dishType;
	public String smallImageUrl;
	public  int dishId;
	public Integer dishSizeId;
	public  int quantity;
	
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
	public int getQuantity() {
		if (quantity == 0)
			return 1;
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getDishId() {
		return dishId;
	}
	public void setDishId(int dishId) {
		this.dishId = dishId;
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
	public String getDishType() {
		return dishType;
	}
	public void setDishType(String dishType) {
		this.dishType = dishType;
	}
	public String getSmallImageUrl() {
		return smallImageUrl;
	}
	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}
	
	
}

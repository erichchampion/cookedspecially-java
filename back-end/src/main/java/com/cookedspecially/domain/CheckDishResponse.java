/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shashank
 *
 */
public class CheckDishResponse implements Serializable {
    private static final long serialVersionUID = 1L;

	private Integer dishId;
	private String name;
	private float price;
	private String dishType;
	private String dishSize;
	List<OrderAddOn> addOnresponse =  new ArrayList<OrderAddOn>(); 
	
	public List<OrderAddOn> getAddOnresponse() {
		return addOnresponse;
	}

	public void setAddOnresponse(List<OrderAddOn> addOnresponse) {
		this.addOnresponse = addOnresponse;
	}

	public CheckDishResponse(OrderDish orderDish) {
		this.dishId = orderDish.getDishId();
		this.name = orderDish.getName();
		this.price = orderDish.getPrice();
		this.dishType = orderDish.getDishType();
		this.addOnresponse = orderDish.getOrderAddOn();
		this.dishSize = orderDish.getDishSize();
	}
	
	public String getDishSize() {
		return dishSize;
	}

	public void setDishSize(String dishSize) {
		this.dishSize = dishSize;
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
	public Integer getDishId() {
		return dishId;
	}
	public void setDishId(Integer dishId) {
		this.dishId = dishId;
	}
	public String getDishType() {
		return dishType;
	}
	public void setDishType(String dishType) {
		this.dishType = dishType;
	}
	
}

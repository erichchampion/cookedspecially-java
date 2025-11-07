package com.cookedspecially.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="STOCK_MANAGEMENT")
public class StockManagement implements Serializable {
    private static final long serialVersionUID = 1L; 
	@Id
	private int id;
	
	@Column(name="dishId")
	private int dishId;
	
	@Column(name="restaurantId")
	private Integer restaurantId;
	
	@Column(name="dishName")
	private String dishName;
	
	@Column(name="fulfillmentCenterId")
	private int fulfillmentCenterId;
	
	@Column(name="addQuantity")
	private int addQuantity;
	
	@Column(name="alertQuantity")
	private int alertQuantity;
	
	@Column(name="remainingQuantity")
	private int remainingQuantity;

	@Column(name="removeQuantity")
	private int removeQuantity;

	@Transient
	String format ="yyyy-MM-dd HH:mm:ss";
	@Transient
	SimpleDateFormat formatterD = new SimpleDateFormat(format);

	@Column(name="expireDate")
	private String expireDate = formatterD.format(new Date());
	
	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate){
		this.expireDate =expireDate;
	}

	public int getRemoveQuantity() {
		return removeQuantity;
	}

	public void setRemoveQuantity(int removeQuantity) {
		this.removeQuantity = removeQuantity;
	}

	public int getAddQuantity() {
		return addQuantity;
	}

	public void setAddQuantity(int addQuantity) {
		this.addQuantity = addQuantity;
	}

	public int getAlertQuantity() {
		return alertQuantity;
	}

	public void setAlertQuantity(int alertQuantity) {
		this.alertQuantity = alertQuantity;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDishId() {
		return dishId;
	}

	public void setDishId(int dishId) {
		this.dishId = dishId;
	}

	public String getDishName() {
		return dishName;
	}

	public void setDishName(String dishName) {
		this.dishName = dishName;
	}

	public int getFulfillmentCenterId() {
		return fulfillmentCenterId;
	}

	public void setFulfillmentCenterId(int fulfillmentCenterId) {
		this.fulfillmentCenterId = fulfillmentCenterId;
	}


	public int getRemainingQuantity() {
		return remainingQuantity;
	}

	public void setRemainingQuantity(int remainingQuantity) {
		this.remainingQuantity = remainingQuantity;
	}
	
	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}
}

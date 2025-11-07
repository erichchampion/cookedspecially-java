package com.cookedspecially.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ADDONDISH_SIZE")
public class AddOnDish_Size implements Serializable {
    private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="id")
	@GeneratedValue
	private int id;
	
	@Column(name="dishSizeId")
	private Integer dishSizeId;
	
	@Column(name="name")
	private String name;
	
	@Column(name="capacity")
	private float capacity;

	@Column(name="addOnDishId")
	private Integer addOnDishId;

	@Column(name="price")
	private float price;
	
	@Column(name="displayPrice")
	private float displayPrice;
	
	@Column(name="factor")
	private float factor;

	public float getCapacity() {
		return capacity;
	}

	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}
	
	public Integer getAddOnDishId() {
		return addOnDishId;
	}

	public void setAddOnDishId(Integer addOnDishId) {
		this.addOnDishId = addOnDishId;
	}
	
	public Integer getDishSizeId() {
		return dishSizeId;
	}

	public void setDishSizeId(Integer dishSizeId) {
		this.dishSizeId = dishSizeId;
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

	public float getDisplayPrice() {
		return displayPrice;
	}

	public void setDisplayPrice(float displayPrice) {
		this.displayPrice = displayPrice;
	}

	public float getFactor() {
		return factor;
	}

	public void setFactor(float factor) {
		this.factor = factor;
	}

}

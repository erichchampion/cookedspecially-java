package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="DISH_SIZE")
public class Dish_Size implements Serializable {
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

	@Column(name="dishId")
	private Integer dishId;

	@Column(name="price")
	private float price;
	
	@Column(name="displayPrice")
	private float displayPrice;
	
	@Column(name="factor")
	private float factor;
	
	@Transient
	private List<Entry> availableStock;
	
	public List<Entry> getAvailableStock() {
		return availableStock;
	}

	public void setAvailableStock(List<Entry> availableStock) {
		this.availableStock = availableStock;
	}

	@JsonIgnore
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Integer getDishSizeId() {
		return dishSizeId;
	}

	public void setDishSizeId(Integer dishSizeId) {
		this.dishSizeId = dishSizeId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public float getCapacity() {
		return capacity;
	}

	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}

	@JsonIgnore
	public Integer getDishId() {
		return dishId;
	}

	public void setDishId(Integer dishId) {
		this.dishId = dishId;
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

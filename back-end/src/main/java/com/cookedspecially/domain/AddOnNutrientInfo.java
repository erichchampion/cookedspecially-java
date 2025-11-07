package com.cookedspecially.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="ADDONNUTRIENTINFO")
public class AddOnNutrientInfo implements Serializable {
    private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue 
	@Column(name="id")
	private int id;
	
	@Column(name ="addOnDishId")
	private int dishId;
	
	
	@Column(name ="name")
	private String name;

	
	@Column(name="value")
	private double value;

	@Column(name="nutrientId")
	private int nutrientId;
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	@JsonIgnore
	public int getDishId() {
		return dishId;
	}

	public void setDishId(int dishId) {
		this.dishId = dishId;
	}

	@JsonIgnore
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@JsonIgnore
	public int getNutrientId() {
		return nutrientId;
	}

	public void setNutrientId(int nutrientId) {
		this.nutrientId = nutrientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}

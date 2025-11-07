package com.cookedspecially.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;

import com.cookedspecially.enums.check.AdditionalCategories;
import com.cookedspecially.enums.restaurant.ChargesType;

@Entity
@Table(name="DISCOUNT_CHARGES")
public class Discount_Charges implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="dcid")
	private int id ;
	
	@Column(name= "name")
	private String name;
	
	@Column(name="restaurantId")
	private Integer restaurantId;
	
	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="category")
	private AdditionalCategories category;
	
	@Column(name="type")
	private ChargesType type;
	
	@Column(name="value")
	private int value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/*public int getCheckId() {
		return checkId;
	}

	public void setCheckId(int checkId) {
		this.checkId = checkId;
	}*/

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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}

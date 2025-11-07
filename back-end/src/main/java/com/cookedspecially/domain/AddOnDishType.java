/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author rahul
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="ADDONDISHTYPES")
public class AddOnDishType implements Serializable {
    private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
	@GeneratedValue
	private Integer dishTypeId;
	
	@Column(name="restaurantId")
	private Integer restaurantId;
	
	@Column(name="name")
	private String name;

	public Integer getDishTypeId() {
		return dishTypeId;
	}

	public void setDishTypeId(Integer dishTypeId) {
		this.dishTypeId = dishTypeId;
	}

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


}

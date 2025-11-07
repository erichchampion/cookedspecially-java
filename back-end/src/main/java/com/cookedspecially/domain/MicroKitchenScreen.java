package com.cookedspecially.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="MICROKITCHEN")
public class MicroKitchenScreen implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id ;
	
	@Column(name="kitchenId")
	private int kitchenId;

	@Column(name="name")
	private String name;
	
	@Column(name="restaurantId")
	private int restaurantId;
	
	/*@Column(name = "kitchenScreenId")
	private int kitchenScreenId;
	

	public int getKitchenScreenId() {
		return kitchenScreenId;
	}

	public void setKitchenScreenId(int kitchenScreenId) {
		this.kitchenScreenId = kitchenScreenId;
	}*/

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public int getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(int kitchenId) {
		this.kitchenId = kitchenId;
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
}

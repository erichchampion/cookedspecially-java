package com.cookedspecially.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="FULFILLMENTCENTER")
public class FulfillmentCenter implements Serializable {
    private static final long serialVersionUID = 1L;
    
	@Transient
    private boolean multiTillSupported=false;
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id ;
	
	@Column(name="restaurantId")
	private int restaurantId;
	
	@Column(name="name")
	private String name;
	
	@Column(name = "location")
	private String location;

	@Column(name = "address")
	private String address;
	
	
	
	public boolean isMultiTillSupported() {
		return multiTillSupported;
	}

//	public void setMultiTillSupported(boolean multiTillSupported) {
//		this.multiTillSupported = multiTillSupported;
//	}

	public int getId() {
		return id;
	}

//	 @OneToOne(cascade=CascadeType.ALL)
//	 @JoinTable(name="RESTAURANT",
//	 joinColumns={@JoinColumn(name="restaurantId", referencedColumnName="restaurantId")})
//	 private Restaurant restaurant;
//	
	public void setId(int id) {
		this.id = id;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}

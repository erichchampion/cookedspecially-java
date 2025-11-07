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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * @author shashank, rahul
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="DELIVERYAREAS")
public class DeliveryArea implements Serializable {
    private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="id")
	@GeneratedValue
	private Integer id;
	
	@Column(name="restaurantId")
	private Integer restaurantId;
	
	@Column(name="name")
	private String name;

	@Column(name="minDeliveryThreshold")
	 private float minDeliveryThreshold;
	
	@Column(name="City")
	private String City;
	
	@Column(name="State")
	private String State;
	
	@Column(name="Country")
	private String Country;
	
	@Column(name="Distance")
	private String distance;
	
	@Column(name="deliveryCharges")
	private float deliveryCharges;
	 
    @Column(name="fulfillmentCenterId")
    private int fulfillmentCenterId;
    
    @Column(name="deliveryTimeInterval")
    private int deliveryTimeInterval;
    
	@Column(name="minDeliveryTime")
    private int  minDeliveryTime;
    
	@Column(name="posVisible")
	private boolean posVisible;
	
	@Column(name="tomorrowOnly")
	private boolean tomorrowOnly;
	
	@Transient
	@com.fasterxml.jackson.annotation.JsonRawValue
	private String todayTimeJson=null;
	
	@Transient
	@com.fasterxml.jackson.annotation.JsonRawValue
	private String tomorrowTimeJson=null;
	
	public boolean isTomorrowOnly() {
		return tomorrowOnly;
	}

	public void setTomorrowOnly(boolean tomorrowOnly) {
		this.tomorrowOnly = tomorrowOnly;
	}
	
	public String getTodayTimeJson() {
		return todayTimeJson;
	}

	public void setTodayTimeJson(String todayTimeJson) {
		this.todayTimeJson = todayTimeJson;
	}

	public String getTomorrowTimeJson() {
		return tomorrowTimeJson;
	}

	public void setTomorrowTimeJson(String tomorrowTimeJson) {
		this.tomorrowTimeJson = tomorrowTimeJson;
	}
	
	@JsonIgnore
	public int getDeliveryTimeInterval() {
		return deliveryTimeInterval;
	}

	public boolean isPosVisible() {
		return posVisible;
	}

	public void setPosVisible(boolean posVisible) {
		this.posVisible = posVisible;
	}
	
	public void setDeliveryTimeInterval(int deliveryTimeInterval) {
		this.deliveryTimeInterval = deliveryTimeInterval;
	}

	@JsonIgnore
	public int getMinDeliveryTime() {
		return minDeliveryTime;
	}

	public void setMinDeliveryTime(int minDeliveryTime) {
		this.minDeliveryTime = minDeliveryTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		Country = country;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public int getFulfillmentCenterId() {
		return fulfillmentCenterId;
	}

	public void setFulfillmentCenterId(int fulfillmentCenterId) {
		this.fulfillmentCenterId = fulfillmentCenterId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public float getDeliveryCharges() {
		return deliveryCharges;
	}

	public void setDeliveryCharges(float deliveryCharges) {
		this.deliveryCharges = deliveryCharges;
	}

	public float getMinDeliveryThreshold() {
		return minDeliveryThreshold;
	}

	public void setMinDeliveryThreshold(float minDeliveryThreshold) {
		this.minDeliveryThreshold = minDeliveryThreshold;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	
}

package com.cookedspecially.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.notification.Device;
import com.google.gson.Gson;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="NOTIFIER")
public class Notifier implements Serializable {
    private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="NOTIFIERID")
	@GeneratedValue
	private int notifierId;
	
	@Column(name="restaurantId")
	private int restaurantId;
	
	@Size(min=20, max=200) 
	@Column(name="TOKEN_KEY")
	private String key;
	
	@NotNull
	@Column(name="DEVICE_TYPE")
	@Enumerated(EnumType.STRING)
	private Device device;
	
	@Column(name="STATUS")
	@Enumerated(EnumType.STRING)
	private Status status=Status.ACTIVE;

	
	public Notifier(){}
	
	public Notifier(int restaurantId2, String appKey, Device deviceType) {
        this.restaurantId=restaurantId2;
        this.device=deviceType;
        this.key=appKey;    
	}

	public int getNotifierId() {
		return notifierId;
	}

	public void setNotifierId(int notifierId) {
		this.notifierId = notifierId;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String to_string(){
		final Gson gson=new Gson();
		return gson.toJson(this);
	}
	
	
}

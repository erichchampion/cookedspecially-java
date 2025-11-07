package com.cookedspecially.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.notification.Device;
import com.cookedspecially.validator.Phone;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="SUBSCRIBER")
public class Subscriber implements Serializable {
    private static final long serialVersionUID = 1L;
    
	@Id
	@Column(name="SUBSCRIBERID")
	@GeneratedValue
	private int subscriberId;
	
	@Min(1)
	@Column(name="customerId")
	private int customerId;
	
	@NotNull
	@Size(min=20, max=200)
	@Column(name="TOKEN")
	private String token;
	
	@Size(min=10) 
	@Phone
	@Column(name="MOBILE_NO")
	private String mobileNo;
	
	@NotNull
	@Column(name="DEVICE_TYPE")
	private Device device;
	
	@Column(name="STATUS")
	private Status status=Status.ACTIVE;

	public int getSubscriberId() {
		return subscriberId;
	}

	public Subscriber(){};
	
	public Subscriber(int customerId, String token, String mobileNo,Device device) {
		super();
		this.customerId = customerId;
		this.token = token;
		this.mobileNo=mobileNo;
		this.device = device;
	}


	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	
	
}

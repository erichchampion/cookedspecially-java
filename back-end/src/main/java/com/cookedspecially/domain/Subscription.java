package com.cookedspecially.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.notification.Device;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="SUBSCRIBER")
public class Subscription implements Serializable {
    private static final long serialVersionUID = 1L;
    
	@Id
	@Column(name="SUBSCRIBERID")
	@GeneratedValue
	private int subscriberId;
	
	
	@Column(name="TOKEN")
	private String token;
	
	@Column(name="APPID")
	private String appId;
	
	@Column(name="MOBILE_NO")
	private String mobileNo;
	
	@Column(name="DEVICE_TYPE")
	private Device device;
	
	@Column(name="STATUS")
	private Status status=Status.ACTIVE;
	
	@ManyToOne
    @JoinColumn(name = "customerId")
	@JsonBackReference(value="subscription")
	private Customer customer;

	public int getSubscriberId() {
		return subscriberId;
	}

	public Subscription(){};
	
	public Subscription(String token, String mobileNo,Device device, String appID) {
		this.token = token;
		this.mobileNo=mobileNo;
		this.device = device;
		this.appId=appID;
	}


	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	
}

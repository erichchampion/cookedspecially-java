package com.cookedspecially.domain;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="USER_PORTRAYAL")
public class UserPortrayal implements Serializable {
    private static final long serialVersionUID = 1L;
	@Id
	@Column(name="USERID")
	private Integer userId;
	
	@Column(name="RESTAURANTID")
	private Integer restaurantId;
	
	@Column(name="KITCHENID")
	private Integer kitchenId;
	
	@Column(name="MICROKITCHENID")
	private Integer microKitchenId;
	
	@Column(name="ORGID")
	private Integer orgId;

	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "userId")
	private User user;
	
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public Integer getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(Integer kitchenId) {
		this.kitchenId = kitchenId;
	}

	public Integer getMicroKitchenId() {
		return microKitchenId;
	}

	public void setMicroKitchenId(Integer microKitchenId) {
		this.microKitchenId = microKitchenId;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
}

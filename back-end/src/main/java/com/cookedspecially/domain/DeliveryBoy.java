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
 * @author Rahul
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="DELIVERYBOYS")
public class DeliveryBoy implements Serializable {
    private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
	@GeneratedValue
	private Integer id;

	@Column(name="userId")
	private Integer userId;
	
	@Column(name="name")
	private String name;
	
	@Column(name="age")
	private Integer age;
	
	@Column(name="kitchenScreenId")
    private int kitchenScreenId;
	
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getKitchenScreenId() {
		return kitchenScreenId;
	}

	public void setKitchenScreenId(int kitchenScreenId) {
		this.kitchenScreenId = kitchenScreenId;
	}
}

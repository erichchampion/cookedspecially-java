package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Rahul
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="EMPLOYEE")
@SecondaryTable(name="USER_PORTRAYAL", pkJoinColumns=@PrimaryKeyJoinColumn(name="USERID", referencedColumnName="USERID"))
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
	@Id
	@Column(name="userId")
	@GeneratedValue
	private Integer userId;

	@Column(name="restaurantId")
	private Integer restaurantId;
	
	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	@Column(name="name")
	private String name;
	
	@Column(name="age")
	private Date age;
	
	@Column(name="dateOfJoining")
	private Date dateOfJoining;
	
	
	public Date getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(Date dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	public Date getAge() {
		return age;
	}

	public void setAge(Date age) {
		this.age = age;
	}

	@Column(name="phone")
	private Integer phone;

	@Column(name="userName")
	private String userName;

	@Column(name="address")
	private String address;
	
	@Column(name="fulfillmentCenterId")
	private Integer fulfillmentCenterId;
	
	/*@OneToOne(cascade=CascadeType.ALL)
	@JoinTable(name="user_roles",
	joinColumns={@JoinColumn(name="userId", referencedColumnName="userId")},
	inverseJoinColumns={@JoinColumn(name="roleId", referencedColumnName="id")})
	private Role roleId;
	*/
	@Column(name="role")
	private Integer roleId;
	
	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
	public Integer getPhone() {
		return phone;
	}

	public void setPhone(Integer phone) {
		this.phone = phone;
	}

	public Integer getFulfillmentCenterId() {
		return fulfillmentCenterId;
	}

	public void setFulfillmentCenterId(Integer fulfillmentCenterId) {
		this.fulfillmentCenterId = fulfillmentCenterId;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	

	public Integer getFulfilmentCenterId() {
		return fulfillmentCenterId;
	}

	public void setFulfilmentCenterId(Integer fulfillmentCenterId) {
		this.fulfillmentCenterId = fulfillmentCenterId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

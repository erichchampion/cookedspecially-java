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
@Table(name="ORDERADDONS")
public class OrderAddOn implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ORDERADDONID")
	@GeneratedValue
	private Integer orderAddOnId;
	
	@Column(name="ADDONID")
	private Integer addOnId;
	
	@Column(name="DISHID")
	private Integer dishId;
	
	@Column(name="PRICE")
	private Float price;

	@Column(name="QUANTITY")
	private Integer quantity;
	
	@Column(name="name")
	private String name;
	
	@Column(name="addOnType")
	private String addOnType;

	@Column(name="dishSize")
	private String dishSize;
	
	@Column(name="smallImageUrl")
	private String smallImageUrl;
	
	public String getDishSize() {
		return dishSize;
	}

	public void setDishSize(String dishSize) {
		this.dishSize = dishSize;
	}
	
	public String getSmallImageUrl() {
		return smallImageUrl;
	}

	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getOrderDishId() {
		return orderAddOnId;
	}

	public void setOrderDishId(Integer orderAddOnId) {
		
		this.orderAddOnId = orderAddOnId;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Integer getDishId() {
		return dishId;
	}

	public void setDishId(Integer dishId) {
		this.dishId = dishId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		
		this.name = name;
	}

	public String getDishType() {
		return addOnType;
	}

	public void setDishType(String dishType) {
		this.addOnType = dishType;
	}
	
	public Integer getAddOnId() {
		return addOnId;
	}

	public void setAddOnId(Integer addOnId) {
		this.addOnId = addOnId;
	}

}

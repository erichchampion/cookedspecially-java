/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * @author shashank
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="ORDERDISHES")
public class OrderDish implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ORDERDISHID")
	@GeneratedValue
	private Integer orderDishId;
	
	@Column(name="DISHID")
	private Integer dishId;
	
	@Column(name="QUANTITY")
	private Integer quantity;
	
	@Column(name="PRICE")
	private Float price;

	@Column(name="name")
	private String name;
	
	@Column(name="dishType")
	private String dishType;
	
	@Column(name="instructions")
	private String instructions;
	
	@Column(name="dishSize")
	private String dishSize;
	
	/*@Column(name="microScreenId")
	private int microScreenId;*/

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(orphanRemoval=true)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE})
	@JoinTable(name="ORDER_ORDERADDON", 
            joinColumns={@JoinColumn(name="ORDERDISHID")}, 
            inverseJoinColumns={@JoinColumn(name="ORDERADDONID")})
	private List<OrderAddOn> orderAddOn = new ArrayList<OrderAddOn>();
	
	
	public String getDishSize() {
		return dishSize;
	}

	public void setDishSize(String dishSize) {
		this.dishSize = dishSize;
	}
	
	public List<OrderAddOn> getOrderAddOn() {
		return orderAddOn;
	}

	public void setOrderAddOn(List<OrderAddOn> orderAddOn) {
		this.orderAddOn = orderAddOn;
	}

	
	public Integer getOrderDishId() {
		return orderDishId;
	}

	public void setOrderDishId(Integer orderDishId) {
		this.orderDishId = orderDishId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}


	public Integer addMore(Integer additionalQuantity) {
		this.quantity += additionalQuantity;
		return this.quantity;
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
		return dishType;
	}

	public void setDishType(String dishType) {
		this.dishType = dishType;
	}
	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	
	
}

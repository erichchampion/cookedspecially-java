/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cookedspecially.enums.table.Status;

/**
 * @author shashank
 *
 */
//MENU_SECTION
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="SEATINGTABLES")
public class SeatingTable implements Serializable {
    private static final long serialVersionUID = 1L;
    
	@Id
	@Column(name="SEATINGTABLEID")
	@GeneratedValue
	private Integer id;
	
	@Column(name="RESTAURANTID")
	private Integer restaurantId;
	
	@Column(name="USERID")
	private Integer userId;
	
	@Column(name="NAME")
	private String name;

	@Column(name="description")
	private String description;
	
	@Column(name="SEATS")
	private Integer seats;

	@Column(name="STATUS")
	private Status status = Status.Available;
	
	@Column(name="STARTTIME")
	private Date startTime;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSeats() {
		return seats;
	}

	public void setSeats(Integer seats) {
		this.seats = seats;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

	
}

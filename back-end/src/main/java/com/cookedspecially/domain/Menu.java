/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;

import com.cookedspecially.enums.Status;

/**
 * @author sagarwal, rahul
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="MENUS")
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;
	@Id
	@Column(name="MENUID")
	@GeneratedValue
	private Integer menuId;
	
	@Column(name="RESTAURANTID")
	private Integer restaurantId;
	
	@Column(name="USERID")
	private Integer userId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
		
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="MODIFIEDTIME")
	private Date modifiedTime;
	
	@Column(name="STATUS")
	private Status status = Status.ACTIVE;
	
	@Column(name="posVisible")
	private boolean posVisible=false;
	
	@Column(name="allDay")
	private boolean allDay=false;
	
	@Column(name="startTime")
	private String startTime;
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Column(name="endTime")
	private String endTime;
	
	@Column(name="IMAGEURL")
	private String imageUrl;
	
	@Column(name="ZOMATOSTATUS")
	private Status zomatoStatus = Status.INACTIVE;
	
	@OneToMany(fetch=FetchType.EAGER, orphanRemoval=true)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE})
	@IndexColumn(name="SECTIONPOSITION")
    @JoinTable(name="MENU_SECTION", 
                joinColumns={@JoinColumn(name="MENUID")}, 
                inverseJoinColumns={@JoinColumn(name="SECTIONID")})
    private List<Section> sections = new ArrayList<Section>();
	
	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	public boolean isPosVisible() {
		return posVisible;
	}

	public void setPosVisible(boolean posVisible) {
		this.posVisible = posVisible;
	}
	
	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(ArrayList<Section> sections) {
		this.sections = sections;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public Status getZomatoStatus() {
		return zomatoStatus;
	}

	public void setZomatoStatus(Status zomatoStatus) {
		this.zomatoStatus = zomatoStatus;
	}
	
}

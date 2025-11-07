/**
 * 
 */
package com.cookedspecially.domain;

import com.cookedspecially.enums.notification.NotificationType;
import com.cookedspecially.utility.DataValidator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author shashank, Abhishek
 *
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="CUSTOMERS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CUSTOMERID")
	@GeneratedValue
	private Integer customerId;
	
	@Column(name="RESTAURANTID")
	private Integer restaurantId;
	
	@Column(name="USERID")
	private Integer userId;
	
	@Column(name="FIRSTNAME")
	private String firstName ="";
	
	@Column(name="LASTNAME")
	private String lastName ="";
	
	@Column(name="ADDRESS")
	private String address;
	
	@Column(name="CITY")
	private String city;
	
	@Column(name="PHONE")
	private String phone;
	
	@Column(name="EMAIL")
	private String email;

	@Column(name="NUMBEROFORDERS")
	private Long numberOfOrders;

	@Column(name="DELIVERYTIME")
	private String deliveryTime;
   
	@Column(name="DELIVERYAREA")
	private String deliveryArea;
	
	@Column(name="orgId")
	private Integer orgId;
	
	@Column(name="simNumber")
	private String simNumber;
	
	@Column(name="facebookId")
	private String facebookId;
	
	@Column(name="facebookEmail")
	private String facebookEmail;
	
	@Column(name="isAuthenticated")
	private Integer isAuthentic=0;
	
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
	@Column(name="accountStatus")
	private int isActive=1;

	@Column(name="CREATEDTIME")
	private Date createdTime;
	
	@Column(name="rewardPoints")
	private double rewardPoints;
	
	@Column(name="stripeId")
	private String stripId;
	@Column(name="preferedNotification")
	private NotificationType preferedNotification=NotificationType.EMAIL_APP;
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference(value="subscription")
	private Set<Subscription> subscription;
    //@JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "customer")
    @JsonManagedReference(value="customer-credit")
	private CustomerCredit credit;
	
	public Customer(String userName, String mobileNumber, String emailAddress,Integer orgId) {
		this.firstName=userName;
		this.phone=mobileNumber;
		this.email=emailAddress;
		this.orgId=orgId;
		this.isActive=1;
		this.isAuthentic=0;
	}


    public Customer() {
	}

    public String getStripId() {
        return stripId;
    }

    @ApiModelProperty(hidden = true)
    public void setStripId(String stripId) {
        this.stripId = stripId;
    }

    public double getRewardPoints() {
        return rewardPoints;
    }

    @ApiModelProperty(hidden = true)
    public void setRewardPoints(double d) {
        this.rewardPoints = d;
    }

	public Integer getOrgId() {
		return orgId;
	}

	@ApiModelProperty(hidden= true)
	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}
	
	public String getDeliveryArea() {
		// Erich: A hack, since many reports have been hardcoded to only work with a non-null value
		return deliveryArea != null ? deliveryArea : "N/A";
	}

	@ApiModelProperty(hidden= true)
	public void setDeliveryArea(String deliveryArea) {
		this.deliveryArea = deliveryArea;
	}

	
	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
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

	@ApiModelProperty(hidden= true)
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}
	
	@ApiModelProperty(hidden= true)
	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	@ApiModelProperty(hidden= true)
	public void setCity(String city) {
		this.city = city;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) throws Exception {
		this.phone = DataValidator.validateAndFormateMobileNo(phone);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreatedTime() {
		return createdTime;
	}
	
	@ApiModelProperty(hidden= true)
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Long getNumberOfOrders() {
		return numberOfOrders;
	}
	
	@ApiModelProperty(hidden= true)
	public void setNumberOfOrders(Long numberOfOrders) {
		this.numberOfOrders = numberOfOrders;
	}
	
	public String getDeliveryTime() {
		return deliveryTime;
	}
	@ApiModelProperty(hidden= true)
	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getSimNumber() {
		return simNumber;
	}
	@ApiModelProperty(hidden= true)
	public void setSimNumber(String simNumber) {
		this.simNumber = simNumber;
	}

	public String getFacebookId() {
		return facebookId;
	}

	@ApiModelProperty(hidden= true)
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
	
	public String getFacebookEmail() {
		return facebookEmail;
	}
	
	@ApiModelProperty(hidden= true)
	public void setFacebookEmail(String facebookEmail) {
		this.facebookEmail = facebookEmail;
	}

	public Integer getIsAuthentic() {
		return isAuthentic;
	}

	@ApiModelProperty(hidden= true)
	public void setIsAuthentic(Integer isAuthentic) {
		this.isAuthentic = isAuthentic;
	}

	public int getIsActive() {
		return isActive;
	}

	@ApiModelProperty(hidden= true)
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public NotificationType getPreferedNotification() {
		return preferedNotification;
	}

	@ApiModelProperty(hidden= true)
	public void setPreferedNotification(NotificationType preferedNotification) {
		this.preferedNotification = preferedNotification;
	}

	public Set<Subscription> getSubscription() {
		return subscription;
	}

	@ApiModelProperty(hidden= true)
	public void setSubscription(Set<Subscription> subscription) {
		this.subscription = subscription;
	}

	public CustomerCredit getCredit() {
		return credit;
	}

//	public void setIsAuthenticated(Integer isAuthenticated) {
//		this.isAuthentic = isAuthenticated;
//	}
	
	@ApiModelProperty(hidden= true)
	public void setCredit(CustomerCredit credit) {
		this.credit = credit;
	}


}

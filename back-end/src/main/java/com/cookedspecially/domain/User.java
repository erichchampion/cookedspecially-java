package com.cookedspecially.domain;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author abhishek
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="USER")
@SecondaryTable(name="USER_PORTRAYAL", pkJoinColumns=@PrimaryKeyJoinColumn(name="USERID", referencedColumnName="USERID"))
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
	@Id
	@Column(name="USERID")
	@GeneratedValue
	private Integer userId;

	@Column(name="USERNAME")
	private String userName;
	
	@Column(name="FIRSTNAME")
	private String firstName;
	
	@Column(name="MIDDLENAME")
	private String middleName;
	
	@Column(name="LASTNAME")
	private String lastName;
	
	@Column(name="PASSWORD")
	private String passwordHash;
	
	@Column(name="CONTACTNO")
	private String contact;
	
	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}
	@Column(name="STATE")
	private String state;
	
	@Column(name="CITY")
	private String city;
	
	@Column(name="COUNTRY")
	private String country;
	
	@Column(name="ADDRESS1")
	private String address1;
	
	@Column(name="ADDRESS2")
	private String address2;
	
	@Column(name="DATEOFJOINNING")
	private String dateOfJoinning;
	
	@Column(name="DATEOFBIRTH")
	private String dateOfBirth;
	
	
	/*@Column(name="restaurantId", table="USER_PORTRAYAL")
	private Integer restaurantId;*/
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "USER_PORTRAYAL",
	joinColumns = {@JoinColumn(name = "userId",
	referencedColumnName = "userId")})
	@Column(name = "restaurantId")
	private List<Integer> restaurantId;
	
	public List<Integer> getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(List<Integer> restaurantId) {
		this.restaurantId = restaurantId;
	}
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "USER_PORTRAYAL",joinColumns = {@JoinColumn(name = "userId",referencedColumnName = "userId")})
	@Column(name = "kitchenId")
	private List<Integer> kitchenId;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "USER_PORTRAYAL",joinColumns = {@JoinColumn(name = "userId",referencedColumnName = "userId")})
	@Column(name = "microKitchenId")
	private List<Integer> microKitchenId;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "USER_PORTRAYAL",joinColumns = {@JoinColumn(name = "userId",referencedColumnName = "userId")})
	@Column(name = "orgID")
	private List<Integer> orgId;
	
	@OneToOne(cascade=CascadeType.PERSIST)
	@JoinTable(name="USER_ROLES",
	joinColumns={@JoinColumn(name="userId", referencedColumnName="userId")},
	inverseJoinColumns={@JoinColumn(name="roleId", referencedColumnName="id")})
	private Role role;

	public Integer getOrgId() {
		if(orgId!=null)
			if(orgId.size()>0)
		       return orgId.get(0);
			else
				return null;
		else
			return null;
	}

	public void setOrgId(Integer orgId) {
		List<Integer> orgList=new ArrayList<>();
		orgList.add(orgId);
		this.orgId = orgList;
	}

	public User() {
		super();
	    role=new Role();
	}

	public List<Integer> getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(List<Integer> kitchenId) {
		this.kitchenId = kitchenId;
	}

	public List<Integer> getMicroKitchenId() {
		return microKitchenId;
	}

	public void setMicroKitchenId(List<Integer> microKitchenId) {
		this.microKitchenId = microKitchenId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getDateOfJoinning() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String d=null;
		try {
			if(this.dateOfJoinning!=null){
			java.util.Date date = formatter.parse(this.dateOfJoinning);
			d=formatter.format(date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	public void setDateOfJoinning(String dateOfJoinning) {
		this.dateOfJoinning = dateOfJoinning;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}


	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
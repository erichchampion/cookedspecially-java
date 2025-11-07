package com.cookedspecially.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

import com.cookedspecially.utility.DateUtil;

@Entity
@Table(name="TILLS")
public class Till implements Serializable {
    private static final long serialVersionUID = 1L;
    
	public enum TILL_STATUS { ACTIVE,INACTIVE,CLOSE,OPEN };

	
	@Id
	@GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "tillId", unique = true)
	private String tillId;
	
	@Column(name="BALANCE")
	private float balance=0;
	
	@Column(name="TILLNAME", unique = true)
	private String tillName;
	
	@Column(name="DATE_CREATED")
	private Timestamp dateCreated;
	
	@Column(name="STATUS")
	private String status;

	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinTable(name="TILL_MAP",
	joinColumns={@JoinColumn(name="tillId", referencedColumnName="tillId")},
	inverseJoinColumns={@JoinColumn(name="fulfillmentCenterId", referencedColumnName="id")})
	private FulfillmentCenter fulfillmentcenter;
	
	public Till(){}
	
	public Till(String tillId){this.tillId=tillId;}
	
	public Till(Float openingBalance, String tillName) {
		super();
		this.tillName = tillName;
		this.balance=(float) 0.0;
		this.status=TILL_STATUS.ACTIVE.toString();
		this.dateCreated=DateUtil.getCurrentTimestampInGMT();
	}

	public String getTillId() {
		return tillId;
	}

	public void setTillId(String tillId) {
		this.tillId = tillId;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public String getTillName() {
		return tillName;
	}

	public void setTillName(String tillName) {
		this.tillName = tillName;
	}

	public Timestamp getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}

	public FulfillmentCenter getFulfillmentcenter() {
		return fulfillmentcenter;
	}

	public void setFulfillmentcenter(FulfillmentCenter fulfillmentcenter) {
		this.fulfillmentcenter = fulfillmentcenter;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		// TODO Auto-generated method stub
		return status;
	}
	
	
	
}

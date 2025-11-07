package com.cookedspecially.domain;

import com.cookedspecially.enums.credit.CustomerCreditAccountStatus;
import com.cookedspecially.utility.DateUtil;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * @author Abhishek
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="CUSTOMER_CREDIT")
public class CustomerCredit implements Serializable {
    private static final long serialVersionUID = 1L;


	@Id  
    @GeneratedValue(generator="myGenerator")  
    @GenericGenerator(name="myGenerator", strategy="foreign", parameters=@Parameter(value="customer", name = "property")) 
	private int customerId;
		
	@Column(name="creditBalance")
	private float creditBalance;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private CustomerCreditAccountStatus status=CustomerCreditAccountStatus.ACTIVE;
	

	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="type")
    private CreditType creditType;
	
	@Column(name="lastModified")
	private Timestamp lastModified=DateUtil.getCurrentTimestampInGMT();
	
	@Column(name="maxLimit")
	private float maxLimit;
	
	@Column(name="ffcId")
	private int ffcId;
	
	@Column(name="billingAddress")
	private String billingAddress;

	@OneToOne(cascade=CascadeType.ALL)  
    @JoinColumn(name="customerId") 
	@JsonBackReference(value="customer-credit")
    private Customer customer;


	public CustomerCredit() {
		super();
	}

	public CustomerCredit(Customer customer, CreditType type, int ffcId, String address) {
		super();
		this.creditBalance = 0;
		this.customer=customer;
		this.creditType=type;
		this.ffcId=ffcId;
		this.billingAddress=address;
	}

	public int getCustomerId() {
	return customerId;
    }
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public float getCreditBalance() {
		return creditBalance;
	}

	public void setCreditBalance(float creditBalance) {
		this.creditBalance = creditBalance;
	}

	public CustomerCreditAccountStatus getStatus() {
		return status;
	}

	public void setStatus(CustomerCreditAccountStatus status) {
		this.status = status;
	}

	public CreditType getCreditType() {
		return creditType;
	}

	public void setCreditType(CreditType creditType) {
		this.creditType = creditType;
	}

	public Timestamp getLastModified() {
		return lastModified;
	}

	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}

	public float getMaxLimit() {
		return maxLimit;
	}

	public void setMaxLimit(float maxLimit) {
		this.maxLimit = maxLimit;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public int getFfcId() {
		return ffcId;
	}

	public void setFfcId(int ffcId) {
		this.ffcId = ffcId;
	}

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}
	
	
}

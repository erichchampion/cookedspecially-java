package com.cookedspecially.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.cookedspecially.utility.DateUtil;

/**
 * @author Abhishek
 *
 */
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="CREDIT_BILL_PAYMENTS")
public class CreditPayment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
	@GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "id", unique = true)
	private String paymentId;
    
    
    @Column(name = "billId")
	private String billId;
    
	@Column(name="amount")
	private float amount;
	
	@Column(name="date")
	private Timestamp date;
	
	@Column(name = "paymentType")
	private String paymentType;
	
	@Column(name = "remarks")
	private String remark;

	
	
	public CreditPayment() {
		super();
	}

	public CreditPayment(String billId, float amount, String paymentType, String remark) {
		super();
		this.billId = billId;
		this.amount = amount;
		this.paymentType = paymentType;
		this.remark = remark;
		this.date=DateUtil.getCurrentTimestampInGMT();
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}

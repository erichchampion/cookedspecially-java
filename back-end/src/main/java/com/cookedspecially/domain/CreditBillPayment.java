package com.cookedspecially.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.cookedspecially.utility.DateUtil;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="CREDIT_BILL_PAYMENTS")
public class CreditBillPayment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
	@GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "paymentId", unique = true)
	private String paymentId;
    
	@Column(name="method")
	private String method;
    
	@Column(name="amount")
	private float amount;
	
	@Column(name="date")
	private Timestamp date;
	
	@Column(name="remarks")
	private String remarks;

	@ManyToOne
    @JoinColumn(name = "billId")
	@JsonBackReference(value="payment")
	private CreditBill creditBill;
	
	public CreditBillPayment() {
		super();
	}

	public CreditBillPayment(String method, float amount, CreditBill bill, String remarks) {
		super();
		this.method = method;
		this.amount = amount;
		this.creditBill = bill;
		this.remarks = remarks;
		this.date=DateUtil.getCurrentTimestampInGMT();
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
}

package com.cookedspecially.domain;

import com.cookedspecially.enums.till.TillTransactionStatus;
import com.cookedspecially.enums.till.TransactionCategory;
import com.cookedspecially.utility.DateUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="SALE_REGISTER_TRANSACTIONS")
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "TRANSACTIONID", unique = true)
	private String transactionId;
	
	@Column(name="TRANSACTION_TYPE")
	private String transactionType;
	
	@Enumerated(EnumType.STRING)
	@Column(name="TRANSACTION_CATEGORY")
	private TransactionCategory transactionCategory;
	
	@Column(name="USERID")
	private Integer userId;
	
	@Column(name="PREVIOUS_USERID")
	private Integer previousUserId;
	
	@Column(name="TRANSACTION_AMOUNT")
	private Float transactionAmount;
	
	@Column(name="TRANSACTION_TIME")
	private Timestamp transactionTime;
	
	@Column(name="TILLID")
	private String tillId;
	
	@Column(name="CHECKID")
	private Integer checkId;
	
	@Column(name="REMARKS")
	private String remarks;
	
	@Column(name="STATUS")
	private String status;

    @Column(name = "isCreditTransaction", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isCreditTransaction = false;

	
	public Transaction(){}

	public Transaction(Integer userId, String tillId, Integer checkId, Float amount, String type, String category) {
	   this.userId=userId;
	   this.tillId=tillId;
	   this.checkId=checkId;
	   this.transactionAmount=amount;
	   this.transactionType=type;
		this.transactionCategory = TransactionCategory.valueOf(category);
	   this.transactionTime=DateUtil.getCurrentTimestampInGMT(); 
	   this.status=TillTransactionStatus.PENDING.name();
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getTransactionCategory() {
		return transactionCategory.name();
	}

	public void setTransactionCategory(String transactionCategory) {
		this.transactionCategory = TransactionCategory.valueOf(transactionCategory);
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Float getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(Float transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public Timestamp getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Timestamp transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getTillId() {
		return tillId;
	}

	public void setTillId(String tillId) {
		this.tillId = tillId;
	}

	public Integer getCheckId() {
		return checkId;
	}

	public void setCheckId(Integer checkId) {
		this.checkId = checkId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remark) {
		this.remarks = remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getPreviousUserId() {
		return previousUserId;
	}

	public void setPreviousUserId(Integer previousUserId) {
		this.previousUserId = previousUserId;
	}

    public boolean getIsCreditTransaction() {
        return isCreditTransaction;
    }

    public void setIsCreditTransaction(boolean isCreditTransaction) {
        this.isCreditTransaction = isCreditTransaction;
    }
	
	
}

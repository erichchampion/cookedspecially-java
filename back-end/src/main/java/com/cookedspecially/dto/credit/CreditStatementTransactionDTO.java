package com.cookedspecially.dto.credit;

import java.util.Date;

/**
 * Created by Abhishek on 2/7/2017.
 */
public class CreditStatementTransactionDTO {

    private Date transactionDate;
    private float amount;
    private String type;
    private String description;
    private String address;
    private String invoiceId;

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(Object amountStr) {
        this.amount = Float.parseFloat("" + amountStr);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
}

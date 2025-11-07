package com.cookedspecially.dto.credit;

import java.util.Date;

/**
 * Created by Abhishek on 11/21/2016.
 */
public class AgedCreditDTO {

    public int customerId;
    public String name;
    public String email;
    public String mobileNo;
    public String billingAddress;
    public float creditBalance;
    public String creditType;
    public Date lastTransactionDate;


    public void setCreditBalance(Object creditBalance) {
        try {
            this.creditBalance = Float.parseFloat("" + creditBalance);
        } catch (NumberFormatException e) {
            this.creditBalance = 0;
        }
    }
}

package com.cookedspecially.dto.credit;

import java.util.Date;

/**
 * Created by Abhishek on 2/3/2017.
 */
public class CreditInfoDTO {
    public float creditBalance;
    public Date recentPaymentDate;
    public float recentPaymentAmount;
    public float maxLimit;
    public float availableCredit;

    public void setAvailableCredit(Object availableCredit) {
        this.availableCredit = Float.parseFloat("" + availableCredit);
    }
}

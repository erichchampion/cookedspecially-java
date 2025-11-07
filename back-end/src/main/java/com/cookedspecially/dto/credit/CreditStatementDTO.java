package com.cookedspecially.dto.credit;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * Created by Abhishek on 11/20/2016.
 */
public class CreditStatementDTO {

    final static Logger logger = Logger.getLogger(CreditStatementDTO.class);

    //Customer Basic Details
    public String name;
    public String mobileNo;
    public String email;
    public Integer orgId;

    //Credit Account And Statement Basic Details
    public String billingAddress;
    public String creditType;
    public String creditName;
	public float openingBanalce;
    public float outStandingBalance;
	public float totalPurchases;
    public float paymentReceived;
    public float maxLimit;
    public float availableCredit;
    public Date statementDate;
    public Date fromDate;
    public Date toDate;
    public List<CreditStatementTransactionDTO> transactions;
    
    public String getCreditName() {
		return creditName;
	}
    
    public float getOpeningBanalce() {
		return openingBanalce;
	}

    public void setOpeningBanalce(Object openingBanalce) {
        try {
            this.openingBanalce = Float.parseFloat("" + openingBanalce);
        } catch (NumberFormatException e) {
            this.openingBanalce = 0;
        }
    }

    public float getOutStandingBalance() {
		return outStandingBalance;
	}

    public void setOutStandingBalance(Object outStandingBalance) {
        try {
            this.outStandingBalance = Float.parseFloat("" + outStandingBalance);
        } catch (NumberFormatException e) {
            this.outStandingBalance = 0;
        }
    }

	public String getName() {
		return name;
	}

    public String getMobileNo() {
        return mobileNo;
	}

	public String getEmail() {
		return email;
	}
	
	public String getBillingAddress() {
		return billingAddress;
	}

	public String getCreditType() {
		return creditType;
	}
	
	public float getTotalPurchases() {
		return totalPurchases;
	}

    public void setTotalPurchases(Object totalPurchases) {
        try {
            logger.info(totalPurchases);
            this.totalPurchases = Float.parseFloat("" + totalPurchases);
        } catch (NumberFormatException e) {
            this.totalPurchases = 0;
        }
    }

	public float getPaymentReceived() {
		return paymentReceived;
	}

    public void setPaymentReceived(Object paymentReceived) {
        try {
            logger.info(paymentReceived);
            this.paymentReceived = Float.parseFloat("" + paymentReceived);
        } catch (NumberFormatException e) {
            this.paymentReceived = 0;
        }
    }

	public float getMaxLimit() {
		return maxLimit;
	}

    public void setMaxLimit(Object maxLimit) {
        try {
            this.maxLimit = Float.parseFloat("" + maxLimit);
        } catch (NumberFormatException e) {
            this.maxLimit = 0;
        }
    }

	public float getAvailableCredit() {
		return availableCredit;
	}

    public Date getStatementDate() {
        return statementDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

    public List<CreditStatementTransactionDTO> getTransactions() {
        return transactions;
	}
}

package com.cookedspecially.domain;

import java.io.Serializable;

/**
 * @author rahul
 *
 */

public class PaymentGatwayDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    
	String MID;
	String TXNID;
	String ORDERID;
	String BANKTXNID;
	String TXNAMOUNT;
	String CURRENCY;
	String STATUS;
	String RESPCODE;
	String RESPMSG;
	String TXNDATE;
	String GATEWAYNAME;
	String BANKNAME;
	String PAYMENTMODE;
	String PROMO_CAMP_ID;
	String PROMO_STATUS;
	String PROMO_RESPCODE;
	String CHECKSUMHASH;
	
	public String getMID() {
		return MID;
	}
	public void setMID(String mID) {
		MID = mID;
	}
	public String getTXNID() {
		return TXNID;
	}
	public void setTXNID(String tXNID) {
		TXNID = tXNID;
	}
	public String getORDERID() {
		return ORDERID;
	}
	public void setORDERID(String oRDERID) {
		ORDERID = oRDERID;
	}
	public String getBANKTXNID() {
		return BANKTXNID;
	}
	public void setBANKTXNID(String bANKTXNID) {
		BANKTXNID = bANKTXNID;
	}
	public String getTXNAMOUNT() {
		return TXNAMOUNT;
	}
	public void setTXNAMOUNT(String tXNAMOUNT) {
		TXNAMOUNT = tXNAMOUNT;
	}
	public String getCURRENCY() {
		return CURRENCY;
	}
	public void setCURRENCY(String cURRENCY) {
		CURRENCY = cURRENCY;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getRESPCODE() {
		return RESPCODE;
	}
	public void setRESPCODE(String rESPCODE) {
		RESPCODE = rESPCODE;
	}
	public String getRESPMSG() {
		return RESPMSG;
	}
	public void setRESPMSG(String rESPMSG) {
		RESPMSG = rESPMSG;
	}
	public String getTXNDATE() {
		return TXNDATE;
	}
	public void setTXNDATE(String tXNDATE) {
		TXNDATE = tXNDATE;
	}
	public String getGATEWAYNAME() {
		return GATEWAYNAME;
	}
	public void setGATEWAYNAME(String gATEWAYNAME) {
		GATEWAYNAME = gATEWAYNAME;
	}
	public String getBANKNAME() {
		return BANKNAME;
	}
	public void setBANKNAME(String bANKNAME) {
		BANKNAME = bANKNAME;
	}
	public String getPAYMENTMODE() {
		return PAYMENTMODE;
	}
	public void setPAYMENTMODE(String pAYMENTMODE) {
		PAYMENTMODE = pAYMENTMODE;
	}
	public String getPROMO_CAMP_ID() {
		return PROMO_CAMP_ID;
	}
	public void setPROMO_CAMP_ID(String pROMO_CAMP_ID) {
		PROMO_CAMP_ID = pROMO_CAMP_ID;
	}
	public String getPROMO_STATUS() {
		return PROMO_STATUS;
	}
	public void setPROMO_STATUS(String pROMO_STATUS) {
		PROMO_STATUS = pROMO_STATUS;
	}
	public String getPROMO_RESPCODE() {
		return PROMO_RESPCODE;
	}
	public void setPROMO_RESPCODE(String pROMO_RESPCODE) {
		PROMO_RESPCODE = pROMO_RESPCODE;
	}
	public String getCHECKSUMHASH() {
		return CHECKSUMHASH;
	}
	public void setCHECKSUMHASH(String cHECKSUMHASH) {
		CHECKSUMHASH = cHECKSUMHASH;
	}

}

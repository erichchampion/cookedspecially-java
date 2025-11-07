package com.cookedspecially.dto.saleRegister;

import java.util.ArrayList;
import java.util.List;


public class TransactionDTO {
public List<SaleTransaction> transationList;
public TillDTO tillDetails;
public String result;
public String message;
public TransactionDTO() {
	super();
	this.transationList=new ArrayList<SaleTransaction>();
	this.tillDetails=new TillDTO();

}


}

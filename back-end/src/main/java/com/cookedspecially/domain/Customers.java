/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author shashank, Abhishek
 *
 */
public class Customers implements Serializable {
    private static final long serialVersionUID = 1L;

	boolean exactMatch;
	boolean newCustomer;
	
	List<Customer> customers;
	
	List<CustomerAddress> customerAddress;

	public List<CustomerAddress> getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(List<CustomerAddress> customerAddress) {
		
		this.customerAddress = customerAddress;
	}

	public boolean getExactMatch() {
		return exactMatch;
	}

	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}

	public boolean getNewCustomer() {
		return newCustomer;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customerList) {
		this.customers = customerList;
	}
	
}

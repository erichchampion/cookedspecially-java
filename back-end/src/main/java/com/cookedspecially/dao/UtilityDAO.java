package com.cookedspecially.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Customer;
import com.cookedspecially.domain.CustomerAddress;

/**
 * @author Abhishek
 *
 */
public interface UtilityDAO {

	public void updateData(List<Object> data);
	public void removeCustomer(List<Integer> customerList);	
	public List<Check> getAllChecks(int lowerCount, int upperCount);
    public int getLastCustomerId();
	public List<Customer> getAllCustomer(int lowerCount, int upperCount, boolean all);
	public List<String> listDuplicateCustomer(Integer orgId);
	public List<Customer> listAllDuplicateCustomer(String mobileNo);
	public void updateCustomerDetails(HashMap<Integer, Set<Integer>> duplicateCustomer);
	public Check getLastChecks();
}

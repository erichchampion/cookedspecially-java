package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.Employee;
import com.cookedspecially.domain.UserPortrayal;

/**
 * @author Rahul
 *
 */

public interface EmployeeService {

	public void addEmployee(Employee deliveryBoy);
	public List<Employee> listEmployees();
	public List<Employee> listEmployeeByUser(Integer userId);
	public void removeEmployee(Integer id);
	public Employee getEmployee(Integer id);	
	public Employee getEmployeeByUserName(String userName);
	
	/*public List<UserPortrayal> listFulfillmentCenterByResturant(Integer resturantId);
	public void addFulfillmentCenterByUser(UserPortrayal userPortrayal);
	public void removeFulfillmentCenterByUser(Integer employeId, Integer kitchenId);*/
	
}

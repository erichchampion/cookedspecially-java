package com.cookedspecially.dao;
import java.util.List;

import com.cookedspecially.domain.Employee;
import com.cookedspecially.domain.UserPortrayal;

/**
 * @author rahul
 *
 */

public interface EmployeeDAO
{
	public void addEmployee(Employee Employee);
	public List<Employee> listEmployee();
	public Employee getEmployee(Integer id);
	public List<Employee> listEmployeeByUser(Integer userId);
	public void removeEmployee(Integer id);
	public Employee getEmployeeByUserName(String userName);
	
	/*
	public List<UserPortrayal> listFulfillmentCenterByResturant(Integer resturantId);
	public void addFulfillmentCenterByUser(UserPortrayal userPortrayal);
	public void removeFulfillmentCenterByUser(Integer employeId, Integer kitchenId);*/
}

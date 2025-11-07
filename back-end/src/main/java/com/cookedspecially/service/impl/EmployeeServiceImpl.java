package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.EmployeeDAO;
import com.cookedspecially.domain.Employee;
import com.cookedspecially.domain.UserPortrayal;
import com.cookedspecially.service.EmployeeService;

/**
 * @author Rahul
 *
 */
@Service
public class EmployeeServiceImpl implements EmployeeService{

	@Autowired
	private EmployeeDAO EmployeeDAO;
	
	@Override
	@Transactional
	public void addEmployee(Employee Employee) {
		EmployeeDAO.addEmployee(Employee);
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public List<Employee> listEmployees() {
		// TODO Auto-generated method stub
		return EmployeeDAO.listEmployee();
	}

	@Override
	@Transactional
	public void removeEmployee(Integer id) {
		// TODO Auto-generated method stub
		EmployeeDAO.removeEmployee(id);
		
	}

	@Override
	@Transactional
	public Employee getEmployee(Integer id) {
		// TODO Auto-generated method stub
		return EmployeeDAO.getEmployee(id);
	}

	@Override
	@Transactional
	public List<Employee> listEmployeeByUser(Integer userId) {
		// TODO Auto-generated method stub
		return EmployeeDAO.listEmployeeByUser(userId);
	}

	@Override
	@Transactional
	public Employee getEmployeeByUserName(String userName) {
		// TODO Auto-generated method stub
		return EmployeeDAO.getEmployeeByUserName(userName);
	}
/*
	@Override
	@Transactional
	public List<UserPortrayal> listFulfillmentCenterByResturant(
			Integer restaurantId) {
		// TODO Auto-generated method stub
		return EmployeeDAO.listFulfillmentCenterByResturant(restaurantId);
	}

	@Override
	@Transactional
	public void addFulfillmentCenterByUser(UserPortrayal userPortrayal) {
		// TODO Auto-generated method stub
		EmployeeDAO.addFulfillmentCenterByUser(userPortrayal);
	}

	@Override
	@Transactional
	public void removeFulfillmentCenterByUser(Integer employeId, Integer kitchenId) {
		// TODO Auto-generated method stub
		EmployeeDAO.removeFulfillmentCenterByUser(employeId, kitchenId);
	}*/

}

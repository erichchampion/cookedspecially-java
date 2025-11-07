package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.EmployeeDAO;
import com.cookedspecially.domain.Employee;
import com.cookedspecially.domain.User;
import com.cookedspecially.domain.UserPortrayal;

/**
 * @author Rahul
 *
 */

@Repository
public class EmployeeDAOImpl implements EmployeeDAO
{
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addEmployee(Employee Employee) {
		sessionFactory.getCurrentSession().saveOrUpdate(Employee);
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Employee> listEmployee() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from Employee").list();

	}
	
	@Override
	public Employee getEmployee(Integer id){
		
		 return  (Employee) sessionFactory.getCurrentSession().get(Employee.class, id);
	}

	@Override
	public void removeEmployee(Integer id) {
		Employee Employee= (Employee) sessionFactory.getCurrentSession().load(Employee.class, id);
		if (null != Employee) {
			sessionFactory.getCurrentSession().delete(Employee);
		}
	}

	@Override
	public List<Employee> listEmployeeByUser(Integer userId) {
		// TODO Auto-generated method stub
		return  sessionFactory.getCurrentSession().createCriteria(Employee.class).add(Restrictions.eq("restaurantId", userId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public Employee getEmployeeByUserName(String userName) {
		// TODO Auto-generated method stub
		return (Employee)sessionFactory.getCurrentSession().createCriteria(Employee.class).add(Restrictions.eq("userName", userName)).uniqueResult();
	}

	/*@Override
	public List<UserPortrayal> listFulfillmentCenterByResturant(
			Integer restaurantId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createCriteria(UserPortrayal.class).add(Restrictions.eq("restaurantId", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public void addFulfillmentCenterByUser(UserPortrayal userPortrayal) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().save(userPortrayal);
	}

	@Override
	public void removeFulfillmentCenterByUser(Integer employeId, Integer kitchenId) {
		// TODO Auto-generated method stub
		UserPortrayal userPortrayal= (UserPortrayal) sessionFactory.getCurrentSession().createCriteria(UserPortrayal.class).add(Restrictions.eq("kitchenId",kitchenId)).add(Restrictions.eq("userId",employeId)).uniqueResult();
		if (null != userPortrayal) {
			sessionFactory.getCurrentSession().delete(userPortrayal);
		}
	}*/

}

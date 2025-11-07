/**
 * 
 */
package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.MenuDAO;
import com.cookedspecially.domain.Menu;
import com.cookedspecially.enums.Status;

/**
 * @author sagarwal, rahul
 *
 */
@Repository
public class MenuDAOImpl implements MenuDAO {


	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addMenu(Menu menu) {
		sessionFactory.getCurrentSession().saveOrUpdate(menu);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Menu> listMenu() {
		return sessionFactory.getCurrentSession().createQuery("from Menu").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Menu> listMenuByRestaurant(Integer restaurantId) {
		return sessionFactory.getCurrentSession().createCriteria(Menu.class).add(Restrictions.eq("restaurantId", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}
	//section_dish
	@Override
	public void removeMenu(Integer id) {
		Menu menu = (Menu) sessionFactory.getCurrentSession().load(Menu.class, id);
		if (null != menu) {
			sessionFactory.getCurrentSession().delete(menu);
		}

	}

	@Override
	public Menu getMenu(Integer id) {
		return (Menu) sessionFactory.getCurrentSession().get(Menu.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Menu> allMenusByStatus(Integer restaurantId, Status status) {
		return sessionFactory.getCurrentSession().createCriteria(Menu.class).add(Restrictions.and(Restrictions.eq("status", status), Restrictions.eq("restaurantId", restaurantId))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Menu> allPosMenus(Integer restaurantId, Status status,boolean posStatus) {
		
		return sessionFactory.getCurrentSession().createCriteria(Menu.class).add(Restrictions.and(Restrictions.eq("restaurantId", restaurantId),Restrictions.or(Restrictions.eq("status", status), Restrictions.eq("posVisible",posStatus)))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public Menu getMenuByMenuName(String name, Integer restaurantId) {
		// TODO Auto-generated method stub
		
		Query queryS =sessionFactory.getCurrentSession().createQuery("from  Menu where  name=:name AND restaurantId=:restaurantId");
		queryS.setParameter("name",name);
		queryS.setParameter("restaurantId", restaurantId);

		return (Menu) queryS.uniqueResult();  
	}

}

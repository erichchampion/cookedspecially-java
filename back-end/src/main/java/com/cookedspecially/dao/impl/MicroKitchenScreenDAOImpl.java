package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.FulfillmentCenterDAO;
import com.cookedspecially.dao.MicroKitchenScreenDAO;
import com.cookedspecially.domain.DeliveryArea;
import com.cookedspecially.domain.FulfillmentCenter;
import com.cookedspecially.domain.MicroKitchenScreen;

@Repository
public class MicroKitchenScreenDAOImpl implements MicroKitchenScreenDAO {

	@Autowired
	SessionFactory  sessionFactory;
	
	@Override
	public void addMicroKitchenScreen(MicroKitchenScreen kitchenScreen) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(kitchenScreen);
		
	}

	@Override
	public void removeMicroKitchenScreen(int id) {
		// TODO Auto-generated method stub
	MicroKitchenScreen microKS= (MicroKitchenScreen)sessionFactory.getCurrentSession().get(MicroKitchenScreen.class, id);
	if(microKS!=null){
		sessionFactory.getCurrentSession().delete(microKS);
		sessionFactory.getCurrentSession().createSQLQuery("SET SQL_SAFE_UPDATES=0;").executeUpdate();
		Query query =sessionFactory.getCurrentSession().createQuery("Update Dish SET microScreen=:id where microScreen=:MScreenId");
		query.setParameter("id",0);
		query.setParameter("MScreenId", microKS.getId());
		query.executeUpdate();
	}
	}

	@Override
	public List<MicroKitchenScreen> getMicroKitchenScreensByUser(int restaurantId) {
		// TODO Auto-generated method stub66
		return sessionFactory.getCurrentSession().createQuery("from MicroKitchenScreen where restaurantId="+restaurantId).list();
	}

	@Override
	public List<MicroKitchenScreen> getMicroKitchenScreensByKitchen(int resturantId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from MicroKitchenScreen where kitchenId="+resturantId).list();
	}
	@Override
	public MicroKitchenScreen getMicroKitchenScreen(int microKitchenId) {
		// TODO Auto-generated method stub
		return (MicroKitchenScreen)sessionFactory.getCurrentSession().get(MicroKitchenScreen.class,microKitchenId);
	}


}

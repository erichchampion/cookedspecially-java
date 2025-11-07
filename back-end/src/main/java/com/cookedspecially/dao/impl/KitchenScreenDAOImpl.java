package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.KitchenScreenDAO;
import com.cookedspecially.domain.DeliveryArea;
import com.cookedspecially.domain.KitchenScreen;

@Repository
public class KitchenScreenDAOImpl implements KitchenScreenDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addKitchenScreen(KitchenScreen kitchenScreen) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(kitchenScreen);
	}

	@Override
	public void removeKitchenScreen(int id) {
		// TODO Auto-generated method stub	
		KitchenScreen kitchenScreen = (KitchenScreen)sessionFactory.getCurrentSession().load(KitchenScreen.class, id);
		if(kitchenScreen !=null){
			sessionFactory.getCurrentSession().delete(kitchenScreen);
			sessionFactory.getCurrentSession().createSQLQuery("SET SQL_SAFE_UPDATES=0;").executeUpdate();
			Query query =sessionFactory.getCurrentSession().createQuery("Update DeliveryArea SET kitchenScreenId=:id where kitchenScreenId=:kitchenScreenId");
			query.setParameter("id",0);
			query.setParameter("kitchenScreenId", kitchenScreen.getId());
			query.executeUpdate();
			
			Query queryS =sessionFactory.getCurrentSession().createQuery("Update Check SET kitchenScreenId=:id where kitchenScreenId=:kitchenScreenId");
			queryS.setParameter("id",0);
			queryS.setParameter("kitchenScreenId", kitchenScreen.getId());
			queryS.executeUpdate();
			
			Query queryT =sessionFactory.getCurrentSession().createQuery("Update DeliveryBoy SET kitchenScreenId=:id where kitchenScreenId=:kitchenScreenId");
			queryT.setParameter("id",0);
			queryT.setParameter("kitchenScreenId", kitchenScreen.getId());
			queryT.executeUpdate();
			
		}
	}

	@Override
	public List<KitchenScreen> getKitchenScreens(int restaurantId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from KitchenScreen where restaurantId="+restaurantId).list();
	}

	@Override
	public KitchenScreen getKitchenScreen(int id) {
		// TODO Auto-generated method stub
		return (KitchenScreen) sessionFactory.getCurrentSession().get(KitchenScreen.class, id);
	}

}

package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.FulfillmentCenterDAO;
import com.cookedspecially.domain.DeliveryArea;
import com.cookedspecially.domain.FulfillmentCenter;

@Repository
public class FulfillmentCenterDAOImpl implements FulfillmentCenterDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addKitchenScreen(FulfillmentCenter kitchenScreen) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(kitchenScreen);
	}

	@Override
	public void removeKitchenScreen(int id) {
		// TODO Auto-generated method stub	
		FulfillmentCenter kitchenScreen = (FulfillmentCenter)sessionFactory.getCurrentSession().load(FulfillmentCenter.class, id);
		if(kitchenScreen !=null){
			sessionFactory.getCurrentSession().delete(kitchenScreen);
			sessionFactory.getCurrentSession().createSQLQuery("SET SQL_SAFE_UPDATES=0;").executeUpdate();
			Query query =sessionFactory.getCurrentSession().createQuery("Update DeliveryArea SET fulfillmentCenterId=:id where fulfillmentCenterId=:fulfillmentCenterId");
			query.setParameter("id",0);
			query.setParameter("fulfillmentCenterId", kitchenScreen.getId());
			query.executeUpdate();
			
			Query queryS =sessionFactory.getCurrentSession().createQuery("Update Check SET kitchenScreenId=:id where kitchenScreenId=:fulfillmentCenterId");
			queryS.setParameter("id",0);
			queryS.setParameter("fulfillmentCenterId", kitchenScreen.getId());
			queryS.executeUpdate();
			
			
			/*Query queryT =sessionFactory.getCurrentSession().createQuery("Update DeliveryBoy SET id=:id where id=:fulfillmentCenterId");
			queryT.setParameter("id",0);
			queryT.setParameter("fulfillmentCenterId", kitchenScreen.getId());
			queryT.executeUpdate();*/
			
		}
	}

	@Override
	public List<FulfillmentCenter> getKitchenScreens(int restaurantId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from FulfillmentCenter where restaurantId="+restaurantId).list();
	}

	@Override
	public FulfillmentCenter getKitchenScreen(int id) {
		// TODO Auto-generated method stub
		return (FulfillmentCenter) sessionFactory.getCurrentSession().get(FulfillmentCenter.class, id);
	}

}

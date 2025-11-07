package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.DeliveryBoyDAO;
import com.cookedspecially.domain.DeliveryBoy;

/**
 * @author Rahul
 *
 */

@Repository
public class DeliveryBoyDAOImpl implements DeliveryBoyDAO
{
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addDeliveryBoy(DeliveryBoy deliveryBoy) {
		sessionFactory.getCurrentSession().saveOrUpdate(deliveryBoy);
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<DeliveryBoy> listDeliveryBoy() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from DeliveryBoy").list();

	}
	
	@Override
	public DeliveryBoy getDeliveryBoy(Integer id){
		
		 return  (DeliveryBoy) sessionFactory.getCurrentSession().get(DeliveryBoy.class, id);
	}

	@Override
	public void removeDeliveryBoy(Integer id) {
		DeliveryBoy deliveryBoy= (DeliveryBoy) sessionFactory.getCurrentSession().load(DeliveryBoy.class, id);
		if (null != deliveryBoy) {
			sessionFactory.getCurrentSession().delete(deliveryBoy);
		}
		
	}

	@Override
	public List<DeliveryBoy> listDeliveryBoyByUser(Integer userId) {
		// TODO Auto-generated method stub
		return  sessionFactory.getCurrentSession().createCriteria(DeliveryBoy.class).add(Restrictions.eq("userId", userId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

}

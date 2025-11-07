package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.NotificationDAO;
import com.cookedspecially.domain.Notifier;
import com.cookedspecially.domain.Subscription;
import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.notification.Device;

@Repository
public class NotificationDAOImpl implements NotificationDAO{

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void addAndUpdateNotifier(Notifier notifier) {
		sessionFactory.getCurrentSession().saveOrUpdate(notifier);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Notifier> getNotifier(int restaurantId) {
		return sessionFactory.getCurrentSession().createQuery("from Notifier where restaurantId="+restaurantId).list();
	}
	
	@Override
	public Notifier getNotifier(int restaurantId, Device device) {
		Query query=sessionFactory.getCurrentSession().createQuery("from Notifier where restaurantId=:restId AND device=:dev AND status=:activeStatus");
		query.setParameter("dev", device);
		query.setInteger("restId", restaurantId);
		query.setParameter("activeStatus", Status.ACTIVE);
		query.setMaxResults(1);
		return (Notifier) query.uniqueResult();
	}

	@Override
	public Notifier isNotifierRegistered(int restaurantId, Device device) {
		Query query=sessionFactory.getCurrentSession().createQuery("FROM Notifier WHERE restaurantId=:restId AND device=:device");
		query.setInteger("restId", restaurantId);
		query.setParameter("device", device);
		query.setMaxResults(1);
		return (Notifier) query.uniqueResult();
	}
	@Override
	public Notifier getNotifierById(int notifierId) {
		return (Notifier) sessionFactory.getCurrentSession().load(Notifier.class, notifierId);
	}

	@Override
	public void deleteNotifier(int notifierIdd) {
		sessionFactory.getCurrentSession().delete(getNotifierById(notifierIdd));
	}
	
	
	
	@Override
	public void deleteNotifierR(int restaurantIdd) {
		Query query=sessionFactory.getCurrentSession().createQuery("DELETE from Notifier WHERE restaurantId="+restaurantIdd);
        query.executeUpdate();
	}

	@Override
	public void addAndUpdateSubscriber(Subscription Subscription) {
		sessionFactory.getCurrentSession().saveOrUpdate(Subscription);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Subscription> getSubscriberList(List<Integer> customerId) {
		Query query = sessionFactory.getCurrentSession().createQuery("FROM Subscription where customerId IN :customerIdList")
				.setParameterList("customerIdList", customerId);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Subscription> getSubscriber(String mobileNo) {
		return sessionFactory.getCurrentSession().createQuery("FROM Subscription WHERE mobileNo="+mobileNo).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Subscription> getSubscriberC(int customerId) {
		return sessionFactory.getCurrentSession().createQuery("FROM Subscription WHERE customerId="+customerId).list();
	}

	@Override
	public Subscription getSubscriber(int subscriberId) {
		return (Subscription) sessionFactory.getCurrentSession().load(Subscription.class, subscriberId);
	}

	@Override
	public void deleteSubscriber(int subscriberIdd) {
		Query query=sessionFactory.getCurrentSession().createQuery("DELETE Subscription WHERE subscriberId="+subscriberIdd);
        query.executeUpdate();
	}

	@Override
	public void deleteSubscriber(String mobile) {
		Query query=sessionFactory.getCurrentSession().createQuery("DELETE Subscription WHERE mobileNo=:mobile");
		query.setString("mobile", mobile);
        query.executeUpdate();
	}
	
	@Override
	public void deSubscriberToken(String tokenn) {
		Query query=sessionFactory.getCurrentSession().createQuery("DELETE Subscription WHERE token=:tok");
		query.setString("tok", tokenn);		
        query.executeUpdate();
	}

	@Override
	public void deSubscriber(int custId) {
//		Query query=sessionFactory.getCurrentSession().createQuery("UPDATE Subscription set status=:currentStatus WHERE customerId="+custId);
//        query.setParameter("currentStatus", Status.INACTIVE);
//        query.executeUpdate();
		sessionFactory.getCurrentSession().createQuery("DELETE Subscription WHERE customerId="+custId).executeUpdate();

	}

	@Override
	public Subscription isRegstered(int customerId, String token, Device deviceType) {
		Query query=sessionFactory.getCurrentSession().createQuery("FROM Subscription WHERE customerId=:custId AND token=:currentToken AND device=:device");
		query.setInteger("custId", customerId);
		query.setString("currentToken", token);
		query.setParameter("device", deviceType);
		return (Subscription) query.uniqueResult();
	}

	
	

}

/**
 * 
 */
package com.cookedspecially.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.persister.collection.CollectionPropertyNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.UserDAO;
import com.cookedspecially.domain.Role;
import com.cookedspecially.domain.User;

import net.sf.ehcache.search.expression.Criteria;

/**
 * @author Abhishek
 *
 */
@Repository
public class UserDAOImpl implements UserDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	
	@Override
	public void saveUser(User user) {
		Role r=(Role) sessionFactory.getCurrentSession().get(Role.class, user.getRole().getId());
		user.setRole(r);
		sessionFactory.getCurrentSession().saveOrUpdate(user);
	}
	
	@Override
	public void removeUser(Integer userId) {
		User user= (User) sessionFactory.getCurrentSession().load(User.class, userId);
		if (null != user) {
			sessionFactory.getCurrentSession().delete(user);
		}
	}

	@Override
	public User getUser(Integer userId) {
		return (User) sessionFactory.getCurrentSession().get(User.class, userId);
	}
	
	@Override
   public User getUserByUsername(String username) {
       Query query = sessionFactory.getCurrentSession().createQuery("from User WHERE userName=:userName order by userId DESC");
       query.setParameter("userName", username);
       query.setMaxResults(1);
       return (User) query.uniqueResult();
       //return (User) sessionFactory.getCurrentSession().createCriteria(User.class).add(Restrictions.eq("userName", username)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).uniqueResult();
   }

	@Override
	public List<Role> getUserRole() {

		return sessionFactory.getCurrentSession().createQuery("from Role").list();
	}

	@Override
	public List<User> listUserByOrg(Integer orgId) {
		return sessionFactory.getCurrentSession().createQuery("FROM User WHERE :organizationId in elements(orgId) ORDER BY role.id").setParameter("organizationId", orgId).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public List<User> listUserByRestaurant(Integer restaurantId) {
		
		return sessionFactory.getCurrentSession().createQuery("FROM User WHERE :restaurantId in elements(restaurantId) ORDER BY role.id").setParameter("restaurantId", restaurantId).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	
	@Override
	public List<User> listAllUserByFulfilmentCenter(Collection<Integer> fId) {
		String q = "FROM User U where U.kitchenId IN (:fids) ORDER BY role.id";

		Query query = sessionFactory.getCurrentSession().createQuery(q);
		query.setParameter("fids", fId);
		return query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public List<User> listAllUserByMicroKitchen(Collection<Integer> mkId) {
		return sessionFactory.getCurrentSession().createCriteria(User.class).add(Restrictions.in("microKitchenId", mkId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}


	@Override
	public List<User> listUserByFulfillmentcenter(Integer fulfillmentcenterId) {
		return sessionFactory.getCurrentSession().createCriteria(User.class).add(Restrictions.eq("kitchenId", fulfillmentcenterId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}


	@Override
	public List<User> listDeliveryBoy(List<Integer> fulfillmentcenterId) {
		String qw = "FROM User U where U.kitchenId IN (:ffcids)";

		Query query = sessionFactory.getCurrentSession().createQuery(qw);
		query.setParameter("ffcids", fulfillmentcenterId);
		return query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public void updateUser(User user) {
		Role r=(Role) sessionFactory.getCurrentSession().get(Role.class, user.getRole().getId());
		user.setRole(r);
		sessionFactory.getCurrentSession().saveOrUpdate(user);
	}

	@Override
	public void updatePassword(User user) {
		Query query = sessionFactory.getCurrentSession().createQuery("UPDATE User u SET u.passwordHash = :password WHERE u.userId = :userId");
		query.setParameter("password", user.getPasswordHash());
		query.setParameter("userId", user.getUserId());
		query.executeUpdate();		
	}


	/*@Override
	public List<User> listUserByRole(Integer fulFillmentCenterId, int roleId) {


		return sessionFactory.getCurrentSession().createCriteria(User.class).add(Restrictions.eq("restaurantId", fulFillmentCenterId)).add(Restrictions.eq("roleId", roleId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}*/


}

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

import com.cookedspecially.dao.CouponDAO;

import com.cookedspecially.domain.Coupon;
import com.cookedspecially.enums.CouponState;



@Repository
public class CouponDAOImpl implements CouponDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addCoupon(Coupon coupon)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(coupon);
	}
	@Override
	public void updateCoupon(Coupon coupon)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(coupon);
		sessionFactory.getCurrentSession().flush();
	}
	
	@Override
	public List<Coupon> listCoupon()
	{
		return sessionFactory.getCurrentSession().createQuery("from Coupon").list();
	}
	
	@Override
	public Coupon getCouponById(Integer CouponId)
	{
		return (Coupon)sessionFactory.getCurrentSession().get(Coupon.class, CouponId);
	}
	
	//Now multiple coupons with same code are allowed in a restaurant
	@Override
	public List<Coupon> getAllCouponsByCode(String couponCode, Integer restaurantId)
	{
		Query queryS =sessionFactory.getCurrentSession().createQuery("from  Coupon where  couponCode=:couponCode AND restaurantId=:restaurantId");
		queryS.setParameter("couponCode",couponCode.toUpperCase());
		queryS.setParameter("restaurantId", restaurantId);

		return (List<Coupon>)queryS.list();
	}
	
	//only one coupon from multiple coupons with same code can be active/enabled.
	@Override
	public Coupon getEnabledCouponByCode(String couponCode, Integer restaurantId)
	{
		Query queryS =sessionFactory.getCurrentSession().createQuery("from  Coupon where  couponCode=:couponCode AND restaurantId=:restaurantId AND state=:state");
		queryS.setParameter("couponCode",couponCode.toUpperCase());
		queryS.setParameter("restaurantId", restaurantId);
		queryS.setParameter("state",CouponState.Enabled);//Enabled : 0

		return (Coupon)queryS.uniqueResult();
	}
	
	@Override
	public List<Coupon> getCouponListByCouponState(CouponState state, Integer restaurantId)
	{
		Query queryS =sessionFactory.getCurrentSession().createQuery("from  Coupon where restaurantId=:restaurantId AND state=:state");		
		queryS.setParameter("restaurantId", restaurantId);
		queryS.setParameter("state",state);

		return (List<Coupon>)queryS.list();
	}
	
	@Override
	public List<Coupon> listCouponByResturantId(Integer restaurantId)
	{
		List<Coupon> listc = (List<Coupon>)sessionFactory.getCurrentSession().createCriteria(Coupon.class).add(Restrictions.eq("restaurantID", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
		return listc;
	}
	
	@Override
	public void removeCoupon(Integer id)
	{
		Coupon coupon = (Coupon) sessionFactory.getCurrentSession().load(Coupon.class, id);
		if (null != coupon) {
			sessionFactory.getCurrentSession().delete(coupon);
		}
	}
	

	@Override
    public boolean isCustomerAssociate(Integer couponId, Integer customerId)
    {
        String sqlQuery = "select count(*) FROM CHECK_COUPON_LIST a INNER JOIN CHECKS b ON a.checkId = b.id WHERE couponId = :couponId AND customerId = :customerId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        
        query.setInteger("couponId", couponId);
        query.setInteger("customerId", customerId);
      // Query queryS =sessionFactory.getCurrentSession().createQuery(sqlQuery);
      //  queryS.setParameter("couponId",couponId);
     //   queryS.setParameter("custId", custId);

        Number nCount = (Number) query.uniqueResult();
        if(nCount.intValue() > 0)
            return true;
        
       return false;    
   }
	
	
}

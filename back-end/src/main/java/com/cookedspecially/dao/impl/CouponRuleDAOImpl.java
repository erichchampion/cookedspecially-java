/**
 * 
 */
package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.CouponRuleDAO;
import com.cookedspecially.domain.Coupon;
import com.cookedspecially.domain.CouponFlatRules;

@Repository
public class CouponRuleDAOImpl implements CouponRuleDAO {

	@Autowired
	private SessionFactory sessionFactory;	
	
	@Override
	public void addRule(CouponFlatRules rule)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(rule);
	}	
	@Override
	public void updateRule(CouponFlatRules rule)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(rule);
		sessionFactory.getCurrentSession().flush();
	}	
	@Override
	public CouponFlatRules getRuleById(Integer ruleId)
	{
		return (CouponFlatRules)sessionFactory.getCurrentSession().get(CouponFlatRules.class, ruleId);
	}
	
	@Override
	public CouponFlatRules getRuleByCouponId(Integer couponId)
	{
		return (CouponFlatRules) sessionFactory.getCurrentSession().createCriteria(CouponFlatRules.class).add(Restrictions.eq("couponId", couponId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list().get(0);
	}
	
	@Override
	public void removeRule(Integer ruleId)
	{
		CouponFlatRules rule = (CouponFlatRules) sessionFactory.getCurrentSession().load(CouponFlatRules.class, ruleId);
		if (null != rule) {
			sessionFactory.getCurrentSession().delete(rule);
		}
	}
	
	public void removeRuleByCouponID(Integer couponId)
	{
		CouponFlatRules rule = getRuleByCouponId(couponId);
		if (null != rule) {
			sessionFactory.getCurrentSession().delete(rule);
		}
	}
	
}

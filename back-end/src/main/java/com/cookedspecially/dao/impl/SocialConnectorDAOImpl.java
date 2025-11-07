package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.SocialConnectorDAO;
import com.cookedspecially.domain.SocialConnector;

@Repository
public class SocialConnectorDAOImpl  implements SocialConnectorDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addSocialConnector(SocialConnector connector) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(connector);
	}

	@Override
	public List<SocialConnector> listSocialConnectorByOrgId(Integer orgId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createCriteria(SocialConnector.class).add(Restrictions.eq("organizationId",orgId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public void removeSocialConnector(Integer id) throws Exception {
		// TODO Auto-generated method stub
		SocialConnector socialConnector = (SocialConnector) sessionFactory.getCurrentSession().load(SocialConnector.class, id);
		if (null != socialConnector) {
			sessionFactory.getCurrentSession().delete(socialConnector);
		}
	}

	@Override
	public SocialConnector getSocialConnector(Integer id) {
		// TODO Auto-generated method stub
		return (SocialConnector)sessionFactory.getCurrentSession().get(SocialConnector.class, id);
	}

}

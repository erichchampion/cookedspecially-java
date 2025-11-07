package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.SocialConnectorDAO;
import com.cookedspecially.domain.SocialConnector;
import com.cookedspecially.service.SocialConnectorService;

@Service
public class SocialConnectorServiceImpl implements SocialConnectorService {

	
	@Autowired
	private SocialConnectorDAO socialConnectorDAO;
	
	@Override
	@Transactional
	public void addSocialConnector(SocialConnector connector) {
		// TODO Auto-generated method stub
		socialConnectorDAO.addSocialConnector(connector);
	}

	@Override
	@Transactional
	public List<SocialConnector> listSocialConnectorByOrgId(Integer orgId) {
		// TODO Auto-generated method stub
		return socialConnectorDAO.listSocialConnectorByOrgId(orgId);
	}

	@Override
	@Transactional
	public void removeSocialConnector(Integer id) throws Exception {
		// TODO Auto-generated method stub
		socialConnectorDAO.removeSocialConnector(id);
	}


	@Override
	@Transactional
	public SocialConnector getSocialConnector(Integer id) {
		// TODO Auto-generated method stub
		return socialConnectorDAO.getSocialConnector(id);
	}

}

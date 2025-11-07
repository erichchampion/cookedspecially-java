package com.cookedspecially.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cookedspecially.domain.SocialConnector;


public interface SocialConnectorDAO {

	public void addSocialConnector(SocialConnector connector);
	public SocialConnector getSocialConnector(Integer id);
	public List<SocialConnector> listSocialConnectorByOrgId(Integer orgId);
	public void removeSocialConnector(Integer id) throws Exception;
}

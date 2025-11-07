package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.SocialConnector;

public interface SocialConnectorService {

	public void addSocialConnector(SocialConnector connector);
	public SocialConnector getSocialConnector(Integer id);
	public List<SocialConnector> listSocialConnectorByOrgId(Integer orgId);
	public void removeSocialConnector(Integer id) throws Exception;
}

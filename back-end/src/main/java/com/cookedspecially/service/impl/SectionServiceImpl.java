/**
 * 
 */
package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.SectionDAO;
import com.cookedspecially.domain.Section;
import com.cookedspecially.service.SectionService;

/**
 * @author shashank
 *
 */
@Service
public class SectionServiceImpl implements SectionService {
	
	@Autowired
	private SectionDAO sectionDAO;

	@Override
	@Transactional
	public void addSection(Section section) {
		sectionDAO.addSection(section);
	}

	@Override
	@Transactional
	public void removeSections(List<Integer> sectionIds) {
		sectionDAO.removeSections(sectionIds);
	}
	
}

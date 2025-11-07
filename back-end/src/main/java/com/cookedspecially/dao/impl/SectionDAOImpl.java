/**
 * 
 */
package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.SectionDAO;
import com.cookedspecially.domain.Section;

/**
 * @author shashank
 *
 */
@Repository
public class SectionDAOImpl implements SectionDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void addSection(Section section) {
		sessionFactory.getCurrentSession().saveOrUpdate(section);

	}

	@Override
	public void removeSections(List<Integer> sectionIds) {
		Integer[] sectionIdsArr = new Integer[sectionIds.size()];
		List<Section> sections = (List<Section>)sessionFactory.getCurrentSession().createCriteria(Section.class).add(Restrictions.in("sectionId", sectionIds.toArray(sectionIdsArr))).list();
		for (Section section : sections) {
			sessionFactory.getCurrentSession().delete(section);
		}
	}
}

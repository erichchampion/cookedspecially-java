/**
 * 
 */
package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.Section;

/**
 * @author shashank
 *
 */
public interface SectionDAO {

	public void addSection(Section section);
	public void removeSections(List<Integer> sectionIds);
}

/**
 * 
 */
package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.Section;

/**
 * @author shashank
 *
 */
public interface SectionService {

	public void addSection(Section section);
	public void removeSections(List<Integer> sectionIds);
}

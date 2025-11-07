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

import com.cookedspecially.dao.SeatingTableDAO;
import com.cookedspecially.domain.SeatingTable;

/**
 * @author shashank
 *
 */
@Repository
public class SeatingTableDAOImpl implements SeatingTableDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void addSeatingTable(SeatingTable seatingTable) {
		sessionFactory.getCurrentSession().saveOrUpdate(seatingTable);
	}


	@Override
	public List<SeatingTable> listSeatingTableByUser(Integer userId) {
		return sessionFactory.getCurrentSession().createCriteria(SeatingTable.class).add(Restrictions.eq("restaurantId", userId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public void removeSeatingTable(Integer id) throws Exception {
		SeatingTable seatingTable = (SeatingTable)sessionFactory.getCurrentSession().load(SeatingTable.class, id);
		if (seatingTable != null) {
			sessionFactory.getCurrentSession().delete(seatingTable);
		}

	}

	@Override
	public SeatingTable getSeatingTable(Integer id) {
		return (SeatingTable) sessionFactory.getCurrentSession().get(SeatingTable.class, id);
	}


	@Override
	public List<SeatingTable> listRestaurantSeatingTables(Integer restaurantId) {
		return sessionFactory.getCurrentSession().createCriteria(SeatingTable.class).add(Restrictions.eq("restaurantId", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

}

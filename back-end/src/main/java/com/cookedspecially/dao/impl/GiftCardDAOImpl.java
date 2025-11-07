package com.cookedspecially.dao.impl;

import com.cookedspecially.dao.GiftCardDAO;
import com.cookedspecially.domain.GiftCard;
import com.cookedspecially.domain.GiftCardSell;
import com.cookedspecially.dto.giftCrad.CustomerCreditAccountOpenINFODTO;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Abhishek on 5/27/2017.
 */

@Repository
public class GiftCardDAOImpl implements GiftCardDAO {

    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public void saveOrUpdateGiftCard(GiftCard giftCard) {
        sessionFactory.getCurrentSession().saveOrUpdate(giftCard);
    }

    @Override
    public void saveOrUpdateGiftCard(List<GiftCard> giftCard) {
        sessionFactory.getCurrentSession().saveOrUpdate(giftCard);
    }

    @Override
    public List<GiftCard> listGiftCard(Date fromDateGMT, Date toDateGMT, String status, int orgId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GiftCard.class);
        criteria = criteria.add(Restrictions.eq("orgId", orgId));
        criteria = criteria.add(Restrictions.ge("createdOn", fromDateGMT));
        criteria = criteria.add(Restrictions.le("createdOn", toDateGMT));
        if (status != null) {
            criteria = criteria.add(Restrictions.eq("status", status));
        }
        return criteria.list();
    }

    @Override
    public GiftCard getGiftCard(String giftCardNo) {
        return (GiftCard) sessionFactory.getCurrentSession().get(GiftCard.class, giftCardNo);
    }

    @Override
    public void saveOrUpdateGiftCardSell(GiftCardSell giftCardSell) {
        sessionFactory.getCurrentSession().saveOrUpdate(giftCardSell);
    }

    @Override
    public List<GiftCard> listGiftCardOfCustomer(int customerId, String filter) {
        String queryString = "select G from GiftCard G, GiftCardSell GS, Customer C where G.giftCardId = GS.giftCardId AND (GS.mobileNoOfRecipient = C.phone OR GS.purchaserMobileNo = C.phone) AND C.customerId=:customerId";
        if (filter == null || filter == "ALL") {
        } else if (filter == "PURCHASED")
            queryString = "select G from GiftCard G, GiftCardSell GS, Customer C where G.giftCardId = GS.giftCardId AND GS.purchaserMobileNo = C.phone AND C.customerId=:customerId";
        else if (filter == "RECEIVED")
            queryString = "select G from GiftCard G, GiftCardSell GS, Customer C where G.giftCardId = GS.giftCardId AND GS.mobileNoOfRecipient = C.phone AND C.customerId=:customerId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryString);
        query.setInteger("customerId", customerId);
        return query.list();
    }

    @Override
    public void deleteGiftCard(String giftCardId) {
        Query query = sessionFactory.getCurrentSession().createQuery("delete GiftCard where giftCardId = :ID");
        query.setParameter("ID", giftCardId);
        query.executeUpdate();
    }

    @Override
    public CustomerCreditAccountOpenINFODTO fetchDetailsRequiredToOpenDefaultCreditAccount(Integer customerId, Integer organisationId, String accountBillingCucleName) {
        Query listCreditTransaction = sessionFactory.getCurrentSession().createSQLQuery("Select DA.fulfillmentCenterId, CA.address, CT.id as creditAccountTypeId from CUSTOMERADDRESS CA,DELIVERYAREAS DA, CREDIT_TYPE CT where CA.customerId = :customerId  AND DA.name=CA.deliveryArea AND CT.billingCycle=:billingCycle AND CT.organisationId=:orgId order by CT.maxLimit DESC limit 1");
        listCreditTransaction.setInteger("customerId", customerId);
        listCreditTransaction.setInteger("orgId", organisationId);
        listCreditTransaction.setString("billingCycle", accountBillingCucleName);

        listCreditTransaction.setResultTransformer(Transformers.aliasToBean(CustomerCreditAccountOpenINFODTO.class));
        return (CustomerCreditAccountOpenINFODTO) listCreditTransaction.uniqueResult();
    }

    @Override
    public void clearTestData() {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("delete FROM GIFT_CARD where category = '____T___ABHI__Test Happy Birth Day'");
         query.executeUpdate();
    }

	@Override
	public void rollBack(GiftCard giftCard) {
		 Query query = sessionFactory.getCurrentSession().createSQLQuery("delete FROM GIFT_CARD_REDEMPTION where giftCardId = :giftId");
         query.setString("giftId", giftCard.getGiftCardId());
		 query.executeUpdate();
         Query query1 = sessionFactory.getCurrentSession().createSQLQuery("delete FROM GIFT_CARD_SELL where giftCardId = :giftId");
         query1.setString("giftId", giftCard.getGiftCardId());
         query1.executeUpdate();
         saveOrUpdateGiftCard(giftCard);
	}
}

package com.cookedspecially.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.ReportDAO;
import com.mysql.fabric.xmlrpc.base.Array;

@SuppressWarnings("unchecked")
@Repository
public class ReportDAOImpl implements ReportDAO{
	
	private String DELIVERY_REPORT_INVOICE_LIST = "select C.invoiceId,O.paymentStatus as paymentType, C.openTime as OrderPlace_On, C.closeTime as OrderDelivered_On, C.deliveryArea, C.deliveryAddress, O.bill from USER U join USER_PORTRAYAL UP on UP.userId=U.userId join USER_ROLES UR on UR.userId=U.userId join ROLES R on R.id=UR.roleId  join ORDERS O on O.deliveryAgent=U.userId join CHECKS C on O.checkId=C.id where R.role='deliveryBoy' AND UP.kitchenId=:ffcId AND C.openTime >= :from AND U.userId=:deliveryBoyId AND C.closeTime <= :to order by SUBSTRING(U.firstName, 1, 1) ASC";
	private String DELIVERY_REPORT = "select U.userId, U.userName, CONCAT(U.firstName, ' ', U.lastName) as name, U.contactNo as mobileNo, MAX(C.closeTime) as lastOrderDelivered_On, MIN(C.closeTime) as firstOrderDelivered_On, count(*) as noOfOrderDelivered from USER U join USER_PORTRAYAL UP on UP.userId=U.userId join USER_ROLES UR on UR.userId=U.userId join ROLES R on R.id=UR.roleId  join ORDERS O on O.deliveryAgent=U.userId join CHECKS C on O.checkId=C.id where R.role='deliveryBoy' AND UP.kitchenId=C.kitchenScreenId AND C.kitchenScreenId=:ffcId AND C.openTime >= :from AND C.closeTime <= :to group by U.userId order by SUBSTRING(U.firstName, 1, 1) ASC";
	
	private String DISH_LIST = "SELECT *, price*quantity as totalPrice from (SELECT ODS.dishId, ODS.name as dishName, ODS.dishType, ODS.price, sum(ODS.quantity) as quantity from ORDERDISHES ODS join ORDER_ORDERDISH OD on OD.orderDishId = ODS.orderDishId join CHECKS C on C.orderId = OD.orderId join ORDERS O on O.orderId=C.orderId %s %s %s %s) dishList";
	
	private String RESTRICT_DATE = "where  C.closeTime >= :from AND C.closeTime <= :to AND O.status =3 ";
	private String RESTRICT_FFC = " %s AND C.kitchenScreenId=:ffcId";
	private String RESTRICT_REST = "  %s AND C.restaurantId=:restaurantId";
	private String RESTRICT_ORG = " join RESTAURANT R on C.restaurantId = R.restaurantId %s AND R.parentRestaurantId=:orgId";
	
	private String GRUOP_BY_DISH_NAME="group by REPLACE(ODS.name, ' ', '')";
	
	private String ORDER_BY="order by quantity DESC, price DESC";
	

	
	
	
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public List<Object> listDeliveryBoyReport(Date from,
			Date to, int ffcId) {
		 Query query = sessionFactory.getCurrentSession().createSQLQuery(DELIVERY_REPORT);
		 query.setInteger("ffcId", ffcId);
		 query.setString("from", from.toString());
		 query.setString("to", to.toString());
		 //query.setResultTransformer(Transformers.aliasToBean(DeliveryBoyDTO.class));
		 //ject[] list = query.list().toArray();
		return query.list();}
	
	@Override
	public List<Object> listInvoceDelivered(Date from, Date to, int ffcId, int deliveryBoyId) {
		 Query query = sessionFactory.getCurrentSession().createSQLQuery(DELIVERY_REPORT_INVOICE_LIST);
		 query.setInteger("ffcId", ffcId);
		 query.setString("from", from.toString());
		 query.setString("to", to.toString());
		 query.setInteger("deliveryBoyId", deliveryBoyId);
		 //query.setResultTransformer(Transformers.aliasToBean(InvoiceListDTO.class));
		return query.list();
	}


	@Override
	public List topDishList(String level, Date from, Date to, int id, int count){
		Query query = null;
		switch (level) {
		case "ORG":
			 query = sessionFactory.getCurrentSession().createSQLQuery(String.format(DISH_LIST, String.format(RESTRICT_ORG, RESTRICT_DATE), GRUOP_BY_DISH_NAME, ORDER_BY, ""));
			 query.setInteger("orgId", id);
			break;
		case "REST":
			query = sessionFactory.getCurrentSession().createSQLQuery(String.format(DISH_LIST, String.format(RESTRICT_REST, RESTRICT_DATE),  GRUOP_BY_DISH_NAME, ORDER_BY, ""));
			query.setInteger("restaurantId", id);
			break;
		case "FFC":
			query = sessionFactory.getCurrentSession().createSQLQuery(String.format(DISH_LIST, String.format(RESTRICT_FFC, RESTRICT_DATE),  GRUOP_BY_DISH_NAME, ORDER_BY, ""));
			query.setInteger("ffcId", id);
			break;
		default:
			return new ArrayList<>();
		}
		if(query!=null){
		query.setString("from", from.toString());
		query.setString("to", to.toString());
		return query.list();
		}
		return new ArrayList<>();
	}
	
	/*@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public List topNDish(String level, Date from, Date to, int id, int count, String category){
		Query query = null;
		String top_dish_list;
		switch (level) {
		case "ORG":
			 query = getQurery(String.format(DISH_LIST, String.format(RESTRICT_ORG, RESTRICT_DATE), GRUOP_BY_DISH_NAME, ORDER_BY, "LIMIT "+count), category);
			 query.setInteger("orgId", id);
			break;
		case "REST":
			query = getQurery(String.format(DISH_LIST, String.format(RESTRICT_REST, RESTRICT_DATE), GRUOP_BY_DISH_NAME, ORDER_BY, "LIMIT "+count), category);
			query.setInteger("restaurantId", id);
			break;
		case "FFC":
			query = getQurery(String.format(DISH_LIST, String.format(RESTRICT_FFC, RESTRICT_DATE), GRUOP_BY_DISH_NAME, ORDER_BY, "LIMIT "+count), category);
			query.setInteger("ffcId", id);
			break;
		default:
			return new ArrayList<>();
		}
		if(query!=null){
		query.setString("from", from.toString());
		query.setString("to", to.toString());
		return query.list();
		}
		return new ArrayList<>();
	}*/
	
	
	/*private SQLQuery getQurery(String query, String category){
		switch (category) {
		case "GN_OS":
			query = String.format(TOP_DISH_LIST_SALE, query);
			break;
		case "GN_OQ":
			query = String.format(TOP_DISH_LIST_QNT, query);
			break;
		case "GC_OS":
			query = String.format(DISH_CAT_LIST_SALE, query);
			break;
		case "GC_OQ":
			query = String.format(DISH_CAT_LIST_QNT, query);
			break;
		}
		return sessionFactory.getCurrentSession().createSQLQuery(query);
	}*/

	@Override
	public List<Object> listDishCategory(String level,int id) {
		Query query = null;
		switch (level) {
		case "ORG":
			 query = sessionFactory.getCurrentSession().createSQLQuery("select D.dishType from DISHES D join RESTAURANT R on R.restaurantId=D.restaurantId where R.parentRestaurantId=:Id group by D.dishType");
			break;
		case "REST":
			 query = sessionFactory.getCurrentSession().createSQLQuery("select D.dishType from DISHES D where D.restaurantId=:Id group by D.dishType");
			break;
		case "FFC":
			 query = sessionFactory.getCurrentSession().createSQLQuery("select D.dishType from DISHES D join FULFILLMENTCENTER F on F.restaurantId=D.restaurantId where F.id=:Id group by D.dishType");
			break;
		default:
			return new ArrayList<>();
		}
		query.setInteger("Id", id);
		return query.list();
	}
	

}

/**
 * 
 */
package com.cookedspecially.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.CheckDAO;
import com.cookedspecially.dao.RestaurantDAO;
import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Coupon;
import com.cookedspecially.domain.EditChecks;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.service.CheckService;
import com.cookedspecially.service.CouponService;
import com.cookedspecially.utility.StringUtility;

/**
 * @author shashank
 *
 */
@Service
public class CheckServiceImpl implements CheckService {

	final static Logger logger = Logger.getLogger(CheckService.class);
	@Autowired
	private CheckDAO checkDAO;
	
	@Autowired
	private RestaurantDAO restaurantDAO;
	
	@Autowired
	private CouponService couponService;
	
	@Override
	@Transactional
	public void addCheck(Check check) {
		if (StringUtility.isNullOrEmpty(check.getInvoiceId())) {
			Restaurant rest=restaurantDAO.getRestaurant(check.getRestaurantId());
			rest.setInvoiceStartCounter(rest.getInvoiceStartCounter()+1);
			restaurantDAO.saveResaurant(rest);
			//User user = userDAO.getUser(check.getRestaurantId());
			//user.setInvoiceStartCounter(user.getInvoiceStartCounter() + 1);
			//userDAO.saveUser(user);
			//check.setInvoiceId(rest.getInvoicePrefix() +String.format("%08d", rest.getInvoiceStartCounter()));
			check.setInvoiceId(rest.getInvoicePrefix() +System.nanoTime());
		}
		
		checkDAO.addCheck(check);
	}

	@Override
	@Transactional
	public void removeCheck(Integer id) {
		checkDAO.removeCheck(id);
	}

	@Override
	@Transactional
	public Check getCheck(Integer id) {
		Check check    = checkDAO.getCheck(id);
		if(check.getCoupon_Applied()!=null){
			List<Coupon> couponList = new ArrayList<Coupon>();
			if(check.getCoupon_Applied().size()>0){
				for(Coupon coupon : check.getCoupon_Applied()){
					couponList.add(couponService.getCouponById(coupon.getCoupanId()));
				}
				check.setCoupon_Applied(couponList);
				}
		}
		return check;
	}
	@Override
	@Transactional
	public List<Check> getCheckByInvoiceId(String invoiceId) {
		List<Check> checkL   = checkDAO.getCheckByInvoiceId(invoiceId);
		List<Check> checkList = new ArrayList<Check>();
		
		for(Check check : checkL){
			if(check.getCoupon_Applied()!=null){
				List<Coupon> couponList = new ArrayList<Coupon>();
				if(check.getCoupon_Applied().size()>0){
					for(Coupon coupon : check.getCoupon_Applied()){
						couponList.add(couponService.getCouponById(coupon.getCoupanId()));
					}
					check.setCoupon_Applied(couponList);
					}
			}
			checkList.add(check);
		}
		
		return checkList;
	}
	
	@Override
	@Transactional
	public Check getCheckByTableId(Integer restaurantId, Integer tableId) {
		return checkDAO.getCheckByTableId(restaurantId, tableId);
	}

	@Override
	@Transactional
	public Check getCheckByCustId(Integer restaurantId, Integer custId) {
		return checkDAO.getCheckByCustId(restaurantId, custId);
	}

	@Override
	@Transactional
	public List<Check> getAllOpenChecks(Integer restaurantId) {
		return checkDAO.getAllOpenChecks(restaurantId);
	}
	
	@Override
	@Transactional
	public List<Integer> getAllCheckIds() {
		return checkDAO.getAllCheckIds();
	}
	
	@Override
	@Transactional
	public List getClosedChecksByDate(Integer restaurantId, Date startDate, Date endDate) {
		return checkDAO.getClosedChecksByDate(restaurantId, startDate, endDate);
	}
	
	@Override
	@Transactional
	public List<Check> getDailyInvoice(Integer restaurantId, Date startDate, Date endDate) {
		List<Check> checkList =  new ArrayList<Check>();
		for(Check check : checkDAO.getDailyInvoice(restaurantId, startDate, endDate)){
			if(check!=null){
				if(check.getCoupon_Applied()!=null){
					List<Coupon> couponList = new ArrayList<Coupon>();
					if(check.getCoupon_Applied().size()>0){
						for(Coupon coupon : check.getCoupon_Applied()){
							couponList.add(couponService.getCouponById(coupon.getCoupanId()));
						}
						check.setCoupon_Applied(couponList);
						}
				}
			}
			checkList.add(check);
		}
		return checkList;
	}
	
	@Override
	@Transactional
	public List<String> getUniqueDishTypes(Integer restaurantId) {
		return checkDAO.getUniqueDishTypes(restaurantId);
	}
	
	@Override
	@Transactional
	public List getDailySalesRecords(Integer restaurantId, Date startDate) {
		return checkDAO.getDailySalesRecords(restaurantId, startDate);
	}
	
	@Override
	@Transactional
	public List<Check> getAllChecks(List<Integer> ids) {
		return checkDAO.getAllChecks(ids);
	}
	
	@Override
	@Transactional
	public List getMonthlyBillSummary(Integer restaurantId, Date startDate, Date endDate) {
		return checkDAO.getMonthlyBillSummary(restaurantId, startDate, endDate);
	}

	@Override
	@Transactional
	public List<Check> getCustomersCheckList(String phone, Integer customerId, Integer restId) {
		// TODO Auto-generated method stub
		return checkDAO.getCustomersCheckList(phone, customerId, restId);
	}

	@Override
	@Transactional
	public List<Check> getCustomersCheckListByYear(String phone,
			Integer customerId, Integer restaurantId, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return checkDAO.getCustomersCheckListByYear(phone, customerId, restaurantId, startDate,endDate);
	}

	@Override
	@Transactional
	public List<Check> getAllCheckIdByRestaurantId(Integer restId) {
		// TODO Auto-generated method stub
		return checkDAO.getAllCheckIdByRestaurantId(restId);
	}

	@Override
	@Transactional
	public List<Check> getDailyInvoiceByFfc(Integer ffcId, Date startTime,
			Date endTime) {
		// TODO Auto-generated method stub
		return checkDAO.getDailyInvoiceByFfc(ffcId, startTime, endTime);
	}

	@Override
	@Transactional
	public void addEditCheck(EditChecks editCheck) {
			checkDAO.addEditCheck(editCheck);
	}

	@Override
	@Transactional
	public void removeEditCheck(Integer id) {
		checkDAO.removeEditCheck(id);
		
	}

	@Override
	@Transactional
	public EditChecks getEditCheck(Integer id) {
		// TODO Auto-generated method stub
		return checkDAO.getEditCheck(id);
	}

	@Override
	@Transactional
	public List<EditChecks> getEditCheckListBycheckId(Integer checkId) {
		// TODO Auto-generated method stub
		return checkDAO.getEditCheckListBycheckId(checkId);
	}
}

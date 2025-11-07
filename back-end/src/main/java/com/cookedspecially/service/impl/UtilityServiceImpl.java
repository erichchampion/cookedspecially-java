package com.cookedspecially.service.impl;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.UtilityDAO;
import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Customer;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.notification.ResultDTO;
import com.cookedspecially.service.UtilityService;
import com.cookedspecially.utility.DataValidator;
import com.cookedspecially.utility.MessageSender;
import com.cookedspecially.utility.StringUtility;

/*
 *  By Abhishek
 * 
 */
@Service
public class UtilityServiceImpl implements UtilityService{


	@Autowired
	private UtilityDAO utilityDAO;
	
	final static Logger logger = Logger.getLogger(UtilityServiceImpl.class);
	
	
	@Override
	@Transactional
	public ResultDTO listInvalidMobileNoCustomer(Integer orgId) {
		int count=0;
		int nulCount=0;
		int sucCount=0;
		ResultDTO resultDTO=new ResultDTO();
		List <String> unParsedMobile=new ArrayList<>();
		try{
     	for(Customer customer:utilityDAO.getAllCustomer(0,0, true)){
     		if(!StringUtility.isNullOrEmpty(customer.getPhone())){
     			try {
     				DataValidator.formateMobileNo(customer.getPhone());
					sucCount=sucCount+1;
				} catch (Exception e) {
					unParsedMobile.add(customer.getPhone()+"("+customer.getFirstName()+")");
					logger.info(e);
				}
     		}
     		else{
     			nulCount=nulCount+1;
     			unParsedMobile.add(customer.getPhone()+"("+customer.getFirstName()+")");
     			}
		}
     	resultDTO.message="Total "+count+" Customer mobile phone is invalid, "+ nulCount+" customer is having Null Mobile No and "+ sucCount+" proper customer";
		resultDTO.resultCode="SUCCESS";
		resultDTO.mobileNo=unParsedMobile;
		}catch(NullPointerException ex){
			logger.info("could not format customer mobile no "+ex.getMessage());
			resultDTO.message="could not format customer mobile no "+ex.getMessage();
			resultDTO.resultCode="ERROR";
		}
		logger.info(resultDTO.message);
		logger.info("Error Mobile no="+resultDTO.mobileNo.toString());
		return resultDTO;
	}
	
	@Override
	@Transactional
	public ResultDTO formateMobileNo(Integer orgId, int batchSize) {
		int count=0;
		int nulCount=0;
		ResultDTO resultDTO=new ResultDTO();
		List <String> unParsedMobile=new ArrayList<>();
		List<Object>customerlist=new ArrayList<>();
		try{
			int lastCustomerId=utilityDAO.getLastCustomerId();
			int loopCount=0;
			if(lastCustomerId%batchSize>0)
				loopCount=lastCustomerId/batchSize +1;
			else
				loopCount=lastCustomerId/batchSize;
			for(int i=0; i<loopCount;i++)
			{
				for(Customer customer:utilityDAO.getAllCustomer(i*batchSize+1, (i+1)*batchSize, false)){
		     			try {
		     				customer.setPhone(DataValidator.formateMobileNo(customer.getPhone()));
		     				customerlist.add(customer);
			                count=count+1;
						} catch (Exception e) {
							unParsedMobile.add(customer.getPhone()+"("+customer.getFirstName()+")");
							logger.info(e);
						}
				}
				if(customerlist.size()>=200 || ((i+1)==loopCount && customerlist.size()>0)){
				utilityDAO.updateData(customerlist);
				customerlist.clear();
				}
			}
     	resultDTO.message="Total "+count+" Customer mobile phone is updated to E164 formate.Count of customer without Mobile No(null) is "+nulCount;
		resultDTO.resultCode="SUCCESS";
		resultDTO.mobileNo=unParsedMobile;
		}catch(NullPointerException ex){
			logger.info("could not format customer mobile no "+ex.getMessage());
			resultDTO.message="could not format customer mobile no "+ex.getMessage();
			resultDTO.resultCode="ERROR";
		}
		logger.info(resultDTO.message);
		logger.info("Error Mobile no="+resultDTO.mobileNo.toString());
		return resultDTO;
	}
	@Override
	@Transactional
	public ResultDTO removeInvalidCustomerWithInvalidPhone(Integer orgId, int batchSize) {
		ResultDTO resultDTO=new ResultDTO();
		List<Integer>inValidCustomerList=new ArrayList<>();
		try{
			int lastCustomerId=utilityDAO.getLastCustomerId();
			int loopCount=0;
			if(lastCustomerId%batchSize>0)
				loopCount=lastCustomerId/batchSize +1;
			else
				loopCount=lastCustomerId/batchSize;
			for(int i=0; i<loopCount;i++)
			{
				for(Customer customer:utilityDAO.getAllCustomer(i*batchSize+1, (i+1)*batchSize, true)){
					try {
						DataValidator.formateMobileNo(customer.getPhone());	
					} catch (Exception e) {
						inValidCustomerList.add(customer.getCustomerId());
					}	
				}
			}
     	utilityDAO.removeCustomer(inValidCustomerList);
     	resultDTO.message="Total "+inValidCustomerList.size()+" Customer is with invalid phone no";
		resultDTO.resultCode="SUCCESS";
		}catch(NullPointerException ex){
			logger.info("could not remove invalid customer mobile no "+ex.getMessage());
			resultDTO.message="could not remove invalid customer mobile no "+ex.getMessage();
			resultDTO.resultCode="ERROR";
		}
		logger.info(resultDTO.message);
		return resultDTO;
	}
	
	/* This is a test method created to cross check if exotel is sending SMS properly */
	public ResponseDTO sendTestOTP(String otpDetails){
		ResponseDTO response=new ResponseDTO();
		try{
		 String[] data = otpDetails.split("-");
		response.message=MessageSender.sendTestMessage(data[0], data[1], data[2]);
		}catch (Exception ex){
			response.message="Exception, Invalid Data.Data must be as :- mobileNo-Message-Priority";	
		}
		return response;
	}

	@Override
	@Transactional
	public ResponseDTO formateMobileNoOfChecks(int batchSize){
		ResponseDTO resultDTO=new ResponseDTO();
		List<Object>validCheckList=new ArrayList<>();
		List<String>invalidCheckList=new ArrayList<>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date startDate=new Date(System.currentTimeMillis());
		logger.info("formateMobileNoOfChecks"+dateFormat.format(startDate)+" batch size="+batchSize);
		Check c=utilityDAO.getLastChecks();
		if(c!=null)
		{
			int loopCount=0;
			if(c.getCheckId()%batchSize>0)
				loopCount=c.getCheckId()/batchSize +1;
			else
				loopCount=c.getCheckId()/batchSize;
			for(int i=0; i<loopCount;i++)
			{
              for(Check check: utilityDAO.getAllChecks(i*batchSize+1, (i+1)*batchSize)){
					try {
						check.setPhone(DataValidator.formateMobileNo(check.getPhone()));
						validCheckList.add(check);
					} catch (Exception e) {
						invalidCheckList.add(check.getPhone()+"customerId("+check.getCustomerId()+")");
						logger.info(e);
					}
				}
				if(validCheckList.size()>=200 || ((i+1)==loopCount && validCheckList.size()>0)){
				   utilityDAO.updateData(validCheckList);	
				   validCheckList.clear();
				}
			}
			
		}
		long timeDiff=(new Date(System.currentTimeMillis())).getTime()-startDate.getTime();
		logger.info("time taken to fetch data "+timeDiff/1000);
		resultDTO.message=" Total Time taken="+timeDiff/1000+"Invalid Mobile No ckeckList="+invalidCheckList.toString()+" Count is="+invalidCheckList.size()+". Valid Check Count="+validCheckList.size();
		resultDTO.result="SUCCESS";
		logger.info(resultDTO.message);
		System.gc();
		return resultDTO;
	}
	
	@Override
	@Transactional
	public ResponseDTO removeDuplicateCustomer(Integer orgId){
		logger.info("In removeDuplicateCustomer");
		ResponseDTO resultDTO=new ResponseDTO();
		List<Integer> dulicateCustomerProfile=new ArrayList<>();
		List<Object> updatedCustomerProfile=new ArrayList<>();
		HashMap<Integer, Set<Integer>> updateCustomerDetails=new HashMap<>();
		List<String>  duplicateMobileNoLis= utilityDAO.listDuplicateCustomer(orgId);
		logger.info("Total Customer with duplicate entry ="+duplicateMobileNoLis.size());
		for(String mobileNo: duplicateMobileNoLis){
			List<Customer>customerProfileList=utilityDAO.listAllDuplicateCustomer(mobileNo);
			Customer baseCustomer=customerProfileList.remove(0);
			baseCustomer.setIsActive(1);
			baseCustomer.setIsAuthentic(1);
			Set<Integer> listOfDupliacteCustomerId=new TreeSet<>();
		     for(int i=0;i<customerProfileList.size();i++){
		    	dulicateCustomerProfile.add(customerProfileList.get(i).getCustomerId());
		    	listOfDupliacteCustomerId.add(customerProfileList.get(i).getCustomerId());
		    	updateCustomerDetails(baseCustomer, customerProfileList.get(i));
		     }
		     updateCustomerDetails.put(baseCustomer.getCustomerId(), listOfDupliacteCustomerId);
		     updatedCustomerProfile.add(baseCustomer);
		     logger.info("Customer Duplicate Id("+baseCustomer.getCustomerId()+" List="+listOfDupliacteCustomerId);
		}
		utilityDAO.updateCustomerDetails(updateCustomerDetails);
		utilityDAO.updateData(updatedCustomerProfile);
		utilityDAO.removeCustomer(dulicateCustomerProfile);
		resultDTO.message="Successfully updated duplicate profile removed size="+dulicateCustomerProfile.size()+" updated customer List="+updateCustomerDetails.size(); 
		resultDTO.result="SUCCESS";
		logger.info(resultDTO.message);
		return resultDTO;
	}

	private void updateCustomerDetails(Customer baseCustomer, Customer customer) {
		baseCustomer.setRewardPoints(baseCustomer.getRewardPoints()+customer.getRewardPoints());
	    baseCustomer.setPreferedNotification(customer.getPreferedNotification());
		if(baseCustomer.getFirstName()==null)
	    	baseCustomer.setFirstName(customer.getFirstName());
	    if(baseCustomer.getLastName()==null)
	    	baseCustomer.setLastName(customer.getLastName());
	    if(baseCustomer.getEmail()==null)
	    	baseCustomer.setEmail(customer.getEmail());
	    if(baseCustomer.getFacebookId()==null)
	    	baseCustomer.setFacebookId(customer.getFacebookId());
	    if(baseCustomer.getCity()==null)
	    	baseCustomer.setCity(customer.getCity());
	}
	
	
	
}

package com.cookedspecially.utility;

import com.cookedspecially.domain.Till;
import com.cookedspecially.domain.User;
import com.cookedspecially.dto.saleRegister.PaymentCategoryDTO;
import com.cookedspecially.dto.saleRegister.TillBalanceSummeryDTO;
import org.apache.log4j.Logger;

import java.util.TreeMap;

public class TillTransactionMailler {
	final static Logger logger = Logger.getLogger(TillTransactionMailler.class);

	public void sendTransactionUpdateMail(User user, String remarks, Integer checkId, float amount, String oldPaymentType, String newPaymentType, String orderStatus,String toAddress){
		StringBuilder strBuilder = initializeMailBody("Admin");
        String sub = "Payment mode is changed.";
        strBuilder.append("<p> payment mode " + oldPaymentType + " to " + newPaymentType + "<p>");
        strBuilder.append("<p> Status : " + orderStatus + "<p>");
        strBuilder.append("<p> Remarks:-" + remarks + "</p>");
		strBuilder.append("<p></p>");
		sendMail(toAddress, sub, strBuilder.toString());
        logger.info("Transaction updated mail sent successfully");
	}

	public void sendHandoverMail(Till till, User user, User handedOverToUser, TillBalanceSummeryDTO saleSummary, String remarks, String toAddress) {
		StringBuilder strBuilder = initializeMailBody("Admin");
		String sub=null;
		float PendingSalesTotal = 0.0f;
		float CompleteSalesTotal = 0.0f;
		float CompleteCreditTotal = 0.0f;
		float PendingCreditTotal = 0.0f;
		strBuilder.append("<p> The " + till.getTillName() + " for " + till.getFulfillmentcenter().getName()
				+ " is handed over to " + handedOverToUser.getUserName() + "(" + handedOverToUser.getRole().getRole()
				+ ") from(By) user " + user.getUserName() + "(" + user.getRole().getRole()
				+ ").The Sale Summary are :-</p>");
		strBuilder.append("<table style='border-top:1px solid black;border-bottom:1px solid black'>");
		strBuilder.append("<tr><td >" + "OPENING BALANCE" + "</td><td>:</td><td>"
				+ saleSummary.Initial_Cash_Balance + "</td></tr>");
		strBuilder.append("<tr><td>" + "TRANSACTION CASH" + "</td><td>:</td><td>" + saleSummary.TRANSACTION_CASH
				+ "</td></tr>");
		strBuilder.append("<tr><td>" + "CURRENT BALANCE" + "</td><td>:</td><td>"
				+ saleSummary.Current_Cash_Balance + "</td></tr>");
		strBuilder.append("</table>");
        strBuilder.append("<p>Sale Summary :-</p>");
        strBuilder.append("<table style='border-top:1px solid black;border-bottom:1px solid black>");
        strBuilder.append(
				"<tr><th style='text-decoration:underline;font-style:italic'>Payment Type</th><th style='text-decoration:underline;font-style:italic'>Completed Sales</th><th style='text-decoration:underline;font-style:italic'>Pending Sales</th><tr>");
		for (PaymentCategoryDTO saleCategory : saleSummary.transactionSummary.saleSummary) {
			CompleteSalesTotal += saleCategory.completedAmount;
			PendingSalesTotal += saleCategory.pendingAmount;
			strBuilder.append("<tr><td>\t" + saleCategory.paymentTypeName + "\t:\t</td><td>\t" + saleCategory.completedAmount + "\t</td><td>\t" + saleCategory.pendingAmount + "\t</td></tr>");
		}
		strBuilder.append("<tr><td style='font-weight:bold'>\tTotal\t:\t</td><td>\t" + CompleteSalesTotal + "\t</td><td>\t" + PendingSalesTotal + "</td></tr>");
		strBuilder.append("</table>");
		strBuilder.append("<p>Credit Summary</p>");
        strBuilder.append("<table style='border-top:1px solid black;border-bottom:1px solid black>");
        strBuilder.append(
				"<tr><th style='text-decoration:underline;font-style:italic'>Payment Type</th><th style='text-decoration:underline;font-style:italic'>Completed Credit</th><th style='text-decoration:underline;font-style:italic'>Pending Credit</th><tr>");
		for (PaymentCategoryDTO creditSummary : saleSummary.transactionSummary.creditSummary) {
			CompleteCreditTotal += creditSummary.completedAmount;
			PendingCreditTotal += creditSummary.pendingAmount;
			strBuilder.append("<tr><td>\t" + creditSummary.paymentTypeName + "\t:\t</td><td>\t" + creditSummary.completedAmount + "\t</td><td>\t" + creditSummary.pendingAmount + "\t</td></tr>");
		}
		strBuilder.append("<tr><td style='font-weight:bold'>\tTotal\t:\t</td><td>\t" + CompleteCreditTotal + "\t</td><td>\t" + PendingCreditTotal + "</td></tr>");
		strBuilder.append("</table>");
		strBuilder.append("<p></p>");
		strBuilder.append("<p>Remark : " + remarks + "</p>");
		strBuilder.append("<p></p>");
		strBuilder.append("</body></html>");
		sub="Sale Register "+till.getTillName()+" is Handed over By "+user.getFirstName()+"("+user.getRole().getRole()+") to "+handedOverToUser.getFirstName()+"("+handedOverToUser.getRole().getRole()+").";
		sendMail(toAddress, sub, strBuilder.toString());
        logger.info("Till Handover mail sent successfully");
	}
	
	public void sendOpenCloseMail(Till till, User user, boolean open,TreeMap<String, Object> map,TillBalanceSummeryDTO saleSummery, String remarks,String toAddress){
		StringBuilder strBuilder = initializeMailBody("Admin");
		String sub=null;
		if(open){
			strBuilder.append("<p> The " + till.getTillName() + " for " + till.getFulfillmentcenter().getName()
					+ " is opened by " + user.getUserName() + "(" + user.getRole().getRole()
					+ ").The Sale Register Details are :-</p>");
			strBuilder.append("<table style='border-top:1px solid black;border-bottom:1px solid black'>");
			for (String key : map.keySet()) {
				strBuilder.append("<tr><td style='font-weight:bold'>" + key + "</td><td>:</td><td>" + map.get(key)
						+ "</td></tr>");
				
			}
			strBuilder.append("</table>");
			sub=till.getTillName() + " is opened.";
		}
		else{
			float PendingSalesTotal = 0.0f;
			float CompleteSalesTotal = 0.0f;
			float CompleteCreditTotal = 0.0f;
			float PendingCreditTotal = 0.0f;
			sub=till.getTillName() + " is closed.";
			strBuilder.append("<p> The " + till.getTillName() + " for " + till.getFulfillmentcenter().getName()
					+ " is closed by " + user.getUserName() + "(" + user.getRole().getRole()
					+ ").The Sale Register Details are :-</p>");
			strBuilder.append("<table style='border-top:1px solid black;border-bottom:1px solid black'>");
			strBuilder.append("<tr><td >" + "OPENING BALANCE" + "</td><td>:</td><td>"
					+ saleSummery.Initial_Cash_Balance + "</td></tr>");
			strBuilder.append("<tr><td>" + "TRANSACTION CASH" + "</td><td>:</td><td>" + saleSummery.TRANSACTION_CASH
					+ "</td></tr>");
			strBuilder.append("<tr><td>" + "CURRENT BALANCE" + "</td><td>:</td><td>"
					+ saleSummery.Current_Cash_Balance + "</td></tr>");
			strBuilder.append(
					"<tr><th style='text-decoration:underline;font-style:italic'>Completed Sales</th><th style='text-decoration:underline;font-style:italic'>Pending Sales</th><tr>");
			strBuilder.append("</table>");
            strBuilder.append("<p>Sale Summary :-</p>");
            strBuilder.append("<table style='border-top:1px solid black;border-bottom:1px solid black>");
            strBuilder.append(
					"<tr><th style='text-decoration:underline;font-style:italic'>Payment Type</th><th style='text-decoration:underline;font-style:italic'>Completed Sales</th><th style='text-decoration:underline;font-style:italic'>Pending Sales</th><tr>");
			for (PaymentCategoryDTO saleCategory : saleSummery.transactionSummary.saleSummary) {
				CompleteSalesTotal += saleCategory.completedAmount;
				PendingSalesTotal += saleCategory.pendingAmount;
				strBuilder.append("<tr><td>\t" + saleCategory.paymentTypeName + "\t:\t</td><td>\t" + saleCategory.completedAmount + "\t</td><td>\t" + saleCategory.pendingAmount + "\t</td></tr>");
			}
			strBuilder.append("<tr><td style='font-weight:bold'>\tTotal\t:\t</td><td>\t" + CompleteSalesTotal + "\t</td><td>\t" + PendingSalesTotal + "</td></tr>");
			strBuilder.append("</table>");
            strBuilder.append("<p>Credit Summary :-</p>");
            strBuilder.append("<table style='border-top:1px solid black;border-bottom:1px solid black>");
            strBuilder.append(
					"<tr><th style='text-decoration:underline;font-style:italic'>Payment Type</th><th style='text-decoration:underline;font-style:italic'>Completed Credit</th><th style='text-decoration:underline;font-style:italic'>Pending Credit</th><tr>");
			for (PaymentCategoryDTO creditSummary : saleSummery.transactionSummary.creditSummary) {
				CompleteCreditTotal += creditSummary.completedAmount;
				PendingCreditTotal += creditSummary.pendingAmount;
				strBuilder.append("<tr><td>\t" + creditSummary.paymentTypeName + "\t:\t</td><td>\t" + creditSummary.completedAmount + "\t</td><td>\t" + creditSummary.pendingAmount + "\t</td></tr>");
			}
			strBuilder.append("<tr><td style='font-weight:bold'>\tTotal\t:\t</td><td>\t" + CompleteCreditTotal + "\t</td><td>\t" + PendingCreditTotal + "</td></tr>");
			strBuilder.append("</table>");
			strBuilder.append("<p></p>");
			strBuilder.append("<p>Remark : " + remarks + "</p>");
			strBuilder.append("<p></p>");
		}
		strBuilder.append("</body></html>");
		sendMail(toAddress, sub, strBuilder.toString());
        logger.info("Till open/close mail sent successfully");
	}

	public void sendTillActivationMail(Till till,TreeMap<String, Object> map, User user, boolean activation, String toAddress) {
		StringBuilder strBuilder = initializeMailBody("Admin");
		String sub=null;
		if(activation){
		strBuilder.append("<p> The " + till.getTillName() + " for " + till.getFulfillmentcenter().getName()
				+ " is created by " + user.getUserName() + "(" + user.getRole().getRole() + ").</p>");
		sub=till.getTillName() + " is created.";
		strBuilder.append("</body></html>");
		}else{
			sub=till.getTillName() + " is deleted.";
			strBuilder.append("<p> The " + till.getTillName() + " for " + till.getFulfillmentcenter().getName()
					+ " is deleted by " + user.getUserName() + "(" + user.getRole().getRole()
					+ ").The Sale Register Details are :-</p>");
			strBuilder.append("<table style='border-top:1px solid black;border-bottom:1px solid black'>");
			for (String key : map.keySet()) {
				strBuilder.append("<tr><td style='font-weight:bold'>" + key + "</td><td>:</td><td>" + map.get(key)
						+ "</td></tr>");
			}
			strBuilder.append("</table>");
		}
		sendMail(toAddress, sub, strBuilder.toString());
        logger.info("Till activation/deactivation mail sent successfully");
	}
	public void sendAddWithDrawCashMail(Till till, float amount,TreeMap<String, Object> map, User user, String remarks, boolean addCash,String toAddress){
		String sub=null;
		StringBuilder strBuilder = initializeMailBody("Admin");
		if(addCash){
		strBuilder.append("<p> The " + till.getTillName() + " for " + till.getFulfillmentcenter().getName()
				+ " is added with cash amount =" + amount + " by " + user.getUserName() + "("
				+ user.getRole().getRole() + ").The Sale Register Details are :-</p>");
		sub=amount+" is added to Till";
		}
		else{
			strBuilder.append("<p> The " + till.getTillName() + " for " + till.getFulfillmentcenter().getName()
					+ " is withdrawl with cash amount =" + amount + " by " + user.getUserName() + "("
					+ user.getRole().getRole() + ").The Sale Register Details are :-</p>");
			sub=amount+" is withdrawn from Till";
		}
		strBuilder.append("<table style='border-top:1px solid black;border-bottom:1px solid black'>");
		for (String key : map.keySet()) {
			strBuilder.append("<tr><td style='font-weight:bold'>" + key + "</td><td>:</td><td>" + map.get(key)
					+ "</td></tr>");
		}
		strBuilder.append("</table>");
		strBuilder.append("<p></p>");
		strBuilder.append("<p>Remark : " + remarks + "</p>");
		strBuilder.append("<p></p>");
		strBuilder.append("</body></html>");
		sendMail(toAddress, sub, strBuilder.toString());
        logger.info("Add/Withdraw cash to till mail sent successfully");
	}
	private StringBuilder initializeMailBody(String name){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<html><body>");
		strBuilder.append("<p>Hi "+name+",</p>");
        strBuilder.append("<p></p>");
        return strBuilder;
	}
	@SuppressWarnings("static-access")
	private void sendMail(String toAddress,String subject, String body){
		MailerUtility mu = new MailerUtility();
        MailerUtility.sendHTMLMail(toAddress, subject, body, mu.username, mu.password);
    }
	public void sendTransactionAmountUpdateMail(User user, String remark, Integer checkId, float balance,
			String transactionType, Float transactionAmount, String orderStatus, String alertMailId) {
		StringBuilder strBuilder = initializeMailBody("Admin");
		String sub="Order amount is changed.";
		strBuilder.append("Order anount for chekId="+checkId+" of initial amount="+transactionAmount+" is updated to "+balance+ ", payment type= "+transactionType);
		strBuilder.append("<p></p>");
		strBuilder.append("<p> Remarks:- " + remark + "</p>");
		strBuilder.append("<p> ORDER STATUS:- "+orderStatus+"</p>");
		strBuilder.append("<p></p>");
		sendMail(alertMailId, sub, strBuilder.toString());
        logger.info("Transaction updated mail sent successfully");
	}
	
}

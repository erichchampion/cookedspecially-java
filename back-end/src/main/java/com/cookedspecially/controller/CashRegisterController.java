package com.cookedspecially.controller;

import com.cookedspecially.domain.Transaction;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.saleRegister.*;
import com.cookedspecially.service.CashRegisterService;
import com.cookedspecially.service.UserService;
import com.cookedspecially.utility.StringUtility;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.naming.directory.InvalidAttributesException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/salesregister")
@Api(value = "CashRegisterController",description="Sales Register REST API's. Require user login")
public class CashRegisterController {
    final static Logger logger = Logger.getLogger(CashRegisterController.class);


    @Autowired
    private CashRegisterService cashRegisterService;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/add", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    Map<String, Object> createNewTill(@RequestBody TillDTO tillDto, HttpServletRequest request) {
        return cashRegisterService.createNewTill(tillDto, (Integer) request.getSession().getAttribute("userId"));
    }

    @RequestMapping(value = "/addCash", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    Map<String, Object> addCashIntoTill(@RequestBody Map<String, String> map, HttpServletRequest request) {
        return cashRegisterService.addCashIntoTill(true, map.get("tillId"), Float.parseFloat(map.get("amount")), map.get("remarks"), (Integer) request.getSession().getAttribute("userId"));
    }

    @RequestMapping(value = "/withdrawCash", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    Map<String, Object> withdrawCash(@RequestBody Map<String, String> map, HttpServletRequest request) {
        return cashRegisterService.addCashIntoTill(false, map.get("tillId"), Float.parseFloat(map.get("amount")), map.get("remarks"), (Integer) request.getSession().getAttribute("userId"));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> listTill(HttpServletRequest request) {
        return cashRegisterService.getTillList((Integer) request.getSession().getAttribute("userId"));
    }

    @RequestMapping(value = "/edit", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")

    public
    @ResponseBody
    Map<String, String> editTill(@RequestBody Map<String, String> map, HttpServletRequest request) {
        return cashRegisterService.editTill(map.get("tillId"), map.get("tillName"), (Integer) request.getSession().getAttribute("userId"));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    Map<String, String> deleteTill(@RequestBody Map<String, String> map, HttpServletRequest request) {
        return cashRegisterService.deleteTill(map.get("tillId"), map.get("remark"), (Integer) request.getSession().getAttribute("userId"));
    }

    @RequestMapping(value = "/open", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    TransactionDTO openTill(@RequestBody Map<String, String> map, HttpServletRequest request) throws Exception {
        return cashRegisterService.openCloseTill(true, map.get("remark"), map.get("tillId"), (Integer) request.getSession().getAttribute("userId"));
    }

    @RequestMapping(value = "/close", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    TransactionDTO closeTill(@RequestBody Map<String, String> map, HttpServletRequest request) throws Exception {
        return cashRegisterService.openCloseTill(false, map.get("remark"), map.get("tillId"), (Integer) request.getSession().getAttribute("userId"));
    }

    @RequestMapping(value = "/checkCashBalance", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    Map<String, String> checkCashBalance(@RequestBody Map<String, String> map, HttpServletRequest request) {
        return cashRegisterService.checkCashBalance(map.get("tillId"), (Integer) request.getSession().getAttribute("userId"));
    }

    @RequestMapping(value = "/updateCash", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    ResponseDTO updateCash(@RequestBody TillCashUpdateDTO updateDTO, HttpServletRequest request) {
        try {
            return cashRegisterService.updateCash(updateDTO, (Integer) request.getSession().getAttribute("userId"));
        } catch (Exception e) {
            ResponseDTO response = new ResponseDTO();
            response.message = e.getMessage();
            response.result = "Error";
            return response;
        }

    }

    @RequestMapping(value = "/listTransactions", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    TransactionDTO listTransactions(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        return cashRegisterService.listTransactions((String) map.get("transactionStatus"), (String) map.get("tillId"), (Long) map.get("fromTime"), (Long) map.get("toTime"), (Integer) request.getSession().getAttribute("userId"));
    }

    @RequestMapping(value = "/cancelOrderTransaction", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    ResponseDTO cancelOrderTransaction(@RequestBody CancelOrderDTO cancelOrderDTO, HttpServletRequest request) {
        try {
            return cashRegisterService.cancelOrderTransaction(cancelOrderDTO.checkId, cancelOrderDTO.paymentType, cancelOrderDTO.status, cancelOrderDTO.createCreditTransaction, (Integer) request.getSession().getAttribute("userId"));
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.message = e.getMessage();
            responseDTO.result = "Error";
            return responseDTO;
        }
    }

    @RequestMapping(value = "/fetchTransactionsByCheck", method = RequestMethod.GET)
    @ResponseBody
    public TransactionDTO fetchTransactionsByCheck(HttpServletRequest request) {
        return cashRegisterService.fetchTransactionsByCheck((Integer) request.getSession().getAttribute("userId"), Integer.parseInt(request.getParameter("checkId")));
    }

    @RequestMapping(value = "/refundOrder", method = RequestMethod.GET)
    @ResponseBody
    public ResponseDTO refundOrder(HttpServletRequest request) {
        Integer checkId = null;
        try {
            checkId = Integer.parseInt(request.getParameter("checkId"));
        } catch (Exception e) {
            ResponseDTO res = new ResponseDTO();
            res.message = "Missing CheckID";
            res.result = "Error";
            return res;
        }
        return cashRegisterService.refundOrder((Integer) request.getSession().getAttribute("userId"), checkId);
    }

    @RequestMapping(value = "/applySpecialCaseDiscount", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    ResponseDTO applySpecialCaseDiscount(@RequestBody ApplyDiscount applyDiscount, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null || userId <= 0) {
            ResponseDTO response = new ResponseDTO();
            response.message = "Invalid session!";
            response.result = "Error";
            return response;
        }
        return cashRegisterService.applyDiscount(userId, applyDiscount.discountedAmount, applyDiscount.checkId, applyDiscount.remarks,"");
    }

    @RequestMapping(value = "/getAllTransaction", method = RequestMethod.GET)
    @ResponseBody
    public List<Transaction> getAllTransaction(HttpServletRequest request) {
        try {
            Integer userId = (Integer) request.getSession().getAttribute("userId");
            return cashRegisterService.getAllTransactionList(userId, request.getParameter("tillId"), request.getParameter("startDate"), request.getParameter("endDate"));
        } catch (ParseException e) {
            return null;
        }
    }

    @RequestMapping(value = "/handoverPendingSales", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public String handoverPendingSales(Map<String, Object> map, @RequestBody SaleHandoverDTO handOverDTO, HttpServletRequest request, HttpServletResponse httpResponse, BindingResult result) throws Exception {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        logger.info("processing Handover Request from userId=" + userId + " to user userName=" + handOverDTO.userName);
        String response = null;
        if(request.getSession().getAttribute("handoverIsInProgress") != null && (boolean) request.getSession().getAttribute("handoverIsInProgress")){
        	return "Error: Handover Process is in progess, Please wait!";
        }
        request.getSession().setAttribute("handoverIsInProgress", true);
        try {
            if (userService.isValidUser(userService.getUserByUsername(handOverDTO.userName), handOverDTO.password)) {
                cashRegisterService.handoverPendingSales(userId, handOverDTO);
                request.getSession().invalidate();
                request.getSession(true);
                response = userService.saleRegisterHandover(handOverDTO.userName, handOverDTO.password);
                synchronized (this) {
                    userController.login(request, httpResponse, map);
                }
            } else
                throw new InvalidAttributesException("Invalid UserName and Password!");
            logger.info("Handover Process is success");
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidAttributesException("Invalid UserName and Password! or " + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listConflictedSaleTransaction", method = RequestMethod.GET)
    public
    @ResponseBody
    ConflictedSaleDTO listConflictedSaleTransaction(HttpServletRequest request) throws Exception {
        if (StringUtility.isNullOrEmpty(request.getSession().getAttribute("organisationId").toString()))
            throw new Exception("Invalid Session. Try after re-login!");
        Integer orgId = Integer.parseInt(request.getSession().getAttribute("organisationId").toString());
        return cashRegisterService.listConflictedSaleTransaction(orgId.intValue());
    }

    @RequestMapping(value = "/forceUpdateTransaction/{transactionId}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public
    @ResponseBody
    ResponseDTO forceUpdateTransaction(@PathVariable("transactionId") String transactionId, HttpServletRequest request) {
        return cashRegisterService.forceUpdateTransaction(transactionId);
    }

    @RequestMapping(value = "/balanceSummary/{tillId}", method = RequestMethod.GET)
    public
    @ResponseBody
    TillBalanceSummeryDTO getBalanceSummery(@PathVariable("tillId") String tillId, HttpServletRequest request) {
        try {
            return cashRegisterService.getBalanceSummary(tillId, (Integer) request.getSession().getAttribute("userId"));
        } catch (Exception e) {
            logger.info(e.toString());
        }
        return null;
    }

}

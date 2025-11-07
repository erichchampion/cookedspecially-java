package com.cookedspecially.controller;

import java.util.List;

import io.swagger.annotations.Api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.cookedspecially.service.Report;

/**
 * Created by Abhishek on 3/1/2017.
 */
@Controller
@RequestMapping("/analysisAndReport")
@Api(description="Analysis and Report REST API's")
public class AnalysisAndReportController {

    final static Logger logger = Logger.getLogger(AnalysisAndReportController.class);


    @Autowired
    private Report report;


    @RequestMapping(value = "/orderReport", method = RequestMethod.GET)
    public String deliveryBoyReport(HttpServletRequest request, HttpServletResponse response) {
    	return "redirect:/report.jsp";
    }
    
    @RequestMapping(value = "/deliveryBoy/{ffcId}", method = RequestMethod.GET)
    @ResponseBody
    public List<Object> deliveryBoyReport(@PathVariable("ffcId") int ffcId, @SessionAttribute("userId") int userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String inputDateTimeZone = request.getParameter("inputDateTimeZone");
    	return report.getDeliveryBoyReport(ffcId, fromDate, toDate, userId, inputDateTimeZone);
    }
 
    @RequestMapping(value = "/listInvoiceDeliveredByDeliveryBoy/{ffcId}/{boyId}", method = RequestMethod.GET)
    @ResponseBody
    public List<Object> listInvoiceDeliveredByDeliveryBoy(@PathVariable("ffcId") int ffcId, @PathVariable("boyId") int deliveryBoyId, @SessionAttribute("userId") int userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String inputDateTimeZone = request.getParameter("inputDateTimeZone");
    	return report.listInvoiceDeliveredByDeliveryBoy(ffcId, deliveryBoyId, fromDate, toDate, userId, inputDateTimeZone);
    }
    
    @RequestMapping(value = "/topDishes/{id}/{count}/{level}", method = RequestMethod.GET)
    @ResponseBody
    public List<Object> topDishes(@PathVariable("id") int id,@PathVariable("count") int count,@PathVariable("level") String level, @SessionAttribute("userId") int userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String inputDateTimeZone = request.getParameter("inputDateTimeZone");
    	return report.topDishes(id,count, fromDate, toDate, userId, inputDateTimeZone, level);
    }
    
    @RequestMapping(value = "/listDishCategory/{id}/{level}", method = RequestMethod.GET)
    @ResponseBody
    public  List<Object> listDishCategory(@PathVariable("id") int id,@PathVariable("level") String level, @SessionAttribute("userId") int userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String inputDateTimeZone = request.getParameter("inputDateTimeZone");
    	return report.listDishCategory(id, userId, level);
    }
    
    
    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest req, Exception ex) {
        logger.error("Request: " + req.getRequestURL() + " raised " + ex);
        ex.printStackTrace();
        ModelAndView mav = new ModelAndView("reportingError");
        mav.addObject("errMsg", ex.getLocalizedMessage());
        mav.addObject("url", req.getRequestURL());
        return mav;
    }
}

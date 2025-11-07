package com.cookedspecially.controller;

import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.giftCrad.*;
import com.cookedspecially.enums.giftCard.FilterGiftCardList;
import com.cookedspecially.service.GiftCardService;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * Created by Abhishek on 5/27/2017.
 */

@Controller
@Api(value = "giftCard", description = "Gift Card REST API's")
@RequestMapping("/giftCard")
public class GiftCardController {

    final static Logger logger = Logger.getLogger(GiftCardController.class);

    @Autowired
    private GiftCardService giftCardService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String manageGiftCard(HttpServletRequest request, Model model) {
        return "redirect:/manageGiftCard.jsp";
    }

    /*----API In PHASE - 1 ----- */

    /**
     * API NO - 1
     * API NAME : Create Gift Card For Print
     * <p>
     * Request Payload :
     * {amount: <<float>>, category: <<String>>, expireAfterDays:<<int>>, noOfCard : <<int>>}
     * <p>
     * Session Attribute :
     * organisationId, userId
     * <p>
     * Response Payload :
     * "[{
     * ""giftCardId"": <<String>>,
     * ""expireAfterDays"": <<int>>,
     * ""amount"": <<float>>,
     * ""category"":<<string>>,
     * ""createdOn"": <<Date>>,
     * ""status"" : <<String>>,
     * ""formattedGiftCardId"":<<String(xxxx-xxxx-xxxx-xxxx)>>
     * }]"
     */
    @RequestMapping(value = "/createGiftCardForPrint", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public List<GiftCardDTO> createGiftCardForPrint(@SessionAttribute("organisationId") int organisationId, @SessionAttribute("userId") int userId, @Valid @RequestBody CreateGiftCardForPrintDTO createGiftCardForPrintDTO, BindingResult result, HttpServletRequest request) throws Exception {
        logger.info("OrganisationId = " + organisationId + " has Error =" + result.hasErrors() + " UserId=" + userId);
        if (result.hasErrors()) {
            throw new Exception("Invalid details, please pass all data in given format!!!!!"+result.getFieldError().getField()+" : "+result.getFieldError().getDefaultMessage());
        }
        return giftCardService.createGiftCardForPrint(createGiftCardForPrintDTO.amount, createGiftCardForPrintDTO.category, createGiftCardForPrintDTO.expireAfterDays, createGiftCardForPrintDTO.noOfCard, userId, organisationId);
    }

    /**
     * API NO - 2
     * API NAME : List Gift Card
     *
     * Request Parameter :
     *   {amount: <<float>>, category: <<String>>, expireAfterDays:<<int>>, noOfCard : <<int>>}
     *
     * Response Payload :
     * "[{
     ""giftCardId"": <<String>>,
     ""expiryDayCount"": <<INT>>,
     ""amount"": <<float>>,
     ""category"": <<String>>,
     ""createdOn"": <<Date>>,
     ""status"": <<String>>,
     ""formattedGiftCardId"": <<String>>,
     ""soldOn"": <<Date>>,
     ""invoiceId"":<<String>>,
     ""mobileNoOfRecipient"": <<String>>,
     ""emailIdOfRecipient"": <<String>>,
     ""message"": <<String>>,
     ""purchaserCustomerId"": <<INT>>,
     ""paymentMode"": <<String>>,
     ""paymentStatus"": <<String>>,
     ""redeemedOn"": <<Date>>,
     ""redeemedByCustomerId"": <<INT>>,
     ""redeemedByUserId"": <<INT>>
     }]"
     *
     **/
    @RequestMapping(value = "/listGiftCard", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<GiftCardInfoDTO> listGiftCard(@SessionAttribute("organisationId") int organisationId, @SessionAttribute("userId") int userId, HttpServletRequest request) throws Exception {
        String status = request.getParameter("status");
        String inputDateTimeZone = request.getParameter("inputDateTimeZone");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        return giftCardService.listGiftCard(organisationId, fromDate, toDate, inputDateTimeZone, status, userId);
    }

/**
 * API NO - 3
 * API NAME : Load Money And Activate
 *
 * Request Payload :
 *  {giftCardNo: <<String>>, amount: <<float>>, customerId : <<int>>, invoiceId :<<string>>}
 *
 * Session Attribute :
 *     organisationId, userId
 *
 *  Response Payload :
 "{
 ""result"" : <<String(SUCCESS/ERROR)>>,
 ""message"" : <String>
 }"
 *
 **/
@RequestMapping(value = "/loadMoneyAndActivate", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
@ResponseBody
public ResponseDTO loadMoneyAndActivate(@SessionAttribute("organisationId") int organisationId, @SessionAttribute("userId") int userId, @Valid @RequestBody LoadMoneyAndActivateDTO activateDTO, BindingResult result, HttpServletRequest request) throws Exception {
    if (result.hasErrors()) {
        throw new Exception("Invalid details, please pass all data in given format!!!!!"+result.getFieldError().getField()+" : "+result.getFieldError().getDefaultMessage());
    }
    return giftCardService.loadMoneyAndActivate(organisationId, userId, activateDTO.giftCardId, activateDTO.amount, activateDTO.customerId, activateDTO.invoiceId, activateDTO.message);
}

    /**
     * API NO - 4
     * API NAME : List Gift Card For Customer
     *
     * Request Parameter :
     *  ../{customerId}/{filter}
     *      filter :- PURCHASED/RECEIVED/ALL
     *
     *  Response Payload :
     "[{
     ""giftCardId"": <<String>>,
     ""expiryDayCount"": <<INT>>,
     ""amount"": <<float>>,
     ""category"": <<String>>,
     ""createdOn"": <<Date>>,
     ""status"": <<String>>,
     ""formattedGiftCardId"": <<String>>,
     ""soldOn"": <<Date>>,
     ""invoiceId"":<<String>>,
     ""mobileNoOfRecipient"": <<String>>,
     ""emailIdOfRecipient"": <<String>>,
     ""message"": <<String>>,
     ""purchaserCustomerId"": <<INT>>,
     ""paymentMode"": <<String>>,
     ""paymentStatus"": <<String>>,
     ""redeemedOn"": <<Date>>,
     ""redeemedByCustomerId"": <<INT>>,
     ""redeemedByUserId"": <<INT>>
     }]"
     *
     *
     **/
    @RequestMapping(value = "/listAllGiftCard/{customerId}/{filter}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<GiftCardInfoDTO> listAllGiftCard(@PathVariable("filter") FilterGiftCardList filter, @PathVariable("customerId") int customerId, HttpServletRequest request) throws Exception {
        if (!(customerId > 0)) {
            throw new Exception("Invalid CustomerId!.");
        }
        return giftCardService.listGiftCardOfCustomer(customerId, filter.name());
    }

/**
 * API NO - 5
 * API NAME : Redeem Gift Card
 *
 * Request Payload :
 *     {giftCardId: <<string>>, customerId : <<int>>}
 *
 * Session Attribute if any :
 *     organisationId, userId
 *
 *  Response Payload :
 "{
 ""result"" : <<String(SUCCESS/ERROR)>>,
 ""message"" : <String>
 }"
 *
 *  */
@RequestMapping(value = "/redeemGiftCard", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
@ResponseBody
public ResponseDTO redeemGiftCard(@Valid @RequestBody RedeemGiftCardDTO redeemGiftCardDTO, BindingResult result, HttpServletRequest request) throws Exception {
    if (result.hasErrors() || redeemGiftCardDTO.customerId==null) {
        throw new Exception("Invalid details, please pass all data in given format!!!!!" +result.getFieldError().getField()+" : "+ result.getFieldError().getDefaultMessage());
    }
    Integer userId = (Integer) request.getSession().getAttribute("userId");
    return giftCardService.redeemGiftCard(redeemGiftCardDTO.giftCardId, redeemGiftCardDTO.customerId, userId);
}


    /**
     * API NO - 6
     * API NAME : De-Activate Gift Card
     * <p>
     * Request Payload :
     * {giftCardNo}
     * <p>
     * Session Attribute :
     * organisationId, userId
     * <p>
     * Response Payload :
     * "{
     * ""result"" : <<String(SUCCESS/ERROR)>>,
     * ""message"" : <String>
     * }"
     */
    @RequestMapping(value = "/deactivateGiftCard", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseDTO deactivateGiftCard(@SessionAttribute("organisationId") int organisationId, @SessionAttribute("userId") int userId, @RequestBody RedeemGiftCardDTO deactivateGiftCardDTO, HttpServletRequest request) throws Exception {
        return giftCardService.deactivateGiftCard(deactivateGiftCardDTO.giftCardId, organisationId, userId);
    }

    /**
     * API NO - 7
     * API NAME : List Gift Card For Customer
     *
     * Request Parameter :
     *  ../{giftCardId}
     *
     *  Response Payload :
     "[{
     ""giftCardId"": <<String>>,
     ""expiryDayCount"": <<INT>>,
     ""amount"": <<float>>,
     ""category"": <<String>>,
     ""createdOn"": <<Date>>,
     ""status"": <<String>>,
     ""formattedGiftCardId"": <<String>>,
     ""soldOn"": <<Date>>,
     ""invoiceId"":<<String>>,
     ""mobileNoOfRecipient"": <<String>>,
     ""emailIdOfRecipient"": <<String>>,
     ""message"": <<String>>,
     ""purchaserCustomerId"": <<INT>>,
     ""paymentMode"": <<String>>,
     ""paymentStatus"": <<String>>,
     ""redeemedOn"": <<Date>>,
     ""redeemedByCustomerId"": <<INT>>,
     ""redeemedByUserId"": <<INT>>
     }]"
     *
     *
     **/
    @RequestMapping(value = "/listGiftCardInfo/{giftCardId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public GiftCardInfoDTO listGiftCardInfo(@PathVariable("giftCardId") String giftCardId, HttpServletRequest request) throws Exception {
        return giftCardService.listGiftCardInfo(giftCardId);
    }


    /**
     * API NO - 8
     * API NAME : De-Activate Gift Card
     * <p>
     * Request Payload :
     * {giftCardNo}
     * <p>
     * Session Attribute :
     * organisationId, userId
     * <p>
     * Response Payload :
     * "{
     * ""result"" : <<String(SUCCESS/ERROR)>>,
     * ""message"" : <String>
     * }"
     */
    @RequestMapping(value = "/restoreGiftCard", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseDTO restoreGiftCard(@SessionAttribute("organisationId") int organisationId, @SessionAttribute("userId") int userId, @RequestBody RedeemGiftCardDTO deactivateGiftCardDTO, HttpServletRequest request) throws Exception {
        return giftCardService.restoreGiftCard(deactivateGiftCardDTO.giftCardId, organisationId, userId);
    }
    
    
    /**
     * API NO - 9
     * API NAME : Create And Activate Gift Card
     * <p>
     * Request Payload :
     * {category,
		amount,
		msg,
		invoiceId,
		expireAfterDayCount
		}
     * <p>
     * Session Attribute :
     * organisationId, userId
     * <p>
     * Response Payload :
     * "{
     * ""result"" : <<String(SUCCESS/ERROR)>>,
     * ""message"" : <String>
     * }"
     */
    @RequestMapping(value = "/createAndActivateGiftCard", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseDTO createAndActivateGiftCard(@SessionAttribute("organisationId") int organisationId, @SessionAttribute("userId") int userId, @RequestBody CreateAndActivateGiftCardDTO createAndActivateGiftCardDTO, HttpServletRequest request) throws Exception {
       return giftCardService.createAndActivateGiftCard(createAndActivateGiftCardDTO.amount, createAndActivateGiftCardDTO.category, createAndActivateGiftCardDTO.msg,
    		   createAndActivateGiftCardDTO.invoiceId, createAndActivateGiftCardDTO.expireAfterDayCount, userId, organisationId, userId);
    }


    
    @ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDTO> exceptionHandler(Exception ex) {
    	ResponseDTO error = new ResponseDTO();
    	if(ex.getMessage().contains("Missing session attribute"))
    		error.message="Please Relogin and try again";
    	else
    		error.message=ex.getMessage();
		return new ResponseEntity<ResponseDTO>(error, HttpStatus.BAD_REQUEST);
	}

}

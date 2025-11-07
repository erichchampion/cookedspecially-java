package com.cookedspecially.controller;

import com.cookedspecially.domain.Notifier;
import com.cookedspecially.dto.notification.PushNotificationDTO;
import com.cookedspecially.dto.notification.ResultDTO;
import com.cookedspecially.dto.notification.SubscriberDTO;
import com.cookedspecially.service.NotificationService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author Abhishek
 *
 */
@Controller
@RequestMapping("/notifier")
@Api(description="Notification REST API's")
public class NotificationController {

	public static String ROOT = "upload-dir";

	@Autowired
	private NotificationService notificationService;

	@RequestMapping(value = "/subscribe", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String subscribe(@RequestBody SubscriberDTO subscribeDTO, HttpServletRequest request) {
		return notificationService.subscribe(subscribeDTO);
	}

	@RequestMapping(value = "/deSubscribeCustomer/{customerIdS}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deSubscribe(@PathVariable String customerIdS, HttpServletRequest request) {
		return notificationService.deSubscribe(Integer.parseInt(customerIdS));
	}

	@RequestMapping(value = "/deSubscribeSubscription/{susbcriberIDS}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public String deSubscribeS(@PathVariable String susbcriberIDS, HttpServletRequest request) {
		return notificationService.deSubscribeS(Integer.parseInt(susbcriberIDS));
	}

	@RequestMapping(value = "/deSubscribeToken/{token}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public String deSubscribeT(@PathVariable String token, HttpServletRequest request) {
		return notificationService.deSubscribeT(token);
	}

	@RequestMapping(value = "/deSubscribeMobile/{mobile}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public String deSubscribeMobile(@PathVariable String mobile, HttpServletRequest request) {
		return notificationService.deSubscribeMobile(mobile);
	}

	/*********************************** NOTIFIER ***********************************************/

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String registerNotifierOpen(HttpServletRequest request, Model model) {
		try {
			int restaurantId = (int) request.getSession().getAttribute("organisationId");
			model.addAttribute("notifierList", notificationService.getListNotifier(restaurantId));
			model.addAttribute("notifier", new Notifier());
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("Error",
                    "Failed to find Notification Details " + e.getMessage() + ". Try after re-login!");
        }
		return "manageNotification";
	}

	@RequestMapping(value = "/registerNotifier", method = RequestMethod.POST)
	public String registerNotifier(@Valid Notifier notifier, BindingResult result, Model model) {
		if (!result.hasErrors()) {
			try {
				notificationService.registerNotifier(notifier);
				model.addAttribute("Success", "Successfully added.");
			} catch (Exception e) {
				model.addAttribute("Error", "Failed to add Notification Details " + e.getMessage());
			}
		}
		model.addAttribute("notifierList", notificationService.getListNotifier(notifier.getRestaurantId()));
		model.addAttribute("notifier", new Notifier());
		return "manageNotification";
	}

	@RequestMapping(value = "/updateNotifier", method = RequestMethod.POST)
	public String updateNotifier(@Valid Notifier notifier, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "manageNotification";
		}
		try {
			model.addAttribute("notifierList", notificationService.updateNotifier(notifier));
			model.addAttribute("Success", "Notifiaction details is added successfully");
		} catch (Exception e) {
			model.addAttribute("Error", "Failed to add Notification Details " + e.getMessage());
		}
		model.addAttribute("notifier", new Notifier());
		return "manageNotification";
	}

	@RequestMapping(value = "/deRegisterNotifier/{restaurantId}", method = RequestMethod.DELETE)
	public String deRegisterNotifier(@PathVariable("restaurantId") int restaurantId, HttpServletRequest request,
			Model model) {
		try {
			notificationService.deRegisterNotifier(restaurantId);
			model.addAttribute("notifierList", notificationService.getListNotifier(restaurantId));
			model.addAttribute("Success", "Notifiaction details is added successfully");
			model.addAttribute("notifier", new Notifier());
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("Error", "Failed to add Notification Details " + e.getMessage());
		}
		return "manageNotification";
	}

	@RequestMapping(value = "/deRegisterNotifierBYID/{notifierID}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deRegisterNotifierBYID(@PathVariable("notifierID") int notifierID, HttpServletRequest request)
			throws Exception {
		notificationService.deRegisterNotifierBYID(notifierID);
		return "Successfully De-Registered";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/uploadP12File")
	@ApiIgnore
	public String uploadP12File(@RequestParam("file") MultipartFile file, HttpServletRequest request, Model model) {

		if (!file.isEmpty()) {
			// try {
			// Files.copy(file.getInputStream(), Paths.get(ROOT,
			// file.getOriginalFilename()));
			model.addAttribute("Success", "You successfully uploaded " + file.getOriginalFilename() + "!");
			// } catch (IOException e) {
			// model.addAttribute("message", "Failued to upload " +
			// file.getOriginalFilename() + " => " + e.getMessage());
			// }

		} else {
			model.addAttribute("message", "Failed to upload " + file.getOriginalFilename() + " because it was empty");
		}
		model.addAttribute("notifierList",
				notificationService.getListNotifier((int) request.getSession().getAttribute("organisationId")));
		model.addAttribute("notifier", new Notifier());
		return "manageNotification";
	}

	/***********************************
	 * SEND NOTIFICATION
	 ***********************************************/

	@RequestMapping(value = "/sendNotification", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO sendNotification(@Valid @RequestBody PushNotificationDTO pushNotificationDTO,BindingResult result,
			HttpServletRequest request) {
		if (result.hasErrors()) {
			ResultDTO resultDTO=new ResultDTO();
			resultDTO.message=result.getAllErrors().toString();
			resultDTO.resultCode="Error";
			return resultDTO;
		}
		return notificationService.sendNotification(pushNotificationDTO);
	}

}

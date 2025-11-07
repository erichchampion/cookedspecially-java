/**
 * 
 */
package com.cookedspecially;

import com.cookedspecially.controller.RestaurantController;
import com.cookedspecially.domain.User;
import com.cookedspecially.service.UserService;
import com.cookedspecially.utility.StringUtility;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sagarwal
 *
 */
public class AdminInterceptors implements HandlerInterceptor {

	final static Logger logger = Logger.getLogger(AdminInterceptors.class);
	
	@Autowired
	private UserService userService;
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		String username = (String) request.getSession().getAttribute("username");
		String token = (String) request.getSession().getAttribute("token");
		String base = request.getContextPath();
		String uri = request.getRequestURI();
		uri = uri.replaceAll(base, "");
		String redirect = request.getContextPath() + "/user/login";
		boolean valid = true;
		if (StringUtility.isNullOrEmpty(username) || StringUtility.isNullOrEmpty(token)) {
			valid = false;
		}else{
			User user = userService.getUserByUsername(username);
			if (user == null) {
				valid = false;
			} else {
				if (!token.equals(user.getPasswordHash())) {
					valid = false;
				}
			}
		}
		if (!valid) {
			logger.info("Un-Authentic Access attempt has been logged, redirecting to login page.");
			request.getSession().setAttribute("requestpath", uri);
			response.sendRedirect(redirect);
			return false;
		}
		return true;
	}

}

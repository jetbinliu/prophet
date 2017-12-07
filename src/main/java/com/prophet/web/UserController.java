package com.prophet.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prophet.service.HiveMetaStoreService;
import com.prophet.service.HiveServerService;
import com.prophet.service.QueryHistoryService;
import com.prophet.service.UserAuthService;

import com.prophet.web.postparameters.HiveQueryCommand;

@RestController
public class UserController extends BaseController{
	private UserAuthService userAuthService;
	
	final static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	public void setUserAuthService(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	/**
	 * 登录接口
	 * @param request
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/login.json", method = RequestMethod.POST)
	public Map<String, Object> loginController(HttpServletRequest request, @RequestParam("username") String username, @RequestParam("password") String password) {
		Map<String, Object> serviceResult = this.userAuthService.authenticate(username, password);
		
		if ((int)(serviceResult.get("data")) == 0) {
			//认证成功，则登录
			HttpSession session = request.getSession();
			session.setAttribute("loginedUser", username);
		}
		
		return this.encodeToJsonResult(serviceResult);
	}
	
	/**
	 * 退出登录的接口
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/logout.json", method = RequestMethod.POST)
	public Map<String, Object> logoutController(HttpServletRequest request) {
		Map<String, Object> controllerResult = new HashMap<String, Object>();
		
		HttpSession session = request.getSession();
		if (session.getAttribute("loginedUser") != null) {
			session.removeAttribute("loginedUser");
			session.invalidate();
		}
		
		controllerResult.put("status", 0);
		controllerResult.put("message", "ok");
		controllerResult.put("data", null);
		
		return controllerResult;
	}
	
	/**
	 * 从session里获取当前登录的用户名
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/get_login_user.json", method = RequestMethod.GET)
	public Map<String, Object> currLoginUserController(HttpServletRequest request) {
		Map<String, Object> controllerResult = new HashMap<String, Object>();
		controllerResult.put("status", 0);
		controllerResult.put("message", "ok");
		controllerResult.put("data", this.getLoginUser(request));
		return controllerResult;
	}
	
}

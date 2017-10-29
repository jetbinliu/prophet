package com.prophet.web;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.prophet.domain.*;
import com.prophet.service.*;
import com.prophet.web.LoginCommand;

@RestController
public class LoginController {
	private UserService userService;
	
	
	@RequestMapping(value = {"/", "/index.html"})		//可以配置多个映射路径
	public ModelAndView loginPage() {
		return new ModelAndView("login");
	}
	
	@RequestMapping(value = "/loginCheck.html")
	public ModelAndView loginCheck(HttpServletRequest request, LoginCommand loginCommand) {
		boolean isValidUser = userService.hasMatchUser(loginCommand.getUserName(), loginCommand.getPassword());
		if (!isValidUser) {
			//ModelAndView第一个参数是视图逻辑名，第二个第三个分别为模型名称和模型对象，作为kv形式存到request属性中。
			return new ModelAndView("login", "error", "用户名或密码错误");
		} else {
			User user = userService.findUserByUserName(loginCommand.getUserName());
			user.setLastIp(request.getLocalAddr());
			user.setLastVisit(new Date());
			userService.loginSuccess(user);
			request.getSession().setAttribute("user", user);
			return new ModelAndView("main");
		}
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}

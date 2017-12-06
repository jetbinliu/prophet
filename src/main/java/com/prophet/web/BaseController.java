package com.prophet.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class BaseController {
	
	/**
	 * 将数据封装成json格式作为RESTFUL接口返回给前端
	 * @param data
	 * @return Map<String, Object> restfulResult
	 */
	protected Map<String, Object> encodeToJsonResult(Map<String, Object> serviceResult) {
		Map<String, Object> restfulResult = new HashMap<String, Object>();
		int status = 0;
		if (!serviceResult.get("msg").equals("ok")) {
			status = 1;
		}
		restfulResult.put("status", status);
		restfulResult.put("message", serviceResult.get("msg"));
		restfulResult.put("data", serviceResult.get("data"));
		return restfulResult;
	}
	
	/**
	 * 方法重载：将数据封装成json格式作为RESTFUL接口返回给前端
	 * @param data
	 * @return Map<String, Object> restfulResult
	 */
	protected Map<String, Object> encodeToJsonResult(int status, String msg, Object data) {
		Map<String, Object> restfulResult = new HashMap<String, Object>();
		restfulResult.put("status", status);
		restfulResult.put("message", msg);
		restfulResult.put("data", data);
		return restfulResult;
	}
	
	/**
	 * 获取当前session里登录的用户名
	 * @param request
	 * @return
	 */
	protected String getLoginUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session == null || session.getAttribute("loginedUser") == null) {
			return null;
		} else {
			return session.getAttribute("loginedUser").toString();
		}
	}
}

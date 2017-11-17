package com.prophet.web;

import java.util.HashMap;
import java.util.Map;

public class BaseController {
	
	/**
	 * 将数据封装成json格式作为RESTFUL接口返回给前端
	 * @param data
	 * @return
	 */
	protected Map<String, Object> encodeToJsonResult(Object data) {
		Map<String, Object> restfulResult = new HashMap<String, Object>();
		restfulResult.put("status", 0);
		restfulResult.put("msg", "ok");
		restfulResult.put("data", data);
		return restfulResult;
	}
}

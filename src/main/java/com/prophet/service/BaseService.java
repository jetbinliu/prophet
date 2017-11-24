package com.prophet.service;

import java.util.HashMap;
import java.util.Map;

public class BaseService {
	protected Map<String, Object> serviceResult;
	
	/**
	 * 所有Service类的基类
	 * 背景：所有DAO层（除多线程包装的之外）会直接向service层返回数据和抛出异常，需要在service层进行捕获并封装成一个Map返回给controller层
	 * Map结构：{"msg":"ok", "data":null}
	 */
	public BaseService() {
		this.serviceResult = new HashMap<String, Object>();
		this.serviceResult.put("msg", "ok");
		this.serviceResult.put("data", null);
	}
}

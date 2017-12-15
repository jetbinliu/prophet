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

import com.prophet.service.HiveSecretDataService;

@RestController
public class HiveSecretDataController extends BaseController{
	private HiveSecretDataService hiveSecretDataService;
	
	@Autowired
	public void setHiveSecretDataService(HiveSecretDataService hiveSecretDataService) {
		this.hiveSecretDataService = hiveSecretDataService;
	}

	final static Logger logger = LoggerFactory.getLogger(HiveSecretDataController.class);


	@RequestMapping(value = "/hive_secret/get_all_secrets.json", method = RequestMethod.GET)
	public Map<String, Object> getAllSecrets(HttpServletRequest request) {
		Map<String, Object> serviceResult = this.hiveSecretDataService.getAllSecretTablesByUser(this.getLoginUser(request));
		return this.encodeToJsonResult(serviceResult);
	}
	
}

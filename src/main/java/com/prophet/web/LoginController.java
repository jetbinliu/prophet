package com.prophet.web;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
//import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.prophet.domain.HiveMetaStore;
import com.prophet.service.HiveMetaStoreService;

@RestController
public class LoginController {
	private HiveMetaStoreService hiveMetaStoreService;
	private com.prophet.dao.HiveServer2Dao h;
	
	@Autowired
	public void setH(com.prophet.dao.HiveServer2Dao h) {
		this.h = h;
	}

	@Autowired
	public void setHiveMetaStoreService(HiveMetaStoreService hiveMetaStoreService) {
		this.hiveMetaStoreService = hiveMetaStoreService;
	}
	
	@RequestMapping(value = "/test1.json" )
	public java.util.HashMap<String, Integer> aaa(HttpServletRequest request) {
		java.util.HashMap<String, Integer> m = new java.util.HashMap<String, Integer>();
		m.put("aaa", 123);
		m.put("bbb", new Integer(444));
		//JSONObject j = JSONObject.fromObject(m);
		return m;
	}
	
	@RequestMapping(value = "/json1.json")
	public Object json1(HttpServletRequest request){
		//return this.hiveMetaStoreService.getAllDbAndTablesInMetaStore();
		return this.hiveMetaStoreService;
	}
	
}

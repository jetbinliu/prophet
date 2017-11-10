package com.prophet.web;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.prophet.service.HiveMetaStoreService;
import com.sun.xml.txw2.output.ResultFactory;

@RestController
public class HiveQueryController {
	private HiveMetaStoreService hiveMetaStoreService;
	
	@Autowired
	public void setHiveMetaStoreService(HiveMetaStoreService hiveMetaStoreService) {
		this.hiveMetaStoreService = hiveMetaStoreService;
	}
	
	@RequestMapping(value = "/hive_query/all_metastore_db_tables.json")
	public Map<String, Object> allDbAndTablesInMetaStore(HttpServletRequest request){
		HashMap<String, ArrayList<HashMap<String, Object>>> data = 
				this.hiveMetaStoreService.getAllDbAndTablesInMetaStore();
		Map<String, Object> restfulResult = new HashMap<String, Object>();
		restfulResult.put("status", 0);
		restfulResult.put("msg", "ok");
		restfulResult.put("data", data);
		return restfulResult;
	}
	
}

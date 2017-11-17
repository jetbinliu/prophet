package com.prophet.web;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.hive.service.server.HiveServer2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sun.xml.txw2.output.ResultFactory;

import com.prophet.service.HiveMetaStoreService;
import com.prophet.service.HiveServer2Service;

@RestController
public class HiveQueryController extends BaseController{
	private HiveMetaStoreService hiveMetaStoreService;
	private HiveServer2Service hiveServer2Service;

	@Autowired
	public void setHiveMetaStoreService(HiveMetaStoreService hiveMetaStoreService) {
		this.hiveMetaStoreService = hiveMetaStoreService;
	}
	
	@Autowired
	public void setHiveServer2Service(HiveServer2Service hiveServer2Service) {
		this.hiveServer2Service = hiveServer2Service;
	}
	
	/**
	 * 向前端返回所有metastore中的库表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hive_query/all_metastore_db_tables.json")
	public Map<String, Object> allDbAndTablesInMetaStore(HttpServletRequest request){
		HashMap<String, ArrayList<HashMap<String, Object>>> data = 
				this.hiveMetaStoreService.getAllDbAndTablesInMetaStore();
		return this.encodeToJsonResult(data);
	}
	
	/**
	 * 查询某个表结构信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hive_query/desc_table.json")
	public Map<String, Object> descTable(HttpServletRequest request) {
		String tableNameWithDb = request.getParameter("tableNameWithDb");
		List<Map<String, Object>> data = this.hiveServer2Service.descTable(tableNameWithDb);
		return this.encodeToJsonResult(data);
	}
	
	/**
	 * 向hiveserver发送SQL语句请求
	 * @param request
	 * @param hiveQueryCommand
	 */
	@RequestMapping(value = "/hive_query/send_query")
	public void hiveSqlQuery(HttpServletRequest request, HiveQueryCommand hiveQueryCommand) {
		
	}
	
	
}

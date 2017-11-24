package com.prophet.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prophet.service.HiveMetaStoreService;
import com.prophet.service.HiveServer2Service;
import com.prophet.service.QueryHistoryService;

import com.prophet.web.postparameters.HiveQueryCommand;

@RestController
public class HiveQueryController extends BaseController{
	private HiveMetaStoreService hiveMetaStoreService;
	private HiveServer2Service hiveServer2Service;
	private QueryHistoryService queryHistoryService;

	@Autowired
	public void setHiveMetaStoreService(HiveMetaStoreService hiveMetaStoreService) {
		this.hiveMetaStoreService = hiveMetaStoreService;
	}
	
	@Autowired
	public void setHiveServer2Service(HiveServer2Service hiveServer2Service) {
		this.hiveServer2Service = hiveServer2Service;
	}
	
	@Autowired
	public void setQueryHistoryService(QueryHistoryService queryHistoryService) {
		this.queryHistoryService = queryHistoryService;
	}
	
	/**
	 * 向前端返回所有metastore中的库表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hive_query/all_metastore_db_tables.json")
	public Map<String, Object> allDbAndTablesInMetaStoreController(HttpServletRequest request){
		Map<String, Object> data = 
				this.hiveMetaStoreService.getAllDbAndTablesInMetaStore();
		return this.encodeToJsonResult(data);
	}
	
	/**
	 * 查询某个表结构信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hive_query/desc_table.json")
	public Map<String, Object> descTableController(HttpServletRequest request) {
		String tableNameWithDb = request.getParameter("tableNameWithDb");
		Map<String, Object> serviceResult = this.hiveServer2Service.descTable(tableNameWithDb);
		
		return this.encodeToJsonResult(serviceResult);
	}
	
	/**
	 * 向hiveserver发送SQL语句请求
	 * @param request
	 * @param hiveQueryCommand
	 */
	@RequestMapping(value = "/hive_query/send_query.json")
	public Map<String, Object> sendHiveSqlQueryController(HttpServletRequest request, HiveQueryCommand hiveQueryCommand) {
		String queryContent = hiveQueryCommand.getQueryContent();
		//去掉结尾的分号
		if (queryContent.endsWith(";")) {
			queryContent = queryContent.substring(0, queryContent.length() - 1);
		}
		Map<String, Object> serviceResult = this.hiveServer2Service.executeHiveSqlQuery(queryContent, "aaa");
		
		return this.encodeToJsonResult(serviceResult);
	}
	
	@RequestMapping(value = "/hive_query/save_query_history.json")
	public Map<String, Object> saveQueryHistoryController(HttpServletRequest request, HiveQueryCommand hiveQueryCommand) {
		String queryContent = hiveQueryCommand.getQueryContent();
		String username = "admin user1";
		//queryContent = "select * from mysql_db.t1fff";
		Map<String, Object> serviceResult = this.queryHistoryService.insertOneQueryHistory(queryContent, username);
		return this.encodeToJsonResult(serviceResult);
	}
	
}

package com.prophet.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
public class HiveQueryController extends BaseController{
	private HiveMetaStoreService hiveMetaStoreService;
	private HiveServerService hiveServerService;
	private QueryHistoryService queryHistoryService;
	private UserAuthService userAuthService;
	
	final static Logger logger = LoggerFactory.getLogger(HiveQueryController.class);

	@Autowired
	public void setHiveMetaStoreService(HiveMetaStoreService hiveMetaStoreService) {
		this.hiveMetaStoreService = hiveMetaStoreService;
	}
	
	@Autowired
	public void setHiveServerService(HiveServerService hiveServerService) {
		this.hiveServerService = hiveServerService;
	}
	
	@Autowired
	public void setQueryHistoryService(QueryHistoryService queryHistoryService) {
		this.queryHistoryService = queryHistoryService;
	}
	
	@Autowired
	public void setUserAuthService(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	/**
	 * 向前端返回所有metastore中的库表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hive_query/all_metastore_db_tables.json", method = RequestMethod.GET)
	public Map<String, Object> allDbAndTablesInMetaStoreController(HttpServletRequest request) throws Exception{
		Map<String, Object> data = 
				this.hiveMetaStoreService.getAllDbAndTablesInMetaStore();
		return this.encodeToJsonResult(data);
	}
	
	/**
	 * 查询某个表结构信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hive_query/desc_table.json", method = RequestMethod.GET)
	public Map<String, Object> descTableController(HttpServletRequest request) {
		String tableNameWithDb = request.getParameter("tableNameWithDb");
		Map<String, Object> serviceResult = this.hiveServerService.descTable(tableNameWithDb);
		
		return this.encodeToJsonResult(serviceResult);
	}
	
	/**
	 * 向hiveserver发送SQL语句请求
	 * @param request
	 * @param hiveQueryCommand
	 */
	@RequestMapping(value = "/hive_query/send_query.json", method = RequestMethod.POST)
	public Map<String, Object> sendHiveSqlQueryController(HttpServletRequest request, HiveQueryCommand hiveQueryCommand) {
		String queryContent = hiveQueryCommand.getQueryContent().trim();
		long queryHistId = hiveQueryCommand.getQueryHistId();
		//去掉结尾的分号
		if (queryContent.endsWith(";")) {
			queryContent = queryContent.substring(0, queryContent.length() - 1);
		}
		Map<String, Object> serviceResult = this.hiveServerService.executeHiveSqlQuery(queryContent, this.getLoginUser(request), queryHistId);
		
		return this.encodeToJsonResult(serviceResult);
	}
	
	/**
	 * 保存一条查询历史记录到数据库里，不管执行成功与失败
	 * @param request
	 * @param hiveQueryCommand
	 * @return
	 */
	@RequestMapping(value = "/hive_query/save_query_history.json", method = RequestMethod.POST)
	public Map<String, Object> saveQueryHistoryController(HttpServletRequest request, HiveQueryCommand hiveQueryCommand) {
		String queryContent = hiveQueryCommand.getQueryContent();
		//queryContent = "select * from mysql_db.t1fff";
		Map<String, Object> serviceResult = this.queryHistoryService.insertOneQueryHistory(queryContent, this.getLoginUser(request));
		return this.encodeToJsonResult(serviceResult);
	}
	
	/**
	 * 获取该用户最近的部分查询历史
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hive_query/get_query_history.json", method = RequestMethod.GET)
	public Map<String, Object> getAllQueryHistoryController(HttpServletRequest request) {
		Map<String, Object> serviceResult = this.queryHistoryService.getAllQueryHistoryByUser(this.getLoginUser(request));
		return this.encodeToJsonResult(serviceResult);
	}
	
	/**
	 * 获取某个查询任务的状态值
	 * @param queryHistId
	 * @return
	 */
	@RequestMapping(value = "/hive_query/get_query_status.json", method = RequestMethod.GET)
	public Map<String, Object> getQueryHistoryStatusController(HttpServletRequest request, @RequestParam("queryHistId") long queryHistId) {
		Map<String, Object> serviceResult = this.queryHistoryService.getQueryHistoryById(queryHistId);
		return this.encodeToJsonResult(serviceResult);
	}
	
	/**
	 * 从磁盘获取某个历史查询文本结果
	 * @param request
	 * @param queryHistId
	 * @return
	 */
	@RequestMapping(value = "/hive_query/get_history_result.json", method = RequestMethod.GET)
	public Map<String, Object> getHistoryResultController(HttpServletRequest request, @RequestParam("queryHistId") long queryHistId) {
		Map<String, Object> serviceResult = this.hiveServerService.getHistoryResultFromDiskById(this.getLoginUser(request), queryHistId);
		return this.encodeToJsonResult(serviceResult);
	}
}

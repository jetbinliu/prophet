package com.prophet.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.prophet.service.HiveSecretDataService;
import com.prophet.web.postparameters.HiveQueryCommand;
import com.prophet.common.HQLParser;
import com.prophet.common.QueryHistoryStatusEnum;

@RestController
public class HiveQueryController extends BaseController{
	private HiveMetaStoreService hiveMetaStoreService;
	private HiveServerService hiveServerService;
	private QueryHistoryService queryHistoryService;
	private HiveSecretDataService hiveSecretDataService;
	
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
	public void setHiveSecretDataService(HiveSecretDataService hiveSecretDataService) {
		this.hiveSecretDataService = hiveSecretDataService;
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
	 * 向hiveserver发送SQL语句请求，需要经过高危过滤、权限验证等前置检验
	 * @param request
	 * @param hiveQueryCommand
	 */
	@RequestMapping(value = "/hive_query/send_query.json", method = RequestMethod.POST)
	public Map<String, Object> sendHiveSqlQueryController(HttpServletRequest request, HiveQueryCommand hiveQueryCommand) {
		String queryContent = hiveQueryCommand.getQueryContent().trim();
		long queryHistId = hiveQueryCommand.getQueryHistId();
		String strEmailNotify = hiveQueryCommand.getEmailNotify();
		int emailNotify = strEmailNotify.equals("true") ? 1 : 0;
		//去掉结尾的分号
		if (queryContent.endsWith(";")) {
			queryContent = queryContent.substring(0, queryContent.length() - 1);
		}
		
		Map<String, Object> restfulResult = new HashMap<String, Object>();
		//首先解析HQL，拦截高危语句不发送到后端执行
		HQLParser hqlParser = new HQLParser();
		try {
			hqlParser.parseHQL(queryContent);
		} catch (Exception ex) {
			//更新状态
			this.queryHistoryService.updateQueryHistoryStatus(queryHistId, QueryHistoryStatusEnum.ERROR);
			
			restfulResult.put("status", 1);
			restfulResult.put("message", ex.getMessage() + "\tCaused by: " + ex.getCause());
			restfulResult.put("data", null);
			return restfulResult;
		}
		
		String oper = "";
		Set<String> queriedTables = null;
		try {
			 oper = hqlParser.getOper();
			 queriedTables = hqlParser.getTables();
		} catch (Exception ex) {
			//更新状态
			this.queryHistoryService.updateQueryHistoryStatus(queryHistId, QueryHistoryStatusEnum.ERROR);
			
			restfulResult.put("status", 1);
			restfulResult.put("message", "该SQL语句类型不支持!");
			restfulResult.put("data", null);
			return restfulResult;
		}
			
		if (	oper.equals("INSERT") || oper.equals("DROP") || oper.equals("TRUNCATE") || 
				oper.equals("LOAD") || oper.equals("CREATETABLE") || oper.equals("ALTER") ||
				oper.equals("CREATEDATABASE") || oper.equals("DROPDATABASE")
			) {
			
			//更新状态
			this.queryHistoryService.updateQueryHistoryStatus(queryHistId, QueryHistoryStatusEnum.ERROR);
			
			restfulResult.put("status", 1);
			restfulResult.put("message", "INSERT、DROP、TRUNCATE、LOAD、CREATETABLE、ALTER、CREATEDATABASE、DROPDATABASE等高危语句不运行执行!");
			restfulResult.put("data", null);
			return restfulResult;
		} 
		
		//高危验证通过后，检查是否包含机密数据
		List<Map<String, Object>> noPrivResult = new ArrayList<Map<String, Object>>();
		for (String queriedTable : queriedTables) {
			String queriedDb = "";
			if (queriedTable.contains(".")) {
				String[] dbAndTable = queriedTable.split("\\.");			//java里点号分割的正则必须是\\.
				if (dbAndTable.length != 2) {
					restfulResult.put("status", 1);
					restfulResult.put("message", String.format("SQL语句%s表解析出来的db和table不对，请检查!", queriedTable));
					restfulResult.put("data", null);
					return restfulResult;
				}
				queriedDb = dbAndTable[0];
				queriedTable = dbAndTable[1];
			} else {
				//如果不包含.  则为默认db
				queriedDb = "default";
			}
			
			List<Map<String, Object>> daoSecretResult = this.hiveSecretDataService.checkIsSecretTable(queriedDb, queriedTable);
			if (daoSecretResult.size() != 0) {
				//判断是机密数据的话，去检查该用户是否对其有权限
				if (this.hiveSecretDataService.checkPrivilege(this.getLoginUserInfo(request).get("loginedUser").toString(), queriedDb, queriedTable)) {
					continue;
				} else {
					//该用户没有权限则提示没有该表权限
					Map<String, Object> noPrivTable = new HashMap<String, Object>();
					noPrivTable.put("table_id", daoSecretResult.get(0).get("id"));
					noPrivTable.put("table_schema", queriedDb);
					noPrivTable.put("table_name", queriedTable);
					noPrivResult.add(noPrivTable);
				}
			} else {
				//压根儿不是机密数据的话，直接跳过
				continue;
			}
		}
		
		//检查完数据权限后，汇总一下发给前端
		if (noPrivResult.size() >= 1) {
			//更新状态
			this.queryHistoryService.updateQueryHistoryStatus(queryHistId, QueryHistoryStatusEnum.ERROR);
			
			restfulResult.put("status", 3);
			restfulResult.put("message", "用户SQL中查询到的以下数据表为机密表，而且您没有权限查询，需要联系管理员申请权限!");
			restfulResult.put("data", noPrivResult);
			return restfulResult;
		}
		
		//最后都通过后发送到hive server执行
		Map<String, Object> serviceResult = this.hiveServerService.executeHiveSqlQuery(queryContent, this.getLoginUserInfo(request).get("loginedUser").toString(), queryHistId, emailNotify);
		hqlParser = null;
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
		String strEmailNotify = hiveQueryCommand.getEmailNotify();
		int emailNotify = strEmailNotify.equals("true") ? 1 : 0;
		Map<String, Object> serviceResult = this.queryHistoryService.insertOneQueryHistory(queryContent, this.getLoginUserInfo(request).get("loginedUser").toString(), emailNotify);
		return this.encodeToJsonResult(serviceResult);
	}
	
	/**
	 * 获取该用户最近的部分查询历史
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hive_query/get_query_history.json", method = RequestMethod.GET)
	public Map<String, Object> getAllQueryHistoryController(HttpServletRequest request) {
		Map<String, Object> serviceResult = this.queryHistoryService.getAllQueryHistoryByUser(this.getLoginUserInfo(request).get("loginedUser").toString());
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
		Map<String, Object> serviceResult = this.hiveServerService.getHistoryResultFromDiskById(this.getLoginUserInfo(request).get("loginedUser").toString(), queryHistId);
		return this.encodeToJsonResult(serviceResult);
	}
	
}

package com.prophet.dao.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.commons.io.FileUtils;

/**
 * 开启多线程执行hive查询任务的线程执行体
 *
 */
public class HiveServerCallableTask implements Callable<Map<String, Object>>{
	private JdbcTemplate jdbcTemplate;
	private String queryContent;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public String getSqlContent() {
		return queryContent;
	}

	public void setSqlContent(String sqlContent) {
		this.queryContent = sqlContent;
	}

	public HiveServerCallableTask(String paramSqlContent) {
		this.queryContent = paramSqlContent;
	}
	
	/**
	 * 返回的数据结构:
	 * result:{
	 * 	"msg":"ok",
	 * 	"data":{
	 * 		"type":"sql_query",
	 * 		"data":{
	 * 			"result_cols":[],
	 * 			"result_data":[]
	 * 		}
	 * 	}
	 * }
	 */
	@Override
	public Map<String, Object> call() {		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("msg", "ok");
		result.put("data", null);
		
		//参照com.prophet.service.HiveServerService.descTable()在hiveResult上层封装操作type
		Map<String, Object> dataWithType = new HashMap<String, Object>();
		dataWithType.put("type", "sql_query");
		dataWithType.put("data", null);
		
		List<Map<String, Object>> hiveResult = null;
		//先查询数据库
		try {
			
			hiveResult = this.jdbcTemplate.queryForList(this.queryContent);
			Map<String, Object> colsAndData = new HashMap<String, Object>();
			Set<String> columnSet = new HashSet<String>();
			if (!hiveResult.isEmpty()) {
				columnSet = hiveResult.get(0).keySet();
			}
			colsAndData.put("result_cols", columnSet);
			colsAndData.put("result_data", hiveResult);
			
			dataWithType.put("data", colsAndData);
		} catch (Exception ex) {
			result.put("msg", ex.getMessage());
		}
		result.put("data", dataWithType);
		
		return result;
	}
	
	
}

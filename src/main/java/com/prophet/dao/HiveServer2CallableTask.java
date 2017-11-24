package com.prophet.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.jdbc.core.JdbcTemplate;

public class HiveServer2CallableTask implements Callable<Map<String, Object>>{
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private String queryContent;
	
	public String getSqlContent() {
		return queryContent;
	}

	public void setSqlContent(String sqlContent) {
		this.queryContent = sqlContent;
	}

	public HiveServer2CallableTask(String paramSqlContent) {
		this.queryContent = paramSqlContent;
	}
	
	@Override
	public Map<String, Object> call() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("msg", "ok");
		result.put("data", null);
		
		//参照com.prophet.service.HiveServer2Service.descTable()在hiveResult上层封装操作type
		Map<String, Object> dataWithType = new HashMap<String, Object>();
		dataWithType.put("type", "sql_query");
		dataWithType.put("data", null);
		
		try {
			List<Map<String, Object>> hiveResult = this.jdbcTemplate.queryForList(this.queryContent);
			Map<String, Object> colsAndData = new HashMap<String, Object>();
			Set<String> columnSet = null;
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

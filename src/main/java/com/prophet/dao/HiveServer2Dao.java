package com.prophet.dao;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import com.prophet.common.ThreadExecutor;


@Repository
public class HiveServer2Dao {
	@Autowired
	@Qualifier("hiveServer2JdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 开启线程异步获取hive查询结果
	 * @param sqlContent
	 * @return
	 */
	public Map<String, Object> getHiveResultAsync(String queryContent) {
		//开启新的线程去连接hive执行任务
		HiveServer2CallableTask task = new HiveServer2CallableTask(queryContent);
		task.setJdbcTemplate(this.jdbcTemplate);
		
		Future<Map<String, Object>> taskFuture = ThreadExecutor.submit(task);
		Map<String, Object> taskResult = null;
			
		try {
			taskResult = taskFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return taskResult;
	}
	
	/**
	 * 查询表结构信息，同步方法
	 * @param tableNameWithDb
	 * @return
	 */
	public List<Map<String, Object>> descTableInfo(String tableNameWithDb) throws Exception {
		String sql = "describe " + tableNameWithDb;
		return jdbcTemplate.queryForList(sql);
	}
}

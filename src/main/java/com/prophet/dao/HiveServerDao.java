package com.prophet.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.prophet.common.ThreadExecutor;
import com.prophet.dao.task.HiveServerCallableTask;
import com.prophet.dao.task.HiveResultWriteDiskTask;

@Repository
public class HiveServerDao {
	@Autowired
	@Qualifier("hiveServerJdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 开启线程异步获取hive查询结果
	 * @param sqlContent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getHiveResultAsync(String queryContent, String username, long queryHistId) {
		//开启新的线程去连接hive执行任务
		HiveServerCallableTask task = new HiveServerCallableTask(queryContent);
		task.setJdbcTemplate(this.jdbcTemplate);
		
		Future<Map<String, Object>> taskFuture = ThreadExecutor.submit(task);
		Map<String, Object> hiveTaskResult = null;
			
		try {
			//get()会一直阻塞直到有结果
			hiveTaskResult = taskFuture.get();
			
			if (hiveTaskResult.get("msg").equals("ok")) {
				Map<String, Object> hiveColsAndData = (Map<String, Object>)(((Map<String, Object>)
						(hiveTaskResult.get("data"))).get("data"));
				List<Map<String, Object>> hiveData = (List<Map<String, Object>>)(hiveColsAndData.get("result_data"));
				Set<String> hiveCols = (Set<String>)(hiveColsAndData.get("result_cols"));
				
				//hive返回结果之后，开启新的线程将数据写入到磁盘文件，同时主线程可以立即返回
				HiveResultWriteDiskTask hiveSyncTask = new HiveResultWriteDiskTask();
				hiveSyncTask.setHiveData(hiveData);
				hiveSyncTask.setHiveCols(hiveCols);
				hiveSyncTask.setUsername(username);
				hiveSyncTask.setQueryHistId(queryHistId);
				
				ThreadExecutor.execute(new Thread(hiveSyncTask, "HiveResultSyncDiskThread-" + queryHistId));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hiveTaskResult;
	}
	
	/**
	 * 查询表结构信息，同步方法
	 * @param tableNameWithDb
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> descTableInfo(String tableNameWithDb) throws Exception {
		String sql = "describe " + tableNameWithDb;
		return jdbcTemplate.queryForList(sql);
	}
	
	/**
	 * 从磁盘上获取历史查询结果
	 * @param username
	 * @param queryHistId
	 * @return 数据结构：
	 * 		{
	 * 		"type":"sql_query",
	 * 		"data":{
	 * 			"result_cols":[],
	 * 			"result_data":[]
	 * 		}
	 * @throws IOException
	 */
	public Map<String, Object> getResultFromDiskById(String username, long queryHistId) throws Exception{
		Map<String, Object> resultWithType = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		
		String[] columns = null;
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		
		File dataFile = new File(com.prophet.config.HiveResultTextConfig.getDataFileName(username, queryHistId));
		File metaFile = new File(com.prophet.config.HiveResultTextConfig.getMetaFileName(username, queryHistId));
		if (dataFile.isFile() && metaFile.isFile()) {
			//先拼装列
			List<String> listColumns = FileUtils.readLines(metaFile, "UTF-8");
			if (listColumns != null && listColumns.size() >= 1) {
				//理论上meta文件只有一行
				columns = listColumns.get(0).split(com.prophet.config.HiveResultTextConfig.HIVE_RESULT_FIELD_DELIMITER);
						
				//再拼装数据
				List<String> listData = FileUtils.readLines(dataFile, "UTF-8");
				if (listData != null && listData.size() >= 1) {
					for (String line : listData) {
						String[] fields = line.split(com.prophet.config.HiveResultTextConfig.HIVE_RESULT_FIELD_DELIMITER);
						//遍历每一个列的数据，加入HashMap
						Map<String, Object> lineMap = new HashMap<String, Object>();
						for (int i=0; i<fields.length; i++) {
							lineMap.put(columns[i], fields[i]);
						}
						data.add(lineMap);
					}
				}
			} else {
				//否则columns继续保持空
			}
		} else {
			throw new Exception(String.format("历史查询结果文件已经不存在，请重新发起查询！查询语句id：%d", queryHistId));
		}
		result.put("result_cols", columns);
		result.put("result_data", data);
		resultWithType.put("type", "sql_query");
		resultWithType.put("data", result);
		return resultWithType;
	}
}

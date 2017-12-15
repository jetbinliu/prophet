package com.prophet.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prophet.dao.HiveServerDao;
import com.prophet.dao.QueryHistoryDao;
import com.prophet.common.QueryHistoryStatusEnum;


@Service
public class HiveServerService extends BaseService{
	private HiveServerDao hiveServerDao;
	private QueryHistoryDao queryHistoryDao;

	@Autowired
	public void setHiveServerDao(HiveServerDao hiveServerDao) {
		this.hiveServerDao = hiveServerDao;
	}

	@Autowired
	public void setQueryHistoryDao(QueryHistoryDao queryHistoryDao) {
		this.queryHistoryDao = queryHistoryDao;
	}

	/**
	 * 向metastore数据字典查询表结构信息
	 * @return
	 */
	public Map<String, Object> descTable(String tableNameWithDb) {
		Map<String, Object> serviceResult = this.initServiceResult();
		Map<String, Object> colsAndData = new HashMap<String, Object>();
		
		Map<String, Object> dataWithType = new HashMap<String, Object>();
		dataWithType.put("type", "desc_table");
		dataWithType.put("data", colsAndData);
		List<Map<String, Object>> daoResult = null;
		try {
			daoResult = this.hiveServerDao.descTableInfo(tableNameWithDb);
			Set<String> columnSet = null;
			if (!daoResult.isEmpty()) {
				columnSet = daoResult.get(0).keySet();
			} else {
				serviceResult.put("msg", String.format("从hiveserver里获取的%s表结构信息错误!请检查hiveserver...", tableNameWithDb));
			}
			colsAndData.put("result_cols", columnSet);
			colsAndData.put("result_data", daoResult);
			
		} catch (Exception ex) {
			serviceResult.put("msg", ex.getMessage());
		}
		//更新data应放在return前一句，防止单次session里在下次更新data之前出现了异常而没来得及更新data，从而返回了之前的data.
		serviceResult.put("data", dataWithType);
		return serviceResult;
	}
	
	
	/**
	 * 利用线程池开启线程, 异步查询hive
	 * @param queryContent
	 * @param username
	 * @return
	 */
	public Map<String, Object> executeHiveSqlQuery(String queryContent, String username, long queryHistId, int emailNotify) {
		//向hiveserver发送query
		Map<String, Object> asyncResult = this.hiveServerDao.getHiveResultAsync(queryContent, username, queryHistId, emailNotify);
		
		//主线程一直阻塞直到任务执行完毕返回结果后，开始同步更新query_history的状态，以便前端轮询
		if (asyncResult.get("msg").equals("ok")) {
			//如果任务执行完毕，返回结果正常
			this.queryHistoryDao.updateQueryHistoryStatus(queryHistId, 
					QueryHistoryStatusEnum.FINISHED.getIndex());
		} else {
			//如果任务执行报错
			this.queryHistoryDao.updateQueryHistoryStatus(queryHistId, 
					QueryHistoryStatusEnum.ERROR.getIndex());
		}
		
		return asyncResult;
	}
	
	/**
	 * 从磁盘上获取某个查询的历史结果
	 * @param username
	 * @param queryHistId
	 * @return
	 */
	public Map<String, Object> getHistoryResultFromDiskById(String username, long queryHistId) {
		Map<String, Object> serviceResult = this.initServiceResult();
		Map<String, Object> daoResult = null;
		try {
			daoResult = this.hiveServerDao.getResultFromDiskById(username, queryHistId);
		} catch (Exception ex) {
			serviceResult.put("msg", ex.getMessage());
		}
		
		serviceResult.put("data", daoResult);
		return serviceResult;
	}
}

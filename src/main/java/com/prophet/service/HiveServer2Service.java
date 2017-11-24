package com.prophet.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prophet.dao.HiveServer2Dao;


@Service
public class HiveServer2Service extends BaseService{
	private HiveServer2Dao hiveServer2Dao;

	public HiveServer2Dao getHiveServer2Dao() {
		return hiveServer2Dao;
	}

	@Autowired
	public void setHiveServer2Dao(HiveServer2Dao hiveServer2Dao) {
		this.hiveServer2Dao = hiveServer2Dao;
	}
	
	/**
	 * 向metastore数据字典查询表结构信息
	 * @return
	 */
	public Map<String, Object> descTable(String tableNameWithDb) {
		Map<String, Object> colsAndData = new HashMap<String, Object>();
		
		Map<String, Object> dataWithType = new HashMap<String, Object>();
		dataWithType.put("type", "desc_table");
		dataWithType.put("data", colsAndData);
		List<Map<String, Object>> daoResult = null;
		try {
			daoResult = this.hiveServer2Dao.descTableInfo(tableNameWithDb);
			Set<String> columnSet = null;
			if (!daoResult.isEmpty()) {
				columnSet = daoResult.get(0).keySet();
			} else {
				this.serviceResult.put("msg", String.format("从hiveserver里获取的%s表结构信息错误!请检查hiveserver...", tableNameWithDb));
			}
			colsAndData.put("result_cols", columnSet);
			colsAndData.put("result_data", daoResult);
			
		} catch (Exception ex) {
			this.serviceResult.put("msg", ex.getMessage());
		}
		//更新data应放在return前一句，防止单次session里在下次更新data之前出现了异常而没来得及更新data，从而返回了之前的data.
		this.serviceResult.put("data", dataWithType);
		return this.serviceResult;
	}
	
	
	/**
	 * 利用线程池开启线程异步查询hive
	 * @param queryContent
	 * @param username
	 * @return
	 */
	public Map<String, Object> executeHiveSqlQuery(String queryContent, String username) {
		//向hiveserver发送query
		Map<String, Object> asyncResult = this.hiveServer2Dao.getHiveResultAsync(queryContent);
		
		return asyncResult;
	}
	
}

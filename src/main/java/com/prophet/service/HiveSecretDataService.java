package com.prophet.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prophet.dao.HiveSecretTableDao;
import com.prophet.dao.HiveSecretUserPrivsDao;

@Service
public class HiveSecretDataService extends BaseService{
	private HiveSecretTableDao hiveSecretTableDao;
	private HiveSecretUserPrivsDao hiveSecretUserPrivsDao;
	
	@Autowired
	public void setHiveSecretTableDao(HiveSecretTableDao hiveSecretTableDao) {
		this.hiveSecretTableDao = hiveSecretTableDao;
	}

	@Autowired
	public void setHiveSecretUserPrivsDao(HiveSecretUserPrivsDao hiveSecretUserPrivsDao) {
		this.hiveSecretUserPrivsDao = hiveSecretUserPrivsDao;
	}

	/**
	 * 验证某个表是否为机密表
	 * @return
	 */
	public List<Map<String, Object>> checkIsSecretTable(String tableSchema, String tableName) {
		return this.hiveSecretTableDao.checkIsSecretTable(tableSchema, tableName);
	}
	
	/**
	 * 检查某个表某个用户是否有权限
	 * @param username
	 * @param tableSchema
	 * @param tableName
	 * @return
	 */
	public boolean checkPrivilege(String username, String tableSchema, String tableName) {
		List<Map<String, Object>> daoResult = this.hiveSecretUserPrivsDao.checkSecretPrivilege(username, tableSchema, tableName);
		if (daoResult.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 所有机密表面板，顺便展示哪些是当前用户有权限的
	 * @param username
	 * @return
	 * 返回数据结构dbTableResult：{
	 *    'default':[
	 *      {'table_id':21, 'table_name':'access_path1', 'info':'...'},
	 *      {'table_id':22, 'table_name':'access_path2', 'info':null},
	 *      ...
	 *    ],
	 *    'users':[
	 *      {'table_id':24, 'table_name':'access_path5', 'info':'...'},
	 *      {'table_id':25, 'table_name':'access_path6', 'info':null},
	 *      ...
	 *    ],
	 * }
	 */
	public Map<String, Object> getAllSecretTablesByUser(String username) {
		Map<String, Object> serviceResult = this.initServiceResult();
		Map<String, ArrayList<HashMap<String, Object>>> dbTableResult = new HashMap<String, ArrayList<HashMap<String, Object>>>();
		List<Map<String, Object>> daoResult = null;
		try {
			daoResult = this.hiveSecretTableDao.getAllSecretTablesByUser(username);
			for (Map<String, Object> line : daoResult) {
				//如果数据里的db在当前db组内
				ArrayList<HashMap<String, Object>> previousList = dbTableResult.get(line.get("table_schema"));
				if (previousList == null) {
					//如果结果集里没有该db，则需要初始化一个空ArrayList出来；否则直接加入即可
					previousList = new ArrayList<HashMap<String, Object>>();
				}
				HashMap<String, Object> currLine = new HashMap<String, Object>();
				currLine.put("table_id", line.get("table_id"));
				currLine.put("table_name", line.get("table_name"));
				currLine.put("info", line.get("info"));
				previousList.add(currLine);
				
				//覆盖性写入
				dbTableResult.put(line.get("table_schema").toString(), previousList);
			}
		} catch (Exception ex) {
			serviceResult.put("msg", ex.getMessage());
		}
		serviceResult.put("data", dbTableResult);
		return serviceResult;
	}
	
}

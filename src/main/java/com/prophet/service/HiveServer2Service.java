package com.prophet.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prophet.dao.HiveServer2Dao;
import com.prophet.domain.HiveMetaStore;

@Service
public class HiveServer2Service {
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
	public List<Map<String, Object>> descTable(String tableNameWithDb) {
		return this.hiveServer2Dao.descTableInfo(tableNameWithDb);
	}
	
}

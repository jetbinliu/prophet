package com.prophet.dao;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;


@Repository
public class HiveServer2Dao {
	@Autowired
	@Qualifier("hiveServer2JdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 获取hive查询结果的方法
	 * @param sqlContent
	 * @return
	 */
	public List<Map<String, Object>> getHiveResult(String sqlContent) {
		List<Map<String, Object>> hiveResult = jdbcTemplate.queryForList(sqlContent);
		return hiveResult;
	}
	
	public List<Map<String, Object>> descTableInfo(String tableNameWithDb) {
		String sql = "describe " + tableNameWithDb;
		return jdbcTemplate.queryForList(sql);
	}
}

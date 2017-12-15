package com.prophet.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HiveSecretTableDao {
	@Autowired
	@Qualifier("prophetJdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	public List<Map<String, Object>> checkIsSecretTable(String tableSchema, String tableName) {
		String sql = "select id, table_schema, table_name from hive_secret_tables where table_schema=? and table_name=?";
		Object[] args = {tableSchema, tableName};
		return jdbcTemplate.queryForList(sql, args);
	}
	
	/**
	 * 所有机密表面板，顺便展示哪些是当前用户有权限的
	 * @param username
	 * @return
	 */
	public List<Map<String, Object>> getAllSecretTablesByUser(String username) {
		String sql = "select a.id as table_id, a.table_schema, a.table_name, if(b.username is null,null,'您已具有查询权限') as info "
				+ "from hive_secret_tables a left join hive_secret_user_privs b on a.id=b.hive_secret_table_id where b.username=? or b.username is null";
		Object[] args = {username};
		return jdbcTemplate.queryForList(sql, args);
	}
}

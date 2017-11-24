package com.prophet.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class QueryHistoryDao {
	@Autowired
	@Qualifier("prophetJdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	public int insertQueryHistory(String queryTime, String queryContent, int status, String username) throws Exception {
		String sql = "insert into query_history(query_time, query_content, status, username) "
				+ "values(?, ?, ?, ?)";
		Object[] args = {queryTime, queryContent, status, username};
		return this.jdbcTemplate.update(sql, args);
	}
}

package com.prophet.dao;

import java.util.List;
import java.util.ArrayList;
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
	
	public List test1() {
		String sql = "use formatter;show tables";
		return jdbcTemplate.queryForList(sql);
	}
}

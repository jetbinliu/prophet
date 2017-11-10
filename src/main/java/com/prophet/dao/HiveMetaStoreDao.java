package com.prophet.dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import com.prophet.domain.HiveMetaStore;

@Repository
public class HiveMetaStoreDao {
	@Autowired
	@Qualifier("hiveMetaStoreJdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	public List<Map<String, Object>> getAllDbAndTablesInMetaStore() {
		String sql = "select DBS.NAME as DB_NAME,TBLS.TBL_ID,TBLS.TBL_NAME,TBLS.TBL_TYPE from TBLS,DBS where TBLS.DB_ID=DBS.DB_ID;";
		return jdbcTemplate.queryForList(sql);
	}
}

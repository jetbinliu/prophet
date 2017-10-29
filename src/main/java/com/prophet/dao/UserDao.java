package com.prophet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import com.prophet.domain.*;

@Repository	//通过Spring注释定义一个DAO
public class UserDao {
	private JdbcTemplate jdbcTemplate;
	private final static String GET_USER_SQL = "select * from t_user where user_name=?";
	private final static String UPDATE_LOGIN_INFO_SQL = "update t_user set " + 
				"last_visit=?,last_ip=?,credits=? where user_id=?";
	
	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public int getMatchCount(String userName, String password) {
		String sqlStr = "select count(*) from t_user " + "where user_name =? and password=? ";
		//jdbcTemplate.queryForInt(sqlStr, new Object[]{userName, password});
		return jdbcTemplate.queryForObject(sqlStr, new Object[]{userName, password}, Integer.class);
	}
	
	public User findUserByUserName(final String userName) {
		final User user = new User();
		jdbcTemplate.query(GET_USER_SQL, new Object[]{userName}, new RowCallbackHandler() {

			public void processRow(ResultSet rs) throws SQLException {
				// TODO Auto-generated method stub
				user.setUserId(rs.getInt("user_id"));
				user.setUserName(userName);
				user.setCredits(rs.getInt("credits"));
			}
			
		});
		return user;
	}
	
	public void updateLoginInfo(User user) {
		jdbcTemplate.update(UPDATE_LOGIN_INFO_SQL, new Object[] {
			user.getLastVisit(), 
			user.getLastIp(),
			user.getCredits(),
			user.getUserId()
		});
	}
}

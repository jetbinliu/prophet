package com.prophet.domain;

import java.util.Date;

/**
 * hive查询语句历史模型类
 * table: query_history
 */
public class QueryHistory {
	private int id;
	private Date queryTime;
	private String queryContent;
	private int status;
	private String username;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getQueryTime() {
		return queryTime;
	}
	public void setQueryTime(Date queryTime) {
		this.queryTime = queryTime;
	}
	public String getQueryContent() {
		return queryContent;
	}
	public void setQueryContent(String queryContent) {
		this.queryContent = queryContent;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
}

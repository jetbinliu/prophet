package com.prophet.web.postparameters;

public class HiveQueryCommand {
	private String queryContent;
	private long queryHistId;
	private String emailNotify;

	public String getQueryContent() {
		return queryContent;
	}

	public void setQueryContent(String queryContent) {
		this.queryContent = queryContent;
	}

	public long getQueryHistId() {
		return queryHistId;
	}

	public void setQueryHistId(long queryHistId) {
		this.queryHistId = queryHistId;
	}

	public String getEmailNotify() {
		return emailNotify;
	}

	public void setEmailNotify(String emailNotify) {
		this.emailNotify = emailNotify;
	}
	
}

package com.prophet.dao.task;

import com.prophet.dao.EmailUtil;

/**
 * 开启线程执行hive任务完毕后发送邮件
 *
 */
public class HiveResultSendmailRunnableTask implements Runnable{
	private EmailUtil emailUtil;
	private long queryHistId;
	private String mailToUser;
	
	public EmailUtil getEmailUtil() {
		return emailUtil;
	}

	public void setEmailUtil(EmailUtil emailUtil) {
		this.emailUtil = emailUtil;
	}

	public long getQueryHistId() {
		return queryHistId;
	}

	public void setQueryHistId(long queryHistId) {
		this.queryHistId = queryHistId;
	}
	
	public String getMailToUser() {
		return mailToUser;
	}

	public void setMailToUser(String mailToUser) {
		this.mailToUser = mailToUser;
	}

	@Override
	public void run() {
		try {
			this.emailUtil.sendSimpleMail(this.mailToUser, "[Prophet系统通知]Hive SQL任务执行完毕", String.format("您的Hive任务已经执行完毕，查询语句Id：%d", this.queryHistId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

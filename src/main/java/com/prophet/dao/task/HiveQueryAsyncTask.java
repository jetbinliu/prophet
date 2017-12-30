package com.prophet.dao.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.prophet.common.QueryHistoryStatusEnum;
import com.prophet.common.ThreadPool;
import com.prophet.dao.EmailUtil;

import org.apache.commons.io.FileUtils;

/**
 * 开启多线程执行hive查询任务的线程执行体
 *
 */
public class HiveQueryAsyncTask implements Runnable{
	private JdbcTemplate jdbcTemplateProphet;
	private JdbcTemplate jdbcTemplateHiveServer;
	private String queryContent;
	private String username;
	private long queryHistId;
	private int emailNotify;
	private EmailUtil emailUtil;
	
	public void setJdbcTemplateProphet(JdbcTemplate jdbcTemplateProphet) {
		this.jdbcTemplateProphet = jdbcTemplateProphet;
	}
	
	public void setJdbcTemplateHiveServer(JdbcTemplate jdbcTemplateHiveServer) {
		this.jdbcTemplateHiveServer = jdbcTemplateHiveServer;
	}

	public void setQueryContent(String queryContent) {
		this.queryContent = queryContent;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getQueryHistId() {
		return queryHistId;
	}

	public void setQueryHistId(long queryHistId) {
		this.queryHistId = queryHistId;
	}

	public void setEmailNotify(int emailNotify) {
		this.emailNotify = emailNotify;
	}
	public void setEmailUtil(EmailUtil emailUtil) {
		this.emailUtil = emailUtil;
	}

	/**
	 * 要做的事：
	 * 1.查询hive
	 * 2.结果写入磁盘,写入结果集大小统计信息
	 * 3.写入完毕更新状态和message
	 * 4.如果勾选了邮件发送邮件
	 * 5.将自己从活跃线程列表剔除
	 */
	@Override
	public void run() {
		List<Map<String, Object>> hiveResult = null;
		
		try {
			//先查询hive
			hiveResult = this.jdbcTemplateHiveServer.queryForList(this.queryContent);
			Set<String> columnSet = new HashSet<String>();
			if (!hiveResult.isEmpty()) {
				columnSet = hiveResult.get(0).keySet();
			}
			
			//然后将结果写入disk
			if (this._writeHiveResultToDisk(hiveResult, columnSet, username, this.queryHistId)) {
				//一旦写入成功将结果集大小记录到db，并传给前端，方便分页
				int resultSize = hiveResult.size();
				this._saveResultSizeById(this.queryHistId, resultSize);
			}
			
			//执行到最后没有问题，则更新状态
			this._updateQueryHistoryStatusAndMsg(this.queryHistId, 
					QueryHistoryStatusEnum.FINISHED.getIndex(), "ok");
		} catch (Exception ex) {
			this._updateQueryHistoryStatusAndMsg(this.queryHistId, 
					QueryHistoryStatusEnum.ERROR.getIndex(), ex.getMessage());
			
		} finally {
			//如果用户选了邮件通知，则异步发送邮件
			if (this.emailNotify == 1) {
				HiveResultSendmailRunnableTask hiveMailTask = new HiveResultSendmailRunnableTask();
				hiveMailTask.setEmailUtil(this.emailUtil);
				hiveMailTask.setQueryHistId(this.queryHistId);
				hiveMailTask.setMailToUser(this.username);
				
				ThreadPool.execute(new Thread(hiveMailTask, ThreadPool.HIVE_EMAIL_THREAD_NAME + this.queryHistId));
			}
			
			//将自己从活跃线程列表剔除
			ThreadPool.stopThread(this.queryHistId);
		}
	}
	
	/**
	 * 结果集写入磁盘
	 * @param hiveData
	 * @param hiveCols
	 * @param username
	 * @param queryHistId
	 * @return
	 */
	private boolean _writeHiveResultToDisk(List<Map<String, Object>> hiveData, Set<String> hiveCols, String username, long queryHistId) {
		Boolean isFinished = true;
		
		final String dataFileName = com.prophet.config.HiveResultTextConfig.getDataFileName(username, queryHistId);
		final String metaFileName = com.prophet.config.HiveResultTextConfig.getMetaFileName(username, queryHistId);
		
		//先生成数据文件，遍历数组加工成一个分割好的list
		List<StringBuffer> diskResult = new ArrayList<StringBuffer>();
		for (Map<String, Object> line : hiveData) {
			//StringBuffer是一个可变对象,当对他进行修改的时候不会像String那样重新建立对象。字符串连接操作效率比String高
			StringBuffer strLine = new StringBuffer("");
			
			Iterator<Entry<String, Object>> iter = line.entrySet().iterator();	//这样遍历HashMap效率较高
			while (iter.hasNext()) {
				Map.Entry<String, Object> entry = iter.next();
				//String key = entry.getKey();
				Object value = entry.getValue();
				if (value == null || value.equals("")) {
					value = " ";
				}
				strLine.append(value + com.prophet.config.HiveResultTextConfig.HIVE_RESULT_FIELD_DELIMITER);
			}
			//一行结束，加入diskResult
			diskResult.add(strLine);
		}
		
		//生成meta文件
		StringBuffer strCols = new StringBuffer("");
		for (String col : hiveCols) {
			strCols.append(col + com.prophet.config.HiveResultTextConfig.HIVE_RESULT_FIELD_DELIMITER);
		}
		
		//然后一次性写入磁盘，避免频繁IO
		try {
			//重要：结果集为空时依然会写文件，但txt和meta文件内容都是""空字符串
			FileUtils.writeLines(new File(dataFileName), "UTF-8", diskResult);
			FileUtils.writeStringToFile(new File(metaFileName), strCols.toString(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			isFinished = false;
		}
		return isFinished;
	}
	
	/**
	 * 保存查询结果集行数到query_history表
	 * @param queryHistId
	 * @param resultSize
	 * @return
	 */
	private int _saveResultSizeById(long queryHistId, int resultSize) {
		String sql = "update query_history set result_size=? where id=?";
		Object[] args = new Object[]{resultSize, queryHistId};
		return this.jdbcTemplateProphet.update(sql, args);
	}
	
	/**
	 * 更改查询历史记录状态值和消息
	 * @param id
	 * @param status
	 * @param message
	 * @return
	 */
	public int _updateQueryHistoryStatusAndMsg(long id, int status, String message) {
		String sql = "update query_history set status=?,message=? where id=?";
		Object[] args = {status, message, id};
		int result = -1;
		try {
			result = this.jdbcTemplateProphet.update(sql, args);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
}

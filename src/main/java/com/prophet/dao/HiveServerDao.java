package com.prophet.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.prophet.dao.EmailUtil;
import com.prophet.common.ThreadExecutor;
import com.prophet.dao.task.HiveServerCallableTask;
import com.prophet.dao.task.HiveResultWriteDiskCallableTask;
import com.prophet.dao.task.HiveResultSendmailRunnableTask;

@Repository
public class HiveServerDao {
	@Autowired
	@Qualifier("hiveServerJdbcTemplate")
	private JdbcTemplate jdbcTemplateHiveServer;
	
	@Autowired
	@Qualifier("prophetJdbcTemplate")
	private JdbcTemplate jdbcTemplateProphet;
	
	private EmailUtil emailUtil;
	
	@Autowired
	public void setEmailUtil(EmailUtil emailUtil) {
		this.emailUtil = emailUtil;
	}
	
	public final static int PAGE_ROWS = 20;
	final static Logger logger = LoggerFactory.getLogger(HiveServerDao.class);

	/**
	 * 开启线程异步获取hive查询结果
	 * @param sqlContent
	 * @return 返回的数据结构:
	 * result:{
	 * 	"msg":"ok",
	 * 	"data":{
	 * 		"type":"sql_query",
	 * 		"data":{
	 * 			"result_cols":[],
	 * 			"result_data":[]
	 * 		},
	 * 		"size":300
	 * 	}
	 * }
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getHiveResultAsync(String queryContent, String username, long queryHistId, int emailNotify) {
		Map<String, Object> hiveResult = new HashMap<String, Object>();
		Map<String, Object> diskResultFirstPage = null;
		
		//开启新的线程去连接hive执行任务
		HiveServerCallableTask task = new HiveServerCallableTask(queryContent);
		task.setJdbcTemplate(this.jdbcTemplateHiveServer);
		
		Future<Map<String, Object>> taskFuture = ThreadExecutor.submit(task);
		Map<String, Object> hiveTaskResult = null;
			
		try {
			//get()会一直阻塞直到有结果
			hiveTaskResult = taskFuture.get();
			hiveResult.put("msg", hiveTaskResult.get("msg"));
			
			if (hiveTaskResult.get("msg").equals("ok")) {
				Map<String, Object> hiveColsAndData = (Map<String, Object>)(((Map<String, Object>)
						(hiveTaskResult.get("data"))).get("data"));
				List<Map<String, Object>> hiveData = (List<Map<String, Object>>)(hiveColsAndData.get("result_data"));
				Set<String> hiveCols = (Set<String>)(hiveColsAndData.get("result_cols"));
				
				//hive返回结果之后，开启新的线程将数据写入到磁盘文件，同时主线程必须等待写入完成后才能继续
				HiveResultWriteDiskCallableTask hiveSyncTask = new HiveResultWriteDiskCallableTask();
				hiveSyncTask.setHiveData(hiveData);
				hiveSyncTask.setHiveCols(hiveCols);
				hiveSyncTask.setUsername(username);
				hiveSyncTask.setQueryHistId(queryHistId);
				
				//ThreadExecutor.execute(new Thread(hiveSyncTask, "HiveResultSyncDiskThread-" + queryHistId));
				Future<Boolean> writeDiskFuture = ThreadExecutor.submit(hiveSyncTask);
				
				Boolean isFinished = writeDiskFuture.get();
				if (isFinished.booleanValue() == true) {
					//一旦完成，则从磁盘上获取第一个分页的数据返回给前端
					diskResultFirstPage = this.getResultFromDiskByIdByPage(username, queryHistId, 1, HiveServerDao.PAGE_ROWS);
					//将结果集大小记录到db，并传给前端，方便分页
					int resultSize = hiveData.size();
					this.saveResultSizeById(queryHistId, resultSize);
					
					diskResultFirstPage.put("size", resultSize);
				}
			}
			//如果用户选了邮件通知，则异步发送邮件
			if (emailNotify == 1) {
				HiveResultSendmailRunnableTask hiveMailTask = new HiveResultSendmailRunnableTask();
				hiveMailTask.setEmailUtil(emailUtil);
				hiveMailTask.setQueryHistId(queryHistId);
				hiveMailTask.setMailToUser(username);
				
				ThreadExecutor.execute(new Thread(hiveMailTask, "HiveResultMailThread-" + queryHistId));
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			hiveResult.put("msg", e.getMessage());
		}
		
		//最终结果
		hiveResult.put("data", diskResultFirstPage);
		return hiveResult;
	}
	
	/**
	 * 查询表结构信息，同步方法
	 * @param tableNameWithDb
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> descTableInfo(String tableNameWithDb) throws Exception {
		String sql = "describe `" + tableNameWithDb + "`";
		return jdbcTemplateHiveServer.queryForList(sql);
	}
	
	/**
	 * 从磁盘上获取历史查询结果，分页获取
	 * @param username
	 * @param queryHistId
	 * @return 数据结构：
	 * 		{
	 * 		"type":"sql_query",
	 * 		"data":{
	 * 			"result_cols":[],
	 * 			"result_data":[]
	 * 		}
	 * @throws IOException
	 */
	public Map<String, Object> getResultFromDiskByIdByPage(String username, long queryHistId, int pageNo, int pageRows) throws Exception{
		Map<String, Object> resultWithType = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		
		String[] columns = null;
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		
		File dataFile = new File(com.prophet.config.HiveResultTextConfig.getDataFileName(username, queryHistId));
		File metaFile = new File(com.prophet.config.HiveResultTextConfig.getMetaFileName(username, queryHistId));
		if (dataFile.isFile() && metaFile.isFile()) {
			//先拼装列
			List<String> listColumns = FileUtils.readLines(metaFile, "UTF-8");
			if (listColumns != null && listColumns.size() >= 1) {
				//理论上meta文件只有一行
				columns = listColumns.get(0).split(com.prophet.config.HiveResultTextConfig.HIVE_RESULT_FIELD_DELIMITER);
						
				//再拼装数据
				List<String> listData = new ArrayList<String>();
				LineIterator it = null;
				try {
					it = FileUtils.lineIterator(dataFile, "UTF-8");
					int startLineNo = 0;				//闭区间

					//先移动startLineNo的指针
					while (it.hasNext() && startLineNo < (pageNo - 1) * pageRows) {
						it.nextLine();
						startLineNo++;
					}
					int endLineNo = startLineNo;		//闭区间
					//再移动endLineNo的指针
					while (it.hasNext() && endLineNo < (pageNo) * pageRows) {
						String line = it.nextLine();
						listData.add(line);
						endLineNo++;
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					LineIterator.closeQuietly(it);
				}
				
				//组装成前端需要的json数据
				if (listData != null && listData.size() >= 1) {
					for (String line : listData) {
						String[] fields = line.split(com.prophet.config.HiveResultTextConfig.HIVE_RESULT_FIELD_DELIMITER);
						//遍历每一个列的数据，加入HashMap
						Map<String, Object> lineMap = new HashMap<String, Object>();
						for (int i=0; i<fields.length; i++) {
							lineMap.put(columns[i], fields[i]);
						}
						data.add(lineMap);
					}
				}
			} else {
				//否则columns继续保持空
			}
		} else {
			throw new Exception(String.format("历史查询结果文件已经不存在，请重新发起查询！查询语句id：%d", queryHistId));
		}
		result.put("result_cols", columns);
		result.put("result_data", data);
		resultWithType.put("type", "sql_query");
		resultWithType.put("data", result);
		return resultWithType;
	}
	
	/**
	 * 保存查询结果集行数到query_history表
	 * @param queryHistId
	 * @param resultSize
	 * @return
	 */
	private int saveResultSizeById(long queryHistId, int resultSize) {
		String sql = "update query_history set result_size=? where id=?";
		Object[] args = new Object[]{resultSize, queryHistId};
		return this.jdbcTemplateProphet.update(sql, args);
	}
}

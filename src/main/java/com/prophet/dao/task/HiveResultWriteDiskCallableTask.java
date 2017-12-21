package com.prophet.dao.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;

/**
 * 开启线程将hive查询结果写入磁盘 
 *
 */
public class HiveResultWriteDiskCallableTask implements Callable<Boolean>{
	private List<Map<String, Object>> hiveData;
	private Set<String> hiveCols;
	private String username;
	private long queryHistId;

	public List<Map<String, Object>> getHiveData() {
		return hiveData;
	}

	public void setHiveData(List<Map<String, Object>> hiveData) {
		this.hiveData = hiveData;
	}

	public Set<String> getHiveCols() {
		return hiveCols;
	}

	public void setHiveCols(Set<String> hiveCols) {
		this.hiveCols = hiveCols;
	}

	public String getUsername() {
		return username;
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

	@Override
	public Boolean call() {
		Boolean isFinished = true;
		
		final String dataFileName = com.prophet.config.HiveResultTextConfig.getDataFileName(this.username, this.queryHistId);
		final String metaFileName = com.prophet.config.HiveResultTextConfig.getMetaFileName(this.username, this.queryHistId);
		
		//先生成数据文件，遍历数组加工成一个分割好的list
		List<StringBuffer> diskResult = new ArrayList<StringBuffer>();
		for (Map<String, Object> line : this.hiveData) {
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
		for (String col : this.hiveCols) {
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

}

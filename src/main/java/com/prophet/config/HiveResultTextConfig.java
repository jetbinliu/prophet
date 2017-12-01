package com.prophet.config;

public class HiveResultTextConfig {
	private final static String HIVE_RESULT_FILE_DIR		= "d:\\tmp\\";
	public final static String HIVE_RESULT_FIELD_DELIMITER 	= "##@@#";
	
	/**
	 * 获取数据文件绝对路径
	 * @param username
	 * @param queryHistId
	 * @return
	 */
	public final static String getDataFileName(String username, long queryHistId) {
		return String.format("%s%s-%d.txt", HIVE_RESULT_FILE_DIR, username, queryHistId);
	}
	
	/**
	 * 获取meta文件绝对路径
	 * @param username
	 * @param queryHistId
	 * @return
	 */
	public final static String getMetaFileName(String username, long queryHistId) {
		return String.format("%s%s-%d.meta", HIVE_RESULT_FILE_DIR, username, queryHistId);
	}
}

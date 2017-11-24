package com.prophet.service;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prophet.common.QueryHistoryStatusConst;
import com.prophet.dao.QueryHistoryDao;
import com.prophet.util.DateTimeUtil;

@Service
public class QueryHistoryService extends BaseService{
	private QueryHistoryDao queryHistoryDao;

	public QueryHistoryDao getQueryHistoryDao() {
		return queryHistoryDao;
	}

	@Autowired
	public void setQueryHistoryDao(QueryHistoryDao queryHistoryDao) {
		this.queryHistoryDao = queryHistoryDao;
	}
	
	/**
	 * 向prophet的数据库中插入一条查询历史
	 */
	public Map<String, Object> insertOneQueryHistory(String queryContent, String username) {
		int result = -1;
		try {
			result = this.queryHistoryDao.insertQueryHistory(DateTimeUtil.getNow(), queryContent, QueryHistoryStatusConst.RUNNING, username);
		} catch (Exception ex) {
			this.serviceResult.put("msg", ex.getMessage());
		}
		//更新data应放在return前一句，防止单次session里在下次更新data之前出现了异常而没来得及更新data，从而返回了之前的data.
		this.serviceResult.put("data", result);
		return this.serviceResult;
	}
}

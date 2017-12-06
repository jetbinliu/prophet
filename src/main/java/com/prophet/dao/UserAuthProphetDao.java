package com.prophet.dao;

import com.prophet.interfaces.UserAuthDaoInterface;

/**
 * prophet内置用户认证基础类，暂未实现
 *
 */
public class UserAuthProphetDao implements UserAuthDaoInterface{

	@Override
	public int authenticate(String username, String password) {
		return 0;
	}
	
}

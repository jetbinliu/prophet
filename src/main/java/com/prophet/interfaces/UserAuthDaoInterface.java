/**
 * 
 */
package com.prophet.interfaces;

/**
 * 用户认证系统的接口
 */
public interface UserAuthDaoInterface {
	public int authenticate(String username, String password);
	
}

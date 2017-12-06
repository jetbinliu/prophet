package com.prophet.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.prophet.dao.UserAuthLdapDao;
import com.prophet.interfaces.UserAuthDaoInterface;

@Service
public class UserAuthService extends BaseService{
	/*用户认证系统的类型*/
	@Value("${authentication.system}")
	private String authSystemType;							
	
	/*LDAP相关配置*/
	@Value("${authentication.ldap.url}")
	private String LDAP_URL;
	@Value("${authentication.ldap.base-dn}")
	private String LDAP_BASE_DN;
	@Value("${authentication.ldap.user-search-dn}")
	private String LDAP_USER_SEARCH_DN;
	@Value("${authentication.ldap.user-search-column}")
	private String LDAP_USER_SEARCH_COLUMN;
	@Value("${authentication.ldap.factory}")
	private String LDAP_FACTORY;
	@Value("${authentication.ldap.security-authentication}")
	private String LDAP_SECURITY_AUTHENTICATION;
	@Value("${authentication.ldap.security-credenticials}")
	private String LDAP_SECURITY_CREDENTIALS;
	
	public UserAuthService() {
		
	}
	
	/*单例，常驻内存，所有UserAuthService实例共享同一个userAuthDao*/
	private static UserAuthDaoInterface userAuthDao;
	
	/**
	 * 工厂模式：生产认证连接类DAO的工厂
	 * 设计思想：UserAuthService由spring初始化后先从配置文件里读取配置的Auth开关、LDAP等信息，然后authenticate时调用该方法
	 * 		获取具体类型的dao（如果null则初始化并常驻内存）。
	 * @return 具体类型的dao
	 * @throws Exception 
	 */
	private UserAuthDaoInterface getUserAuthDao() throws Exception {
		if (!this.validateConfig(this.authSystemType)) {
			throw new Exception(String.format("application.properties文件里%s相关参数配置不正确，请检查!", this.authSystemType));
		}
		switch (this.authSystemType.toLowerCase()) {
			case "ldap":
				if (userAuthDao == null) {
					synchronized(UserAuthService.class) {
						if (userAuthDao == null) {
							userAuthDao = new UserAuthLdapDao(LDAP_URL, LDAP_BASE_DN, LDAP_USER_SEARCH_DN, LDAP_USER_SEARCH_COLUMN, 
								LDAP_FACTORY, LDAP_SECURITY_AUTHENTICATION, LDAP_SECURITY_CREDENTIALS);
						}
					}
				}
				break;
			case "prophet":
				break;
		}
		return userAuthDao;
	}

	/**
	 * 验证用户名密码是否正确
	 * @param uid
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> authenticate(String uid, String password) {
		Map<String, Object> serviceResult = this.initServiceResult();
		int daoResult = -1;
		try {
			daoResult = this.getUserAuthDao().authenticate(uid, password);
			
		} catch (Exception ex) {
			serviceResult.put("msg", ex.getMessage());
		}
		serviceResult.put("data", daoResult);
		return serviceResult;
	}
	
	/**
	 * 根据auth系统类型验证对应的参数是否在application.properties文件里配置正确
	 * @param authSystemType
	 * @return
	 */
	private boolean validateConfig(String authSystemType) {
		return true;
	}
}

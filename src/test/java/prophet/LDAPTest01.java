package prophet;
import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class LDAPTest01 {
	private final String URL = "ldap://ldap.baijiahulian.com/";
	private final String BASE_DN = "CN=s-ldap,OU=Service Account,DC=baijiahulian,DC=com";
	private final String SEARCH_DN = "OU=newcompany,DC=baijiahulian,DC=com";
	private final String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private LdapContext ctx = null;
	private final Control[] connCtls = null;

	private void LDAP_connect() {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
		env.put(Context.PROVIDER_URL, URL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		
		env.put(Context.SECURITY_PRINCIPAL, this.BASE_DN); 
		env.put(Context.SECURITY_CREDENTIALS, "6j8f^FjkiCsA~q7e"); // 管理员密码
		//env.put("java.naming.ldap.attributes.binary", "objectSid objectGUID");
	
		try {
			ctx = new InitialLdapContext(env, connCtls);
			System.out.println( "连接成功123" ); 
		} catch (javax.naming.AuthenticationException e) {
			System.out.println("连接失败：");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("连接出错：");
			e.printStackTrace();
		}

	}
	
	private void closeContext(){
		if (ctx != null) {
		try {
		ctx.close();
		}
		catch (NamingException e) {
		e.printStackTrace();
		}
	
		}
	}
	
	private String getUserDN(String uid) {
		String userDN = "";
		LDAP_connect();
		try {
			SearchControls constraints = new SearchControls();
			String filter = "(&(objectClass=user)(SamAccountName=" + uid + "))";
			//String[] attrPersonArray = { "uid", "userPassword", "displayName", "cn", "sn", "mail", "description" };
			String[] attrPersonArray = { "uid", "displayName", "cn" };
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			constraints.setReturningAttributes(attrPersonArray); 
			
			NamingEnumeration<SearchResult> en = ctx.search(this.SEARCH_DN, filter, constraints);
		
			if (en == null || !en.hasMoreElements()) {
				System.out.println("未找到该用户");
			}
			// maybe more than one element
			while (en != null && en.hasMoreElements()) {
				Object obj = en.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult si = (SearchResult) obj;
					userDN += si.getName();
					userDN += "," + this.SEARCH_DN;
				} else {
					System.out.println(obj);
				}
			}
			
		} catch (Exception e) {
			System.out.println("查找用户时产生异常。");
			e.printStackTrace();
		}
		
		//userDN = "CN=贾利阳,OU=产品运维,OU=基础平台部,OU=总部,OU=百家互联,OU=newcompany,DC=baijiahulian,DC=com";
		return userDN;
	}

	public boolean authenticate(String UID, String password) {
		boolean valide = false;
		String userDN = getUserDN(UID);
		
		try {
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			ctx.reconnect(connCtls);
			System.out.println(userDN + " 验证通过");
			valide = true;
		} catch (AuthenticationException e) {
			System.out.println(userDN + " 验证失败");
			System.out.println(e.toString());
			valide = false;
		} catch (NamingException e) {
			System.out.println(userDN + " 验证失败");
			valide = false;
		}
		closeContext();
		return valide;
	}

	public boolean addUser(String usr, String pwd,String uid,String description) {
		try {
			LDAP_connect();
			BasicAttributes attrsbu = new BasicAttributes();
			BasicAttribute objclassSet = new BasicAttribute("objectclass");
			objclassSet.add("inetOrgPerson");
			attrsbu.put(objclassSet);
			attrsbu.put("sn", usr);
			attrsbu.put("cn", usr);
			attrsbu.put("uid", uid);
			attrsbu.put("userPassword", pwd);
			attrsbu.put("description", description);
			ctx.createSubcontext("uid="+uid+"", attrsbu);
		
			return true;
		} catch (NamingException ex) {
			ex.printStackTrace();
		}
		closeContext();
		return false;
	} 
	
	public static void main(String[] args) {
		LDAPTest01 ldap = new LDAPTest01();

		System.out.println(ldap.authenticate("jialiyang", "jason@19901008"));
		
		//ldap.addUser("yorker","secret");
		System.out.println(ldap.getUserDN("jialiyang"));
		
		ldap.closeContext();
		
	}

}

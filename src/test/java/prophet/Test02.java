package prophet;
import java.io.File;
import java.io.IOException;
import java.lang.Thread;
import java.lang.ThreadGroup;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import com.prophet.common.Encryptor;

public class Test02 extends Test01{
	private int a;
	
	public Test02() {
		super();
		this.a = 30;
	}

	public static void main(String[] args) {
		try {
	        System.out.println(Encryptor.encryptMD5("hello world的发"));
	        System.out.println(Encryptor.encryptSHA("213456"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package prophet;
import java.io.File;
import java.io.IOException;
import java.lang.Thread;
import java.lang.ThreadGroup;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.prophet.common.Encryptor;
import java.lang.Thread;

public class Test02{
	public int a = 10;
	public static Map<String, Test02> h = new HashMap<String, Test02>(); 
	public void test1() {
		Test02.h.get("t").a = 20; 
		System.out.println(h.get("t").a);
		System.out.println(h.get("t").hashCode());
	}

	public static void main(String[] args) {
		
	}

}

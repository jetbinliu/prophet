package prophet;
import java.io.File;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.prophet.common.ThreadPool;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.ql.parse.ParseDriver;

public class Test01 implements Callable<String>{
	private int a = 10;
	public int getA() {
		return this.a;
	}
	
	public Test01(){
		System.out.println("a值原本是："+this.a);
		this.a = 20;
	}
	
	@Override
	public String call() throws Exception {
		// TODO Auto-generated method stub
		return java.lang.Thread.currentThread().getName()+"";
	}
	
	public static void main(String[] args) {
		Test02 t = new Test02();
		Test02.h.put("t", t);
		System.out.println(t.hashCode());
		System.out.println(Test02.h.get("t").a);
		
		t.test1();
	}

}

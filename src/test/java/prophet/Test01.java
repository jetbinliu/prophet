package prophet;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.prophet.common.ThreadExecutor;

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
		// TODO Auto-generated method stub
		Test01 t1 = new Test01();
		Test01 t2 = new Test01();
		Future<String> f1 = ThreadExecutor.submit(t1);
		Future<String> f2 = ThreadExecutor.submit(t2);
		try {
			System.out.println(f1.get());
			System.out.println(f2.get(3000, TimeUnit.MILLISECONDS));
		} catch (InterruptedException  | ExecutionException  | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ThreadExecutor.shutdown();
	}

}

package prophet;
import java.util.HashMap;
import java.util.Arrays;

public class Test01 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<String, int[]> a = new HashMap<String, int[]>();
		a.put("abc", new int[]{1,2,3});
		a.put("abc", new int[]{1,2,3,4});
		System.out.println(a.get("aaa")==null);
	}

}

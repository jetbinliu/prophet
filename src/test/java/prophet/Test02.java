package prophet;
import java.io.File;
import java.io.IOException;
import java.lang.Thread;
import java.lang.ThreadGroup;
import java.util.ArrayList;
import java.util.HashMap;

import com.prophet.common.QueryHistoryStatusEnum;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class Test02 extends Test01{
	private int a;
	
	public Test02() {
		super();
		this.a = 30;
	}

	public static void main(String[] args) {
		File file = new File("d:\\tmp\\aaa.txt");
		List<Map<String, Object>> l1 = new ArrayList<Map<String, Object>>();
		Map<String, Object> m1 = new HashMap<String, Object>();
		Map<String, Object> m2 = new HashMap<String, Object>();
		m1.put("key1", "value1");
		m1.put("key2", "的发啊2");
		m2.put("key1", "value2");
		m2.put("key2", "的发啊3");
		l1.add(m1);
		l1.add(m2);
		
//		try {
//			//FileUtils.writeLines(file, "UTF-8", l1);
//			for (Map<String, Object> m : l1) {
//				for (Object o : m.values()) {
//					FileUtils.write(file, o.toString(), "UTF-8", true);
//					FileUtils.write(file, "\t", "UTF-8", true);
//				}
//				FileUtils.write(file, "\r\n", "UTF-8", true);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			List<String> l2 = FileUtils.readLines(file, "UTF-8");
			for (String line : l2) {
				System.out.println(line);
				String[] s1 = line.split("\t");
				System.out.println(java.util.Arrays.toString(s1));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("over.");
	}

}

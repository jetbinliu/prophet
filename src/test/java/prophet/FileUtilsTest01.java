package prophet;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class FileUtilsTest01 {

	public static void main(String[] args) {
		//要显示4、5、6行
		int pageNo = 2;
		int pageRows = 10;
		LineIterator it = null;
		try {
			it = FileUtils.lineIterator(new File("D:\\eclipse_workspace\\prophet\\data\\jialiyang-156.txt"), "UTF-8");
			int startLineNo = 0;				//闭区间

			//先移动startLineNo的指针
			while (it.hasNext() && startLineNo < (pageNo - 1) * pageRows) {
				it.nextLine();
				startLineNo++;
			}
			int endLineNo = startLineNo;		//闭区间
			//再移动endLineNo的指针
			while (it.hasNext() && endLineNo < (pageNo) * pageRows) {
				String line = it.nextLine();
				System.out.println(line);
				endLineNo++;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			LineIterator.closeQuietly(it);
		}

	}

}

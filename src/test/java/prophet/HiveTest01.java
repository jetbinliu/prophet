package prophet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

public class HiveTest01 {
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";
	
	public static void main(String[] args) {
		try {
			Class.forName(driverName);
			Connection con = DriverManager.getConnection("jdbc:hive2://XX.XX.XX.XX:10000/default", "root", "");
			Statement stmt = con.createStatement();
	        String sql = "SHOW TABLES";
	        System.out.println("Running: " + sql);
	        ResultSet res = stmt.executeQuery(sql);
	        while (res.next()) {
	            System.out.println(res.getString(1));
	        }
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}

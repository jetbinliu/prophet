package prophet;

import org.apache.hadoop.hive.ql.parse.ParseException;

import com.prophet.common.HQLParser;

public class HQLParserTest01 {
	public static void main(String[] args) {
        String parsesql = "select terminal_id, count(1) from formatter.app_event_log where pt=20171226 and event_type=4 and get_json_object(event_attr,'$.event_id') in ('homepage_entry', 'homepage_entry_show') group by terminal_id";
        
        HQLParser hp= new HQLParser();
		try {
			hp.parseHQL(parsesql);
		} catch (ParseException | org.antlr.runtime.NoViableAltException e) {
			System.out.println("catch");
		}
		System.out.println(hp.getOper()+"==================");
        for (String table : hp.getTables()) {
        	System.out.println(table);
        }
        
	}
}

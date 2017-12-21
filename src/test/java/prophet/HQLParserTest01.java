package prophet;

import org.apache.hadoop.hive.ql.parse.ParseException;

import com.prophet.common.HQLParser;

public class HQLParserTest01 {
	public static void main(String[] args) {
        String sql1 = "Select * from zpc1";
        String sql2 = "Select name,ip from zpc2 bieming where age > 10 and area in (select area from city)";
        String sql3 = "Select d.name,d.ip from (select * from zpc3 where age > 10 and area in (select area from city)) d";
        String sql4 = "create table zpc(id string, name string)";
        String sql5 = "insert overwrite table tmp1 PARTITION (partitionkey='2008-08-15') select * from tmp";
        String sql6 = "FROM (  SELECT p.datekey datekey, p.userid userid, c.clienttype  FROM detail.usersequence_client c JOIN fact.orderpayment p ON p.orderid = c.orderid "
                + " JOIN default.user du ON du.userid = p.userid WHERE p.datekey = 20131118 ) base  INSERT OVERWRITE TABLE `test`.`customer_kpi` SELECT base.datekey, "
                + "  base.clienttype, count(distinct base.userid) buyer_count GROUP BY base.datekey, base.clienttype";
        String sql7 = "SELECT id, value FROM (SELECT id, value FROM p1 UNION ALL  SELECT 4 AS id, 5 AS value FROM p1 limit 1) u";
        String sql8 = "select dd from(select id+1 dd from zpc) d";
        String sql9 = "select dd+1 from(select id+1 dd from zpc) d";
        String sql10 = "truncate table db1.zpc";
        String sql11 = "drop table zpc";
        String sql12 = "select * from tablename where unix_timestamp(cz_time) > unix_timestamp('2050-12-31 15:32:28')";
        String sql15 = "alter table old_table_name RENAME TO new_table_name";
        String sql16 = "select statis_date,time_interval,gds_cd,gds_nm,sale_cnt,discount_amt,discount_rate,price,etl_time,pay_amt from o2ostore.tdm_gds_monitor_rt where time_interval = from_unixtime(unix_timestamp(concat(regexp_replace(from_unixtime(unix_timestamp('201506181700', 'yyyyMMddHHmm')+ 84600 ,  'yyyy-MM-dd HH:mm'),'-| |:',''),'00'),'yyyyMMddHHmmss'),'yyyy-MM-dd HH:mm:ss')";
        String sql13 = "INSERT OVERWRITE TABLE u_data_new SELECT TRANSFORM (userid, movieid, rating, unixtime) USING 'python weekday_mapper.py' AS (userid, movieid, rating, weekday) FROM u_data";
        String sql14 = "SELECT a.* FROM a JOIN b ON (a.id = b.id AND a.department = b.department)";
        String sql17 = "LOAD DATA LOCAL INPATH \"/opt/data/1.txt\" OVERWRITE INTO TABLE table1";
        String sql18 = "CREATE TABLE  table1     (    column1 STRING COMMENT 'comment1',    column2 INT COMMENT 'comment2'        )";
        String sql19 = "ALTER TABLE events RENAME TO 3koobecaf";
        String sql20 = "ALTER TABLE invites ADD COLUMNS (new_col2 INT COMMENT 'a comment')";
        String sql21 = "alter table mp add partition (b='1', c='1')";
        String sql22 = "select login.uid from login day_login left outer join (select uid from regusers where dt='20130101') day_regusers on day_login.uid=day_regusers.uid where day_login.dt='20130101' and day_regusers.uid is null";
        String sql23 = "select name from (select * from db1.zpc zzz left outer join db2.def) d";
        String sql24 = "drop database db4";
        String parsesql = "set -v";
        
        HQLParser hp= new HQLParser();
		try {
			hp.parseHQL(parsesql);
		} catch (ParseException | org.antlr.runtime.NoViableAltException e) {
			System.out.println("catch");
		}
		System.out.println(hp.getOper()+"==================");
//        for (String table : hp.getTables()) {
//        	System.out.println(table);
//        }
        
	}
}

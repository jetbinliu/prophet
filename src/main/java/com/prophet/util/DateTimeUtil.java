package com.prophet.util;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
	public static String getNow() {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(now);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(DateTimeUtil.getNow());
	}

}

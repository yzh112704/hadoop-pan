package com.example.d.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	// long型时间戳转化为对应格式的时间字符串
	public static String longToString(String dateFormat,Long millSec) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    Date date= new Date(millSec);
	    return sdf.format(date);
	}

	// Date型日期转换为对应格式的时间字符串
	public static String DateToString(String dateFormat,Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(date);
	}

	// 时间字符串转换为Long行时间戳
	public static Long StringToLong(String time){
		try{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = simpleDateFormat.parse(time);
			Long stamp = date.getTime();
			return stamp;
		}
		catch (Exception e){
			System.out.println("StringToLong Error!");
			e.printStackTrace();
			return -1l;
		}
	}
}

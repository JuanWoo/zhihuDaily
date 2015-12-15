package main.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @desc	
 * @author	JuanWoo
 * @date	2015年12月5日
 **/

public class DateUtils {
	public static final String FORMAT_YYYYMMDD = "yyyyMMdd";
	public static final String FORMAT_YYYY_MM_DD = "yyyy/MM/dd";
	public static final String FORMAT_YYYY_MM_DD_EEEE = "yyyy.MM.dd EEEE";

	public static Date getDate(int year, int month, int date) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, date);
		return cal.getTime();
	}
	
	public static Date parse(String date) throws ParseException {
		return new SimpleDateFormat(FORMAT_YYYYMMDD).parse(date);
	}

	public static String format(Date date) {
		return format(date, FORMAT_YYYYMMDD);
	}

	public static String format(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	public static Date addDay(Date d, int amount) {
		return addDate(d, amount, Calendar.DAY_OF_MONTH);
	}

	public static Date addDate(Date d, int amount, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(field, amount);
		return cal.getTime();
	}
}

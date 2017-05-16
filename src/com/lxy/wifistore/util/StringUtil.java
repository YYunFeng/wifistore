package com.lxy.wifistore.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import android.text.TextUtils;


/**
 * 
 * Depiction: 字符串处理工具类
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年8月14日 上午11:03:45
 * <p>
 * Modify:
 * <p> 
 * @version 1.0
 * @since 1.0
 */
public final class StringUtil {
	private StringUtil() {
	}
	
	/**
	 * 格式化文件大小
	 * 
	 * @param size
	 * @return String
	 */
	public static String formatSize(long size) {
		String sizestring = null;
		DecimalFormat dfsmall = new DecimalFormat("0");
		DecimalFormat df = new DecimalFormat("0.00");
		double d = size;
		if ((d / 1024) < 1024) {
			if ((d / 1024) < 1024) {
				sizestring = dfsmall.format(d / 1024) + "KB";
			} else {
				sizestring = df.format(d / (1024 * 1024)) + "MB";
			}
		} else if (d / (1024 * 1024) < 1024) {
			sizestring = df.format(d / (1024 * 1024)) + "MB";
		} else {
			sizestring = df.format(d / (1024 * 1024 * 1024)) + "GB";
		}
		return sizestring;
	}
	
	/**
	 * 计算百分比
	 * 
	 * @param used
	 *            已经使用的
	 * @param total
	 *            总计
	 * @return String
	 */
	public static int percentSize(long used, long total) {
		if (used == 0 || total == 0 || (used > total)) {
			return 0;
		}
		double u = used;
		double t = total;
		double r = u / t * 100;
		DecimalFormat format = new DecimalFormat("0");
		int p = Integer.parseInt(format.format(r));
		return (p > 100) ? 0 : p;
	}
	
	/**
	 * 格式化时间
	 * 
	 * @param time
	 * @return String
	 */
	public static String formatTime(long time) {
		if (time <= 0) {
			return "";
		}
		Date date = new Date(time);
		DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		return format.format(date);
	}
	
	public static Date parseFormatTime(String time) {
		if (time == null) {
			return new Date();
		}
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 格式化时间标签，形如：2012-08-09T11:10:05 只获取年月日部分
	 * 
	 * @param time
	 * @return 格式化后的结果
	 */
	public static String formatTime(String time) {
		if (!TextUtils.isEmpty(time)) {
			String[] array = time.split("T");
			return array.length > 0 ? array[0] : null;
		}
		return null;
	}
	
	/**
	 * 格式化下载次数为为标准格式,用逗号分开的形式，形如：1,000,000
	 * 
	 * @param count
	 *            个数
	 * @return 格式化的结果字符串
	 */
	public static String formatCount(long count) {
		if (count < 1000) {
			return String.valueOf(count);
		}
		DecimalFormat df = new DecimalFormat("###,###");
		return df.format(count);
	}
	
	/**
	 * 判断字符串是否过长，按字符算
	 * 
	 * @param source
	 *            源字符串
	 * @param capacity
	 *            字符串的最大长度
	 * @return 过长返回true，否则返回false
	 */
	public static boolean checkStringLong(String source, int capacity) {
		if (TextUtils.isEmpty(source)) {
			return false;
		}
		return source.length() > capacity;
	}
	
	/**
	 * 根据日期获取log文件名字
	 * 
	 * @return yyyy-MM-dd
	 */
	public static String getLogNameByDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(System.currentTimeMillis());
		return format.format(date);
	}
	
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
	
	public static boolean isImei(String str) {
		return str != null && str.length() == 15 && isNumeric(str);
	}
}

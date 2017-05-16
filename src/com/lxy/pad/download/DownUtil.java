package com.lxy.pad.download;

import java.io.File;

import android.os.Environment;

import com.google.bitmapcache.ImageCache;


/**
 * Depiction: 应用同步工具类
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年5月23日 上午11:55:20
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class DownUtil {
	private final static String SDCARD  = Environment.getExternalStorageDirectory().getAbsolutePath();
	private final static String ROOT    = SDCARD + "/wifistore";
	public final static String  APP_DIR = ROOT + "/apps";                                             //在线应用存放目录
	                                                                                                   
	private DownUtil() {
	}
	
	/**
	 * 初始化下载目录
	 */
	public static void initDir() {
		final String[] array = {
		        ROOT,
		        APP_DIR
		};
		for (String path : array) {
			File dir = new File(path);
			dir.mkdir();
		}
	}
	
	/**
	 * 根据appId获取应用安装包的路径
	 * 
	 * @param context
	 *            Context
	 * @param appId
	 *            应用id
	 * @return String
	 */
	public static String getApkPath(int appId) {
		String name = ImageCache.hashKeyForDisk(String.valueOf(appId)) + ".apk";
		return APP_DIR + "/" + name;
	}
	
}

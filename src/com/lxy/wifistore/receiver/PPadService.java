package com.lxy.wifistore.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.android.lib.util.LogUtil;
import com.lxy.wifistore.WifiApp;


/**
 * Depiction: 客户端后台定时服务
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年6月25日 上午11:43:38
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class PPadService {
	public static final long   UPGRADE_DURATION    = 1 * 60 * 60 * 1000;                //客户端检查更新，4个小时一次
	private static final long  UPLOAD_LOG_DURATION = 1 * 2 * 60 * 1000;               //日志上传机制，1个小时一次
	                                                                                    
	public static final String UPGRADE_ACTION      = "com.lxy.UPGRADE_ACTION";
	public static final String UPLOAD_LOG_ACTION   = "com.lxy.UPLOAD_LOG_ACTION";
	
	private Context            context             = WifiApp.getInstance();
	public static boolean      isRunning;
	private static PPadService instance            = null;
	
	private AlarmManager       alarmManager;
	private PendingIntent      piUpgrade;
	private PendingIntent      piLog;
	
	private PPadService() {
	}
	
	public synchronized static PPadService getInstance(Context context) {
		if (instance == null) {
			instance = new PPadService();
		}
		return instance;
	}
	
	public void start() {
		LogUtil.e(this, "start background service......");
		isRunning = true;
//		upgrade();
		log();
	}
	
//	private void upgrade() {
//		Intent intent = new Intent(UPGRADE_ACTION);
//		piUpgrade = PendingIntent.getBroadcast(context, 0, intent, 0);
//		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//		long triggerAtTime = System.currentTimeMillis();
//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, UPGRADE_DURATION, piUpgrade);
//	}
	
	private void log() {
		Intent intent = new Intent(UPLOAD_LOG_ACTION);
		piLog = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		long triggerAtTime = System.currentTimeMillis();
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, UPLOAD_LOG_DURATION, piLog);
	}
	
	public void stop() {
		isRunning = false;
		if (alarmManager == null) {
			alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		}
		
		alarmManager.cancel(piUpgrade);
		alarmManager.cancel(piLog);
		
		LogUtil.e(this, "stop background service......");
	}
}

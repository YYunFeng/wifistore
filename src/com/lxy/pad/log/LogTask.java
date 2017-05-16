package com.lxy.pad.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.android.lib.util.DeviceInfo;
import com.android.lib.util.LogUtil;
import com.lxy.wifistore.WifiApp;


/**
 * Depiction:日志操作单体类
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年6月19日 上午11:23:46
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class LogTask implements OnUploadListener {
	private Context          context = WifiApp.getInstance();
	private static LogTask   logTask = null;
	private boolean          running;
	private List<LogBean>    logs;
	private OnUploadListener onUploadListener;
	
	private LogTask() {
	}
	
	public static LogTask getInstance() {
		if (logTask == null) {
			logTask = new LogTask();
		}
		return logTask;
	}
	
	public void start() {
		if(running){
			return;
		}
		LogUtil.i(this, "start upload log task");
		running = true;
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				logs = DBLogDao.getDao(context).selectAllNonUploadedLog();
				String imei = new DeviceInfo(context).imei();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss"); 
				String dateTime =formatter.format(new Date(System.currentTimeMillis()));
				final String path = "/sdcard/"+dateTime+"_"+imei + ".log";
				LogUtil.e(this, "================begin write log to file================");
				for (LogBean log : logs) {
					LogUtil.e(this, "a log is -->"+log.toLogString());
					LogWriter writer = new LogWriter();
					writer.write(log, path);
				}
				LogUtil.e(this, "================end write log to file================");
				
				File file = new File(path);
				if (file != null && file.exists()) {
					LogUpload upload = new LogUpload(file);
					upload.setOnUploadListener(LogTask.this);
					upload.start();
					LogUtil.e(this, "begin upload log file");
				} else {
					LogUtil.e(this, "the log file is not exist.");
				}
				running = false;
			}
		});
	}
	
	/**
	 * @return the onUploadListener
	 */
	public OnUploadListener getOnUploadListener() {
		return onUploadListener;
	}
	
	/**
	 * @param onUploadListener
	 *            the onUploadListener to set
	 */
	public void setOnUploadListener(OnUploadListener onUploadListener) {
		this.onUploadListener = onUploadListener;
	}
	
	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public void onUploadStart(String path) {
		if (onUploadListener != null) {
			onUploadListener.onUploadStart(path);
		}
	}
	
	@Override
	public void onUploadFinish(boolean success, String date, String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		
		if (success) {
			DBLogDao.getDao(context).updateLogStatus(logs);
		}
		running = false;
		
		if (onUploadListener != null) {
			onUploadListener.onUploadFinish(success, date, path);
		}
	}
}

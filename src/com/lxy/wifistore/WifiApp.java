package com.lxy.wifistore;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.res.Configuration;

import com.android.lib.util.DeviceInfo;
import com.lxy.pad.download.DownloadTask;
import com.lxy.wifistore.util.HttpConfig;


/**
 * 类描述：程序入口
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午4:35:44
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
@ReportsCrashes (formKey = "", // This is required for backward compatibility but not used
formUri = HttpConfig.HOST + HttpConfig.PORT + HttpConfig.INC + HttpConfig.REPORT_EXCEPTION, reportType = org.acra.sender.HttpSender.Type.JSON, httpMethod = org.acra.sender.HttpSender.Method.POST)
public class WifiApp extends Application {
	private static WifiApp     appContext;
	public static DownloadTask downloadTask = null;
	
	public static WifiApp getInstance() {
		return appContext;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		appContext = this;
		// 异常处理，不需要处理时注释掉这两句即可！
		//		CrashHandler crashHandler = CrashHandler.getInstance();
		// 注册crashHandler
		//		crashHandler.init(getApplicationContext());
		ACRA.init(this);
		/*
		 * reportExceptionLog.action?padImei=143&saler=112&exceptionMess=thdasdadasd
		 */
		String formUri = ACRA.getConfig().formUri();
		String imei = new DeviceInfo(getApplicationContext()).imei();
		if (!formUri.contains("padImei=")) {
			formUri += "?padImei=" + imei;
			ACRA.getConfig().setFormUri(formUri);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}

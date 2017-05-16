package com.lxy.wifistore.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.lib.data.JsonMap;
import com.android.lib.util.DeviceInfo;
import com.android.lib.util.LogUtil;
import com.google.gson.Gson;
import com.lxy.pad.download.DownloadDao;
import com.lxy.pad.download.DownloadInfo;
import com.lxy.pad.log.DBLogDao;
import com.lxy.pad.log.LogBean;
import com.lxy.pad.log.LogStatus;
import com.lxy.wss.wifi.WifiUtil;


/**
 * Depiction:新安装的软件广播
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年8月15日 上午9:22:21
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class PackageReceiver extends BroadcastReceiver {
	
	public PackageReceiver() {
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_PACKAGE_ADDED.equalsIgnoreCase(action) || Intent.ACTION_PACKAGE_CHANGED.equalsIgnoreCase(action) || Intent.ACTION_PACKAGE_REPLACED.equalsIgnoreCase(action)) {
			//安装、替换或者更新了一个软件
			String pck = intent.getDataString().split(":")[1];
			LogUtil.e(this, "new install app ,pck -->" + pck);
			logInstallApp(context, pck);
		} else if (Intent.ACTION_PACKAGE_REMOVED.equalsIgnoreCase(action)) {
			//删除了一个软件
			//			String pck = intent.getDataString().split(":")[1];
		}
	}
	
	private void logInstallApp(Context context, String pck) {
		DownloadInfo info = DownloadDao.getDao().select(pck);
		if (info == null) {
			LogUtil.e(this, "cant search download info");
			return;
		}
		
		final DBLogDao dao = DBLogDao.getDao(context);
		DeviceInfo device = new DeviceInfo(context);
		boolean isInstalled = dao.isInstalled(info.id, null);
		if (!isInstalled) {
			LogUtil.e(this, "uninstalled the app");
			final LogBean log = new LogBean();
			log.cpId = 0;
			log.from = 0;
			log.installWay = 1;
			log.verifyCode = "00000";
			
			log.appId = info.id;
			log.appName = info.name;
			log.version = info.versionName;
			
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			String date = format.format(new Date(System.currentTimeMillis()));
			log.date = date;
			
			log.imeiOfPad = WifiUtil.getMac();
			log.staffId = WifiUtil.getId();
			log.uniqueNmuber = WifiUtil.getId();
			log.name = WifiUtil.getName();
			log.shopName = WifiUtil.getShopName();
			log.channel = WifiUtil.getChannel();
			
			log.factory = device.product();
			log.imeiOfPhone = device.imei();
			log.model = device.model();
			log.os = device.getAndroidPlatform();
			
			//实时上传log的实体类
			LogInfo logInfo = new LogInfo();
			logInfo.appId = info.id;
			logInfo.appType = 0;
			logInfo.appVersionName = info.versionName;
			logInfo.cid = WifiUtil.getChannel();
			logInfo.cpid = 0;
			logInfo.installModel = 1;
			logInfo.installTime = date;
			logInfo.loginUser = log.staffId;
			logInfo.padImei = log.imeiOfPad;
			logInfo.phoneImei = log.imeiOfPhone;
			logInfo.phoneModelName = log.model;
			logInfo.phoneOsVer = log.os;
			logInfo.phoneVenderName = log.factory;
			logInfo.salerName = log.name;
			logInfo.salerNo = log.staffId;
			logInfo.shopName = log.shopName;
			
			String json = new Gson().toJson(logInfo);
			LogUtil.e(this, "post json---》"+json);
			new PostLogTask(json) {
				
				@Override
				public void onStartTask() {
				}
				
				@Override
				public void onEndTask(JsonMap data, String json) {
					LogUtil.e(this, "result json---》"+json);
					if (data != null && data.getMap("resultObj") != null && data.getMap("resultObj").getInt("resultCode") == 0) {
						//成功
						log.status = LogStatus.UPLOADED;
						dao.addLogs(log);
						LogUtil.e(this, "post install-log success !");
					} else {
						//失败
						LogUtil.e(this, "post install-log fail !");
						dao.addLogs(log);
					}
				}
			}.perform();
		}else{
			LogUtil.e(this, "already installed the app");
		}
		
		DownloadDao.getDao().delete(info.id);
	}
	
}

package com.lxy.wifistore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lxy.pad.log.LogTask;


/**
 * Depiction:客户端后台定时服务广播接收器
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年6月25日 上午11:42:33
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class PPadReceiver extends BroadcastReceiver {
	
	public PPadReceiver() {
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		//		if (PPadService.UPGRADE_ACTION.equalsIgnoreCase(action)) {
		//			UpgradeTask.getInstance(context).checkUpgrade(false);
		//		} else 
		if (PPadService.UPLOAD_LOG_ACTION.equalsIgnoreCase(action)) {
			LogTask.getInstance().start();
		}
	}
	
}

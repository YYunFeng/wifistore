package com.lxy.wifistore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lxy.pad.log.LogTask;
import com.lxy.pad.upgrade.UpgradeTask;


/**
 * Depiction: 网络变化监听广播接收器
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年6月19日 上午10:50:40
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class NetworkReceiver extends BroadcastReceiver {
	private static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	private static final String ACTION_WIFI_STATE_CHANGED  = "android.net.wifi.WIFI_STATE_CHANGED";
	
	public NetworkReceiver() {
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_CONNECTIVITY_CHANGE) || intent.getAction().equals(ACTION_WIFI_STATE_CHANGED)) {
			LogTask.getInstance().start();
			
			if (!PPadService.isRunning) {
				PPadService.getInstance(context).start();
			}
			
			UpgradeTask.getInstance(context).checkUpgrade(false);
		}
	}
}

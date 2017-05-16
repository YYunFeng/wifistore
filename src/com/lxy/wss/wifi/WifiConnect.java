package com.lxy.wss.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.android.lib.util.LogUtil;


/**
 * Depiction:打开并根据已知wifi名称和密码连接wifi工具。
 * <p>
 * android.permission.CHANGE_NETWORK_STATE
 * <p>
 * android.permission.CHANGE_WIFI_STATE
 * <p>
 * android.permission.ACCESS_NETWORK_STATE
 * <p>
 * android.permission.ACCESS_WIFI_STATE
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2013-10-28 下午8:48:06
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class WifiConnect extends Thread {
	/**
	 * 打开wifi最大时长
	 */
	private final static int      TIME_OUT = 10;
	private final Handler         handler  = new Handler();
	private Context               context;
	private String                sid;
	private String                passwd;
	private OnWifiConnectListener onWifiConnectListener;
	
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            Context对象
	 * @param sid
	 *            wifi名称
	 * @param passwd
	 *            wifi密码
	 */
	public WifiConnect(Context context, String sid, String passwd) {
		this.context = context;
		this.sid = sid;
		this.passwd = passwd;
	}
	
	public void run() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		wifing();
		boolean isOpen = false;
		int time = 0;
		while (!(isOpen = wifiManager.isWifiEnabled())) {
			try {
				sleep(1000);
				time++;
				
				if (time >= TIME_OUT) {
					//10秒内如果无法打开wifi，则连接失败
					break;
				}
			} catch (InterruptedException e) {
				LogUtil.e(this, e.toString());
			}
		}
		if (isOpen && connect(wifiManager)) {
			success();
		} else {
			failure();
		}
	}
	
	private boolean connect(WifiManager wifiManager) {
		//		WifiConfiguration netConfig = new WifiConfiguration();
		//		netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
		//		netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		//		netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		//		netConfig.SSID = "\"" + sid + "\"";
		//		netConfig.preSharedKey = "\"" + passwd + "\"";
		//		netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		//		netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		//		netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		//		netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		//		netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		//		netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		//		int wcgID = wifiManager.addNetwork(netConfig);
		//		wifiManager.enableNetwork(wcgID, true);
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + sid + "\"";
		// 没有密码
		if (passwd != null) {
			config.wepKeys[0] = "";
			config.wepTxKeyIndex = 0;
		}
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		wifiManager.enableNetwork(wifiManager.addNetwork(config), true);
		
		int time = 0;
		int state = wifiManager.getWifiState();
		while (state == WifiManager.WIFI_STATE_ENABLED) {
			try {
				Thread.sleep(1000);
				time++;
				
				if (time >= TIME_OUT / 2) {
					//连接wifi后，如果5秒内wifi仍然无法使用，则连接失败
					break;
				}
			} catch (InterruptedException e) {
				LogUtil.e(this, "connect wifi exception" + e.toString());
			}
		}
		return state == WifiManager.WIFI_STATE_ENABLED;
	}
	
	private void wifing() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (onWifiConnectListener != null) {
					onWifiConnectListener.onConnecting(sid);
				}
			}
		});
	}
	
	private void success() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (onWifiConnectListener != null) {
					onWifiConnectListener.onConnectSuccess(sid);
				}
			}
		});
	}
	
	private void failure() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (onWifiConnectListener != null) {
					onWifiConnectListener.onConnectFailure(sid);
				}
			}
		});
	}
	
	public OnWifiConnectListener getOnWifiConnectListener() {
		return onWifiConnectListener;
	}
	
	public void setOnWifiConnectListener(OnWifiConnectListener onWifiConnectListener) {
		this.onWifiConnectListener = onWifiConnectListener;
	}
	
}

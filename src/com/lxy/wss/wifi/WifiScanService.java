package com.lxy.wss.wifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.android.lib.util.LogUtil;


/**
 * Depiction:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年8月14日 下午2:11:02
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class WifiScanService {
	private Context            context;
	private WifiManager        wifiManager;
	private WifiReceiver       receiver;
	private OnWifiScanListener onWifiScanListener;
	
	public WifiScanService(Context context) {
		this.context = context;
	}
	
	public OnWifiScanListener getOnWifiScanListener() {
		return onWifiScanListener;
	}
	
	public void setOnWifiScanListener(OnWifiScanListener onWifiScanListener) {
		this.onWifiScanListener = onWifiScanListener;
	}
	
	public void startScan() {
		LogUtil.e(this, "start scan router");
		receiver = new WifiReceiver();
		context.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
		wifiManager.startScan();
		
	}
	
	class WifiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equalsIgnoreCase(intent.getAction())) {
				context.unregisterReceiver(receiver);
				List<ScanResult> retList = wifiManager.getScanResults();
				if (retList == null || retList.size() == 0) {
					if (onWifiScanListener != null) {
						onWifiScanListener.onWifiScanFinish(null, null);
					}
					return;
				}
				
				List<ScanResult> tempList = new ArrayList<ScanResult>();
				for (ScanResult scanResult : retList) {
					if (!WifiUtil.isSmartWifi(scanResult.SSID)) {
						tempList.add(scanResult);
					}
				}
				if (tempList.size() > 0) {
					for (ScanResult scanResult : tempList) {
						retList.remove(scanResult);
					}
					tempList.clear();
				}
				
				if (retList != null && retList.size() > 0) {
					ComparatorWifi comparator = new ComparatorWifi();
					Collections.sort(retList, comparator);
					if (onWifiScanListener != null) {
						onWifiScanListener.onWifiScanFinish(retList, retList.get(0));
					}
				} else {
					if (onWifiScanListener != null) {
						onWifiScanListener.onWifiScanFinish(null, null);
					}
				}
			}
		}
	}//end WifiReceiver
	
	class ComparatorWifi implements Comparator<Object> {
		@Override
		public int compare(Object lhs, Object rhs) {
			ScanResult s1 = (ScanResult) lhs;
			ScanResult s2 = (ScanResult) rhs;
			if (s1.level > s2.level) {
				return -1;
			} else if (s1.level < s2.level) {
				return 1;
			}
			return 0;
		}
	}//end ComparatorWifi
	
}

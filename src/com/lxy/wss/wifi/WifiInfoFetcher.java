package com.lxy.wss.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;


/**
 * Depiction: 获取wifi信息，比如当前ip，网管等
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年8月13日 上午10:57:39
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class WifiInfoFetcher {
	private static WifiInfoFetcher fetcher = null;
	private Context                context;
	private WifiManager            wifiManager;
	private WifiInfo               wifiInfo;
	private DhcpInfo               dhcpInfo;
	
	private WifiInfoFetcher(Context context) {
		this.context = context;
	}
	
	public static synchronized WifiInfoFetcher getInstance(Context context) {
		if (fetcher == null) {
			fetcher = new WifiInfoFetcher(context);
		}
		return fetcher;
	}
	
	/**
	 * 获取网关
	 * 
	 * @return String
	 */
	public String getGateWay() {
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		dhcpInfo = wifiManager.getDhcpInfo();
		return formatIP(dhcpInfo.gateway);
	}
	
	/**
	 * 获取Wi-Fi状态是否开启
	 */
	public boolean isWifiEnabled() {
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.isWifiEnabled();
	}
	
	/**
	 * 获取IP
	 * 
	 * @return String
	 */
	public String getIP() {
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		dhcpInfo = wifiManager.getDhcpInfo();
		return formatIP(dhcpInfo.ipAddress);
	}
	
	public String getSSID() {
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getSSID();
	}
	
	public String getMAC() {
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getMacAddress();
	}
	
	// IP地址转化为字符串格式  
	@SuppressWarnings ("deprecation")
	private static String formatIP(int ipAddress) {
		return Formatter.formatIpAddress(ipAddress);
	}
}

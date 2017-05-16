package com.lxy.wss.wifi;

/**
 * Depiction:自动连接wifi回调接口
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2013-10-28 下午8:48:41
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public interface OnWifiConnectListener {
	/**
	 * 连接wifi时回调此方法
	 */
	void onConnecting(String ssid);
	
	/**
	 * 连接成功时回调此方法
	 */
	void onConnectSuccess(String ssid);
	
	/**
	 * 连接失败时回调此方法
	 */
	void onConnectFailure(String ssid);
}

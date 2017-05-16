package com.lxy.wifistore.util;

import android.text.TextUtils;

import com.android.lib.util.LogUtil;
import com.lxy.wifistore.WifiApp;
import com.lxy.wifistore.util.NetworkState.NetworkType;
import com.lxy.wss.wifi.WifiInfoFetcher;
import com.lxy.wss.wifi.WifiUtil;


/**
 * 类描述：所有网络请求接口
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午5:41:53
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public final class HttpConfig {
	private static WifiApp     context          = WifiApp.getInstance();
	/** 接口正式地址 */
	public static final String HOST             = "http://gw.wifi-shhs.taotaojing.cn";
	/** 接口地址访问端口号 */
	public static final String PORT             = ":80";
	/** 主机地址后缀 */
	public static final String INC              = "/inc/";
	//-------------------------------------------------------------------------------------------
	/** 异常上报接口 */
	public static final String REPORT_EXCEPTION = "reportExceptionLog.action";
	
	HttpConfig() {
	}
	
	public static final String getBaseUrl() {
		String baseUrl = HOST + PORT;
		if (NetworkState.isAvailable(context) && NetworkState.getNetworkType(context) == NetworkType.WIFI) {
			String ssid = WifiInfoFetcher.getInstance(context).getSSID();
			LogUtil.i(new HttpConfig(), "wifi network, the ssid is --> " + ssid);
			if (WifiUtil.isSmartWifi(ssid)) {
				baseUrl = "http://" + WifiInfoFetcher.getInstance(context).getGateWay() + ":8080";
			}
		} else {
			LogUtil.i(new HttpConfig(), "mobile network");
		}
		return baseUrl + INC;
	}
	
	public static final String getLogUrl() {
		return HOST + PORT + "/inc/createMaketingLog.action";
	}
	
	public static final String getPostJsonLogUrl() {
		return HOST + PORT + "/inc/marketingLog.action";
	}
	
	public static final String getUpgradeUrl() {
		return HOST + PORT + "/inc/clientUpdate.action";
	}
	
	public static final String getFileUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return url;
		}
		String baseUrl = url;
		if(!url.startsWith("http://")){
			baseUrl = "http://" + WifiInfoFetcher.getInstance(context).getGateWay() + ":8080" + url;
		}
//		if (NetworkState.isAvailable(context) && NetworkState.getNetworkType(context) == NetworkType.WIFI) {
//			String ssid = WifiInfoFetcher.getInstance(context).getSSID();
//			if (WifiUtil.isSmartWifi(ssid)) {
//				baseUrl = "http://" + WifiInfoFetcher.getInstance(context).getGateWay() + ":8080" + url;
//			}
//		}
		return baseUrl;
	}
	
}

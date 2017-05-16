package com.lxy.wss.wifi;

import com.android.lib.util.Preferences;
import com.lxy.wifistore.WifiApp;

import android.text.TextUtils;


/**
 * Depiction:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年8月14日 下午2:14:18
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class WifiUtil {
	private static WifiApp context = WifiApp.getInstance();
	
	public WifiUtil() {
	}
	
	public static final boolean isSmartWifi(String ssid) {
		if (TextUtils.isEmpty(ssid)) {
			return false;
		}
		String sid = ssid.replaceAll("\"", "");
		return sid.contains("乐语免费WIFI") || sid.contains("快推免费WIFI");//乐语免费WIFI或者快推应用
	}
	
	//	public final static void clear() {
	//		saveName("");
	//		saveId("");
	//		saveMac("");
	//		saveShopName("");
	//		saveChannel("");
	//		saveSalesMode("");
	//	}
	
	public final static boolean isBind() {
		boolean flag1 = !TextUtils.isEmpty(getShopName());
		boolean flag2 = !TextUtils.isEmpty(getChannel());
		boolean flag3 = !TextUtils.isEmpty(getId());
		boolean flag4 = !TextUtils.isEmpty(getMac());
		boolean flag5 = !TextUtils.isEmpty(getName());
		return flag1 && flag2 && flag3 && flag4 && flag5;
	}
	
	public static final void saveName(String name) {
		Preferences.getPrefer(context).putString("name", name);
	}
	
	public static final String getName() {
		return Preferences.getPrefer(context).getString("name", "");
	}
	
	public static final void saveId(String id) {
		Preferences.getPrefer(context).putString("id", id);
	}
	
	public static final String getId() {
		return Preferences.getPrefer(context).getString("id", "");
	}
	
	public static final void saveMac(String mac) {
		Preferences.getPrefer(context).putString("mac", mac);
	}
	
	public static final String getMac() {
		return Preferences.getPrefer(context).getString("mac", "");
	}
	
	public static final void saveShopName(String shopName) {
		Preferences.getPrefer(context).putString("shopName", shopName);
	}
	
	public static final String getShopName() {
		return Preferences.getPrefer(context).getString("shopName", "");
	}
	
	public static final void saveChannel(String channel) {
		Preferences.getPrefer(context).putString("channel", channel);
	}
	
	public static final String getChannel() {
		return Preferences.getPrefer(context).getString("channel", "");
	}
	
	public static final void saveSalesMode(String salesMode) {
		Preferences.getPrefer(context).putString("salesMode", salesMode);
	}
	
	public static final String getSalesMode() {
		return Preferences.getPrefer(context).getString("salesMode", "");
	}
	
	public static final void setSaleSuccess(boolean flag) {
		Preferences.getPrefer(context).putBoolean("is_setted", flag);
	}
	
	public static final boolean isSettedSale() {
		return Preferences.getPrefer(context).getBoolean("is_setted", false);
	}
}

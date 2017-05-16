package com.lxy.wss.wifi;

import android.content.Context;
import android.text.TextUtils;

import com.android.lib.data.JsonMap;
import com.android.lib.http.HttpCallback;
import com.android.lib.http.HttpRequest;


/**
 * Depiction:检查该路由器是否已经被促销员绑定过
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年8月16日 上午9:37:00
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public abstract class BindService implements HttpCallback {
	private Context context;
	
	public BindService(Context context) {
		this.context = context;
	}
	
	public void start() {
		String bindUrl = "http://" + WifiInfoFetcher.getInstance(context).getGateWay() + ":8080/inc/bind.action";
		HttpRequest http = new HttpRequest(bindUrl, null);
		http.setDebug(false);
		http.setHttpCallback(this);
		http.start();
	}
	
	@Override
	public void onStart(String tag) {
	}
	
	@Override
	public void onFinish(String result, String tag) {
		JsonMap data = JsonMap.parseJson(result);
		if (data != null) {
			String salesName = data.getString("salesName");
			String salesId = data.getString("salesId");
			String mac = data.getString("mac");
			String shopName = data.getString("salesShop");
			String salesChannel = data.getString("salesChannel");
			String salesMode = data.getString("salesMode");
			
			if (!TextUtils.isEmpty(salesName))
				WifiUtil.saveName(salesName);
			if (!TextUtils.isEmpty(salesId))
				WifiUtil.saveId(salesId);
			if (!TextUtils.isEmpty(mac))
				WifiUtil.saveMac(mac);
			if (!TextUtils.isEmpty(shopName))
				WifiUtil.saveShopName(shopName);
			if (!TextUtils.isEmpty(salesChannel))
				WifiUtil.saveChannel(salesChannel);
			if (!TextUtils.isEmpty(salesMode))
				WifiUtil.saveSalesMode(salesMode);
		}
		
		onBindFinish(WifiUtil.isBind());
	}
	
	public abstract void onBindFinish(boolean isBindSuccess);
}

package com.lxy.wss.wifi;

import java.util.Map;

import com.android.lib.data.JsonMap;
import com.android.lib.http.HttpCallback;
import com.android.lib.http.HttpRequest;


/**
 * Depiction:异步获取数据O
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年10月14日 下午12:20:03
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public abstract class FetchDataTask implements HttpCallback {
	private Map<String, String> params = null;
	private HttpRequest         http;
	
	public FetchDataTask(Map<String, String> params) {
		this.params = params;
	}
	
	public void perform(String url, boolean isDebug) {
		http = new HttpRequest(url, params);
		http.setDebug(isDebug);
		http.setHttpCallback(this);
		http.start();
	}
	
	@Override
	public void onFinish(String arg0, String arg1) {
		onEndTask(JsonMap.parseJson(arg0), arg0);
	}
	
	@Override
	public void onStart(String arg0) {
		onStartTask();
	}
	
	public abstract void onStartTask();
	
	public abstract void onEndTask(JsonMap data, String json);
}

package com.lxy.wss.wifi;

import android.text.TextUtils;

import com.android.lib.http.HttpCallback;
import com.android.lib.http.HttpRequest;
import com.android.lib.util.LogUtil;


/**
 * Depiction:模拟用户点击下载进而打通wifi可以使用
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年8月15日 下午1:35:13
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class WifiBribeService implements HttpCallback {
	final String                       START_TAG = "id=\"get-online\" href=\"";
	final String                       END_TAG   = "\">get online";
	final String                       url       = "http://www.sina.com.cn";
	private HttpRequest                http;
	private OnWifiBribeServiceListener onWifiBribeServiceListener;
	
	public WifiBribeService() {
	}
	
	public void start() {
		http = new HttpRequest(url, null);
		http.setDebug(false);
		http.setHttpCallback(this);
		http.start();
	}
	
	@Override
	public void onFinish(String result, String tag) {
		if (!tag.equals("open")) {
			//重定向路由默认页面 
			if (!TextUtils.isEmpty(result)) {
				int start = result.indexOf(START_TAG);
				int end = result.indexOf(END_TAG);
				
				if (start > -1 && end > -1) {
					String url = result.substring(start + START_TAG.length(), end);
					LogUtil.e(this, "url-->"+url);
					http = new HttpRequest("open", url, null);
					http.setDebug(false);
					http.setHttpCallback(this);
					http.start();
				} else {
					if (onWifiBribeServiceListener != null) {
						onWifiBribeServiceListener.onWifiBribeFinish(true);
					}
				}
			} else {
				if (onWifiBribeServiceListener != null) {
					onWifiBribeServiceListener.onWifiBribeFinish(true);
				}
			}
		} else {
			if (onWifiBribeServiceListener != null) {
				onWifiBribeServiceListener.onWifiBribeFinish(true);
			}
		}
	}
	
	@Override
	public void onStart(String tag) {
	}
	
	/**
	 * @return the onWifiBribeServiceListener
	 */
	public OnWifiBribeServiceListener getOnWifiBribeServiceListener() {
		return onWifiBribeServiceListener;
	}
	
	/**
	 * @param onWifiBribeServiceListener
	 *            the onWifiBribeServiceListener to set
	 */
	public void setOnWifiBribeServiceListener(OnWifiBribeServiceListener onWifiBribeServiceListener) {
		this.onWifiBribeServiceListener = onWifiBribeServiceListener;
	}
	
}

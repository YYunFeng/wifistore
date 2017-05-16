package com.lxy.pad.upgrade;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.WindowManager;

import com.android.lib.data.JsonMap;
import com.android.lib.http.HttpCallback;
import com.android.lib.http.HttpRequest;
import com.android.lib.util.ToastUtil;
import com.lxy.wifistore.R;
import com.lxy.wifistore.util.HttpConfig;


/**
 * Depiction: 查询客户端更新
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年6月18日 下午5:19:31
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class UpgradeTask implements HttpCallback {
	public static boolean      isRunning;
	private static UpgradeTask instance = null;
	private Context            context;
	private AlertDialog        dialog;
	private boolean            isShowToast;
	
	public synchronized static UpgradeTask getInstance(Context context) {
		if (instance == null) {
			instance = new UpgradeTask(context);
		}
		return instance;
	}
	
	private UpgradeTask(Context context) {
		this.context = context;
	}
	
	public synchronized void checkUpgrade(boolean isShowToast) {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
			isRunning = false;
		}
		
		if (isRunning && isShowToast) {
			ToastUtil.showToast(context, R.string.upgrade_loading);
		}
		//查询更新
		isRunning = true;
		this.isShowToast = isShowToast;
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("clientType", String.valueOf(3));
		params.put("versionCode", String.valueOf(getVersionCode()));
		HttpRequest http = new HttpRequest(HttpConfig.getUpgradeUrl(), params);
		http.setHttpCallback(this);
		http.setDebug(false);
		http.start();
	}
	
	public synchronized int getVersionCode() {
		int versionCode = 0;
		try {
			versionCode = this.context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
		}
		
		return versionCode;
	}
	
	@Override
	public void onStart(String taskId) {
		if (isShowToast) {
			ToastUtil.showToast(context, R.string.upgrade_loading);
		}
	}
	
	@Override
	public void onFinish(String response, String taskId) {
		ToastUtil.cancel();
		JsonMap data = JsonMap.parseJson(response);
		if (data != null && data.getMap("resultObj").getInt("resultCode") == 0) {
			JsonMap map = data.getMap("clientVersionObj");
			final boolean isNeedUpdate = map.getBoolean("needUpdate");
			final String intro = map.getString("intro");
			final String url = map.getString("packageUrl");
			final String versionName = map.getString("version");
			
			if (isNeedUpdate) {
				if (dialog != null && dialog.isShowing()) {
					return;
				}
				
				dialog = new AlertDialog.Builder(context).create();
				dialog.setCancelable(false);
				dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				dialog.setTitle(R.string.upgrade_intro);
				dialog.setMessage(intro + "\n\n" + context.getString(R.string.version_code) + versionName);
				
				dialog.setButton(Dialog.BUTTON_NEGATIVE, context.getString(R.string.upgrade), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						dialog = null;
						new DownloadUpgrade(context, url).start();
					}
				});
				
				dialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						dialog = null;
						isRunning = false;
					}
				});
				dialog.show();
			} else {
				if (isShowToast) {
					ToastUtil.showToast(context, R.string.client_no_update);
				}
			}
		} else {
			if (isShowToast) {
				ToastUtil.showToast(context, R.string.loading_fail);
			}
		}
	}
}

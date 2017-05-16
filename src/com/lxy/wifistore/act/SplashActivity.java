package com.lxy.wifistore.act;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.util.LogUtil;
import com.android.lib.util.ToastUtil;
import com.lxy.wifistore.R;
import com.lxy.wifistore.bean.AppEntity;
import com.lxy.wifistore.util.NetworkState;
import com.lxy.wss.wifi.BindService;
import com.lxy.wss.wifi.OnWifiBribeServiceListener;
import com.lxy.wss.wifi.OnWifiConnectListener;
import com.lxy.wss.wifi.OnWifiScanListener;
import com.lxy.wss.wifi.WifiBribeService;
import com.lxy.wss.wifi.WifiConnect;
import com.lxy.wss.wifi.WifiInfoFetcher;
import com.lxy.wss.wifi.WifiScanService;
import com.lxy.wss.wifi.WifiUtil;


/**
 * Depiction: 闪屏页
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年7月21日 下午3:56:05
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class SplashActivity extends BaseActivity implements OnWifiScanListener, OnWifiConnectListener, OnWifiBribeServiceListener {
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setCancelable(false);
		setContentView(R.layout.activity_splash);
		TextView version_tv = (TextView) findViewById(R.id.version_tv);
		version_tv.setText(getVersionCode());
		
		if (WifiInfoFetcher.getInstance(getApplicationContext()).isWifiEnabled()) {
			showLoadingDialog(R.string.binding_router);
			WifiScanService wifiService = new WifiScanService(this);
			wifiService.setOnWifiScanListener(this);
			wifiService.startScan();
		} else {
			enterMain();
		}
	}
	
	private AppEntity startFromWeb() {
		Intent intent = getIntent();
		String data = intent.getDataString();
		if (!TextUtils.isEmpty(data)) {
			//web start
			if (!NetworkState.isAvailable(this)) {
				ToastUtil.showToast(this, R.string.network_error_tip);
				return null;
			}
			
			Uri uri = getIntent().getData();
			int appid = 0;
			try {
				appid = Integer.parseInt(uri.getQueryParameter("appId"));
			} catch (Exception e) {
				return null;
			}
			String appname = uri.getQueryParameter("appName");
			String pckName = uri.getQueryParameter("pckName");
			String downPath = uri.getQueryParameter("downPath");
			AppEntity app = new AppEntity();
			app.id = appid;
			app.name = appname;
			app.pckName = pckName;
			app.downPath = downPath;
			return app;
		} else {
			LogUtil.DEBUG = true;
			LogUtil.e(this, "not html start app");
		}
		return null;
	}
	
	private String getVersionCode() {
		String versionCode = "1.0";
		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		
		return "Ver " + versionCode;
	}
	
	@Override
	public void onWifiScanFinish(List<ScanResult> retList, ScanResult strongRet) {
		//扫描结束
		if (strongRet != null) {
			//连接wifi
			WifiConnect con = new WifiConnect(this, strongRet.SSID, null);
			con.setOnWifiConnectListener(this);
			con.start();
		} else {
			//没有发现，直接进入主界面
			enterMain();
		}
	}
	
	@Override
	public void onConnecting(String ssid) {
	}
	
	@Override
	public void onConnectSuccess(String ssid) {
		LogUtil.e(this, "connect wifi success");
		//当前已经连接到本公司wifi，绑定设备
		if (!WifiUtil.isBind()) {
			new BindService(this) {
				@Override
				public void onBindFinish(boolean isBindSuccess) {
					bribeWifi(isBindSuccess);
				}
			}.start();
		} else {
			bribeWifi(true);
		}
	}
	
	@Override
	public void onConnectFailure(String ssid) {
		LogUtil.e(this, "connect wifi fail");
		showToast(R.string.bind_fail);
		enterMain();
	}
	
	private void bribeWifi(boolean isBindSuccess) {
		//打通wifi以便使用免费网络
		if (isBindSuccess) {
			WifiBribeService bribe = new WifiBribeService();
			bribe.setOnWifiBribeServiceListener(this);
			bribe.start();
		} else {
			cancelLoadingDialog();
			showToast(R.string.bind_fail);
			enterMain();
		}
	}
	
	@Override
	public void onWifiBribeFinish(boolean ret) {
		showToast(ret ? R.string.bind_ok : R.string.bind_fail);
		enterMain();
	}
	
	private void enterMain() {
		cancelLoadingDialog();
		post(new Runnable() {
			@Override
			public void run() {
				AppEntity app = startFromWeb();
				Bundle bundle = new Bundle();
				bundle.putSerializable("web_data", app);
				openActivity(MainActivity.class, bundle);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
			}
		}, 500);
	}
	
}

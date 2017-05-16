package com.lxy.wifistore.act;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.JsonMap;
import com.android.lib.util.LogUtil;
import com.lxy.pad.download.DownUtil;
import com.lxy.pad.download.DownloadDao;
import com.lxy.pad.upgrade.UpgradeTask;
import com.lxy.wifistore.R;
import com.lxy.wifistore.bean.AppEntity;
import com.lxy.wifistore.receiver.DownReceiver;
import com.lxy.wifistore.receiver.PPadService;
import com.lxy.wifistore.util.HttpConfig;
import com.lxy.wifistore.util.NetworkState;
import com.lxy.wifistore.util.NetworkState.NetworkType;
import com.lxy.wifistore.util.PadUtils;
import com.lxy.wifistore.view.BaseView;
import com.lxy.wifistore.view.BoutiqueView;
import com.lxy.wifistore.view.SalesPopMenu;
import com.lxy.wifistore.view.SalesPopMenu.OnPopMenuItemClickListener;
import com.lxy.wifistore.view.TopicView;
import com.lxy.wss.wifi.FetchDataTask;
import com.lxy.wss.wifi.WifiInfoFetcher;
import com.lxy.wss.wifi.WifiUtil;


/**
 * Depiction: 主页
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
public class MainActivity extends BaseActivity implements OnCheckedChangeListener, OnPopMenuItemClickListener {
	private RadioGroup   tabBar;
	private BaseView[]   fragments;
	private DownReceiver downReceiver;
	private boolean      isSmartWifi;
	private AlertDialog  dialog;
	private FrameLayout  contentLayout;
	private ImageButton  settingButton;
	private SalesPopMenu pop;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		PadUtils.clearDir(DownUtil.APP_DIR);
		DownloadDao.getDao().initStatus();
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancelAll();
		
		setCancelable(false);
		setContentView(R.layout.activity_main);
		settingButton = (ImageButton) findViewById(R.id.main_set_btn);
		
		contentLayout = (FrameLayout) findViewById(R.id.content_layout);
		tabBar = (RadioGroup) findViewById(R.id.radio_group);
		tabBar.setOnCheckedChangeListener(this);
		
		fragments = new BaseView[2];
		fragments[0] = new TopicView(this);
		fragments[1] = new BoutiqueView(this);
		
		changeView(0);
		
		PPadService.getInstance(getApplicationContext()).start();
		UpgradeTask.getInstance(this).checkUpgrade(false);
		
		downReceiver = new DownReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.platomix.appstore.download.ADD");
		filter.addAction("com.platomix.appstore.download.UPDATE");
		filter.addAction("com.platomix.appstore.download.FAIL");
		filter.addAction("com.platomix.appstore.download.CANCEL");
		filter.addAction("com.platomix.appstore.download.SUCCESS");
		filter.addAction("com.platomix.appstore.download.ALL_FINISH");
		filter.addAction("com.notifications.intent.action.BUTTON_CANCEL");
		filter.addAction("com.notifications.intent.action.BUTTON_RETRY");
		registerReceiver(downReceiver, filter);
		
		networkReceiver = new NetworkReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
		intentFilter.addAction(DownReceiver.ACTION_DOWNLOAD_STATE_CHANGE);
		registerReceiver(networkReceiver, intentFilter);
		
		isSmartWifi = isOurWifi();
		
		final AppEntity app = (AppEntity) getIntent().getExtras().getSerializable("web_data");
		if (app != null) {
			//from html
			post(new Runnable() {
				@Override
				public void run() {
					PadUtils.download(app);
				}
			}, 1000);
		}
	}
	
	public void onSaleModeAction(View v) {
		if (pop != null) {
			pop.dismiss();
		}
		setDebug(false);
		
		String url = HttpConfig.getBaseUrl() + "sales.action?action=get";
		new FetchDataTask(null) {
			@Override
			public void onStartTask() {
				showLoadingDialog(R.string.loading);
			}
			
			@Override
			public void onEndTask(JsonMap data, String json) {
				cancelLoadingDialog();
				if (data != null) {
					List<JsonMap> sales = data.getListMap("sales");
					if (sales != null && sales.size() > 0) {
						pop = new SalesPopMenu(MainActivity.this, sales);
						pop.setOnPopMenuItemClickListener(MainActivity.this);
						pop.showAsDropDown(settingButton, -settingButton.getWidth(), 0);
					} else {
						//empty data
						showToast(R.string.no_data_tip);
					}
				} else {
					//request fail
					showToast(R.string.loading_fail);
				}
			}
		}.perform(url, false);
	}
	
	@Override
	public void onPopMenuItem(int index, JsonMap data) {
		WifiUtil.setSaleSuccess(true);
		
		int vis = WifiUtil.isSettedSale() ? View.INVISIBLE : View.VISIBLE;
		settingButton.setVisibility(vis);
		
		String salesName = data.getString("salesName");
		String salesId = data.getString("salesId");
		String mac = data.getString("mac");
		String shopName = data.getString("salesShop");
		String salesChannel = data.getString("salesChannel");
		
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
		
		showToast(R.string.bind_sale_success);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		boolean multi = !WifiUtil.getSalesMode().equalsIgnoreCase("single");
		if (multi) {
			settingButton.setVisibility(WifiUtil.isSettedSale() ? View.INVISIBLE : View.VISIBLE);
			LogUtil.i(this, "multi mode");
		} else {
			settingButton.setVisibility(View.INVISIBLE);
			LogUtil.i(this, "single mode");
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(downReceiver);
		unregisterReceiver(networkReceiver);
		PadUtils.clearDir(DownUtil.APP_DIR);
		DownloadDao.getDao().initStatus();
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancelAll();
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.radio_boutique:
				changeView(0);
				break;
			default:
				changeView(1);
				break;
		}
	}
	
	private void changeView(int index) {
		contentLayout.removeAllViews();
		contentLayout.addView(fragments[index]);
		fragments[index].initData();
	}
	
	public void onSearchAction(View v) {
		openActivity(SearchActivity.class, null);
		overridePendingTransition(R.anim.from_right_to_left, R.anim.from_left_to_right);
	}
	
	private boolean isPressed = false;
	private long    clickTime;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (pop != null && pop.isShowing()) {
				pop.dismiss();
				return true;
			}
			if (!isPressed) {
				isPressed = true;
				showToast(R.string.double_click_quit);
				clickTime = System.currentTimeMillis();
				return true;
			} else {
				long time = System.currentTimeMillis();
				long duration = time - clickTime;
				if (duration <= 1500) {
					finish();
				} else {
					isPressed = true;
					showToast(R.string.double_click_quit);
					clickTime = System.currentTimeMillis();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private NetworkReceiver networkReceiver;
	
	class NetworkReceiver extends BroadcastReceiver {
		private static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
		private static final String ACTION_WIFI_STATE_CHANGED  = "android.net.wifi.WIFI_STATE_CHANGED";
		
		public NetworkReceiver() {
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DownReceiver.ACTION_DOWNLOAD_STATE_CHANGE)) {
				((BoutiqueView) fragments[1]).refresh();
			} else if (intent.getAction().equals(ACTION_CONNECTIVITY_CHANGE) || intent.getAction().equals(ACTION_WIFI_STATE_CHANGED)) {
				post(new Runnable() {
					@Override
					public void run() {
						isNeedRestart();
					}
				}, 100);
			}
		}
	}//end NetworkReceiver
	
	private void isNeedRestart() {
		LogUtil.i(this, "old network type is our wifi ? -->" + isSmartWifi);
		LogUtil.i(this, "new network type is our wifi ? -->" + isOurWifi());
		boolean isNeedRestart = false;
		if (isSmartWifi) {
			if (!isOurWifi()) {
				isNeedRestart = true;
			}
		} else {
			if (isOurWifi()) {
				isNeedRestart = true;
			}
		}
		
		isSmartWifi = isOurWifi();
		
		if (isNeedRestart) {
			if (dialog != null) {
				return;
			}
			dialog = new AlertDialog.Builder(getApplicationContext()).create();
			dialog.setCancelable(false);
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.setMessage(getString(R.string.network_change_tip));
			
			dialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.quit), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					System.exit(0);
				}
			});
			
			dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.restart), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					PadUtils.reStart(getApplicationContext());
				}
			});
			dialog.show();
		}
	}
	
	private boolean isOurWifi() {
		NetworkType type = NetworkState.getNetworkType(getApplicationContext());
		boolean isOurWifi = WifiUtil.isSmartWifi(WifiInfoFetcher.getInstance(getApplicationContext()).getSSID());
		return type == NetworkType.WIFI && isOurWifi;
	}
}

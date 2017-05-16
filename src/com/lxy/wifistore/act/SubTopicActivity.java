package com.lxy.wifistore.act;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lxy.pad.download.DownloadConstant;
import com.lxy.wifistore.R;
import com.lxy.wifistore.bean.AppEntity;
import com.lxy.wifistore.receiver.DownReceiver;
import com.lxy.wifistore.view.BoutiqueView;


/**
 * Depiction: 专题详情界面
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
public class SubTopicActivity extends BaseActivity {
	private FrameLayout  contentLayout;
	private BoutiqueView fragment;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setCancelable(false);
		setContentView(R.layout.activity_subtopic);
		TextView titleTv = (TextView) findViewById(R.id.title_tv);
		titleTv.setText(getIntent().getExtras().getString("title"));
		
		fragment = new BoutiqueView(this);
		fragment.setFromTopic(true);
		String appDatas = getIntent().getExtras().getString("applist");
		ArrayList<AppEntity> appList = parseArray(appDatas);
		fragment.setAppList(appList);
		
		contentLayout = (FrameLayout) findViewById(R.id.content_layout);
		contentLayout.addView(fragment);
		fragment.initData();
	}
	
	private ArrayList<AppEntity> parseArray(String data) {
		ArrayList<AppEntity> listMap = new ArrayList<AppEntity>();
		try {
			Type listType = new TypeToken<ArrayList<AppEntity>>() {}.getType();
			Gson gson = new Gson();
			listMap = gson.fromJson(data, listType);
		} catch (Exception e) {
		}
		return listMap;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.from_right_to_left, R.anim.from_left_to_right);
	}
	
	public void onBackAction(View v) {
		onBackPressed();
	}
	
	public void onResume() {
		super.onResume();
		receiver = new DownloadReceiver();
		IntentFilter filter = new IntentFilter(DownReceiver.ACTION_DOWNLOAD_STATE_CHANGE);
		filter.addAction(DownloadConstant.DOWNLOAD_CANCEL_ACTION);
		registerReceiver(receiver, filter);
	}
	
	public void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
			LogUtil.e(this, e.toString());
		}
	}
	
	private DownloadReceiver receiver;
	
	class DownloadReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DownReceiver.ACTION_DOWNLOAD_STATE_CHANGE)) {
				fragment.refresh();
			}
		}
	}//end DownReceiver
}

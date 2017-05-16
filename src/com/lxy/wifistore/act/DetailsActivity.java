package com.lxy.wifistore.act;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.http.NetworkState;
import com.android.lib.util.HtmlText;
import com.android.lib.util.JsonUtil;
import com.android.lib.util.ToastUtil;
import com.lxy.pad.download.DownloadDao;
import com.lxy.pad.download.DownloadInfo;
import com.lxy.pad.download.DownloadStatus;
import com.lxy.wifistore.R;
import com.lxy.wifistore.bean.AppEntity;
import com.lxy.wifistore.bean.DetailEntity;
import com.lxy.wifistore.receiver.DownReceiver;
import com.lxy.wifistore.util.HttpConfig;
import com.lxy.wifistore.util.PadUtils;


/**
 * Depiction: 详情
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
public class DetailsActivity extends BaseActivity {
	private ImageView    appIcon;
	private TextView     appSize;
	private TextView     appName;
	private TextView     appintro;
	private LinearLayout snapLayout;
	private Button       installBtn;
	
	private DetailEntity detailInfo;
	private String       downPath;
	private int          appId;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setCancelable(false);
		setContentView(R.layout.activity_detail);
		TextView titleTv = (TextView) findViewById(R.id.title_tv);
		titleTv.setText(getIntent().getExtras().getString("title"));
		
		appIcon = (ImageView) findViewById(R.id.app_iv);
		appSize = (TextView) findViewById(R.id.app_size_and_company);
		appName = (TextView) findViewById(R.id.app_name_tv);
		appintro = (TextView) findViewById(R.id.app_describe);
		snapLayout = (LinearLayout) findViewById(R.id.snap_layout);
		installBtn = (Button) findViewById(R.id.app_install_btn);
		
		receiver = new DownloadReceiver();
		IntentFilter filter = new IntentFilter(DownReceiver.ACTION_DOWNLOAD_STATE_CHANGE);
		registerReceiver(receiver, filter);
		
		appId = getIntent().getExtras().getInt("id");
		put("padChannelId", "139");
		put("clientType", "1");
		put("appId", String.valueOf(appId));
		request(HttpConfig.getBaseUrl() + "appDetails.action");
		
		refreshButton(appId);
	}
	
	private void refreshButton(int id) {
		DownloadInfo info = DownloadDao.getDao().select(id);
		if (info != null && PadUtils.isDownloaded(info.id)) {
			installBtn.setBackgroundResource(R.drawable.orange_border_btn);
			installBtn.setTextColor(getResources().getColor(R.color.orange));
			installBtn.setText(R.string.install);
			installBtn.setEnabled(true);
		} else if (info != null && (info.status == DownloadStatus.GOING || info.status == DownloadStatus.WAIT)) {
			installBtn.setBackgroundResource(R.drawable.blue_border_btn);
			installBtn.setTextColor(getResources().getColor(R.color.leyu_yellow));
			installBtn.setText(R.string.downloading);
			installBtn.setEnabled(false);
		} else {
			installBtn.setBackgroundResource(R.drawable.orange_border_btn);
			installBtn.setTextColor(getResources().getColor(R.color.orange));
			installBtn.setText(R.string.install);
			installBtn.setEnabled(true);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.from_right_to_left, R.anim.from_left_to_right);
	}
	
	public void onBackAction(View v) {
		onBackPressed();
	}
	
	public void onInsallAction(View v) {
		if (detailInfo == null || detailInfo.baseInfo == null) {
			return;
		}
		AppEntity app = new AppEntity();
		app.icon = detailInfo.baseInfo.icon;
		app.id = detailInfo.baseInfo.appId;
		app.name = detailInfo.baseInfo.name;
		app.downPath = detailInfo.baseInfo.downPath;
		app.pckName = detailInfo.baseInfo.pckName;
		
		if (!NetworkState.isAvailable(v.getContext())) {
			ToastUtil.showToast(v.getContext(), R.string.network_error_tip);
			return;
		}
		app.downPath = HttpConfig.getFileUrl(app.downPath);
		PadUtils.download(app);
		
		refreshButton(app.id);
	}
	
	public void onShareAction(View v) {
		String title = getIntent().getExtras().getString("title");
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + "这是一款很有趣的手机软件，下载地址：" + downPath);
		startActivity(Intent.createChooser(intent, getString(R.string.select_share_title)));
	}
	
	@Override
	public void onFinish(String response, String taskId) {
		super.onFinish(response, taskId);
		detailInfo = JsonUtil.parse(response, DetailEntity.class);
		if (detailInfo == null || detailInfo.baseInfo == null) {
			showToast(R.string.network_error_tip);
			return;
		}
		downPath = detailInfo.baseInfo.downPath;
		
		int appId = getIntent().getExtras().getInt("id");
		refreshButton(appId);
		
		appName.setText(detailInfo.baseInfo.name);
		PadUtils.getFetcher().loadImage(HttpConfig.getFileUrl(detailInfo.baseInfo.icon), appIcon);
		appSize.setText(detailInfo.baseInfo.size + " | " + detailInfo.baseInfo.cpName);
		appintro.setText(HtmlText.htmlEscape(detailInfo.baseInfo.summary));
		
		List<String> snaps = new ArrayList<String>();
		for (int i = 0, size = detailInfo.resourceInfos != null ? detailInfo.resourceInfos.size() : 0; i < size; i++) {
			snaps.add(detailInfo.resourceInfos.get(i).url);
		}
		
		handleScreenShot(snaps);
	}
	
	// 处理软件的截图显示
	private void handleScreenShot(List<String> snaps) {
		for (String url : snaps) {
			View view = (View) LayoutInflater.from(this).inflate(R.layout.activity_details_snap_item, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.snap_iv);
			snapLayout.addView(view);
			PadUtils.getFetcher().loadImage(HttpConfig.getFileUrl(url), imageView);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	private DownloadReceiver receiver;
	
	class DownloadReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DownReceiver.ACTION_DOWNLOAD_STATE_CHANGE)) {
				refreshButton(appId);
			}
		}
	}//end DownReceiver
}

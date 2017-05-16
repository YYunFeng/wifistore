package com.lxy.wifistore.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.lib.http.NetworkState;
import com.android.lib.util.HtmlText;
import com.android.lib.util.ToastUtil;
import com.lxy.pad.download.DownloadDao;
import com.lxy.pad.download.DownloadInfo;
import com.lxy.pad.download.DownloadStatus;
import com.lxy.wifistore.R;
import com.lxy.wifistore.bean.AppEntity;
import com.lxy.wifistore.util.HttpConfig;
import com.lxy.wifistore.util.PadUtils;


/**
 * Depiction: 应用列表适配器
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年7月22日 下午3:15:45
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class AppItemAdapter extends BaseAdapter implements OnClickListener {
	private List<AppEntity>     appList;
	private Map<String, Button> buttons;
	
	public AppItemAdapter() {
		appList = new ArrayList<AppEntity>();
		buttons = new HashMap<String, Button>();
	}
	
	public void addDatas(List<AppEntity> appList) {
		if (appList != null) {
			this.appList.addAll(appList);
		}
		notifyDataSetChanged();
	}
	
	public void refreshButton(String appId) {
		if(buttons.containsKey(appId)){
//			buttons.get(appId).setText(buttons.get(appId).getResources().getString(R.string.install));
		}
	}
	
	@Override
	public int getCount() {
		return appList != null ? appList.size() : 0;
	}
	
	@Override
	public AppEntity getItem(int position) {
		return appList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return appList.get(position).id;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_boutique_adapter, null);
			holder.appIcon = (ImageView) convertView.findViewById(R.id.app_iv);
			holder.appName = (TextView) convertView.findViewById(R.id.app_name_tv);
			holder.comName = (TextView) convertView.findViewById(R.id.app_company_tv);
			holder.appButton = (Button) convertView.findViewById(R.id.app_install_btn);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.appName.setText(getItem(position).name);
		holder.comName.setText(HtmlText.htmlEscape(getItem(position).summary));
		PadUtils.getFetcher().setImageSize(300);
		PadUtils.getFetcher().loadImage(HttpConfig.getFileUrl(getItem(position).icon), holder.appIcon);
		holder.appButton.setTag(getItem(position));
		holder.appButton.setOnClickListener(this);
		buttons.put(String.valueOf(getItem(position).id), holder.appButton);
		
		DownloadInfo info = DownloadDao.getDao().select(getItem(position).id);
		if (PadUtils.isDownloaded(getItem(position).id)) {
			holder.appButton.setBackgroundResource(R.drawable.orange_border_btn);
			holder.appButton.setTextColor(parent.getContext().getResources().getColor(R.color.orange));
			holder.appButton.setText(R.string.install);
		} else if (info != null && (info.status == DownloadStatus.GOING || info.status == DownloadStatus.WAIT)) {
			holder.appButton.setBackgroundResource(R.drawable.blue_border_btn);
			holder.appButton.setTextColor(parent.getContext().getResources().getColor(R.color.leyu_yellow));
			holder.appButton.setText(R.string.downloading);
		} else {
			holder.appButton.setBackgroundResource(R.drawable.orange_border_btn);
			holder.appButton.setTextColor(parent.getContext().getResources().getColor(R.color.orange));
			holder.appButton.setText(R.string.install);
		}
		
		return convertView;
	}
	
	static class Holder {
		ImageView appIcon;
		TextView  appName;
		TextView  comName;
		Button    appButton;
	}
	
	@Override
	public void onClick(View v) {
		Button btn = (Button) v;
		String text = btn.getText().toString();
		if (text != null && text.equals(v.getResources().getString(R.string.downloading))) {
			return;
		}
		AppEntity app = (AppEntity) v.getTag();
		if (!NetworkState.isAvailable(v.getContext())) {
			ToastUtil.showToast(v.getContext(), R.string.network_error_tip);
			return;
		}
		app.downPath = HttpConfig.getFileUrl(app.downPath);
		PadUtils.download(app);
		
		notifyDataSetChanged();
	}
}

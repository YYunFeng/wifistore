package com.lxy.wifistore.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.lib.http.NetworkState;
import com.android.lib.util.ToastUtil;
import com.lxy.wifistore.R;
import com.lxy.wifistore.bean.AppEntity;
import com.lxy.wifistore.bean.TopicEntity.TopicInfo;
import com.lxy.wifistore.util.HttpConfig;
import com.lxy.wifistore.util.PadUtils;


/**
 * Depiction: 专题列表适配器
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
public class TopicItemAdapter extends BaseAdapter {
	private List<TopicInfo> datas;
	
	public TopicItemAdapter(List<TopicInfo> datas) {
		this.datas = datas;
	}
	
	@Override
	public int getCount() {
		return datas != null ? datas.size() : 0;
	}
	
	@Override
	public TopicInfo getItem(int position) {
		return datas.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).id;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_topic_adapter, null);
			holder.topicIcon = (ImageView) convertView.findViewById(R.id.topic_iv);
			holder.topicName = (TextView) convertView.findViewById(R.id.topic_name_tv);
			holder.onkeyBtn = (Button) convertView.findViewById(R.id.one_install_btn);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		TopicInfo data = getItem(position);
		holder.topicName.setText(data.name);
		PadUtils.getFetcher().setImageSize(300);
		PadUtils.getFetcher().loadImage(HttpConfig.getFileUrl(data.icon), holder.topicIcon);
		
		holder.onkeyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!NetworkState.isAvailable(v.getContext())) {
					ToastUtil.showToast(v.getContext(), R.string.network_error_tip);
					return;
				}
				
				TopicInfo topic = getItem(position);
				if (topic.appInfos.size() > 0) {
					for (AppEntity app : topic.appInfos) {
						app.downPath = HttpConfig.getFileUrl(app.downPath);
						PadUtils.download(app);
					}
				}
			}
		});
		
		return convertView;
	}
	
	static class Holder {
		ImageView topicIcon;
		TextView  topicName;
		Button    onkeyBtn;
	}
}

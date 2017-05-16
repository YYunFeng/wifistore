package com.lxy.wifistore.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.JsonMap;
import com.android.lib.http.OnHttpListener;
import com.android.lib.util.JsonUtil;
import com.google.gson.Gson;
import com.lxy.wifistore.R;
import com.lxy.wifistore.act.SubTopicActivity;
import com.lxy.wifistore.adapter.TopicItemAdapter;
import com.lxy.wifistore.bean.TopicEntity;
import com.lxy.wifistore.util.HttpConfig;


/**
 * Depiction:专辑界面
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年7月22日 下午8:38:41
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class TopicView extends BaseView implements OnHttpListener, OnItemClickListener {
	private GridView        gridview;
	private TopicItemAdapter adapter;
	
	public TopicView(Context context) {
		super(context);
		inflate(context,R.layout.gridview,this);
		gridview = (GridView)findViewById(R.id.gridview);
		gridview.setOnItemClickListener(this);
	}
	
	
	public void onResume() {
		if (adapter == null || adapter.getCount() == 0) {
			loadData(null);
		} else {
			gridview.setAdapter(adapter);
		}
	}
	
	@Override
	public void loadData(Object obj) {
		BaseActivity bay = (BaseActivity) getBaseActivity();
		bay.setDebug(false);
		bay.setOnHttpListener(this);
		bay.put("padChannelId", "139");
		bay.put("clientType", "1");
		bay.request(HttpConfig.getBaseUrl() + "syncApps.action");
	}
	
	@Override
	public void onFinish(JsonMap datas, String response, String taskId) {
		super.onFinish(datas, response, taskId);
		TopicEntity topic = JsonUtil.parse(response, TopicEntity.class);
		if (topic != null) {
			if (topic.topicList != null && topic.topicList.size() > 0) {
				adapter = new TopicItemAdapter(topic.topicList);
				gridview.setAdapter(adapter);
			} else {
				//数据为空
				((BaseActivity) getBaseActivity()).showToast(R.string.no_data_tip);
			}
		} else {
			//网络错误
			((BaseActivity) getBaseActivity()).showToast(R.string.network_error_tip);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Bundle bundle = new Bundle();
		bundle.putString("title", adapter.getItem(position).name);
		Gson gson = new Gson();
		String json = gson.toJson(adapter.getItem(position).appInfos);
		bundle.putString("applist", json);
		getBaseActivity().openActivity(SubTopicActivity.class, bundle);
		getBaseActivity().overridePendingTransition(R.anim.from_right_to_left, R.anim.from_left_to_right);
	}
	
}

package com.lxy.wifistore.view;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.JsonMap;
import com.android.lib.http.OnHttpListener;
import com.android.lib.util.JsonUtil;
import com.lxy.wifistore.R;
import com.lxy.wifistore.act.DetailsActivity;
import com.lxy.wifistore.adapter.AppItemAdapter;
import com.lxy.wifistore.bean.AppEntity;
import com.lxy.wifistore.bean.BoutiqueBean;
import com.lxy.wifistore.util.HttpConfig;


/**
 * Depiction:精品界面
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
public class BoutiqueView extends BaseView implements OnHttpListener, OnItemClickListener, OnScrollListener {
	private ListView             listview;
	private ArrayList<AppEntity> appList;
	private AppItemAdapter        adapter;
	private boolean              isFromTopic;
	private int                  totalPage   = 1;
	private int                  currentPage = 1;
	private int                  lastItem    = 0;
	private View                 footer;
	
	public BoutiqueView(Context context) {
		super(context);
		
		inflate(context, R.layout.listview, this);
		listview = (ListView) findViewById(R.id.listview);
		listview.setOnItemClickListener(this);
		if (!isFromTopic) {
			listview.setOnScrollListener(this);
		}
		footer = inflate(context, R.layout.load_more, null);
	}
	
	/**
	 * @return the isFromTopic
	 */
	public boolean isFromTopic() {
		return isFromTopic;
	}
	
	/**
	 * @param isFromTopic
	 *            the isFromTopic to set
	 */
	public void setFromTopic(boolean isFromTopic) {
		this.isFromTopic = isFromTopic;
	}
	
	/**
	 * @return the appList
	 */
	public ArrayList<AppEntity> getAppList() {
		return appList;
	}
	
	/**
	 * @param appList
	 *            the appList to set
	 */
	public void setAppList(ArrayList<AppEntity> appList) {
		this.appList = appList;
	}
	
	@Override
	public void loadData(Object obj) {
		if (isFromTopic) {
			adapter = new AppItemAdapter();
			adapter.addDatas(appList);
			listview.setAdapter(adapter);
		} else {
			BaseActivity bay = (BaseActivity) getContext();
			bay.setDebug(false);
			bay.setOnHttpListener(this);
			bay.put("padChannelId", "139");
			bay.put("clientType", "1");
			bay.put("rt", "0");
			bay.put("pageSize", "20");
			bay.put("pageNum", obj == null ? "0" : obj.toString());
			bay.request(HttpConfig.getBaseUrl() + "rankingApps.action");
		}
	}
	
	@Override
	public void onStart(String taskId) {
		if (adapter == null || adapter.getCount() == 0) {
			BaseActivity bay = (BaseActivity) getContext();
			bay.showLoadingDialog(R.string.loading);
		}
	}
	
	@Override
	public void onFinish(JsonMap datas, String response, String taskId) {
		super.onFinish(datas, response, taskId);
		BoutiqueBean boutique = JsonUtil.parse(response, BoutiqueBean.class);
		if (boutique != null) {
//			LogUtil.DEBUG = true;
//			LogUtil.e(this, response);
			currentPage = boutique.an;
			totalPage = boutique.tp;
			
			if (boutique.appList != null && boutique.appList.size() > 0) {
				if (adapter == null) {
					adapter = new AppItemAdapter();
					if (currentPage < totalPage) {
						listview.addFooterView(footer);
					}
					listview.setAdapter(adapter);
				}
				
				adapter.addDatas(boutique.appList);
				if (currentPage >= totalPage) {
					listview.removeFooterView(footer);
				}
			} else {
				//数据为空
				((BaseActivity) getContext()).showToast(R.string.no_data_tip);
			}
		} else {
			//网络错误
			((BaseActivity) getContext()).showToast(R.string.network_error_tip);
		}
		listview.requestFocusFromTouch();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Bundle bundle = new Bundle();
		bundle.putInt("id", adapter.getItem(position).id);
		bundle.putString("title", adapter.getItem(position).name);
		BaseActivity bay = (BaseActivity) getContext();
		bay.openActivity(DetailsActivity.class, bundle);
		bay.overridePendingTransition(R.anim.from_right_to_left, R.anim.from_left_to_right);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//停止滑动
		if (null != adapter) {
			BaseActivity bay = (BaseActivity) getContext();
			boolean when = OnScrollListener.SCROLL_STATE_IDLE == scrollState && lastItem == adapter.getCount() + 1;
			boolean isAlive = (null != bay.getHttp()) ? bay.getHttp().isRunning() : false;
			if (when && !isAlive && (currentPage < totalPage)) {
				loadData(String.valueOf(currentPage + 1));
			}
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		lastItem = firstVisibleItem + visibleItemCount;
	}
	
	public void refresh() {
		if (adapter != null) {
			listview.post(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
					listview.setOnItemClickListener(BoutiqueView.this);
					listview.requestFocus();
					listview.requestFocusFromTouch();
				}
			});
		}
	}
	
}

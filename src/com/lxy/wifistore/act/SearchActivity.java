package com.lxy.wifistore.act;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.android.lib.app.BaseActivity;
import com.android.lib.util.JsonUtil;
import com.android.lib.util.ToastUtil;
import com.lxy.wifistore.R;
import com.lxy.wifistore.adapter.AppItemAdapter;
import com.lxy.wifistore.bean.BoutiqueBean;
import com.lxy.wifistore.receiver.DownReceiver;
import com.lxy.wifistore.util.HttpConfig;
import com.lxy.wifistore.util.StringUtil;


/**
 * Depiction: 搜索界面
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
public class SearchActivity extends BaseActivity implements OnItemClickListener, OnScrollListener, OnKeyListener {
	private EditText      searchInput;
	private ListView      listview;
	private AppItemAdapter adapter;
	
	private int           totalPage   = 1;
	private int           currentPage = 1;
	private int           lastItem    = 0;
	private View          footer;
	private String        currentKey;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setCancelable(false);
		setContentView(R.layout.activity_search);
		searchInput = (EditText) findViewById(R.id.search_input);
		searchInput.setOnKeyListener(this);
		listview = (ListView) findViewById(R.id.listview);
		listview.setOnItemClickListener(this);
		listview.setOnScrollListener(this);
		footer = LayoutInflater.from(getApplicationContext()).inflate(R.layout.load_more, null);
		
		receiver = new DownloadReceiver();
		IntentFilter filter=new IntentFilter(DownReceiver.ACTION_DOWNLOAD_STATE_CHANGE);
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.from_right_to_left, R.anim.from_left_to_right);
	}
	
	public void onBackAction(View v) {
		onBackPressed();
	}
	
	public void onSearchAction(View v) {
		String keyword = searchInput.getText().toString();
		if (TextUtils.isEmpty(keyword)) {
			ToastUtil.showToast(getApplicationContext(), R.string.search_hint);
			return;
		} else if (StringUtil.checkStringLong(keyword, 40)) {
			ToastUtil.showToast(getApplicationContext(), R.string.search_keyword_tip);
			return;
		}
		
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
		if (keyword.equals(currentKey) && adapter != null && adapter.getCount() != 0) {
			return;
		}
		
		currentPage = 1;
		adapter = null;
		currentKey = keyword;
		loadData(currentPage);
	}
	
	private void loadData(int page) {
		boolean isAlive = (null != getHttp()) ? getHttp().isRunning() : false;
		if (isAlive) {
			return;
		}
		setDebug(false);
		put("padChannelId", "139");
		put("clientType", "1");
		put("pageSize", "20");
		put("pageNum", String.valueOf(page));
		put("keyword", currentKey);
		request(HttpConfig.getBaseUrl() + "appSearch.action");
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Bundle bundle = new Bundle();
		bundle.putInt("id", adapter.getItem(position).id);
		bundle.putString("title", adapter.getItem(position).name);
		openActivity(DetailsActivity.class, bundle);
	}
	
	@Override
	public void onStart(String taskId) {
		if (adapter == null || adapter.getCount() == 0) {
			showLoadingDialog(R.string.loading);
		}
	}
	
	@Override
	public void onFinish(String response, String taskId) {
		super.onFinish(response, taskId);
		BoutiqueBean boutique = JsonUtil.parse(response, BoutiqueBean.class);
		if (boutique != null) {
			totalPage = boutique.tp;
			
			if (boutique.appList != null || boutique.appList.size() > 0) {
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
				showToast(R.string.no_data_tip);
			}
		} else {
			//网络错误
			showToast(R.string.network_error_tip);
		}
		
		if (boutique == null || boutique.appList != null) {
			currentPage = currentPage > 1 ? currentPage -= 1 : currentPage;
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//停止滑动
		if (null != adapter) {
			boolean when = OnScrollListener.SCROLL_STATE_IDLE == scrollState && lastItem == adapter.getCount() + 1;
			boolean isAlive = (null != getHttp()) ? getHttp().isRunning() : false;
			if (when && !isAlive && (currentPage < totalPage)) {
				currentPage += 1;
				loadData(currentPage);
			}
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		lastItem = firstVisibleItem + visibleItemCount;
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
			InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
			}
			onSearchAction(searchInput);
			return true;
		}
		return false;
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
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
			}
		}
	}//end DownReceiver
}

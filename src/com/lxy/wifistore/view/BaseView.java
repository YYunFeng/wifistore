package com.lxy.wifistore.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.JsonMap;
import com.android.lib.http.OnHttpListener;


/**
 * Depiction:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年9月8日 下午2:56:23
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public abstract class BaseView extends FrameLayout implements OnHttpListener {
	protected boolean isFirst = true;
	
	/**
	 * @param context
	 */
	public BaseView(Context context) {
		super(context);
	}
	
	/**
	 * @param context
	 * @param attrs
	 */
	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public BaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public abstract void loadData(Object obj);
	
	public BaseActivity getBaseActivity() {
		BaseActivity act = (BaseActivity) getContext();
		act.setOnHttpListener(this);
		return act;
	}
	
	public void initData() {
		if (isFirst) {
			isFirst = false;
			loadData(null);
		}
	}
	
	public void onStart(String taskId) {
		BaseActivity act = (BaseActivity) getContext();
		if (act.isShowDialog()) {
			getBaseActivity().showLoadingDialog("加载中....");
		}
	}
	
	public void onFinish(JsonMap datas, String response, String taskId) {
	}
	
	protected int getCount() {
		return 0;
	}
}

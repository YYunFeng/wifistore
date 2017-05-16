package com.lxy.wifistore.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.android.lib.data.JsonMap;
import com.lxy.wifistore.R;
import com.lxy.wifistore.adapter.SalesAdapter;


/**
 * Depiction:右上角弹出菜单
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014-4-14 上午9:47:57
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class SalesPopMenu extends PopupWindow implements OnItemClickListener, OnClickListener {
	private OnPopMenuItemClickListener onPopMenuItemClickListener;
	private List<JsonMap>              dataList;
	
	public SalesPopMenu(Context context, List<JsonMap> dataList) {
		super(context);
		this.dataList = dataList;
		LayoutInflater inflater = LayoutInflater.from(context);
		ListView listview = (ListView) inflater.inflate(R.layout.listview, null);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.edit_text_bg));
		listview.setFocusable(true);
		listview.setFocusableInTouchMode(true);
		listview.setOnItemClickListener(this);
		listview.setAdapter(new SalesAdapter(dataList, this));
		setContentView(listview);
		setWidth(context.getResources().getDimensionPixelSize(R.dimen.pop_menu_width));
		setHeight(LayoutParams.WRAP_CONTENT);
		setOutsideTouchable(true);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		dismiss();
		if (onPopMenuItemClickListener != null) {
			onPopMenuItemClickListener.onPopMenuItem(position, dataList.get(position));
		}
	}
	
	public OnPopMenuItemClickListener getOnPopMenuItemClickListener() {
		return onPopMenuItemClickListener;
	}
	
	public void setOnPopMenuItemClickListener(OnPopMenuItemClickListener onPopMenuItemClickListener) {
		this.onPopMenuItemClickListener = onPopMenuItemClickListener;
	}
	
	public interface OnPopMenuItemClickListener {
		void onPopMenuItem(int index, JsonMap data);
	}
	
	@Override
	public void onClick(View v) {
		dismiss();
		int position = Integer.parseInt(v.getTag().toString());
		if (onPopMenuItemClickListener != null) {
			onPopMenuItemClickListener.onPopMenuItem(position, dataList.get(position));
		}
	}
}

package com.lxy.wifistore.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.lib.data.JsonMap;
import com.lxy.wifistore.R;


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
public class SalesAdapter extends BaseAdapter {
	private List<JsonMap>   dataList;
	private OnClickListener onClickListener;
	
	public SalesAdapter(List<JsonMap> dataList, OnClickListener onClickListener) {
		this.dataList = dataList;
		this.onClickListener = onClickListener;
	}
	
	@Override
	public int getCount() {
		return dataList != null ? dataList.size() : 0;
	}
	
	@Override
	public JsonMap getItem(int position) {
		return dataList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return dataList.get(position).getInt("salesId");
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sales_adapter, null);
			holder.name = (TextView) convertView.findViewById(R.id.name_view);
			holder.id = (TextView) convertView.findViewById(R.id.id_view);
			holder.bindButton = (Button) convertView.findViewById(R.id.bind_sale_btn);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.name.setText(parent.getContext().getString(R.string.sale_name) + getItem(position).getString("salesName"));
		holder.id.setText(parent.getContext().getString(R.string.sale_id) + getItem(position).getString("salesId"));
		holder.bindButton.setTag(position);
		holder.bindButton.setOnClickListener(onClickListener);
		
		return convertView;
	}
	
	static class Holder {
		TextView name;
		TextView id;
		Button   bindButton;
	}
	
}

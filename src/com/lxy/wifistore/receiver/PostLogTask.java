package com.lxy.wifistore.receiver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.android.lib.data.JsonMap;
import com.lxy.wifistore.util.HttpConfig;


/**
 * Depiction:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Email: kevin185@foxmail.com
 * <p>
 * Create Date: 2015年3月9日 下午3:05:02
 * <p>
 * Modify:
 * 
 * @version 1.0
 * @since 1.0
 */
public abstract class PostLogTask {
	private String      data    = null;
	
	public PostLogTask(String data) {
		this.data = data;
	}
	
	public void perform() {
		new Thread() {
			public void run() {
				StringBuffer sb = new StringBuffer("");
				try {
					onStartTask();
					//创建连接
					URL url = new URL(HttpConfig.getPostJsonLogUrl());
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");
					connection.setUseCaches(false);
					//					connection.setInstanceFollowRedirects(true);
					// 这里务必设置成 Content-Type：application/json，否则服务器接受不到数据
					connection.setRequestProperty("Content-Type", "application/json");
					
					connection.connect();
					
					//POST请求
					DataOutputStream out = new DataOutputStream(connection.getOutputStream());
					
					out.write(data.getBytes("utf-8"));
					out.flush();
					out.close();
					
					//读取服务器响应
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String lines;
					while ((lines = reader.readLine()) != null) {
						lines = new String(lines.getBytes(), "utf-8");
						sb.append(lines);
					}
					// 这里是接口返回的值
					reader.close();
					// 断开连接
					connection.disconnect();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				onEndTask(JsonMap.parseJson(sb.toString()), sb.toString());
			}
		}.start();
	}
	
	public abstract void onStartTask();
	
	public abstract void onEndTask(JsonMap data, String json);
}

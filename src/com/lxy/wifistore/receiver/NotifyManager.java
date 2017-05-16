package com.lxy.wifistore.receiver;

import java.util.LinkedHashMap;

import android.app.Notification;


/**
 * Depiction: 通知栏管理
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年7月28日 下午1:45:03
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class NotifyManager {
	private static NotifyManager                        instance = null;
	private static LinkedHashMap<Integer, Notification> listTask;
	
	private NotifyManager() {
		listTask = new LinkedHashMap<Integer, Notification>();
	}
	
	public synchronized static NotifyManager getInstance() {
		if (instance == null) {
			instance = new NotifyManager();
		}
		return instance;
	}
	
	public synchronized void put(Integer key, Notification notif) {
		listTask.put(key, notif);
	}
	
	public boolean containsKey(Integer key) {
		return listTask.containsKey(key);
	}
	
	public Notification get(Integer key) {
		return listTask.get(key);
	}
	
	public void remove(Integer key) {
		listTask.remove(key);
	}
	
	public void removeAll() {
		listTask.clear();
	}
}

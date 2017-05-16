package com.lxy.wifistore.receiver;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.RemoteViews;

import com.android.lib.util.LogUtil;
import com.lxy.pad.download.DownUtil;
import com.lxy.pad.download.DownloadConstant;
import com.lxy.pad.download.DownloadDao;
import com.lxy.pad.download.DownloadInfo;
import com.lxy.pad.download.DownloadStatus;
import com.lxy.wifistore.R;


/**
 * Depiction: 监听下载队列广播
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年7月24日 下午1:51:18
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class DownReceiver extends BroadcastReceiver {
	public final static String   ACTION_DOWNLOAD_STATE_CHANGE = "com.lxy.DOWNLOAD_STATE_CHANGE";
	public final static String   ACTION_BUTTON_CANCEL         = "com.notifications.intent.action.BUTTON_CANCEL";
	public final static String   ACTION_BUTTON_RETRY          = "com.notifications.intent.action.BUTTON_RETRY";
	public final static String   ACTION_BUTTON_INFO           = "action_button_info";
	private final static Handler handler                      = new Handler();
	
	public DownReceiver() {
	}
	
	@Override
	public void onReceive(final Context context, Intent intent) {
//		Log.e(this.getClass().getSimpleName(), "DownReceiver action send = ()"+intent.getAction());
		DownloadDao.getDao().startTask();
		final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		String action = intent.getAction();
		if (DownloadConstant.DOWNLOAD_ALL_FINISH_ACTION.equalsIgnoreCase(action)) {
			manager.cancelAll();
			NotifyManager.getInstance().removeAll();
			return;
		}
		
		context.sendBroadcast(new Intent(ACTION_DOWNLOAD_STATE_CHANGE));
		
		if (ACTION_BUTTON_CANCEL.equalsIgnoreCase(action)||DownloadConstant.DOWNLOAD_CANCEL_ACTION.equalsIgnoreCase(action)) {
			//取消
			final DownloadInfo info = (DownloadInfo) intent.getExtras().getSerializable(ACTION_BUTTON_INFO);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (info != null) {
						DownloadDao.getDao().delete(info.id);
						manager.cancel(info.id);
						NotifyManager.getInstance().remove(info.id);
					}
				}
			}, 20);
			return;
		}
		if (ACTION_BUTTON_RETRY.equalsIgnoreCase(action)) {
			//重试
			DownloadInfo info = (DownloadInfo) intent.getExtras().getSerializable(ACTION_BUTTON_INFO);
			DownloadDao.getDao().update(info.id, 0, 0, DownloadStatus.WAIT);
			DownloadDao.getDao().startTask();
			
			Notification notif = NotifyManager.getInstance().get(info.id);
			notif.contentView.setTextViewText(R.id.app_name_view, info.name);
			notif.contentView.setViewVisibility(R.id.progress_bar, View.GONE);
			notif.contentView.setViewVisibility(R.id.down_tip, View.VISIBLE);
			notif.contentView.setViewVisibility(R.id.cancel_btn, View.GONE);
			
			Intent buttonIntent = new Intent(ACTION_BUTTON_CANCEL);
			buttonIntent.putExtra(ACTION_BUTTON_INFO, info);
			PendingIntent pendButtonIntent = PendingIntent.getBroadcast(context, 0, buttonIntent, 0);
			notif.contentView.setOnClickPendingIntent(R.id.cancel_btn, pendButtonIntent);
			return;
		}
		
		final DownloadInfo info = (DownloadInfo) intent.getExtras().getSerializable(DownloadConstant.DOWNLOAD_EXTRAS_INFO);
		if (info == null) {
			LogUtil.e(this, "info is null");
			return;
		}
		if (DownloadConstant.DOWNLOAD_ADD_ACTION.equalsIgnoreCase(action)) {
			//新加下载任务
			final DownloadInfo dInfo = (DownloadInfo) intent.getExtras().getSerializable(DownloadConstant.DOWNLOAD_EXTRAS_INFO);
			addTask(context, dInfo, manager);
		} else if (DownloadConstant.DOWNLOAD_UPDATE_ACTION.equalsIgnoreCase(action)) {
			//下载进度更新任务
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (info != null && NotifyManager.getInstance().containsKey(info.id)) {
						Notification notif = NotifyManager.getInstance().get(info.id);
						if (notif == null || notif.contentView == null) {
							addTask(context, info, manager);
							return;
						}
						String path = DownUtil.getApkPath(info.id)+".tmp";
						File tmp = new File(path);
						long progress = tmp.exists()?tmp.length():0;
						info.progress = progress;
						notif.contentView.setTextViewText(R.id.app_name_view, info.name);
						notif.contentView.setProgressBar(R.id.progress_bar, (int) info.total, (int) info.progress, false);
						notif.contentView.setViewVisibility(R.id.progress_bar, View.VISIBLE);
						notif.contentView.setViewVisibility(R.id.down_tip, View.GONE);
						notif.contentView.setViewVisibility(R.id.cancel_btn, View.VISIBLE);
						notif.contentView.setImageViewResource(R.id.icon_view, R.drawable.ic_launcher);
						manager.notify(info.id, notif);
					}
				}
			}, 10);
		} else if (DownloadConstant.DOWNLOAD_SUCCESS_ACTION.equalsIgnoreCase(action)) {
			//某个任务下载成功
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					manager.cancel(info.id);
					NotifyManager.getInstance().remove(info.id);
				}
			}, 200);
			
		} else if (DownloadConstant.DOWNLOAD_FAIL_ACTION.equalsIgnoreCase(action)) {
			//某个任务下载失败
			if (info != null) {
				LogUtil.i(this, "download fail, the name is -->" + info.name);
			}
			if (info != null && !NotifyManager.getInstance().containsKey(info.id)) {
				Notification notif = NotifyManager.getInstance().get(info.id);
				if (notif == null || notif.contentView == null) {
					return;
				}
				notif.contentView.setTextViewText(R.id.app_name_view, info.name);
				notif.contentView.setImageViewResource(R.id.icon_view, R.drawable.ic_launcher);
				notif.contentView.setViewVisibility(R.id.progress_bar, View.GONE);
				notif.contentView.setViewVisibility(R.id.down_tip, View.VISIBLE);
				notif.contentView.setTextViewText(R.id.down_tip, context.getText(R.string.fail_download_list));
				notif.contentView.setViewVisibility(R.id.cancel_btn, View.VISIBLE);
				notif.contentView.setTextViewText(R.id.cancel_btn, context.getText(R.string.retry));
				
				Intent buttonIntent = new Intent(ACTION_BUTTON_RETRY);
				buttonIntent.putExtra(ACTION_BUTTON_INFO, info);
				PendingIntent pendButtonIntent = PendingIntent.getBroadcast(context, 0, buttonIntent, 0);
				notif.contentView.setOnClickPendingIntent(R.id.cancel_btn, pendButtonIntent);
				
				manager.notify(info.id, notif);
			}
		}
	}
	
	private void addTask(final Context context, final DownloadInfo dInfo, final NotificationManager manager) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (dInfo != null && !NotifyManager.getInstance().containsKey(dInfo.id)) {
					Notification notif = new Notification();
					notif.flags = Notification.FLAG_NO_CLEAR;
					notif.icon = R.drawable.ic_launcher;
					notif.tickerText = dInfo.name + context.getString(R.string.start_download);
					notif.contentIntent = null;
					notif.contentView = new RemoteViews(context.getPackageName(), R.layout.download_view);
					
					notif.contentView.setTextViewText(R.id.app_name_view, dInfo.name);
					notif.contentView.setViewVisibility(R.id.progress_bar, View.GONE);
					notif.contentView.setViewVisibility(R.id.down_tip, View.VISIBLE);
					notif.contentView.setImageViewResource(R.id.icon_view, R.drawable.ic_launcher);
					Intent buttonIntent = new Intent(ACTION_BUTTON_CANCEL);
					buttonIntent.putExtra(ACTION_BUTTON_INFO, dInfo);
					PendingIntent pendButtonIntent = PendingIntent.getBroadcast(context, dInfo.id, buttonIntent, 0);
					notif.contentView.setOnClickPendingIntent(R.id.cancel_btn, pendButtonIntent);
					
					NotifyManager.getInstance().put(dInfo.id, notif);
					manager.notify(dInfo.id, notif);
				}
			}
		}, 1);
	}
}

package com.lxy.pad.download;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;

import com.android.lib.http.FileResult;
import com.android.lib.util.LogUtil;
import com.lxy.pad.download.FileDownload.OnDownloadListener;
import com.lxy.pad.download.FileDownload.OnNetSpeedListener;
import com.lxy.wifistore.WifiApp;
import com.lxy.wifistore.bean.AppEntity;
import com.lxy.wifistore.util.PadUtils;
import com.lxy.wifistore.util.VerifyCode;


/**
 * Depiction:同步下载任务
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年5月23日 下午1:51:46
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class DownloadTask extends Thread implements OnDownloadListener, OnNetSpeedListener {
	public final static int        START_ID  = -1;
	public final static int        FINISH_ID = -2;
	public static boolean          isRunning;
	private static WifiApp         context   = WifiApp.getInstance();
	private OnNetSpeedListener     onNetSpeedListener;
	private OnDownloadTaskListener onDownloadTaskListener;
	private FileDownload           download;
	
	private DownloadInfo           tempInfo;
	
	public DownloadTask() {
	}
	
	/**
	 * @return the onNetSpeedListener
	 */
	public OnNetSpeedListener getOnNetSpeedListener() {
		return onNetSpeedListener;
	}
	
	/**
	 * @param onNetSpeedListener
	 *            the onNetSpeedListener to set
	 */
	public void setOnNetSpeedListener(OnNetSpeedListener onNetSpeedListener) {
		this.onNetSpeedListener = onNetSpeedListener;
	}
	
	/**
	 * @return the onDownloadTaskListener
	 */
	public OnDownloadTaskListener getOnDownloadTaskListener() {
		return onDownloadTaskListener;
	}
	
	/**
	 * @param onDownloadTaskListener
	 *            the onDownloadTaskListener to set
	 */
	public void setOnDownloadTaskListener(OnDownloadTaskListener onDownloadTaskListener) {
		this.onDownloadTaskListener = onDownloadTaskListener;
	}
	
	@Override
	public void run() {
		isRunning = true;
		sendBroadcast(START_ID, DownloadConstant.DOWNLOAD_START_ACTION);
		DownloadDao dao = DownloadDao.getDao();
		while (dao.selectAllWait().size() > 0) {
			DownloadInfo info = dao.selectAllWait().get(0);
			LogUtil.i(this, "downloading app,the name is " + info.name);
			sendBroadcast(info, DownloadConstant.DOWNLOAD_UPDATE_ACTION);
			download(info);
			try {
				//休眠一会儿后，进行下一个任务 
				sleep(100);
			} catch (InterruptedException e) {
			}
		}
		
		if (onDownloadTaskListener != null) {
			onDownloadTaskListener.onDownFinish();
		}
//		LogUtil.i(this, "will send all_finish_action");
		sendBroadcast(FINISH_ID, DownloadConstant.DOWNLOAD_ALL_FINISH_ACTION);
		isRunning = false;
	}
	
	public int getTaskId() {
		return tempInfo != null ? tempInfo.id : -1;
	}
	
	private void download(DownloadInfo info) {
		tempInfo = info;
		String destPath = DownUtil.getApkPath(info.id);
		download = new FileDownload(info.url, destPath);
		download.setOnDownloadListener(this);
		download.setOnNetSpeedListener(this);
		
		boolean isOverride = false;
		//如果是apk安装文件，和本地校验码不一致则覆盖下载
		if (new File(destPath).exists() && !new VerifyCode(destPath).verifyCode().equals(info.verifyCode)) {
			isOverride = true;
		}
		
		FileResult ret = download.download(isOverride);
		switch (ret) {
			case EXSIT:
			case SUCCESS:
				DownloadDao.getDao().update(tempInfo.id, -1, -1, DownloadStatus.FINISH);
				if (onDownloadTaskListener != null) {
					onDownloadTaskListener.onSuccess(info.id);
				}
				
				//download success begin start installing
				AppEntity app = new AppEntity();
				app.id = info.id;
				app.name = info.name;
				app.icon = info.icon;
				app.versionName = info.versionName;
				app.pckName = info.packageName;
				PadUtils.installApk2Phone(app);
				
				sendBroadcast(info, DownloadConstant.DOWNLOAD_SUCCESS_ACTION);
				break;
			case FAILURE:
				DownloadDao.getDao().update(tempInfo.id, -1, -1, DownloadStatus.FAIL);
				if (onDownloadTaskListener != null) {
					onDownloadTaskListener.onFail(info.id);
				}
				sendBroadcast(info, DownloadConstant.DOWNLOAD_FAIL_ACTION);
				break;
			case CANCEL:
				DownloadDao.getDao().delete(info.id);
				sendBroadcast(info, DownloadConstant.DOWNLOAD_CANCEL_ACTION);
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onProgress(long progress, long total) {
		DownloadDao.getDao().update(tempInfo.id, progress, total, DownloadStatus.GOING);
		if (onDownloadTaskListener != null) {
			onDownloadTaskListener.onProgress(progress, total, tempInfo.name, tempInfo.id);
		}
		
		DownloadInfo info = new DownloadInfo();
		info.id = tempInfo.id;
		info.icon = tempInfo.icon;
		info.name = tempInfo.name;
		info.progress = progress;
		info.total = total;
		sendBroadcast(info, DownloadConstant.DOWNLOAD_UPDATE_ACTION);
	}
	
	public void cancel() {
		if (download != null) {
			download.cancel();
		}
	}
	
	@Override
	public void onSpeed(long speed) {
		Intent intent = new Intent(DownloadConstant.DOWNLOAD_SPEED_ACTION);
		intent.putExtra(DownloadConstant.DOWNLOAD_EXTRAS_INFO, speed);
		context.sendBroadcast(intent);
	}
	
	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	//发送下载通知广播
	private void sendBroadcast(Object data, String action) {
		Intent intent = new Intent(action);
		Bundle bundle = new Bundle();
		if (data instanceof DownloadInfo) {
			bundle.putSerializable(DownloadConstant.DOWNLOAD_EXTRAS_INFO, (DownloadInfo) data);
		} else {
			bundle.putInt(DownloadConstant.DOWNLOAD_EXTRAS_INFO, (Integer) data);
		}
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}
	
}

package com.lxy.pad.upgrade;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import com.android.lib.http.FileDownload;
import com.android.lib.http.FileResult;
import com.android.lib.util.ToastUtil;
import com.lxy.wifistore.R;


/**
 * Depiction:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年6月25日 上午10:36:03
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class DownloadUpgrade extends Thread {
	private final Handler handler = new Handler();
	private Context       context;
	private String        url;
	
	public DownloadUpgrade(Context context, String url) {
		this.context = context;
		this.url = url;
	}
	
	@Override
	public void run() {
		String destPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ppad/upgrade/ppad.apk";
		FileDownload download = new FileDownload(url, destPath);
		final FileResult ret = download.download(true);
		final String apk = destPath;
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (ret == FileResult.SUCCESS) {
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(new File(apk)), "application/vnd.android.package-archive");
					context.startActivity(intent);
				} else if (ret == FileResult.FAILURE) {
					ToastUtil.showToast(context, R.string.upgrade_fail);
				}
			}
		});
		UpgradeTask.isRunning = false;
	}
}

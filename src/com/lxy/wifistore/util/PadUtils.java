package com.lxy.wifistore.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.WindowManager;

import com.android.lib.util.AvailableBlocks;
import com.android.lib.util.AvailableBlocks.StorageType;
import com.android.lib.util.ExternalStorage;
import com.android.lib.util.ToastUtil;
import com.google.bitmapcache.ImageFetcher;
import com.lxy.pad.download.DownUtil;
import com.lxy.pad.download.DownloadDao;
import com.lxy.pad.download.DownloadInfo;
import com.lxy.pad.download.DownloadStatus;
import com.lxy.wifistore.R;
import com.lxy.wifistore.WifiApp;
import com.lxy.wifistore.bean.AppEntity;


/**
 * Depiction: 常用操作工具
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年5月19日 上午10:33:53
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class PadUtils {
	
	private static WifiApp context = WifiApp.getInstance();
	
	private PadUtils() {
	}
	
	public static ImageFetcher getFetcher() {
		ImageFetcher fetcher = ImageFetcher.getImageFetcher(context);
		fetcher.setLoadingImage(R.drawable.loading);
		return fetcher;
	}
	
	public static void startWifiCfg() {
		//Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
		Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	/**
	 * 手机号校验
	 * 
	 * @param mobiles
	 * @return 是否为合法的手机号
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动号段：134~139，147，150~152，157（TD）~159，181~183，187~188（3G_TD-SCDMA）
		 * 联通号段：130~132，155~156，185~186（3G-WCDMA）
		 * 电信号段：133，153，180（3G），189（3G-CDMA2000evdo）
		 */
		Pattern p = Pattern.compile("^((13[0-9])|(17[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		Log.d("convert#", m.matches() + "---");
		return m.matches();
	}
	
	/**
	 * 删除文件
	 * 
	 * @param path
	 *            文件路径
	 */
	public static void deleteFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}
	
	/**
	 * 判断文件是否存在
	 * 
	 * @param path
	 *            文件路径
	 * @return 存在返回true，否则返回false
	 */
	public static boolean existFile(String path) {
		File file = new File(path);
		return file.exists();
	}
	
	/**
	 * 添加下载任务
	 * 
	 * @param app
	 */
	public static void download(AppEntity app) {
		DownloadInfo info = new DownloadInfo();
		info.icon = app.icon;
		info.id = app.id;
		info.detailId = 0;
		info.name = app.name;
		info.date = System.currentTimeMillis();
		info.url = app.downPath;
		info.packageName = app.pckName;
		download(info);
	}
	
	/**
	 * 添加下载任务
	 * 
	 * @param app
	 */
	private static void download(DownloadInfo info) {
		if (!ExternalStorage.isExternalStorageAvailable()) {
			ToastUtil.showToast(context, R.string.sdcard_invalid);
			return;
		}
		if (!checkSDCardAvailableSize()) {
			return;
		}
		
		if (info.id <= 0) {
			return;
		}
		
		if (DownloadDao.getDao().isDownloaded(info.id)) {
			//已经下载，直接安装
			AppEntity app = new AppEntity();
			app.id = info.id;
			installApk2Phone(app);
		} else if (!DownloadDao.getDao().exist(info.id)) {
			//任务不存在，添加在下载队列中
			DownloadDao.getDao().insert(info);
		}
	}
	
	public static void installApk2Phone(AppEntity app) {
		String qqPath = DownUtil.getApkPath(app.id);
		installApk2Phone(qqPath);
	}
	
	private static String getPackageName(String path) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pinfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
		if (null != pinfo) {
			ApplicationInfo appInfo = pinfo.applicationInfo;
			return appInfo.packageName;
		}
		return null;
	}
	
	public static void installApk2Phone(String path) {
		if (getPackageName(path) == null) {
			deleteFile(path);
			return;
		}
		
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}
	
	/**
	 * 检查SDCard是否enough available size 可用，不可用则弹出对话框提示 (>80%)
	 * 
	 * @return 可用返回true，否则返回false
	 */
	public static boolean checkSDCardAvailableSize() {
		AvailableBlocks block1 = new AvailableBlocks(StorageType.External);
		AvailableBlocks block2 = new AvailableBlocks(StorageType.Internal);
		boolean flag = block1.availablePercent() > 10 || block2.availablePercent() > 10;
		if (!flag) {
			ToastUtil.showToast(context, context.getString(R.string.unenough_available_sdcard_size));
		}
		return flag;
	}
	
	public static void quitDialog(Context context) {
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.setTitle(R.string.tip);
		dialog.setMessage(context.getString(R.string.is_quit_tip));
		dialog.setButton(Dialog.BUTTON_NEGATIVE, context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.exit(0);
			}
		});
		
		dialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public static boolean isDownloaded(int id) {
		DownloadInfo info = DownloadDao.getDao().select(id);
		return info != null && info.status == DownloadStatus.FINISH && new File(DownUtil.getApkPath(id)).exists();
	}
	
	/**
	 * 检查某个软件是否安装
	 * 
	 * @param pck
	 *            软件包名
	 * @return 安装返回true，否则返回false
	 */
	public static boolean isInstalled(String pck) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(pck, 0);
		} catch (NameNotFoundException e) {
		}
		
		return packageInfo != null;
	}
	
	/**
	 * 启动已经安装的app
	 * <p>
	 * 注意：此种方法启动的app，必须是一个完整的可以显示在launcher上的app，不能是插件形式的app,即：category属性必须包含：
	 * android.intent.category.LAUNCHER
	 * 
	 * @param pck
	 *            软件包名
	 */
	public static void startApp(String pck) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(pck, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		if (packageInfo != null) {
			Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(pck);
			if (launchIntent != null) {
				context.startActivity(launchIntent);
			}
		}
	}
	
	/**
	 * 重新启动应用
	 */
	public static void reStart(Context context) {
		Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(i);
	}
	
	/**
	 * 清空指定目录下的临时文件
	 * 
	 * @param dirPath
	 */
	public static void clearDir(final String dirPath) {
		new Thread() {
			public void run() {
				try {
					File dir = new File(dirPath);
					if (dir.exists()) {
						File[] files = dir.listFiles();
						for (File file : files) {
							if (file.isFile() && file.getName().endsWith(".tmp")) {
								file.delete();
							}
						}
					}
				} catch (Exception e) {
				}
			}
		}.start();
	}
}

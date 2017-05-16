package com.lxy.pad.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.text.TextUtils;
import android.util.Log;

import com.android.lib.http.FileResult;


/**
 * Depiction: 文件下载
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年8月13日 下午3:23:58
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class FileDownload {
	private final static String TAG      = "FileDownload";
	private final static String UA       = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
	private final static String TEMP     = ".tmp";
	private final static int    TIME_OUT = 20000;
	private final static int    DURATION = 1000;
	private HttpURLConnection   conn     = null;
	private String              fileUrl;
	private String              destPath;
	private boolean             cancel;
	private boolean             isRetry  = false;
	
	/**
	 * 构造函数
	 * 
	 * @param url
	 *            下载地址
	 * @param destPath
	 *            文件路径
	 */
	public FileDownload(String url, String destPath) {
		this.fileUrl = url;
		this.destPath = destPath;
		initDirs(destPath);
	}
	
	/**
	 * 下载文件
	 * 
	 * @param isOverride
	 *            是否覆盖
	 * @return {@link FileResult}
	 */
	public FileResult download(boolean isOverride) {
		FileResult ret = FileResult.FAILURE;
		if (isOverride) {
			deleteFile(destPath);
		} else if (existFile(destPath)) {
			return FileResult.EXSIT;
		}
		URL url = null;
		InputStream is = null;
		OutputStream out = null;
		try {
			if (onNetSpeedListener != null) {
				onNetSpeedListener.onSpeed(0);
			}
			CookieManager cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);
			url = new URL(fileUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIME_OUT);
			conn.setReadTimeout(TIME_OUT);
			conn.setDoInput(true);
			conn.setRequestProperty("User-Agent", UA);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect();
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				long total = conn.getContentLength();
				long progress = 0;
				long tempProgress = 0;
				int read = 0;
				long startTime = System.currentTimeMillis();
				byte[] buffer = new byte[1024 * 1];
				out = new FileOutputStream(destPath + TEMP);
				while ((read = is.read(buffer)) != -1) {
					if (cancel) {
						break;
					}
					out.write(buffer, 0, read);
					tempProgress += read;
					progress += read;
					
					long speed = 0;
					long endTime = System.currentTimeMillis();
					if (endTime - startTime >= DURATION) {
						speed = tempProgress / (endTime - startTime) * 1024;
						if (onNetSpeedListener != null) {
							onNetSpeedListener.onSpeed(speed);
						}
						tempProgress = 0;
						startTime = System.currentTimeMillis();
						
						if (onDownloadListener != null) {
							onDownloadListener.onProgress(progress, total);
						}
					}
				}
				
				File temp = new File(destPath + TEMP);
				File file = new File(destPath);
				if (!cancel) {
					temp.renameTo(file);
					ret = FileResult.SUCCESS;
					if (onDownloadListener != null) {
						onDownloadListener.onProgress(total, total);
					}
				} else {
					deleteFile(destPath + TEMP);
					deleteFile(destPath);
				}
			} else {
				Log.e(TAG, "the respond code is ---> " + conn.getResponseCode());
				Log.e(TAG, "the url is:" + fileUrl);
			}
		} catch (MalformedURLException e) {
			deleteFile(destPath + TEMP);
			Log.e(TAG, "MalformedURLException ---> " + e.toString());
		} catch (IOException e) {
			deleteFile(destPath + TEMP);
			Log.e(TAG, "IOException ---> " + e.toString());
		} catch (Exception e) {
			deleteFile(destPath + TEMP);
			Log.e(TAG, "Exception ---> " + e.toString());
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				deleteFile(destPath + TEMP);
				Log.e(TAG, e.toString());
			}
		}
		ret = cancel ? FileResult.CANCEL : ret;
		if (ret == FileResult.FAILURE && !isRetry) {
			isRetry = true;
			return download(true);
		}
		return ret;
	}
	
	/**
	 * 取消下载
	 */
	public void cancel() {
		try {
			cancel = true;
			if (conn != null) {
				conn = null;
				deleteFile(destPath + TEMP);
			}
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}
	
	/**
	 * 删除文件
	 * 
	 * @param path
	 *            文件路径
	 */
	private void deleteFile(final String path) {
//		new Thread(){
//			public void run(){
//				File file = new File(path);
//				if (file.exists()) {
//					file.delete();
//				}
//			}
//		}.start();
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
	private boolean existFile(String path) {
		File file = new File(path);
		return file.exists();
	}
	
	private void initDirs(String destPath) {
		if (!TextUtils.isEmpty(destPath)) {
			int index = destPath.lastIndexOf("/");
			if (index != -1) {
				String destDir = destPath.substring(0, index);
				File dir = new File(destDir);
				dir.mkdirs();
			}
		}
	}
	
	/**
	 * @return the onDownloadListener
	 */
	public OnDownloadListener getOnDownloadListener() {
		return onDownloadListener;
	}
	
	/**
	 * @param onDownloadListener
	 *            the onDownloadListener to set
	 */
	public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
		this.onDownloadListener = onDownloadListener;
	}
	
	private OnDownloadListener onDownloadListener;
	
	public interface OnDownloadListener {
		void onProgress(long progress, long total);
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
	
	private OnNetSpeedListener onNetSpeedListener;
	
	public interface OnNetSpeedListener {
		/**
		 * 网速监控
		 * 
		 * @param speed
		 *            每秒下载的字节数
		 */
		void onSpeed(long speed);
	}
	
}

package com.lxy.pad.download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.android.lib.util.LogUtil;
import com.lxy.pad.download.DownloadDBHelper.Download;
import com.lxy.wifistore.WifiApp;


/**
 * 类描述：下载管理数据库操作类
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午5:38:28
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public final class DownloadDao {
	final String               TAG     = "DownloadDao";
	private DownloadDBHelper   helper;
	private static DownloadDao dao     = null;
	private static WifiApp     context = WifiApp.getInstance();
	
	private DownloadDao() {
		this.helper = new DownloadDBHelper(context);
	}
	
	public static synchronized DownloadDao getDao() {
		if (null == dao) {
			dao = new DownloadDao();
		}
		return dao;
	}
	
	/**
	 * 程序重启时，初始化下载状态
	 * 
	 * @return 成功返回true，否则返回false
	 */
	public synchronized boolean initStatus() {
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "delete from " + DownloadDBHelper.TABLE_NAME + " where " + Download.STATUS + "!=" + DownloadStatus.FINISH.ordinal();
		db.execSQL(sql);
		return true;
	}
	
	/**
	 * 是否存在某个应用的下载信息
	 * 
	 * @param id
	 * @return 存在返回true，否则返回false
	 */
	public synchronized boolean exist(int id) {
		DownloadInfo info = select(id);
		if (info == null) {
			return false;
		}
		
		if (info.status == DownloadStatus.GOING || info.status == DownloadStatus.WAIT) {
			//正在下载或者等待状态
			return true;
		} else if (info.status == DownloadStatus.FINISH) {
			//状态为下载完成时，判断文件是否还存在
			String path = DownUtil.getApkPath(id);
			return new File(path).exists();
		}
		
		return false;
	}
	
	/**
	 * 判断app是否已经下载成功
	 * 
	 * @param id
	 *            app id
	 * @return 是否已经下载
	 */
	public synchronized boolean isDownloaded(int id) {
		DownloadInfo info = select(id);
		if (info != null && info.status == DownloadStatus.FINISH) {
			//状态为下载完成时，判断文件是否还存在
			String path = DownUtil.getApkPath(id);
			return new File(path).exists();
		}
		
		return false;
	}
	
	/**
	 * 增加一条下载信息
	 * 
	 * @param info
	 *            DownloadInfo
	 */
	public synchronized void insert(DownloadInfo info) {
		try {
			if (!exist(info.id)) {
				SQLiteDatabase db = helper.getReadableDatabase();
				String sql = "insert into " + DownloadDBHelper.TABLE_NAME//
				        + " ("//
				        + Download.ID//
				        + ","//
				        + Download.DETAIL_ID//
				        + ","//
				        + Download.PROGRESS//
				        + ","//
				        + Download.TOTAL//
				        + ","//
				        + Download.PATH//
				        + ","//
				        + Download.ICON//
				        + ","//
				        + Download.URL//
				        + ","//
				        + Download.NAME//
				        + ","//
				        + Download.PACKAGE//
				        + ","//
				        + Download.VERSION_NAME//
				        + ","//
				        + Download.STATUS//
				        + ","//
				        + Download.DATE//
				        + ") values (?,?,?,?,?,?,?,?,?,?,?,?)";
				Object[] bindArgs = {
				        info.id,
				        info.detailId,
				        info.progress,
				        info.total,
				        info.path,
				        info.icon,
				        info.url,
				        info.name,
				        info.packageName,
				        info.versionName,
				        DownloadStatus.WAIT.ordinal(),
				        info.date
				};
				db.execSQL(sql, bindArgs);
			} else if (select(info.id).status == DownloadStatus.FAIL) {
				delete(info.id);
				insert(info);
			}
			
			Intent intent = new Intent(DownloadConstant.DOWNLOAD_ADD_ACTION);
			intent.putExtra(DownloadConstant.DOWNLOAD_EXTRAS_INFO, info);
			context.sendBroadcast(intent);
			//			LogUtil.e(this, "send new task notify");
			
			Thread.sleep(10);
			startTask();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
	/**
	 * 开启下载任务
	 */
	public void startTask() {
		if (!DownloadTask.isRunning) {
			WifiApp.downloadTask = new DownloadTask();
			WifiApp.downloadTask.start();
		}
	}
	
	/**
	 * 删除下载信息
	 * 
	 * @param id
	 *            应用id
	 */
	public synchronized void delete(int id) {
		if (WifiApp.downloadTask != null && WifiApp.downloadTask.getTaskId() == id) {
			LogUtil.i(this, "cancel downloading task");
			WifiApp.downloadTask.cancel();
		}
		try {
			DownloadInfo info = select(id);
			SQLiteDatabase db = helper.getReadableDatabase();
			String sql = "delete from " + DownloadDBHelper.TABLE_NAME + " where " + Download.ID + " = " + id;
			db.execSQL(sql);
			
			String path = DownUtil.getApkPath(id);
			if (info != null && !TextUtils.isEmpty(path)) {
				File file = new File(path);
				if (file != null && file.exists()) {
					file.delete();
				}
			}
		} catch (Exception e) {
			Log.d(TAG, "delete(int id)" + e.toString());
		}
	}
	
	/**
	 * 更新下载信息
	 */
	public synchronized void update(int appId, long progress, long total, DownloadStatus status) {
		try {
			SQLiteDatabase db = helper.getReadableDatabase();
			String where = Download.ID + " = ?";
			String[] whereArgs = {
				Integer.toString(appId)
			};
			ContentValues values = new ContentValues();
			if (progress > 0) {
				values.put(Download.PROGRESS, progress);
			}
			
			if (total > 0) {
				values.put(Download.TOTAL, total);
			}
			
			values.put(Download.STATUS, status.ordinal());
			db.update(DownloadDBHelper.TABLE_NAME, values, where, whereArgs);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
	/**
	 * 查询下载信息
	 * 
	 * @param id
	 *            应用id
	 * @return DownloadInfo
	 */
	public synchronized DownloadInfo select(int id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select * from " + DownloadDBHelper.TABLE_NAME + " where " + Download.ID + " = " + id;
		Cursor cursor = db.rawQuery(sql, null);
		DownloadInfo info = null;
		if (null != cursor) {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				info = getInfo(cursor);
			}
		}
		if (null != cursor) {
			cursor.close();
			cursor = null;
		}
		return info;
	}
	
	/**
	 * 查询下载信息
	 * 
	 * @param pck
	 *            应用包名
	 * @return DownloadInfo
	 */
	public synchronized DownloadInfo select(String pck) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select * from " + DownloadDBHelper.TABLE_NAME + " where " + Download.PACKAGE + " = '" + pck + "'";
		Cursor cursor = db.rawQuery(sql, null);
		DownloadInfo info = null;
		if (null != cursor) {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				info = getInfo(cursor);
			}
		}
		if (null != cursor) {
			cursor.close();
			cursor = null;
		}
		return info;
	}
	
	/**
	 * 查询所有下载信息
	 * 
	 * @return List
	 */
	public synchronized List<DownloadInfo> selectAll() {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.beginTransaction();
		String sql = "select * from " + DownloadDBHelper.TABLE_NAME;
		Cursor cursor = db.rawQuery(sql, null);
		ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
		if (null != cursor) {
			cursor.moveToFirst();
			for (int i = 0, size = cursor.getCount(); i < size; i++) {
				DownloadInfo info = getInfo(cursor);
				list.add(info);
				cursor.moveToNext();
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		if (null != cursor) {
			cursor.close();
			cursor = null;
		}
		return list;
	}
	
	/**
	 * 查询所有未开始下载的任务
	 * 
	 * @return List
	 */
	public synchronized List<DownloadInfo> selectAllWait() {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.beginTransaction();
		String sql = "select * from " + DownloadDBHelper.TABLE_NAME + " where " + Download.STATUS + "=" + DownloadStatus.WAIT.ordinal();
		Cursor cursor = db.rawQuery(sql, null);
		ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
		if (null != cursor) {
			cursor.moveToFirst();
			for (int i = 0, size = cursor.getCount(); i < size; i++) {
				DownloadInfo info = getInfo(cursor);
				list.add(info);
				cursor.moveToNext();
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		if (null != cursor) {
			cursor.close();
			cursor = null;
		}
		return list;
	}
	
	/**
	 * 查询所有未成功的任务
	 * 
	 * @return List
	 */
	public synchronized List<DownloadInfo> selectAllNonSuccess() {
		SQLiteDatabase db = helper.getReadableDatabase();
		db.beginTransaction();
		String sql = "select * from " + DownloadDBHelper.TABLE_NAME + " where " + Download.STATUS + "!=" + DownloadStatus.FINISH.ordinal();
		Cursor cursor = db.rawQuery(sql, null);
		ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
		if (null != cursor) {
			cursor.moveToFirst();
			for (int i = 0, size = cursor.getCount(); i < size; i++) {
				DownloadInfo info = getInfo(cursor);
				list.add(info);
				cursor.moveToNext();
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		if (null != cursor) {
			cursor.close();
			cursor = null;
		}
		return list;
	}
	
	private DownloadInfo getInfo(Cursor cursor) {
		DownloadInfo info = new DownloadInfo();
		info.id = cursor.getInt(cursor.getColumnIndex(Download.ID));
		info.detailId = cursor.getInt(cursor.getColumnIndex(Download.DETAIL_ID));
		info.total = cursor.getLong(cursor.getColumnIndex(Download.TOTAL));
		info.icon = cursor.getString(cursor.getColumnIndex(Download.ICON));
		info.path = cursor.getString(cursor.getColumnIndex(Download.PATH));
		info.progress = cursor.getLong(cursor.getColumnIndex(Download.PROGRESS));
		info.url = cursor.getString(cursor.getColumnIndex(Download.URL));
		info.name = cursor.getString(cursor.getColumnIndex(Download.NAME));
		info.packageName = cursor.getString(cursor.getColumnIndex(Download.PACKAGE));
		info.versionName = cursor.getString(cursor.getColumnIndex(Download.VERSION_NAME));
		int status = cursor.getInt(cursor.getColumnIndex(Download.STATUS));
		info.status = DownloadStatus.status(status);
		info.date = cursor.getLong(cursor.getColumnIndex(Download.DATE));
		return info;
	}
	
	/**
	 * 关闭数据库
	 */
	public synchronized void close() {
		if (null != helper) {
			helper.close();
		}
	}
}

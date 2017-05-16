package com.lxy.pad.log;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.android.lib.util.LogUtil;


/**
 * Depiction: app安装日志数据库操作
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年6月23日 下午10:00:42
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public final class DBLogDao {
	final static String     TAG = "DBDao";
	private DBLogHelper     helper;
	private static DBLogDao dao = null;
	
	private DBLogDao(Context context) {
		this.helper = new DBLogHelper(context);
	}
	
	/**
	 * 获取数据库操作实例
	 * 
	 * @param context
	 *            Context
	 * @return CartDao
	 */
	public static synchronized DBLogDao getDao(Context context) {
		if (dao == null) {
			dao = new DBLogDao(context);
		}
		return dao;
	}
	
	/**
	 * 插入一条安装记录
	 * 
	 * @param log
	 *            LogBean
	 * @return 成功返回true，否则返回false
	 */
	public synchronized boolean addLogs(LogBean log) {
		if (log == null) {
			return false;
		}
		boolean flag = true;
		try {
			SQLiteDatabase db = helper.getReadableDatabase();
			db.beginTransaction();
			String logSql = "insert into " + DBLogHelper.TABLE_LOG //
			        + " (" + DBLogTable.IMEI_PAD// 
			        + " ," + DBLogTable.LOGIN_NAME//
			        + " ," + DBLogTable.NAME //
			        + " ," + DBLogTable.STAFF_ID//
			        + " ," + DBLogTable.SHOP_NAME// 
			        + " ," + DBLogTable.CHANNEL //
			        + " ," + DBLogTable.IMEI_PHONE// 
			        + " ," + DBLogTable.OS //
			        + " ," + DBLogTable.FACTORY//
			        + " ," + DBLogTable.MODEL //
			        + " ," + DBLogTable.FROM//
			        + " ," + DBLogTable.INSTALL_WAY// 
			        + " ," + DBLogTable.APPID //
			        + " ," + DBLogTable.CPID //
			        + " ," + DBLogTable.VERSION //
			        + " ," + DBLogTable.DATE//
			        + " ," + DBLogTable.VERIFY_CODE//
			        + " ," + DBLogTable.APP_NAME //
			        + " ," + DBLogTable.STATUS//
			        + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//
			Object[] logArgs = {
			        log.imeiOfPad,
			        log.uniqueNmuber,
			        log.name,
			        log.staffId,
			        log.shopName,
			        log.channel,
			        log.imeiOfPhone,
			        log.os,
			        log.factory,
			        log.model,
			        log.from,
			        log.installWay,
			        log.appId,
			        log.cpId,
			        log.version,
			        log.date,
			        log.verifyCode,
			        log.appName,
			        log.status.name()
			};
			db.execSQL(logSql, logArgs);
			
			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
		} catch (Exception e) {
			flag = false;
			LogUtil.e(this, "updateAppData-->" + e.toString());
		}
		return flag;
	}
	
	/**
	 * 判断某个应用是否在某个手机上已经安装过
	 * 
	 * @param appId
	 *            应用id
	 * @param phoneImei
	 *            手机imei号
	 * @return 已经安装返回true，否则返回false
	 */
	public boolean isInstalled(int appId, String phoneImei) {
		boolean flag = false;
		try {
			SQLiteDatabase db = helper.getReadableDatabase();
			db.beginTransaction();
//			String sql = "select * from " + DBLogHelper.TABLE_LOG //
//			        + " where " + DBLogTable.IMEI_PHONE + " = '" + phoneImei //
//			        + "' and " + DBLogTable.APPID + "=" + appId;
			String sql = "select * from " + DBLogHelper.TABLE_LOG //
			        + " where " + DBLogTable.APPID + "=" + appId;
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0){
				flag = true;
			}
			closeCursor(cursor);
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			LogUtil.e(this, "isInstalled method-->"+e.toString());
		}
		return flag;
	}
	
	/**
	 * 删除某用户已经上传的log
	 * 
	 * @param userName
	 *            当前登录的用户
	 */
	public synchronized void deleteLog(String userName) {
		try {
			SQLiteDatabase db = helper.getReadableDatabase();
			String sql = "delete from " + DBLogHelper.TABLE_LOG + " where " + DBLogTable.STATUS + " = '" + LogStatus.UPLOADED.name() + "' and " + DBLogTable.LOGIN_NAME + " = '" + userName + "'";
			db.execSQL(sql);
			db.close();
		} catch (SQLException e) {
			LogUtil.e(this, e.toString());
		}
	}
	
	/**
	 * 修改传递的log的状态为已经上传
	 * 
	 * @param logs
	 */
	public synchronized void updateLogStatus(List<LogBean> logs) {
		if (logs == null) {
			return;
		}
		try {
			SQLiteDatabase db = helper.getReadableDatabase();
			db.beginTransaction();
			for (LogBean logBean : logs) {
				String sql = "update " + DBLogHelper.TABLE_LOG //
				        + " set " + DBLogTable.STATUS + " = '" + LogStatus.UPLOADED //
				        + "' where " + DBLogTable.IMEI_PHONE + " = '" + logBean.imeiOfPhone + "'"//
				        + " and " + DBLogTable.APPID + " = " + logBean.appId;
				db.execSQL(sql);
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
		} catch (Exception e) {
			LogUtil.e(this, e.toString());
		}
	}
	
	/**
	 * 查询所有未上传的日志
	 * 
	 * @return {@link LogBean}
	 */
	public synchronized List<LogBean> selectAllNonUploadedLog() {
		try {
			List<LogBean> logList = new ArrayList<LogBean>();
			SQLiteDatabase db = helper.getReadableDatabase();
			db.beginTransaction();
			String sql = "select distinct * from " + DBLogHelper.TABLE_LOG + " where " + DBLogTable.STATUS + " = '" + LogStatus.WAIT.name() + "'";
//			String sql = "select * from " + DBLogHelper.TABLE_LOG;
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0, count = cursor.getCount(); i < count; i++) {
					logList.add(getLog(db, cursor));
					cursor.moveToNext();
				}
			}
			closeCursor(cursor);
			db.setTransactionSuccessful();
			db.endTransaction();
			return logList;
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 查询指定促销员所有未上传的日志
	 * 
	 * @param userName
	 *            促销员登录名字
	 * @return List
	 */
	public synchronized List<LogBean> selectNonUploadedLog(String userName) {
		return selectLog(userName, LogStatus.WAIT);
	}
	
	/**
	 * 查询指定促销员所有已经上传的日志
	 * 
	 * @param userName
	 *            促销员登录名字
	 * @return List
	 */
	public synchronized List<LogBean> selectUploadedLog(String userName) {
		return selectLog(userName, LogStatus.UPLOADED);
	}
	
	private synchronized List<LogBean> selectLog(String userName, LogStatus status) {
		try {
			List<LogBean> logList = new ArrayList<LogBean>();
			SQLiteDatabase db = helper.getReadableDatabase();
			db.beginTransaction();
			String sql = "select * from " + DBLogHelper.TABLE_LOG// 
			        + " where " + DBLogTable.LOGIN_NAME + " = '" + userName// 
			        + "' and " + DBLogTable.STATUS + " = '" + status.name() + "'";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0, count = cursor.getCount(); i < count; i++) {
					logList.add(getLog(db, cursor));
					cursor.moveToNext();
				}
			}
			closeCursor(cursor);
			db.setTransactionSuccessful();
			db.endTransaction();
			return logList;
		} catch (Exception e) {
		}
		return null;
	}
	
	public synchronized List<LogBean> selectLog(String userName, String date) {
		try {
			List<LogBean> logList = new ArrayList<LogBean>();
			SQLiteDatabase db = helper.getReadableDatabase();
			db.beginTransaction();
			String sql = "select * from " + DBLogHelper.TABLE_LOG// 
			        + " where " + DBLogTable.LOGIN_NAME + " = '" + userName// 
			        + "' and " + DBLogTable.DATE + " = '" + date + "'";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0, count = cursor.getCount(); i < count; i++) {
					logList.add(getLog(db, cursor));
					cursor.moveToNext();
				}
			}
			closeCursor(cursor);
			db.setTransactionSuccessful();
			db.endTransaction();
			return logList;
		} catch (Exception e) {
		}
		return null;
	}
	
	private LogBean getLog(SQLiteDatabase db, Cursor cursor) {
		LogBean log = new LogBean();
		log.imeiOfPad = cursor.getString(cursor.getColumnIndex(DBLogTable.IMEI_PAD));
		log.uniqueNmuber = cursor.getString(cursor.getColumnIndex(DBLogTable.LOGIN_NAME));
		log.name = cursor.getString(cursor.getColumnIndex(DBLogTable.NAME));
		try {
			log.staffId = cursor.getString(cursor.getColumnIndex(DBLogTable.STAFF_ID));
		} catch (Exception e) {
		}
		log.shopName = cursor.getString(cursor.getColumnIndex(DBLogTable.SHOP_NAME));
		log.channel = cursor.getString(cursor.getColumnIndex(DBLogTable.CHANNEL));
		log.imeiOfPhone = cursor.getString(cursor.getColumnIndex(DBLogTable.IMEI_PHONE));
		log.os = cursor.getString(cursor.getColumnIndex(DBLogTable.OS));
		log.factory = cursor.getString(cursor.getColumnIndex(DBLogTable.FACTORY));
		log.model = cursor.getString(cursor.getColumnIndex(DBLogTable.MODEL));
		
		log.from = 0;
		try {
			log.from = cursor.getInt(cursor.getColumnIndex(DBLogTable.FROM));
		} catch (Exception e) {
		}
		log.installWay = cursor.getInt(cursor.getColumnIndex(DBLogTable.INSTALL_WAY));
		log.appId = cursor.getInt(cursor.getColumnIndex(DBLogTable.APPID));
		log.cpId = cursor.getInt(cursor.getColumnIndex(DBLogTable.CPID));
		log.version = cursor.getString(cursor.getColumnIndex(DBLogTable.VERSION));
		log.date = cursor.getString(cursor.getColumnIndex(DBLogTable.DATE));
		log.verifyCode = cursor.getString(cursor.getColumnIndex(DBLogTable.VERIFY_CODE));
		log.appName = cursor.getString(cursor.getColumnIndex(DBLogTable.APP_NAME));
		String status = cursor.getString(cursor.getColumnIndex(DBLogTable.STATUS));
		log.status = LogStatus.valueOf(status);
		return log;
	}
	
	private void closeCursor(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	
	/**
	 * 关闭数据库
	 */
	public synchronized void close() {
		try {
			if (null != helper) {
				helper.close();
			}
		} catch (Exception e) {
			LogUtil.f(this, "close db fail, the exception is -->" + e.toString());
		}
	}
}

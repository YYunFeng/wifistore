package com.lxy.pad.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 类描述：数据库创建助手
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-1-7 下午12:19:25
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
class DBLogHelper extends SQLiteOpenHelper {
	
	private final static String NAME      = "install_log.db";
	private final static int    VERSION   = 1;
	public final static String  TABLE_LOG = "log";
	
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            Context
	 */
	public DBLogHelper(Context context) {
		super(context, NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(getLogSql());
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
		onCreate(db);
	}
	
	private String getLogSql() {
		return "create table " + TABLE_LOG + "("//
		        + DBLogTable._ID + " integer PRIMARY KEY AUTOINCREMENT,"//
		        + DBLogTable.IMEI_PAD + " text,"//
		        + DBLogTable.LOGIN_NAME + " text,"//
		        + DBLogTable.NAME + " text,"//
		        + DBLogTable.STAFF_ID + " text,"//
		        + DBLogTable.SHOP_NAME + " text,"//
		        + DBLogTable.CHANNEL + " text,"//
		        + DBLogTable.IMEI_PHONE + " text,"//
		        + DBLogTable.OS + " text,"//
		        + DBLogTable.FACTORY + " text,"//
		        + DBLogTable.MODEL + " text,"//
		        + DBLogTable.FROM + " text,"//
		        + DBLogTable.INSTALL_WAY + " text,"//
		        + DBLogTable.APPID + " text,"//
		        + DBLogTable.CPID + " text,"//
		        + DBLogTable.VERSION + " text,"//
		        + DBLogTable.DATE + " text,"//
		        + DBLogTable.VERIFY_CODE + " text,"//
		        + DBLogTable.APP_NAME + " text,"//
		        + DBLogTable.STATUS + " text)";//
	}
}

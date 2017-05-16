package com.lxy.pad.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 类描述：下载管理数据库创建类
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午5:38:45
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
class DownloadDBHelper extends SQLiteOpenHelper {
	private final static String NAME       = "download.db";
	private final static int    VERSION    = 5;
	public final static String  TABLE_NAME = "download";
	
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            Context
	 */
	public DownloadDBHelper(Context context) {
		super(context, NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(getSql());
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}
	
	private String getSql() {
		return "create table " + TABLE_NAME//
		        + "("//
		        + Download._ID//
		        + " integer PRIMARY KEY AUTOINCREMENT,"//
		        + Download.ID//
		        + " integer, "//
		        + Download.DETAIL_ID//
		        + " integer, "//
		        + Download.PROGRESS//
		        + " text,"//
		        + Download.TOTAL//
		        + " text,"//
		        + Download.PATH//
		        + " text,"//
		        + Download.URL//
		        + " text,"//
		        + Download.ICON//
		        + " text,"//
		        + Download.NAME//
		        + " text,"//
		        + Download.PACKAGE//
		        + " text,"//
		        + Download.STATUS//
		        + " integer,"//
		        + Download.VERSION_NAME//
		        + " text,"//
		        + Download.DATE//
		        + " text)";
	}
	
	public final static class Download {
		private Download() {
		}
		
		public final static String _ID          = "_id";
		public final static String ID           = "id";
		public final static String DETAIL_ID    = "detail_id";
		public final static String PROGRESS     = "progress";
		public final static String TOTAL        = "total";
		public final static String PATH         = "path";
		public final static String URL          = "url";
		public final static String ICON         = "icon";
		public final static String NAME         = "name";
		public final static String PACKAGE      = "package";
		public final static String STATUS       = "status";
		public final static String VERSION_NAME = "version_name";
		public final static String DATE         = "date";
	}
}

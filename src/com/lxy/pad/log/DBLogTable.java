package com.lxy.pad.log;

import android.provider.BaseColumns;


/**
 * 类描述：数据库表结构定义
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-1-7 下午12:18:28
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public final class DBLogTable implements BaseColumns {
	/** pad的imei号 */
	public final static String IMEI_PAD      = "imei_pad";
	/** 促销员用户名 */
	public final static String LOGIN_NAME    = "login_name";
	/** 员工编号 */
	public final static String STAFF_ID      = "staff_id";
	/** 促销员真实姓名 */
	public final static String NAME          = "name";
	/** 营业厅名称 */
	public final static String SHOP_NAME     = "shop_name";
	/** 软件渠道号 */
	public final static String CHANNEL       = "channel";
	/** 连接的手机imei号 */
	public final static String IMEI_PHONE    = "imei_phone";
	/** 连接的手机系统 */
	public final static String OS            = "os";
	/** 连接的手机厂商 */
	public final static String FACTORY       = "factory";
	/** 连接的手机型号 */
	public final static String MODEL         = "model";
	/** 是否为在线应用，1 在线应用 ,0 离线应用 */
	public final static String FROM          = "off_line";
	/** 软件安装方式，批量安装，单个安装 */
	public final static String INSTALL_WAY   = "install_way";
	/** 软件id */
	public final static String APPID         = "app_id";
	/** cp的id */
	public final static String CPID          = "cp_id";
	/** 安装的软件版本 */
	public final static String VERSION       = "version";
	/** 安装日期 */
	public final static String DATE          = "date";
	/** apk校验码 */
	public final static String VERIFY_CODE   = "verify_code";
	public final static String APP_NAME      = "app_name";
	/** 日志状态，上传或者未上传 */
	public final static String STATUS        = "status";
	
	private DBLogTable() {
	}
	
}

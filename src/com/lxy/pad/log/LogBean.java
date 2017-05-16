package com.lxy.pad.log;

/**
 * 类描述：Log实体类
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-1-18 下午2:41:55
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class LogBean {
	/** pad的imei号 */
	public String    imeiOfPad;
	/** 唯一号 */
	public String    uniqueNmuber;
	/** 促销员真实姓名 */
	public String    name;
	/** 员工编号 */
	public String    staffId;
	/** 营业厅名称 */
	public String    shopName;
	/** 软件渠道号 */
	public String    channel;
	/** 连接的手机imei号 */
	public String    imeiOfPhone;
	/** 连接的手机系统 */
	public String    os;
	/** 连接的手机厂商 */
	public String    factory;
	/** 连接的手机型号 */
	public String    model;
	/** 是否为在线应用，1 在线应用 ,0 离线应用 */
	public int       from;
	/** 软件安装方式，批量安装，单个安装 */
	public int       installWay;
	/** 软件id */
	public int       appId;
	/** cp的id */
	public int       cpId;
	/** 安装的软件版本 */
	public String    version;
	/** 安装日期 */
	public String    date;
	/** apk校验码 */
	public String    verifyCode;
	/** 日志状态，上传或者未上传 */
	public String    appName;
	public LogStatus status;
	
	public LogBean() {
		status = LogStatus.WAIT;
	}
	
	@Override
	public String toString() {
		return imeiOfPad + "," + uniqueNmuber + "," + name + "," + staffId + "," + shopName + "," + channel + "," + imeiOfPhone + "," + os + "," + factory + "," + model + "," + from + "," + installWay + "," + appId + "," + cpId + "," + version + "," + date + "," + verifyCode;
	}
	
	public String toLogString() {
		return imeiOfPad + "," + uniqueNmuber//唯一号
		        + "," + name//真实姓名
		        + "," + staffId//唯一号
		        + "," + shopName + "," + channel + "," + imeiOfPhone + "," + os + "," + factory + "," + model + "," + from + "," + installWay + "," + appId + "," + cpId + "," + version + "," + date + "," + verifyCode;
	}
}

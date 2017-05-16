package com.lxy.pad.download;

/**
 * 类描述：下载常量信息
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午5:37:48
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public final class DownloadConstant {
	private DownloadConstant() {
	}
	
	/** 下载失败广播 */
	public final static String DOWNLOAD_START_ACTION      = "com.platomix.appstore.download.START";
	/** 添加新下载任务广播 */
	public final static String DOWNLOAD_ADD_ACTION        = "com.platomix.appstore.download.ADD";
	/** 下载进度更新广播 */
	public final static String DOWNLOAD_UPDATE_ACTION     = "com.platomix.appstore.download.UPDATE";
	/** 下载失败广播 */
	public final static String DOWNLOAD_FAIL_ACTION       = "com.platomix.appstore.download.FAIL";
	/** 下载取消广播 */
	public final static String DOWNLOAD_CANCEL_ACTION     = "com.platomix.appstore.download.CANCEL";
	/** 单个任务下载完成广播 */
	public final static String DOWNLOAD_SUCCESS_ACTION    = "com.platomix.appstore.download.SUCCESS";
	/** 全部下载完成广播 */
	public final static String DOWNLOAD_ALL_FINISH_ACTION = "com.platomix.appstore.download.ALL_FINISH";
	/** 下载速度广播 */
	public final static String DOWNLOAD_SPEED_ACTION      = "com.platomix.appstore.download.SPEED";
	/** 下载广播携带的信息 */
	public final static String DOWNLOAD_EXTRAS_INFO       = "extras_info";
}

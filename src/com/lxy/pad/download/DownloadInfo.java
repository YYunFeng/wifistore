package com.lxy.pad.download;

import java.io.Serializable;

import com.lxy.pad.download.DownloadStatus;


/**
 * 类描述：下载信息实体类
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午5:39:11
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class DownloadInfo implements Serializable {
	private static final long serialVersionUID = -3730414850366852256L;
	/** 应用id */
	public int                id;
	/** cpid */
	public int                detailId;
	/** 日期 */
	public long               date;
	/** 下载进度 */
	public long               progress;
	/** 应用总大小 */
	public long               total;
	/** 软件保存路径 */
	public String             path;
	/** 下载地址 */
	public String             url;
	/** 软件图标网络地址 */
	public String             icon;
	/** 软件名称 */
	public String             name;
	/** 软件包名 */
	public String             packageName;
	/** 下载状态 */
	public DownloadStatus     status;
	/** 下载错误时的信息 */
	public String             error;
	public String             versionName      = "1.0";
	public String             verifyCode;
	
	public DownloadInfo() {
		this.id = 0;
		this.detailId = 0;
		this.date = System.currentTimeMillis();
		this.progress = 0;
		this.total = 0;
		this.path = null;
		this.url = null;
		this.icon = null;
		this.name = null;
		this.packageName = null;
		this.status = DownloadStatus.WAIT;
	}
	
	@Override
	public String toString() {
		return "DownloadInfo [id=" + id + ", detailId=" + detailId + ", date=" + date + ", progress=" + progress + ", total=" + total + ", path=" + path + ", url=" + url + ", icon=" + icon + ", name=" + name + ", packageName=" + packageName + ", status=" + status + ", error=" + error + ", versionName=" + versionName + "]";
	}
}

package com.lxy.wifistore.receiver;

import java.io.Serializable;


/**
 * Depiction: 实时上传安装日志实体类
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Email: kevin185@foxmail.com
 * <p>
 * Create Date: 2015年3月9日 下午2:36:41
 * <p>
 * Modify:
 * 
 * @version 1.0
 * @since 1.0
 */
public class LogInfo implements Serializable {
	private static final long serialVersionUID = -4881864176326306868L;
	public String             cid;
	public int                appType;
	public String             padImei;
	public String             loginUser;
	public String             salerName;
	public String             salerNo;
	public String             shopName;
	public String             phoneImei;
	public String             phoneOsVer;
	public String             phoneVenderName;
	public String             phoneModelName;
	public int                installModel;
	public int                appId;
	public int                cpid;
	public String             appVersionName;
	public String             installTime;
	
	public LogInfo() {
	}
	
}

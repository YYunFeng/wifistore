package com.lxy.wifistore.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;


/**
 * 类描述：软件基本信息实体类
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午4:43:18
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class AppEntity implements Serializable {
	private static final long serialVersionUID = -4238420926184905877L;
	/** 软件id */
	@SerializedName ("appId")
	public int                id;
	/** 类目id */
	@SerializedName ("cid")
	public int                cid;                                     // 类目id
	/** 软件图标url */
	@SerializedName ("logo")
	public String             icon;                                    // 图标
	/** 软件名称 */
	@SerializedName ("name")
	public String             name;                                    // 名
	/** 软件包名 */
	@SerializedName ("packageName")
	public String             pckName;                                 // 包名
	/** 开发商名称 */
	@SerializedName ("cpName")
	public String             author;                                  // 开发商
	/** 软件大小 */
	@SerializedName ("pkgSize")
	public String             size             = "";                   // 大小
	/** 软件版本号 */
	@SerializedName ("appVersion")
	public String             versionCode;                             //版本号
	/** 软件版本名称 */
	@SerializedName ("version_name")
	public String             versionName;                             //版本名称
	/** 软件价格 */
	@SerializedName ("price")
	public int                price;                                   //价格
	/** 软件下载次数 */
	@SerializedName ("downloads")
	public int                download;                                // 下载次数
	/** 软件评论数 */
	@SerializedName ("cn")
	public int                commentCount;                            // 评论数
	/** 软件星级 */
	@SerializedName ("star")
	public int                star;                                    //星级
	/** 软件简介 */
	@SerializedName ("intro")
	public String             summary;                                 //说明
	/** 软件更新日期 */
	@SerializedName ("update")
	public String             time;                                    // 上架时间
	/** 软件购买时间 */
	@SerializedName ("boughtTime")
	public String             boughtTime;                              //购买时间
	/** 软件类别 */
	@SerializedName ("defaultCategoryName")
	public String             category;                                //软件类别
	/** 软件默认截图url */
	@SerializedName ("defaultScreenshot")
	public String             shotscreen;                              // 某张截图链接
	/** 软件安装需要的最低系统版本 */
	@SerializedName ("lv")
	public String             minVersion       = "Android 2.2";        // 最低系统版本
	/** 软件校验码 */
	@SerializedName ("apkCode")
	public String             code;                                    // 校验码
	/** 软件所有截图url */
	@SerializedName ("picList")
	public List<String>       shots;                                   //软件截图
	public String[]           snaps;                                   //软件截图
	/** 软件激活标志 */
	@SerializedName ("activation")
	public int                activation;                              // 激活标志
	@SerializedName ("downPath")
	public String             downPath;                                //下载地址
	                                                                    
	public AppEntity() {
	}
	
	@Override
	public String toString() {
		return "AppEntity [id=" + id + ", cid=" + cid + ", icon=" + icon + ", name=" + name + ", pckName=" + pckName + ", author=" + author + ", size=" + size + ", versionCode=" + versionCode + ", versionName=" + versionName + ", price=" + price + ", download=" + download + ", commentCount=" + commentCount + ", star=" + star + ", summary=" + summary + ", time=" + time + ", boughtTime=" + boughtTime + ", category=" + category + ", shotscreen=" + shotscreen + ", minVersion=" + minVersion + ", code=" + code + ", snaps=" + snaps + ", activation=" + activation + ", downPath=" + downPath + "]";
	}
	
}

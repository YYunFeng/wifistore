package com.lxy.wifistore.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;


/**
 * Depiction: 排行榜实体类
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年7月23日 上午11:59:45
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class BoutiqueBean implements Serializable {
	private static final long serialVersionUID = -6773923034320716686L;
	
	@SerializedName ("resultObj")
	public ResultEntity       result;
	public int                an;
	public int                tp;
	@SerializedName ("appBaseInfoObjs")
	public List<AppEntity>    appList;
	
	public BoutiqueBean() {
	}
	
}

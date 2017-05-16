package com.lxy.wifistore.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


/**
 * 
 * 类描述：数据返回结果实体类
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午5:09:37  
 * <p>     
 * 修改备注：    
 * <p>
 * @version 1.0
 * @since 1.0
 */
public class ResultEntity implements Serializable {
	private static final long serialVersionUID = 7455220140192506041L;
	
	public ResultEntity() {
	}
	
	@SerializedName ("resultCode")
	public int    resultCode = -1; // 结果状态码
	@SerializedName ("resultMsg")
	public String resultMsg;      // 结果信息
	                               
	/**
	 * 请求数据是否成功
	 * 
	 * @return boolean 成功返回true，否则返回false
	 */
	public boolean isSuccess() {
		return resultCode == 0;
	}
	
	@Override
	public String toString() {
		return "ResultEntity [resultCode=" + resultCode + ", resultMsg=" + resultMsg + "]";
	}
}

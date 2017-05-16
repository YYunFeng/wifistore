package com.lxy.pad.log;

/**
 * 类描述：文件上传回调接口
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-1-18 下午5:09:47  
 * <p>     
 * 修改备注：    
 * <p>
 * @version 1.0
 * @since 1.0 
 */
public interface OnUploadListener {
	/**
	 * 开始上传文件时回调此方法
	 * 
	 * @param path 文件路径
	 */
	void onUploadStart(String path);
	
	/**
	 * 上传文件结束时回调此方法
	 * 
	 * @param success 是否成功
	 * @param date 日期
	 * @param path 文件路径
	 */
	void onUploadFinish(boolean success, String date, String path);
}

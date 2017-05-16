package com.lxy.pad.download;

import java.io.Serializable;

import com.lxy.pad.download.DownloadStatus;


/**
 * 
 * 类描述：等待中 WAIT,暂停 PAUSE,下载中 GOING,已完成 FINISH,暂停状态 PAUSE, 下载失败FAIL
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午5:39:55  
 * <p>     
 * 修改备注：    
 * <p>
 * @version 1.0
 * @since 1.0
 */
public enum DownloadStatus implements Serializable {
	/** 等待中 */
	WAIT,
	/** 暂停状态 */
	GOING,
	/** 下载完成 */
	FINISH,
	/** 下载失败 */
	FAIL;
	
	/**
	 * 根据传入的值获取相应枚举
	 * 
	 * @param value
	 * @return DownloadStatus
	 */
	public static DownloadStatus status(int value) {
		DownloadStatus status = DownloadStatus.WAIT;
		switch (value) {
			case 0:
				status = DownloadStatus.WAIT;
				break;
			case 1:
				status = DownloadStatus.GOING;
				break;
			case 2:
				status = DownloadStatus.FINISH;
				break;
			case 3:
				status = DownloadStatus.FAIL;
				break;
			default:
				break;
		}
		return status;
	}
}

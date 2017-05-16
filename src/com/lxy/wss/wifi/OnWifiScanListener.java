package com.lxy.wss.wifi;

import java.util.List;

import android.net.wifi.ScanResult;


/**
 * Depiction:wifi热点扫描回调接口
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年8月16日 下午12:49:05
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public interface OnWifiScanListener {
	/**
	 * @param retList
	 *            扫描到的所有wifi热点
	 * @param strongRet
	 *            扫描到的信号最强的wifi热点
	 */
	void onWifiScanFinish(List<ScanResult> retList, ScanResult strongRet);
}

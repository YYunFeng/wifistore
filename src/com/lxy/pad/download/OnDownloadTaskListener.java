package com.lxy.pad.download;

/**         
 * Depiction: 同步下载文件回调接口
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年5月23日 下午1:52:46
 * <p>
 * Modify:
 * <p> 
 * @version 1.0
 * @since 1.0
 */
public interface OnDownloadTaskListener {
	void onProgress(long progress,long total,String name,int appId);
	void onSuccess(int appId);
	void onFail(int appId);
	void onDownFinish();
}

package com.lxy.pad.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.android.lib.util.LogUtil;


/**
 * 类描述：日志记录工具
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-1-18 下午2:45:15
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class LogWriter {
	
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            Context
	 * @param dir
	 *            目录
	 */
	public LogWriter() {
	}
	
	/**
	 * 写入日志数据
	 * 
	 * @param log
	 *            {@link LogBean}
	 * @return 成功返回true，否则返回false
	 */
	public boolean write(LogBean log, String path) {
		if (log == null) {
			return false;
		}
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				LogUtil.e(this, e.toString());
			}
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(path, true));
			writer.append(log.toLogString().replace("\n", ""));
			writer.append("\r\n");
		} catch (FileNotFoundException e) {
			LogUtil.f(this, e.toString());
			return false;
		} catch (UnsupportedEncodingException e) {
			LogUtil.f(this, e.toString());
			return false;
		} catch (IOException e) {
			LogUtil.f(this, e.toString());
			return false;
		} finally {
			try {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} catch (Exception e) {
				LogUtil.f(this, e.toString());
			}
		}
		return true;
	}
}

package com.lxy.pad.log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Handler;
import android.os.Message;

import com.android.lib.util.JsonUtil;
import com.android.lib.util.LogUtil;
import com.google.gson.annotations.SerializedName;
import com.lxy.wifistore.util.HttpConfig;


/**
 * 类描述：日志上传工具
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-1-18 下午5:06:39  
 * <p>     
 * 修改备注：    
 * <p>
 * @version 1.0
 * @since 1.0 
 */
public class LogUpload extends Thread {
	private final String     uploadUrl = HttpConfig.getLogUrl();
	private OnUploadListener onUploadListener;
	private File             file;
	private boolean          running   = false;
	private UploadHandler    handler;
	
	/**
	 * 构造函数
	 *    
	 * @param path 日志文件路径
	 */
	public LogUpload(String path) {
		this(new File(path));
	}
	
	/**
	 * 构造函数
	 *    
	 * @param file file对象
	 */
	public LogUpload(File file) {
		if (file != null) {
			this.file = file;
		}
		handler = new UploadHandler();
	}
	
	/**    
	 * running      
	 * @return  the running       
	 */
	
	public boolean isRunning() {
		return running;
	}
	
	/**    
	 * @param running the running to set    
	 */
	private void setRunning(boolean running) {
		this.running = running;
	}
	
	public void run() {
		setRunning(true);
		if (file == null) {
			handler.sendMessage(makeMsg(END, null, null, false));
			setRunning(false);
			return;
		} else {
			handler.sendMessage(makeMsg(START, null, null, false));
		}
		
		String result = upload(uploadUrl, file.getPath());
		LogUtil.e(this, "result is -->" + result);
		
		Reuslt rt = JsonUtil.parse(result, Reuslt.class);
		boolean flag = rt != null && rt.isSuccess();
		String date = "";
		try {
			String[] array = file.getName().split("\\.");
			String[] array_ = array[0].split("_");
			date = array_[0];
		} catch (Exception e) {
		}
		handler.sendMessage(makeMsg(END, file.getPath(), date, flag));
		setRunning(false);
	}
	
	private Message makeMsg(int what, String path, String date, boolean success) {
		Message msg = new Message();
		msg.what = what;
		ResultMessage res = new ResultMessage();
		res.path = path;
		res.isSuccess = success;
		res.date = date;
		msg.obj = res;
		return msg;
	}
	
	/**
	 * 上传文件
	 * 
	 * @param url 接口地址
	 * @param path 本地文件路径
	 * @return 返回结果
	 */
	private String upload(String url, String path) {
		LogUtil.i(this, "log url-->"+url);
		LogUtil.i(this, "log path-->"+path);
		HttpURLConnection conn = null;
		StringBuffer resultData = null;
		BufferedReader buffered = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", "Android");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setConnectTimeout(20 * 1000);
			conn.setReadTimeout(5 * 60 * 1000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			String BOUNDARY = "--------------";
			String MULTIPART_FORM_DATA = "multipart/form-data";
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + ";boundary=" + BOUNDARY);
			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
			byte[] content = getBytesFromFile(new File(path));
			if (content == null) {
				return null;
			}
			StringBuilder split = new StringBuilder();
			split.append("--");
			split.append(BOUNDARY);
			split.append("\r\n");
			split.append("Content-Disposition: form-data;name=\"uplodafile\";filename=\"" + file.getName() + "\"\r\n");
			split.append("Content-Type: application/octet-stream; charset=UTF-8\r\n");
			split.append("Content-Transfer-Encoding: binary" + "\r\n\r\n");
			outStream.write(split.toString().getBytes("UTF-8"));
			outStream.write(content, 0, content.length);
			outStream.write("\r\n".getBytes());
			byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();
			outStream.write(end_data);
			outStream.flush();
			outStream.close();
			
			resultData = new StringBuffer();
			int code = conn.getResponseCode();
			if (code == 200) {
				buffered = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				String readData = null;
				while ((readData = buffered.readLine()) != null) {
					resultData.append(readData);
				}
			}
		} catch (MalformedURLException e1) {
			LogUtil.e(this, e1.toString());
		} catch (UnsupportedEncodingException e2) {
			LogUtil.e(this, e2.toString());
		} catch (IOException e3) {
			LogUtil.e(this, e3.toString());
		} finally {
			try {
				if (buffered != null) {
					buffered.close();
				}
			} catch (IOException e) {
				LogUtil.e(this, e.toString());
			}
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		if(resultData == null){
			return null;
		}
		return resultData.toString();
	}
	
	private byte[] getBytesFromFile(File file) {
		byte[] data = null;
		try {
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
			byte[] buf = new byte[4096];
			int n;
			while ((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			in.close();
			out.close();
			data = out.toByteArray();
			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (IOException e) {
			LogUtil.e(this, "get bytes from file process error!");
		}
		return data;
	}
	
	public OnUploadListener getOnUploadListener() {
		return onUploadListener;
	}
	
	public void setOnUploadListener(OnUploadListener onUploadListener) {
		this.onUploadListener = onUploadListener;
	}
	
	class Reuslt {
		@SerializedName ("resultCode")
		public int    code;
		@SerializedName ("resultMsg")
		public String msg;
		
		/**    
		 * flag      
		 * @return  the flag       
		 */
		
		public boolean isSuccess() {
			return code == 0;
		}
	}
	
	final int START = 100;
	final int END   = 101;
	
	private class UploadHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (onUploadListener != null) {
				if (msg.what == START) {
					onUploadListener.onUploadStart(file.getPath());
				} else if (msg.what == END) {
					ResultMessage res = (ResultMessage) msg.obj;
					onUploadListener.onUploadFinish(res.isSuccess, res.date, res.path);
				}
			}
		}
	}//end UploadHandler
	
	private class ResultMessage {
		public boolean isSuccess;
		public String  path;
		public String  date;
	}
}

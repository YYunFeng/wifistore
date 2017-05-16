package com.lxy.wifistore.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 类描述：apk校验码工具
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-2-1 下午6:42:59
 * <p>
 * 修改备注：
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class VerifyCode {
	final static String TAG = "VerifyUtil";
	private File        file;
	
	/**
	 * 要校验的文件路径
	 * 
	 * @param path
	 */
	public VerifyCode(String path) {
		this(new File(path));
	}
	
	/**
	 * 要校验的文件
	 * 
	 * @param file
	 */
	public VerifyCode(File file) {
		this.file = file;
		if (file == null) {
			throw new RuntimeException("the file is null");
		}
		if (this.file.length() < 4096) {
			throw new RuntimeException("the file is a invalid apk");
		}
	}
	
	private byte[] getData() {
		byte[] data = new byte[4100];//4096+4
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			int readSize = bis.read(data);
			if (readSize > -1) {
				long len = file.length();
				String length = String.valueOf(len);
				char last = length.charAt(length.length() - 1);
				byte[] size = intToByte(last);
				data[4096] = size[0];
				data[4097] = size[1];
				data[4098] = size[2];
				data[4099] = size[3];
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bis!=null){
				try {
	                bis.close();
                } catch (IOException e) {
	                e.printStackTrace();
                }
			}
		}
		return data;
	}
	
	/**
	 * 生成校验码
	 * 
	 * @return 校验码
	 */
	public String verifyCode() {
		return sha256(md5(getData()));
	}
	
	/**
	 * sha256加密方式
	 * 
	 * @param source
	 *            加密的源串
	 * @return 密文
	 */
	public String sha256(String source) {
		String result;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
			mDigest.update(source.getBytes());
			result = bytesToHexString(mDigest.digest()).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			result = String.valueOf(source.hashCode()).toUpperCase();
		}
		return result;
	}
	
	/**
	 * MD5加密
	 * 
	 * @param source
	 *            要加密的字节数组
	 * @return 加密后的结果
	 */
	public String md5(byte[] source) {
		String result;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(source);
			result = bytesToHexString(mDigest.digest()).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			result = String.valueOf(source.hashCode()).toUpperCase();
		}
		return result;
	}
	
	/**
	 * MD5加密
	 * 
	 * @param source
	 *            要加密的源串
	 * @return 加密后的结果
	 */
	public String md5(String source) {
		String result;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(source.getBytes());
			result = bytesToHexString(mDigest.digest()).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			result = String.valueOf(source.hashCode()).toUpperCase();
		}
		return result;
	}
	
	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
	
	/**
	 * 整形转换为字节数组
	 * 
	 * @param i
	 *            要转换的整形
	 * @return 转换后的字节数组
	 */
	public byte[] intToByte(int i) {
		byte[] abyte0 = new byte[4];
		abyte0[0] = (byte) (0xff & i);
		abyte0[1] = (byte) ((0xff00 & i) >> 8);
		abyte0[2] = (byte) ((0xff0000 & i) >> 16);
		abyte0[3] = (byte) ((0xff000000 & i) >> 24);
		return abyte0;
	}
}

package com.lianggao.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;

public class Crypt {
	/**
	 * 字符串编码转换的实现方法
	 * @param str 待转换编码的字符串
	 * @param newCharset 目标编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
//	public static String changeCharset(String str, String newCharset)
//			throws UnsupportedEncodingException {
//		if (str != null) {
//			//用默认字符编码解码字符串。
//			byte[] bs = str.getBytes();
//			//用新的字符编码生成字符串
//			return new String(bs, newCharset);
//		}
//		return null;
//	}
	private static final String CHARSET = "UTF-8";

	//DEC 加密过程
	public static String DESEncrypt(String pToEncrypt, String sKey) throws Exception {
//		pToEncrypt=  changeCharset(pToEncrypt,"utf-8");
		byte[] key = sKey.getBytes(CHARSET);
		// 初始化向量
		IvParameterSpec iv = new IvParameterSpec(key);
		DESKeySpec desKey = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成securekey
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, iv);
		// 现在，获取数据并加密
		// 正式执行加密操作
		byte[] inputByteArray = pToEncrypt.getBytes();// 把字符串放到byte数组中
		byte encryptedData[] = cipher.doFinal(inputByteArray);
		String value = byteArr2HexStr(encryptedData);
		//System.out.println(pToEncrypt+"=>"+value);
		return value;
	}

	// 把加密后的字节数组转换成十六进制的字符串
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	// DEC 解密过程
	public static String DESDecrypt(String pToDecrypt, String sKey) throws Exception {
		if (pToDecrypt == "")
			return "";
		if (pToDecrypt.contains(";")) {
			pToDecrypt = pToDecrypt.replace(";", "");
		}
		if (pToDecrypt == "")
			return "";

		byte[] inputByteArray = new byte[pToDecrypt.length() / 2];
		for (int x = 0; x < pToDecrypt.length() / 2; x++) {
			int i = Integer.parseInt(pToDecrypt.substring(x * 2, x * 2 + 2), 16);
			inputByteArray[x] = (byte) i;
		}

		byte[] key = sKey.getBytes(); // 密钥
		// 初始化向量
		IvParameterSpec iv = new IvParameterSpec(key);
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(key);
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, iv);
		// 真正开始解密操作
		byte decryptedData[] = cipher.doFinal(inputByteArray);
		String value = new String(decryptedData, "UTF-8");
		//System.out.println(pToDecrypt+"=>"+value);
		return value;// decryptedData;
	}
}

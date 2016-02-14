package com.apputils.example.utils.verify;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.annotation.SuppressLint;
import android.util.Base64;

/**
 * AES加密：AES/CBC/ZeroPadding 128位模式，默认向量为：L+\\~f4,Ir)b$=pkf android加密方式
 * AES/CBC/NOPadding 加密key:12345678901234567890123456789012
 */
public class MCrypt {

	private static String iv = "L+\\~f4,Ir)b$=pkf";
	private static IvParameterSpec ivspec;
	private static SecretKeySpec keyspec;
	private Cipher cipher;
	private static String secretKey = "12345678901234567890123456789012";
	private static MCrypt mCrypt;
	private static boolean inited;

	/**
	 * 加密
	 */
	public static String cusEncrypt(String encryptStr) {
		try {
			return Base64.encodeToString(getInstance().encrypt(encryptStr), Base64.NO_WRAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 */
	public static String cusDecrypt(String decryptStr) {
		try {
			return new String(getInstance().decrypt(MCrypt.bytesToHex(Base64.decode(decryptStr, Base64.NO_WRAP))))
					.trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 设置向量和key
	 * 
	 * @param iv
	 *            向量
	 * @param secretKey
	 *            加密key
	 */
	public static void init(String iv, String secretKey) {
		if (!inited) {
			ivspec = new IvParameterSpec(iv.getBytes());
			keyspec = new SecretKeySpec(secretKey.getBytes(), "AES");
			inited = true;
		}
	}

	/**
	 * 设置向量和key
	 * 
	 * @param iv
	 *            向量
	 * @param secretKey
	 *            加密key
	 */
	public static void init(String secretKey) {
		if (!inited) {
			ivspec = new IvParameterSpec(iv.getBytes());
			keyspec = new SecretKeySpec(secretKey.getBytes(), "AES");
			inited = true;
		}
	}

	private static MCrypt getInstance() {
		if (mCrypt == null) {
			mCrypt = new MCrypt();
		}
		return mCrypt;
	}

	private MCrypt() {
		init(iv, secretKey);
		try {
			cipher = Cipher.getInstance("AES/CBC/NOPadding");
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		} catch (NoSuchPaddingException e) {

			e.printStackTrace();
		}
	}

	@SuppressLint("TrulyRandom")
	private byte[] encrypt(String text) throws Exception {
		if (text == null || text.length() == 0)
			throw new Exception("Empty string");

		byte[] encrypted = null;

		try {
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			encrypted = cipher.doFinal(padString(text).getBytes());
		} catch (Exception e) {
			throw new Exception("[encrypt] " + e.getMessage());
		}

		return encrypted;
	}

	private byte[] decrypt(String code) throws Exception {
		if (code == null || code.length() == 0)
			throw new Exception("Empty string");

		byte[] decrypted = null;

		try {
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

			decrypted = cipher.doFinal(hexToBytes(code));
		} catch (Exception e) {
			throw new Exception("[decrypt] " + e.getMessage());
		}
		return decrypted;
	}

	private static String bytesToHex(byte[] data) {
		if (data == null) {
			return null;
		}

		int len = data.length;
		String str = "";
		for (int i = 0; i < len; i++) {
			if ((data[i] & 0xFF) < 16)
				str = str + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
			else
				str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
		}
		return str;
	}

	private static byte[] hexToBytes(String str) {
		if (str == null) {
			return null;
		} else if (str.length() < 2) {
			return null;
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];
			for (int i = 0; i < len; i++) {
				buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
			}
			return buffer;
		}
	}

	private static String padString(String source) {
		char paddingChar = ' ';
		int size = 16;
		int x = source.length() % size;
		int padLength = size - x;

		for (int i = 0; i < padLength; i++) {
			source += paddingChar;
		}
		return source;
	}
}
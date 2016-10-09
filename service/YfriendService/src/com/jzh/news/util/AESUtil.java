package com.jzh.news.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
	private final static String HEX = "0123456789ABCDEF";
	private final static int JELLY_BEAN_4_2 = 17;



	/**
	 * 加密
	 * 
	 * @param key
	 *            密钥
	 * @param src
	 *            加密文本
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String key, String src) throws Exception {
		byte[] rawKey = getRawKey(key.getBytes());
		byte[] result = encrypt(rawKey, src.getBytes());
		return toHex(result);
	}

	/**
	 * 解密
	 * 
	 * @param key
	 *            密钥
	 * @param encrypted
	 *            待揭秘文本
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String key, String encrypted) throws Exception {
		byte[] rawKey = getRawKey(key.getBytes());
		byte[] enc = toByte(encrypted);
		byte[] result = decrypt(rawKey, enc);
		return new String(result);
	}

	/**
	 * 获取256位的加密密钥
	 * 
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = null;
//		// 在4.2以上版本中，SecureRandom获取方式发生了改变
//		if (android.os.Build.VERSION.SDK_INT >= JELLY_BEAN_4_2) {
//			sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
//		} else {
			sr = SecureRandom.getInstance("SHA1PRNG");
//		}
		sr.setSeed(seed);
		// 256 bits or 128 bits,192bits
		kgen.init(256, sr);
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	/**
	 * 真正的加密过程
	 * 
	 * @param key
	 * @param src
	 * @return
	 * @throws Exception
	 */
	private static byte[] encrypt(byte[] key, byte[] src) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(src);
		return encrypted;
	}

	/**
	 * 真正的解密过程
	 * 
	 * @param key
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] key, byte[] encrypted)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	public static String toHex(String txt) {
		return toHex(txt.getBytes());
	}

	public static String fromHex(String hex) {
		return new String(toByte(hex));
	}

	public static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
					16).byteValue();
		return result;
	}

	public static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}
}

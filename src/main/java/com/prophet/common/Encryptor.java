package com.prophet.common;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;  
import javax.crypto.Mac;  
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 加密算法基础工具类
 *
 */
@SuppressWarnings("restriction")
public abstract class Encryptor {
	public static final String KEY_SHA = "SHA";  
    public static final String KEY_MD5 = "MD5";  
  
    /** 
     * MAC算法可选以下多种算法 
     *  
     * HmacMD5  
     * HmacSHA1  
     * HmacSHA256  
     * HmacSHA384  
     * HmacSHA512 
     */  
    public static final String KEY_MAC = "HmacMD5";  
  
    /** 
     * BASE64解密 
     *  
     * @param key 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decryptBASE64(String key) throws Exception {  
        return (new BASE64Decoder()).decodeBuffer(key);  
    }  
  
    /** 
     * BASE64加密 
     *  
     * @param key 
     * @return 
     * @throws Exception 
     */  
    public static String encryptBASE64(byte[] key) throws Exception {  
        return (new BASE64Encoder()).encodeBuffer(key);  
    }  
  
    /** 
     * MD5加密 
     *  
     * @param data 
     * @return 
     * @throws Exception 
     */  
    public static String encryptMD5(String data){  
        MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance(KEY_MD5);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}  
        md5.update(data.getBytes());  
        BigInteger bi = new BigInteger(md5.digest());
        return bi.toString(16);  
    }  
  
    /** 
     * SHA加密 
     *  
     * @param data 
     * @return 
     * @throws Exception 
     */  
    public static String encryptSHA(String data){
        MessageDigest sha = null;
		try {
			sha = MessageDigest.getInstance(KEY_SHA);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}  
        sha.update(data.getBytes());  
        BigInteger bi = new BigInteger(sha.digest());
        return bi.toString(32);
    } 
  
    /** 
     * 初始化HMAC密钥 
     *  
     * @return 
     * @throws Exception 
     */  
    public static String initMacKey() throws Exception {  
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);  
        SecretKey secretKey = keyGenerator.generateKey();  
        return encryptBASE64(secretKey.getEncoded());  
    }  
  
    /** 
     * HMAC加密 
     *  
     * @param data 
     * @param key 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptHMAC(byte[] data, String key) throws Exception {  
        SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);  
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());  
        mac.init(secretKey);  
        return mac.doFinal(data);  
  
    } 
}

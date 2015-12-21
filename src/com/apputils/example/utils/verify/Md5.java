package com.apputils.example.utils.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class Md5 {
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };
    
    public static String md5(byte[] bytes) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(bytes, 0, bytes.length);
        return bufferToHex(messageDigest.digest());
    }
    
    public static String md5(File file) throws Exception {
        return md5(new FileInputStream(file));
    }
    
    public static String md5(InputStream in) throws Exception {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bt = new byte[1024];
            int ilen = 0;
            while ((ilen = in.read(bt)) >= 0) {
                messageDigest.update(bt, 0, ilen);
            }
            return bufferToHex(messageDigest.digest());
        }
        finally {
            in.close();
        }
    }
    
    /** 
     * ToDo：截获Exception加密
     * @author M2_
     * @param str
     * @return 
     * @return String 
     * @throws 
     */
    public static String mD5(String str) {
        try {
            return md5(str.getBytes());
        }
        catch (Exception e) {
        }
        return str;
    }
    
    /** 
     * ToDo：不截获Exception加密
     * @author M2_
     * @param str
     * @return
     * @throws Exception 
     * @return String 
     * @throws 
     */
    public static String md5(String str) throws Exception {
        return md5(str.getBytes());
    }
    
    public static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }
    
    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }
    
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[bt >> 4 & 0X0F];
        char c1 = hexDigits[bt & 0X0F];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
    
    /**
     * 描述：MD5加密 .原理描述
     * @param str 要加密的字符串
     * @return String 加密的字符串
     */
    @Deprecated
    public final static String MD5(String str) {
        char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] strTemp = str.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte tmp[] = mdTemp.digest(); // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char strs[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                strs[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                strs[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            return new String(strs).toUpperCase(); // 换后的结果转换为字符串
        }
        catch (Exception e) {
            return null;
        }
    }
    
}

package com.apputils.example.xmlencrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BuildDesInitFrame {

    public static void main(String arg[]) {
	File file = new File("./initFrame.xml");
	try {
	    String test = readToString(file);
	    XMLDES des = new XMLDES();// 使用默认密钥
	    outPut2Xml(des.encrypt(test));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static String readToString(File file) {
	Long filelength = file.length(); // 获取文件长度
	byte[] filecontent = new byte[filelength.intValue()];
	try {
	    FileInputStream in = new FileInputStream(file);
	    in.read(filecontent);
	    in.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return new String(filecontent);// 返回文件内容,默认编码
    }

    private static void outPut2Xml(String str) {
	byte[] buff = new byte[] {};
	try {
	    buff = str.getBytes();
	    FileOutputStream out = new FileOutputStream("./initFrame_p.xml");
	    out.write(buff, 0, buff.length);

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}

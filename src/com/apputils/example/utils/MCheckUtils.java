package com.apputils.example.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.text.TextUtils;

public class MCheckUtils {

	/**
	 * 判断手机号码格式是否正确
	 * @param mobiles
	 * @return
	 */
	public static boolean idMobileNO(String mobiles) {
		String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

	/**
	 * 密码正则验证
	 * 
	 * @param loginPwdSet
	 * @return
	 */
	public static boolean loginPwdSet(String loginPwdSet) {
		String telRegex = "[0-9a-zA-Z,.~!@#$%^&*()_-]{6,16}";// "[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(loginPwdSet))
			return false;
		else
			return loginPwdSet.matches(telRegex);
	}

	/**
	 * 验证昵称格式是否正确
	 * 
	 * @param nickname
	 * @return
	 */
	public static boolean NickNameCheck(String nickname) {
		String telRegex = "[a-zA-Z\\u4e00-\\u9fa5]{3,16}";
		if (TextUtils.isEmpty(nickname))
			return false;
		else
			return nickname.matches(telRegex);
	}

	/**
	 * 验证身份证号格式是否正确
	 */
	public static boolean idCardNO(String idCardNo) {
		String telRegex = "([1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$)|([1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)";
		if (TextUtils.isEmpty(idCardNo))
			return false;
		else
			return idCardNo.matches(telRegex);
	}

	/***
	 * 验证身份证后六位格式是否正确
	 * 
	 * @param idcardId
	 * @return
	 */
	public static boolean idcardId(String idcardId) {
		String telRegex = "[0-9]{5}x";// "[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		String telRegex1 = "[0-9]{6}";
		if (TextUtils.isEmpty(idcardId))
			return false;
		else
			return idcardId.matches(telRegex) || idcardId.matches(telRegex1);
	}

	/**
	 * 验证真实姓名是否正确
	 */
	public static boolean realName(String realName) {
		String name = "(^[\u4E00-\u9FA5\uF900-\uFA2D]+$)";
		if (TextUtils.isEmpty(realName))
			return false;
		else
			return realName.matches(name);
	}

	/**
	 * 格式化金额，添加千分符，小数点后保留两位
	 * 
	 * @param money
	 * @return
	 */
	public static String FormatMoney(String money) {
		if (money.length() == 0) {
			return "";
		}
		money = money.replace(",", "");
		String str_money = "";
		BigDecimal bd = new BigDecimal(money);
		DecimalFormat myformat = new DecimalFormat();
		myformat.setRoundingMode(RoundingMode.DOWN);
		myformat.applyPattern("##,##0.00");
		str_money = myformat.format(bd);
		return str_money;
	}
}

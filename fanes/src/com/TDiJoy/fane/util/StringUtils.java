package com.TDiJoy.fane.util;

import java.io.FileInputStream;

import org.apache.http.util.EncodingUtils;

public class StringUtils {
	public static String getCategory(int category) {
		String sCategory = "其它类型";
		switch (category) {
		case 1:
			sCategory = "角色扮演";
			break;
		case 2:
			sCategory = "动作冒险";
			break;
		case 3:
			sCategory = "休闲益智";
			break;
		case 4:
			sCategory = "策略塔防";
			break;
		case 5:
			sCategory = "经营养成";
			break;
		case 6:
			sCategory = "赛车竞速";
			break;
		case 7:
			sCategory = "体育运动";
			break;
		case 8:
			sCategory = "飞行射击";
			break;
		case 9:
			sCategory = "桌面棋牌";
			break;
		case 10:
			sCategory = "网络游戏";
			break;
		}
		return String.format("游戏分类：%s", sCategory);
	}

	public static String getSize(int size) {
		return String.format("软件大小：%dMB", size);
	}

	public static String getPrice(String price) {
		return String.format("售        价：%s", price);
	}

	public static String getLanguage(String language) {
		return String.format("软件语言：%s", language);
	}

	public static String getAge(int age) {
		return String.format("适合年龄：%d+", age);
	}

	public static String getCtrlType(int ctrlType) {
		// mark 控制器文字描述
		int byte1 = ctrlType & 1;
		int byte2 = ctrlType >> 1 & 1;
		int byte3 = ctrlType >> 2 & 1;
		int byte4 = ctrlType >> 3 & 1;
//		return (byte2 == 1 ? "遥控器" + (byte1 == 1 ? "/" : "") : "") + (byte3 == 1 ? "手机" + (byte1 == 1 ? "/" : "") : "") + (byte4 == 1 ? "游戏手柄" : "") + (byte1 == 1 && byte4 == 1 && (byte2 == 1 || byte3 == 1)? "/" : "");
//		return (byte2 == 1 ? "遥控器" : "") + (byte3 == 1 ? (byte2 == 1 ? "/" : "") + "手机" : "") + (byte4 == 1 ? (byte3 == 1 ? "/" : "") + "游戏手柄" : "") + (byte1 == 1 ? (byte4 == 1 ? "/" : "") + "智能体感手柄" : "");
		String str = "";
		if (byte1 == 1)
			str += (str.length() == 0 ? "" : "\n") + "智能体感手柄";
		if (byte2 == 1)
			str += (str.length() == 0 ? "" : "\n") + "遥控器";
		if (byte3 == 1)
			str += (str.length() == 0 ? "" : "\n") + "手机";
		if (byte4 == 1)
			str += (str.length() == 0 ? "" : "\n") + "游戏手柄";
//		return "遥控器/手机/游戏手柄/智能体感手柄";
		return str;
	}

	public static String readTxt(String path) {
		String txt = "";
		try {
			FileInputStream fin = new FileInputStream(path);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			txt = EncodingUtils.getString(buffer, "unicode");
			fin.close();// 关闭资源
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txt;
	}
}

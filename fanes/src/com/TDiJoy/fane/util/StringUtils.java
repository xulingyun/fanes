package com.TDiJoy.fane.util;

import java.io.FileInputStream;

import org.apache.http.util.EncodingUtils;

public class StringUtils {
	public static String getCategory(int category) {
		String sCategory = "��������";
		switch (category) {
		case 1:
			sCategory = "��ɫ����";
			break;
		case 2:
			sCategory = "����ð��";
			break;
		case 3:
			sCategory = "��������";
			break;
		case 4:
			sCategory = "��������";
			break;
		case 5:
			sCategory = "��Ӫ����";
			break;
		case 6:
			sCategory = "��������";
			break;
		case 7:
			sCategory = "�����˶�";
			break;
		case 8:
			sCategory = "�������";
			break;
		case 9:
			sCategory = "��������";
			break;
		case 10:
			sCategory = "������Ϸ";
			break;
		}
		return String.format("��Ϸ���ࣺ%s", sCategory);
	}

	public static String getSize(int size) {
		return String.format("�����С��%dMB", size);
	}

	public static String getPrice(String price) {
		return String.format("��        �ۣ�%s", price);
	}

	public static String getLanguage(String language) {
		return String.format("������ԣ�%s", language);
	}

	public static String getAge(int age) {
		return String.format("�ʺ����䣺%d+", age);
	}

	public static String getCtrlType(int ctrlType) {
		// mark ��������������
		int byte1 = ctrlType & 1;
		int byte2 = ctrlType >> 1 & 1;
		int byte3 = ctrlType >> 2 & 1;
		int byte4 = ctrlType >> 3 & 1;
//		return (byte2 == 1 ? "ң����" + (byte1 == 1 ? "/" : "") : "") + (byte3 == 1 ? "�ֻ�" + (byte1 == 1 ? "/" : "") : "") + (byte4 == 1 ? "��Ϸ�ֱ�" : "") + (byte1 == 1 && byte4 == 1 && (byte2 == 1 || byte3 == 1)? "/" : "");
//		return (byte2 == 1 ? "ң����" : "") + (byte3 == 1 ? (byte2 == 1 ? "/" : "") + "�ֻ�" : "") + (byte4 == 1 ? (byte3 == 1 ? "/" : "") + "��Ϸ�ֱ�" : "") + (byte1 == 1 ? (byte4 == 1 ? "/" : "") + "��������ֱ�" : "");
		String str = "";
		if (byte1 == 1)
			str += (str.length() == 0 ? "" : "\n") + "��������ֱ�";
		if (byte2 == 1)
			str += (str.length() == 0 ? "" : "\n") + "ң����";
		if (byte3 == 1)
			str += (str.length() == 0 ? "" : "\n") + "�ֻ�";
		if (byte4 == 1)
			str += (str.length() == 0 ? "" : "\n") + "��Ϸ�ֱ�";
//		return "ң����/�ֻ�/��Ϸ�ֱ�/��������ֱ�";
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
			fin.close();// �ر���Դ
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txt;
	}
}

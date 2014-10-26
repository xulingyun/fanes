package com.TDiJoy.fane.util;

import org.json.JSONException;
import org.json.JSONObject;

public class AndroidSysInfo {
	public String	SysVersion;
	public String	SysModel;
	public String	SysProduct;
	public String	SysInfo;
	public String	SysManufacturer;
	public String	SysCpu;
	public String	HwMemory;
	public String	HwCpu;
	
	public String sysInfo() {
		String infoStr = "";
		try {
			JSONObject json = new JSONObject();
			json.put("SysVersion", SysVersion);
			json.put("SysModel", SysModel);
			json.put("SysProduct", SysProduct);
			json.put("SysInfo", SysInfo);
			json.put("SysManufacturer", SysManufacturer);
			json.put("SysCpu", SysCpu);
			infoStr = json.toString(0).replace("\n", "").replace("\r", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return infoStr;
	}
	
	public String hwInfo() {
		String infoStr = "";
		try {
			JSONObject json = new JSONObject();
			json.put("HwMemory", HwMemory);
			json.put("HwCpu", HwCpu);
			infoStr = json.toString(0).replace("\n", "").replace("\r", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return infoStr;
	}
}

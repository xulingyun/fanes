package com.TDiJoy.fane.util;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class SystemInfo {

	
	public AndroidSysInfo GetAndroidSystemInfo ()
	{
		AndroidSysInfo		info;
		info = new AndroidSysInfo ();
		
		info.SysVersion 		= android.os.Build.VERSION.RELEASE;
		info.SysModel 			= android.os.Build.MODEL;
		info.SysProduct			= android.os.Build.PRODUCT;
		info.SysInfo			= android.os.Build.DISPLAY;
		info.SysManufacturer	= android.os.Build.MANUFACTURER;
		info.SysCpu				= android.os.Build.CPU_ABI;
		
		info.HwMemory			= GetTotalMemory ();
		info.HwCpu				= GetCPUInfo ();
		return info;
	}
	
	private String GetTotalMemory ()
	{
		String str = "", strMem = "", totalMem = "0 kB";
		try {
			Process pp = Runtime.getRuntime().exec("cat /proc/meminfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (int i = 1; i < 100; i++) 
			{
				str = input.readLine();
				if (str != null) 
				{
					if (str.indexOf("MemTotal") > -1) //���ҵ�Total Memory������
					{						
						strMem = str.substring(str.indexOf(":") + 1, str.length());	//��ȡTotal Memory
						totalMem = strMem.trim();//ȥ�ո�
						//Log.i("system info", "==== Total Memory: " + totalMem);
						break;
					}
				}
				else 
				{
					break; //�ļ���β
				} 
			}
		} 
		catch (IOException ex) {ex.printStackTrace(); }//����Ĭ��ֵ
		
		return totalMem;
	}

	private String GetCPUInfo ()
	{
		int cpuCount = 0;
		String BogoMips = "";
		try {
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (int i = 1; i < 100; i++) 
			{
				String str = input.readLine();
				if (str != null) 
				{
					if (str.indexOf("processor") > -1) 
					{
						cpuCount ++;
						continue;
					}
					else if (str.indexOf("BogoMIPS") > -1)
					{
						String strBogoMips = str.substring(str.indexOf(":") + 1, str.length());
						BogoMips = strBogoMips.trim();
						continue;
					}
				}
				else //�ļ���β
				{
					break;
				} 
			}
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		}		
		
		return "CPU process number: " + cpuCount + "; BogoMIPS: " + BogoMips;
	}
	
}

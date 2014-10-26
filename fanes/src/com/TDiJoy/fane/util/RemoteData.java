package com.TDiJoy.fane.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.util.Log;

public class RemoteData {

	//private int			_MAX_DEV_NUMBER = 4;
	
	private FileOutputStream 	_OutToServer;
	private FileInputStream 	_InFromServer;
	
	private boolean 	_Initialed = false;
	private boolean 	_ThreadEnable = false;
	
	private boolean		_CSready = false;
	private boolean		_SCready = false;
	private int			_Pid = 0;
	private String		_NattPath = "";
	
	private int			_DevNumber = 0;
	//private int			_DevType[];
	
	
	//大厅resume时调用
	public boolean ipcInitial ()
	{
		if (_Initialed) return true;
		
		if (!ipcServiceCheck ()) return false;
		
		//_DevType = new int[_MAX_DEV_NUMBER];
		
		_Pid = android.os.Process.myPid ();
		
		_ThreadEnable = true;
        Thread thread = new Thread(new ipcProcess());
        thread.start();	
		
		return true;
	}
	
	//大厅pause时调用
	public void ipcStop ()
	{
		_ThreadEnable = false;
		try {
			if (_CSready) {_CSready = false; _OutToServer.close ();}
			if (_SCready) {_SCready = false; _InFromServer.close ();}
		} 
		catch (IOException e) {e.printStackTrace();}
		_Initialed = false;
	}
	
	//获取已连接手柄个数
	public int ipcGetRemoteNumber ()
	{
		return _DevNumber;
	}
	
	public int remote_exist_usbhost(Context context)
	   {  
	      int      __VID = 9639;         
	      int      __PID = 13961;     
	      
	      UsbManager      _UsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
	      if(_UsbManager == null) {
//	    	  Log.i("USB check", "_UsbManager fail");
	    	  return 0;}
	      
	      HashMap<String, UsbDevice> deviceList = _UsbManager.getDeviceList();
	      if(deviceList.isEmpty())  {
//	    	  Log.i("USB check", "deviceList isEmpty");
	    	  return 0;}
	      int count = 0;
	      for (UsbDevice device : deviceList.values()) {       
//	          Log.i("USB check", "device list: " + device.getVendorId() + " --- " + device.getProductId());
	         
	          if ((__VID == device.getVendorId()) && (__PID == device.getProductId()))
	          {
//	             Log.i("USB check", "getInterfaceCount: " + device.getInterfaceCount ());

	             if (device.getInterfaceCount () < 1) {continue;}
	             UsbInterface _Intf = device.getInterface(0);

//	            Log.i("USB check", "getEndpointCount: " + _Intf.getEndpointCount());

	            if (_Intf.getEndpointCount() < 1) {continue;}
	                                    
	            for (int i=0; i<_Intf.getEndpointCount(); i++)
	            {
	                UsbEndpoint ep = _Intf.getEndpoint(i);
	                
	                //Log.i("USB check", "UsbEndpoint type: " + ep.getType() + "; direction: " + ep.getDirection() + " getEndpointNumber: " + ep.getEndpointNumber());
	                //Log.i("USB check", "UsbEndpointw: " + _EndpointIntrW);
	                
	                if (ep.getType() != UsbConstants.USB_ENDPOINT_XFER_INT) {continue;}
	                if (ep.getDirection() == UsbConstants.USB_DIR_IN) {count++;}
	            }
	          }
	       }  
	          
	      return count;
	   }

		
	//回到大厅设置为true
	public void ipcSetMouse (boolean enable)
	{
		byte databtn[]	= {0x37, 0x04, 0x01, (byte) (enable ? 0x01 : 0x00)};
		byte datamove[] = {0x42, 0x05, 0x31, 0x01, (byte) (enable ? 0x01 : 0x00)};
    	
		ipcSendData (4, databtn);
    	ipcSendData (5, datamove);
	}

	public void ipcSetGameConfigFile (String cfgfile)
	{
		byte		buff[] = cfgfile.getBytes();
		ipcSetGameConfig (cfgfile.length(), buff);
	}

	public void ipcSetGameConfigStop ()
	{
		byte		buff[] = new byte[1];
		ipcSetGameConfig (0, buff);
	}
	
	///////////////////////////////////////////
	
	private void ipcSendData (int len, byte[] data)
	{
		if (!_CSready)		return;
		if (len > 300) 		return;
		if (data == null)	return;
		
		byte[] buff = new byte[308];
		
		buff[0] = (byte)(_Pid ); 		//取最低8位放到3下标
		buff[1] = (byte)(_Pid >>> 8); 	//取次低8位放到2下标
		buff[2] = (byte)(_Pid >>> 16);	//取次高8为放到1下标
		buff[3] = (byte)(_Pid >>> 24);	//取最高8位放到0下标
		buff[4] = (byte)(len ); 		//取最低8位放到3下标
		buff[5] = (byte)(len >>> 8); 	//取次低8位放到2下标
		buff[6] = (byte)(len >>> 16);	//取次高8为放到1下标
		buff[7] = (byte)(len >>> 24);	//取最高8位放到0下标
		
		for (int i=0; i<len; i++) {buff[8+i] = data[i];}
		
		//Log.i("nptest", "send message:  ");
		
		try {_OutToServer.write(buff);} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	private boolean ipcFindServer ()
	{
		String pipeName[] = {"/tmp/", "/data/", "/home/", "/mnt/"};
		for (int i=0; i<pipeName.length; i++)
		{
			String	filename = pipeName[i] + "natt_cs";
			try{
				File file = new File(filename);
				if (file.exists()) 
				{
					Log.i("nptest", "open fifo ok: " + filename);
					_OutToServer = new FileOutputStream (file, true);
					_NattPath = pipeName[i];
					_CSready = true;
					break;
				}
			}
			catch (IOException io) {Log.i("nptest", "open fifo error: " + io.toString());}
		}
				
		return _CSready;
	}
	
	private boolean ipcOpenReadpipe ()
	{
		String	filename = _NattPath + "natt_sc_" + _Pid;
		try{
			File file = new File(filename);
			if (file.exists()) 
			{
				Log.i("nptest", "open fifo ok: " + filename);
				_InFromServer = new FileInputStream (file);
				_SCready = true;
			}
		}
		catch (IOException io) {Log.i("nptest", "open fifo error: " + io.toString());}
		
		return _SCready;
	}
	
	private class ipcProcess implements Runnable {
		private long 	lastMsgTime;
		private long 	lastDataTime;

		public ipcProcess ()
		{
		}
		
		@Override
		public void run() 
		{
			lastMsgTime = (long) 0;
			lastDataTime = (long) 0;
			
			while (_ThreadEnable)
			{
				if (!_CSready) 
				{
					if (!ipcFindServer()) {SystemClock.sleep (1000); continue;}
				}

				long sysTime = System.currentTimeMillis();
				if ((sysTime - lastMsgTime) > 200)
				{
					byte buff[] = {(byte) 0x87};
					ipcSendData (1, buff);
					lastMsgTime = sysTime;
				}
					
				if (!_SCready)
				{
					if (!ipcOpenReadpipe()) {SystemClock.sleep (1000); continue;}
					else {ipcSetMouse(true);}
				}
				
				try {					
					byte[] buffer = new byte[308];  						
					int size = _InFromServer.read(buffer);					
					//Log.i ("remote data", "buff size: " + size);
					
					int pid = (buffer[0] & 0x000000ff) | ((buffer[1] << 8) & 0x0000ff00) | ((buffer[2] << 16) & 0x00ff0000) | ((buffer[3] << 24) & 0xff000000);
					int len = (buffer[4] & 0x000000ff) | ((buffer[5] << 8) & 0x0000ff00) | ((buffer[6] << 16) & 0x00ff0000) | ((buffer[7] << 24) & 0xff000000);
					
					if (buffer[8] == (byte)0xA2) {lastDataTime = sysTime;}
					if ((sysTime - lastDataTime) > 500) {_DevNumber = 0;}
					
					ipcParseCommand (len, buffer);
					//Log.i ("remote data", "buff size: " + size + "; pid = " + pid + "; len = " + len);
				} 
				catch (IOException e) {e.printStackTrace();}
				
				SystemClock.sleep (5); 
			}			
		}
	};
		
	private void ipcParseCommand (int len, byte[] data)
	{
		if (len < 2) {return;}
		
		int head = 8;
		switch (data[head])
		{
			case (byte) 0xA3: //IN_SEND_DEV_INFO_EXT
			{
				int dlen = data[head+1];
				int	devnum = data[head+2];
				
				if (dlen != len) {break;}

	            //for (int i=0; i<devnum; i++)
	            //{
	            //    int id     = data[head + 3 + (i * 7) + 0];
	            //    _DevType[id]    = data[head + 3 + (i * 7) + 1];
				//	Log.i("remote data", "dev type: " + _DevType[id]);
	            //}	
	            
				_DevNumber = devnum;
				//Log.i("remote data", "dev number: " + _DevNumber);
				
				break;
			}
			default:
				break;
		}		
	}
	
	private boolean ipcServiceCheck ()
	{
        Runtime runtime = Runtime.getRuntime();
        try {
        	Process process = runtime.exec("ps");
        	//获得结果的输入流
        	InputStream input = process.getInputStream();
        	BufferedReader br = new BufferedReader(new InputStreamReader(input));
        	String strLine;
        	while(null != (strLine = br.readLine())){
        		//System.out.println(strLine);
            	int indexOf = strLine.indexOf("TDRemoteCtrl");                    	
            	if (indexOf != -1) {Log.i ("nptest", "TDRemoteCtrl found"); return true;}
        	}
        	
        }
        catch (IOException e) {e.printStackTrace();}  		
		return false;		
	}
	
	private boolean ipcSetGameConfig (int len, byte[] data)
	{
	    if (len > 200)      return false;

	    byte		buff[];
	    buff = new byte[204];
	    buff[0] = 0x45;
	    buff[1] = (byte) (len + 4);
	    buff[2] = (byte) 0xfd;
	    buff[3] = (byte) len;
	    for (int i=0; i<len; i++)
	    {
	    	buff[4+i] = data[i];
	    }
	    ipcSendData (buff[1], buff);
	    return true;
	}	
}

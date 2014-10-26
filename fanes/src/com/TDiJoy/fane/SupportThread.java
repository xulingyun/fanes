package com.TDiJoy.fane;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import android.content.res.AssetManager;
import android.util.Log;

public class SupportThread {
	
	
	public static void run()
	{
		String Data_path = "/data/data/com.TDiJoy.fane/TDRemoteCtrl";
		File Data_file = new File(Data_path);
    	if(!Data_file.exists())
    	{	
//    		AssetManager am = com.TDiJoy.fane.LoadActivity.pointer.getAssets();
//    		InputStream inStream = am.open(Data_path + ".png");
    		try{
    			int bytesum	= 0;
    			int	byteread = 0;	
    			//File oldfile = new File(oldPath);	
    			//if(oldfile.exists())	 
    			{	 
        			//InputStream inStream = new FileInputStream(oldPath);
        			AssetManager am = com.TDiJoy.fane.LoadActivity.pointer.getAssets();
        			File N_DIR = new File("/data/data/com.TDiJoy.fane");
			    	if(!N_DIR.exists())
			    	{
//			    		Log.i("3DiJoy", String.format("Will Create Dir:[%s]", ObjectPath + "/" + FileOrDirName[i]));
			    		N_DIR.mkdir();
			    	}
			    	InputStream inStream = am.open("TDRemoteCtrl.png");
        			FileOutputStream fs = new FileOutputStream(Data_path);	
        			byte[] buffer = new byte[1444];	 
        			//int	 length;	 
        			//Log.w("3DiJoy", "Enter flags2");
        			while((byteread = inStream.read(buffer)) != -1)	 
        			{	//Log.w("3DiJoy", "Enter flags3");
    	    			bytesum += byteread;	 
    	    			//System.out.println(bytesum);	
    	    			fs.write(buffer, 0, byteread);	
    	    			//Log.w("3DiJoy", "Enter flags4");
        			}
        			
        		
        			
//        			pw.write("/system/etc/permissions/android.hardware.usb.host.xml");
        			inStream = am.open("android.hardware.usb.host.xml");
        			fs = new FileOutputStream("/data/data/com.TDiJoy.fane/android.hardware.usb.host.xml");	
        			buffer = new byte[1444];	 
        			//int	 length;	 
        			//Log.w("3DiJoy", "Enter flags2");
        			while((byteread = inStream.read(buffer)) != -1)	 
        			{	//Log.w("3DiJoy", "Enter flags3");
    	    			bytesum += byteread;	 
    	    			//System.out.println(bytesum);	
    	    			fs.write(buffer, 0, byteread);	
    	    			//Log.w("3DiJoy", "Enter flags4");
        			}
        			inStream.close();	
        			Process proc = Runtime.getRuntime().exec("su");
        			PrintWriter pw = new PrintWriter(proc.getOutputStream());
        			String command = "cp /data/data/com.TDiJoy.fane/android.hardware.usb.host.xml /system/etc/permissions/";
        			pw.println(command);
        			pw.flush();
        			pw.close();
    			}	
    			
    			
    		}	
    		catch	(Exception	 e)	 
    		{	
    			System.out.println( "error");	
    			e.printStackTrace();	
    		}
//			lastIndexOfDot = a.lastIndexOf('.');
//			fileNameLength = FileOrDirName[i].length();
//			extension = FileOrDirName[i].substring(0, lastIndexOfDot);
//			CopyAssets(AssetsPath + "/" + FileOrDirName[i]/* + ".png"*/, ObjectPath + "/" + extension);
//			extension = FileOrDirName[i].substring(lastIndexOfDot+1, fileNameLength);
//    		Log.e("3DiJoy", "Data not exist in data.");
//    		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
//    		{
//    			File path = Environment.getExternalStorageDirectory();
//    			if(com.TDiJoy.fane.LoadActivity.pointer != null)
//    			{
//    				File ExternalFilespath = com.TDiJoy.fane.LoadActivity.pointer.getApplicationContext().getExternalFilesDir(null);
//    				Log.e("3DiJoy", "getExternalFilesDir.");
//    				if(ExternalFilespath != null)
//    				{
//    					Log.e("3DiJoy", "ExternalFilesDir find.");
//    					path = ExternalFilespath;
//    				}
//    			}
//    			Data_path = path.getPath();
//    			File Data_file2 = new File("/data/TDRemoteCtrl");
//    			if(!Data_file2.exists())
//    			{
//        			StatFs statfs = new StatFs(path.getPath());
//        			long blocSize = statfs.getBlockSize();
//        			long availaBlock = statfs.getAvailableBlocks();
//        			long availaleSize = (blocSize*availaBlock)/1024/1024;
//
//        			if(availaleSize > 80)
//        			{
//        				Log.e("3DiJoy", "Data not exist in ExternalStorage. Creat it!");
//        				File Data_file3 = new File(Data_path);
//        				if(!Data_file3.exists())
//        				{
//        					Data_file3.mkdir();
//        				}
//        				Log.e("3DiJoy", "Data_path:" + Data_path);
//        				LoadActivity.CopyAssetsPath("Fisher", Data_path);
//        			}
//        			else
//        			{
//            			path = Environment.getDataDirectory();
//            			StatFs statfs2 = new StatFs(path.getPath());
//            			blocSize = statfs2.getBlockSize();
//            			availaBlock = statfs2.getAvailableBlocks();
//            			availaleSize = (blocSize*availaBlock)/1024/1024;
//            			
//            			if(availaleSize > 80)
//            			{
//                			Data_path = "/data/data/com.TDiJoy.Fisher/";
//                			Log.e("3DiJoy", "Data_path:" + Data_path);
//                    		LoadActivity.CopyAssetsPath("Fisher", Data_path);    				
//            			}
//            			else
//            			{
//            				LoadActivity.error_code = 2;
//            				return;
//            			}
//        			}
//    			}
//    		}
//    		else
//    		{
//    			File path = Environment.getDataDirectory();
//    			StatFs statfs = new StatFs(path.getPath());
//    			long blocSize = statfs.getBlockSize();
//    			long availaBlock = statfs.getAvailableBlocks();
//    			long availaleSize = (blocSize*availaBlock)/1024/1024;
//    			
//    			if(availaleSize > 80)
//    			{
//        			Data_path = "/data/data/com.TDiJoy.Fisher/";
//        			Log.e("3DiJoy", "Data_path:" + Data_path);
//            		LoadActivity.CopyAssetsPath("Fisher", Data_path);    				
//    			}
//    			else
//    			{
//    				LoadActivity.error_code = 1;
//    				return;
//    			}
//    		}
    	}
    	else
    	{
    		Log.e("3DiJoy", "Data already exist.");
    	}
	try {
		//String command = "su cd /data && su (./test.sh &)";"chmod 777 /data/data/com.TDiJoy.fane/start.sh && cd /data/data/com.TDiJoy.fane && ./start.sh &";
		/*Process process = Runtime.getRuntime().exec(command);*/
//		BufferedReader  bufferedReader  = new BufferedReader(new InputStreamReader(process.getInputStream()));
//		String ls_1;   
//		while ((ls_1=bufferedReader.readLine()) != null)   
//			Log.e("fane",ls_1);   
//		bufferedReader.close();
//		command = "cd /data/data/com.TDiJoy.fane";
//		Runtime.getRuntime().exec(command);
//		command = "./TDRemoteCtrl";
//		Runtime.getRuntime().exec(command);
	} catch (Exception e) {e.printStackTrace();}
    	LoadActivity.enable_boot = true;
    	//LoadActivity.pointer.finish();
    }
}

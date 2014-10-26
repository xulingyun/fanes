package com.TDiJoy.fane.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public final class HttpDownLoad {

	private static String _TAG 		= "HttpDownLoad";
	private int FILESIZE 			= 262144; //256k 
	
	private int DownloadFileSize 	= 0;
	private float DownloadProcess 	= 0.f;
	
	private boolean StopDownloadProcess = false;
	
	
	
	/**
	 * 
	 * @param urlStr
	 * @param path
	 * @param fileName
	 * @return -1:�ļ����س��� -2:�ļ�����ȡ�� -3:ʣ��ռ䲻��,�ļ�����ȡ�� 0:�ļ����سɹ� 1:�ļ��Ѿ�����
	 */
	public int DlFileXML(String urlStr, String path, String fileName,
			boolean overWrite) {
		if (IsFileExist(path, fileName)) {
			if (!overWrite) {
				return 1;
			}
		}

		String tmpFileName = fileName + ".tmp";

		StopDownloadProcess = false;
		InputStream inputStream = null;
		try {
			Log.i(_TAG, "downFile: " + urlStr);
			inputStream = getInputStreamFromURL(urlStr);
			if (inputStream == null) {
				return -1;
			}

			// long availsize = getStorageAvailable ();
			if (!getStorageAvailable(path)) {
				inputStream.close();
				return -3;
			}

			// Log.i(_TAG, "writeFromInput in");
			File resultFile = writeFromInputXML(path, tmpFileName, inputStream);
			// Log.i(_TAG, "writeFromInput out: " + resultFile);
			if ((resultFile == null) && (!StopDownloadProcess)) {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
		}

		if (StopDownloadProcess) {
			File file = new File(path, tmpFileName);
			if (file.isFile() && file.exists()) {
				file.delete();
			}
			return -2;
		}

		File from = new File(path, tmpFileName);
		File to = new File(path, fileName);
		from.renameTo(to);

		String command = "chmod 777 " + path + fileName;
		try {
			Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	
	
	
	/**  
	*   
	* @param urlStr  
	* @param path  
	* @param fileName  
	* @return   
	*      -1:�ļ����س���  
	*      -2:�ļ�����ȡ��  
	*      -3:ʣ��ռ䲻��,�ļ�����ȡ��  
	*       0:�ļ����سɹ�  
	*       1:�ļ��Ѿ�����  
	*/  
	public int DlFile(String urlStr, String path, String fileName, boolean overWrite)
	{  
		if (IsFileExist (path, fileName))
		{
			if (!overWrite) {return 1;}
		}
		
		String tmpFileName = fileName + ".tmp";
		
		StopDownloadProcess = false;
		InputStream inputStream = null;  
		try 
		{
			Log.i(_TAG, "downFile: " + urlStr);
			inputStream = getInputStreamFromURL(urlStr);
			if (inputStream == null) {return -1;}

//			long availsize = getStorageAvailable ();			
			if (!getStorageAvailable (path)) {inputStream.close(); return -3;}

			//Log.i(_TAG, "writeFromInput in");
			File resultFile = writeFromInput(path, tmpFileName, inputStream);  
			//Log.i(_TAG, "writeFromInput out: " + resultFile);
			if((resultFile == null) && (!StopDownloadProcess)) {return -1;}  
		}
		catch (Exception e) {e.printStackTrace(); return -1;}
		finally
		{
			try 
			{  
				if (inputStream != null) {inputStream.close();}  
			} 
			catch (IOException e) {e.printStackTrace(); return -1;}
		}
		
		if (StopDownloadProcess) 
		{
			File file = new File(path, tmpFileName);  
			if (file.isFile() && file.exists()) {file.delete();}
			return -2;
		}
		
		File from =new File(path, tmpFileName);
		File to =new File(path, fileName);
		from.renameTo(to);
		
    	String command = "chmod 777 " + path + fileName;
    	try {
    		Runtime.getRuntime().exec(command);
    	} 
    	catch (Exception e) {e.printStackTrace();}		
		
		return 0;  
	}

	public float DlGetProcess ()
	{
		return DownloadProcess;
	}
	
	public void DlStop ()
	{
		StopDownloadProcess = true;
	}
	
//////////////////////////////////////////////////////////////////	
	
	private boolean IsFileExist (String path, String fileName)
	{
		File file = new File(path, fileName);  
		if (file != null) {return file.exists();}  
		return false;
	}
	
	private File createDir (String path, String fileName)
	{
		File	dir;
		if (fileName == "") //dir  
		{
			dir = new File(path); 
			dir.mkdir();
			String command = "chmod 777 " + path;
			try {
				Runtime.getRuntime().exec(command);
			} 
			catch (Exception e) {e.printStackTrace();}			
		}
		else 				//file
		{
			dir = new File(path, fileName);
		}		  
		return dir;  
	}	
	
	
	
	private File writeFromInputXML(String path, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		// int receivedlen = 0;
		try {
			createDir(path, "");
			file = createDir(path, fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			/**
			 * ԭ���Ĵ��룬�������ע�͵�
			 */
			int delaycount = 0;
			boolean fisrt = true;
			while (!StopDownloadProcess) {
				// Log.i(_TAG,
				// "Download in =================== "+input.available() +
				// " ===== " + delaycount + " ===== " + fileName);
				if (fisrt) {
					output.write(input.read());
					fisrt = false;
				}
				if (input.available() <= 0) {
					if (delaycount > 50) {
						break;
					} else {
						delaycount++;
						Thread.sleep(100);
						continue;
					}
				}
				delaycount = 0;

				int size = input.read(buffer);
				if (size == -1) {
					// Log.i(_TAG, "Download in =========size == -1==========");
					break;
				}
				// Log.i(_TAG, "Package size: " + size + ((size > 10000) ?
				// "++++++" : ""));

				output.write(buffer, 0, size);
				// receivedlen += size;

				// if (DownloadFileSize <= 0) {DownloadProcess = 0;}
				// else {DownloadProcess = (float)receivedlen /
				// DownloadFileSize;}
				// DownloadProcess = (DownloadProcess > 1.f) ? 1.f :
				// DownloadProcess;
				// //Log.i(_TAG, "DownloadProcess: " + DownloadProcess +
				// "  Package size: " + size + ((size > 10000) ? "++++++" :
				// ""));
				//
				// if (receivedlen == DownloadFileSize) {break;}
				// Thread.sleep(100);
			}

			// �µ����ط���
			// int len = 0;
			// while ((len = input.read(buffer)) != -1) {
			// output.write(buffer, 0, len);
			// }

			if (!StopDownloadProcess) {
				output.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		// if (receivedlen != DownloadFileSize) {return null;}
		return file;
	}
	
	
	private File writeFromInput (String path,String fileName,InputStream input)
	{
		File file = null;  
		OutputStream output = null;  
		int receivedlen = 0;
		try 
		{  
			createDir(path, "");  
			file = createDir(path, fileName);  
			output = new FileOutputStream(file);  
			byte[] buffer = new byte[FILESIZE]; 
			int delaycount = 0;
			while(!StopDownloadProcess)
			{  
//				Log.i(_TAG, "Download in =================== "+input.available() + " ===== " + delaycount + " ===== " + fileName);
				if (input.available() <= 0) {
					if (delaycount > 50) 	{break;}
					else					{delaycount ++; Thread.sleep(100); continue;}
				}
				delaycount = 0;
				
				int size = input.read(buffer);
				if (size == -1) {
//					Log.i(_TAG, "Download in =========size == -1=========="); 
					break;}
//				Log.i(_TAG, "Package size: " + size + ((size > 10000) ? "++++++" : ""));
				
				output.write(buffer, 0, size);  
				receivedlen += size;
								
				if (DownloadFileSize <= 0) {DownloadProcess = 0;} 
				else {DownloadProcess = (float)receivedlen / DownloadFileSize;} 
				DownloadProcess = (DownloadProcess > 1.f) ? 1.f : DownloadProcess;
				//Log.i(_TAG, "DownloadProcess: " + DownloadProcess + "  Package size: " + size + ((size > 10000) ? "++++++" : ""));
				
				if (receivedlen == DownloadFileSize) {break;}
				Thread.sleep(100);
			}
			if (!StopDownloadProcess) {output.flush();}  
		}
		catch (Exception e) {e.printStackTrace(); return null;}  
		finally
		{  
			try 
			{  
				if (output != null) {output.close();}  
			} 
			catch (IOException e) {e.printStackTrace(); return null;}
		}  
		if (receivedlen != DownloadFileSize) {return null;}		
		return file;  
	}
	
	/**  
	* ����URL�õ�������  
	* @param urlStr  
	* @return  
	*/  
	private InputStream getInputStreamFromURL(String urlStr) 
	{  
		HttpURLConnection urlConn = null;  
		InputStream inputStream = null;  
		try 
		{
			URL url = new URL(urlStr);  
			urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setConnectTimeout(5000);
			urlConn.connect();
			DownloadFileSize = urlConn.getContentLength();
			inputStream = urlConn.getInputStream();  
		} 
		catch (MalformedURLException e) {e.printStackTrace();} 
		catch (IOException e) {e.printStackTrace();}  
		return inputStream;  
	}  
	
	private boolean getStorageAvailable (String path)
	{
		long internal_availablesize = 0;
		long external_availablesize = 0;
		
		// data 
        File root = Environment.getDataDirectory ();  
        StatFs sf = new StatFs(root.getPath());  
        long blockSize = sf.getBlockSize();  
        //long blockCount = sf.getBlockCount();  
        long availCount = sf.getAvailableBlocks();  
        
        internal_availablesize = blockSize * availCount;
        //Log.d("", "block��С:"+ blockSize+",block��Ŀ:"+ blockCount+",�ܴ�С:"+blockSize*blockCount/1024/1024+"MB");  
        //Log.d("", "���õ�block��Ŀ��:"+ availCount+",���ô�С:"+ availCount*blockSize/1024/1024+"MB");          		
		
        //sdcard
        
        long sdblockCount = 0;
        
        String state = Environment.getExternalStorageState();  
        if(Environment.MEDIA_MOUNTED.equals(state)) {  
            File sdcardDir = Environment.getExternalStorageDirectory();  
            StatFs sdsf = new StatFs(sdcardDir.getPath());  
            long sdblockSize = sdsf.getBlockSize();  
            sdblockCount = sdsf.getBlockCount();  
            long sdavailCount = sdsf.getAvailableBlocks();  
            
            external_availablesize = sdblockSize * sdavailCount;            
            //Log.d("", "block��С:"+ sdblockSize+",block��Ŀ:"+ sdblockCount+",�ܴ�С:"+sdblockSize*sdblockCount/1024/1024+"MB");  
            //Log.d("", "���õ�block��Ŀ��:"+ sdavailCount+",ʣ��ռ�:"+ sdavailCount*sdblockSize/1024/1024+"MB");  
        }		
		
        
		String fName = path.trim();
        while (true)
        {
        	if (fName.length() < 10) {return false;}
    		//Log.i("1234", "path: " + fName);
        	
    		File file = new File(fName);  
    		if (!file.exists()) {
    			String 	FileName = fName.substring(0, fName.lastIndexOf("/"));
        		fName = FileName;
        		continue;
            }
    		
    		StatFs datasf = new StatFs(fName);  
    	    long datablockSize = datasf.getBlockSize();  
    	    //long datablockCount = datasf.getBlockCount();  
    	    long dataavailCount = datasf.getAvailableBlocks();  
    	    long datasize = datablockSize * dataavailCount;
    	        
    	    if (datasize < (DownloadFileSize * 1.1)) {return false;}
    	    break;
        }
        
	    if ((external_availablesize + internal_availablesize) < 2.3) {return false;}

	    //Log.d("", "block��С:"+ datablockSize+",block��Ŀ:"+ datablockCount+",�ܴ�С:"+datablockSize*datablockCount/1024/1024+"MB");  
        //Log.d("", "���õ�block��Ŀ��:"+ dataavailCount+",���ô�С:"+ dataavailCount*datablockSize/1024/1024+"MB");          		
        
        if (sdblockCount == 0) //���ⲿ�洢
        {
        	if (internal_availablesize < (DownloadFileSize * 2.3)) {return false;}
        }
        else //���ⲿ�洢
        {
        	if (external_availablesize < (DownloadFileSize * 1.1)) {return false;}
        	else if (external_availablesize < (DownloadFileSize * 2.3))
        	{
        		if (internal_availablesize < (DownloadFileSize * 1.3)) {return false;}
        	}
        }        
        
        return true;
	}

}

package com.TDiJoy.fane.data;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public final class CommFunc {
	
	public boolean CommInternetCheck (Context context)
	{
	    try 
	    { 
	        ConnectivityManager conn = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	        if (conn != null) 
	        {
	            NetworkInfo info = conn.getActiveNetworkInfo(); // ��ȡ�������ӹ���Ķ��� 
	            if (info != null&& info.isConnected()) 
	            {	                 
	                if (info.getState() == NetworkInfo.State.CONNECTED) {return true;} // �жϵ�ǰ�����Ƿ��Ѿ�����
	            } 
	        } 
	    } 
	    catch (Exception e) {Log.v("error",e.toString());} 
		return false;
	}

	
}

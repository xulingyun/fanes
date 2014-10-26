package com.TDiJoy.fane.manager;

import android.content.Context;

import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.util.RemoteData;

public class RemoteManager implements Constants{
	/**
	 * singleton
	 */
	private static RemoteManager _sharedManager = null;
	public static RemoteManager sharedInstance() {
		if (_sharedManager == null) {
			_sharedManager = new RemoteManager();
		}
		return _sharedManager;
	}
	
	/**
	 * ��ʼ��
	 */
	public RemoteManager() {
		remoteData = new RemoteData();
		// mark ---�ֱ��������,�޵ײ����Ҫ��---
		if (PLAT_TYPE == VER_ALL_U || PLAT_TYPE == VER_LETV) {
			ISUSB = true;
		}
	}
	
	private RemoteData remoteData;
	private boolean ISUSB = false;
	
	public void ipcInitial() {
		remoteData.ipcInitial();
	}
	
	public void ipcStop() {
		remoteData.ipcStop();
	}
	
	public void ipcSetMouse(boolean enable) {
		remoteData.ipcSetMouse(enable);
	}
	
	public int ipcGetRemoteNumber(Context context) {
		if (!ISUSB) {
			return remoteData.ipcGetRemoteNumber();
		}
		else {
			return remoteData.remote_exist_usbhost(context);
		}
	}
	
	public void ipcSetGameConfigFile(String cfgfile) {
		remoteData.ipcSetGameConfigFile(cfgfile);
	}
	
	/**
	 * onPauseʱ����
	 * @param context
	 */
	public void onPauseMethod(Context context) {
		if (!GameInfoManager.sharedInstance().isRunningForeground(context)) {
//			Log.v("", "----- onPauseMethod -----");
//			remoteData.ipcSetMouse(false);
//			remoteData.ipcStop();
		}
	}
	
	/**
	 * onResumeʱ����
	 */
	public void onResumeMethod() {
//		Log.v("", "----- onResumeMethod -----");
		remoteData.ipcInitial();
		remoteData.ipcSetGameConfigStop();
//		remoteData.ipcSetMouse(true);
	}
}

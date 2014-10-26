package com.TDiJoy.fane.manager;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.TDiJoy.fane.data.GameListCtrl;
import com.TDiJoy.fane.model.CellData;
import com.TDiJoy.fane.model.GameInfo;
import com.TDiJoy.fane.model.GameType;

public class GameInfoManager {
	/**
	 * singleton
	 */
	private static GameInfoManager _sharedManager = null;
	private static GameListCtrl _sharedCtrl = null;
	private Context context;
	private List<Integer> waitToInstall; 
	private List<Integer> waitToDeleteApk; 
	public static GameInfoManager sharedInstance() {
		return _sharedManager;
	}
	
	public static GameListCtrl sharedCtrl() {
		return _sharedCtrl;
	}
	
	
	/**
	 * ��ʼ����Ϸ�б���� GameListUpdate
	 * -1 - û�г�ʼ��  -2 - ����gamelistʧ�ܣ�����Ҳû�п���gamelist  
	 * 1  - ���� gamelist�ɹ�   2  - ����gamelistʧ��
	 * @param context
	 * @return
	 */
	public static int initWithContext(Context context) {
		if (_sharedCtrl == null)
			_sharedCtrl = new GameListCtrl(context);
		int result = _sharedCtrl.GameListUpdate();
//		Log.v("", String.format("----- GameListUpdate : %d -----", result));
		if (_sharedManager == null) {
			_sharedManager = new GameInfoManager(context);
		}
		return result;
	}
	
	public static void destroy(){
		_sharedCtrl = null;
	}
	
	/**
	 * ��ʼ��
	 */
	public GameInfoManager(Context context) {
		this.context = context;
		waitToInstall = new ArrayList<Integer>();
		waitToDeleteApk = new ArrayList<Integer>();
	}
	
	/**
	 * ��ȡ��ҳ����
	 * @return
	 */
	public List<CellData> getMainCellDataList() {
		return null;
	}
	
	/**
	 * ��ȡ�ҵ���Ϸ�б�
	 * @return
	 */
	public List<GameInfo> getMyGameList() {
		return null;
	}
	
	/**
	 * ��ȡҪ��ʾ����Ϸ�����б�
	 * @return
	 */
	public List<GameType> getGameTypeList() {
		return null;
	}
	
	/**
	 * ������Ϸ���ͷ�����Ϸ�б�
	 * @param type	��Ϸ����
	 * @return
	 */
	public List<GameInfo> getGameListByType(GameType type) {
		return null;	
	}
	
	public void installGame(int uuid) {
		if (isRunningForeground(context)) {
			GameInfoManager.sharedCtrl().ApkInstall(uuid);
			waitToDeleteApk.add(uuid);
		}
		else {
			waitToInstall.add(uuid);
		}
	}
	
	public void addToDeleteList(int uuid) {
		waitToDeleteApk.add(uuid);
	}
	
	public void checkInstall() {
		for(Integer uuid : waitToDeleteApk) {
			GameInfoManager.sharedCtrl().ApkDeleteDlFile(uuid);
		}
		waitToDeleteApk.clear();
		
		for(Integer uuid : waitToInstall) {
			GameInfoManager.sharedCtrl().ApkInstall(uuid);
		}
		waitToDeleteApk.addAll(waitToInstall);
		waitToInstall.clear();
		// mark ˢ�°�װ��Ϸ�б�
		GameInfoManager.sharedCtrl().UpdateInstalledAppList();
	}
	
	public boolean isRunningForeground (Context context)
	{
	    ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
	    String currentPackageName = cn.getPackageName();
	    if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName()))
	    {
//	    	Log.v("", "----- Foreground -----");
	        return true ;
	    }
//	    Log.v("", "----- Background -----");
	    return false ;
	}
	
}

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
	 * 初始化游戏列表调用 GameListUpdate
	 * -1 - 没有初始化  -2 - 下载gamelist失败，本地也没有可用gamelist  
	 * 1  - 更新 gamelist成功   2  - 更新gamelist失败
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
	 * 初始化
	 */
	public GameInfoManager(Context context) {
		this.context = context;
		waitToInstall = new ArrayList<Integer>();
		waitToDeleteApk = new ArrayList<Integer>();
	}
	
	/**
	 * 获取首页数据
	 * @return
	 */
	public List<CellData> getMainCellDataList() {
		return null;
	}
	
	/**
	 * 获取我的游戏列表
	 * @return
	 */
	public List<GameInfo> getMyGameList() {
		return null;
	}
	
	/**
	 * 获取要显示的游戏分类列表
	 * @return
	 */
	public List<GameType> getGameTypeList() {
		return null;
	}
	
	/**
	 * 根据游戏类型返回游戏列表
	 * @param type	游戏类型
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
		// mark 刷新安装游戏列表
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

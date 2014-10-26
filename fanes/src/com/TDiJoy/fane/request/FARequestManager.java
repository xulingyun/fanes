package com.TDiJoy.fane.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.TDiJoy.fane.util.AndroidSysInfo;
import com.TDiJoy.fane.util.DES;
import com.TDiJoy.fane.util.SystemInfo;

public class FARequestManager {
	/**
	 * singleton
	 */
	private static FARequestManager _sharedManager = null;
	public static FARequestManager sharedInstance() {
		if (_sharedManager == null) {
			_sharedManager = new FARequestManager();
		}
		return _sharedManager;
	}
	
	private final String LOCAL_ENCODE_KAY = "v23e;@#9";
	private Context context;
	private String aid;


	private long startTime = -1;
	/**
	 * init 查询本地用户账号，没有则注册
	 */
	public void initWithContext(Context context) {
		this.context = context;
		startTime = System.currentTimeMillis();
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		String encodeAid = sp.getString("aid", null);
		aid = null;
		// mark !!!打印出账号用于测试!!!
		Log.v("fanes", "load aid : " + encodeAid);
		// 解密账号
		if (encodeAid != null) {
			try {
				aid = DES.decryptDES(encodeAid, LOCAL_ENCODE_KAY);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.v("", "decode aid:" + aid);
			if (aid == null) {
				requestAid();
			}
		}
		else {
			requestAid();
		}
	}
	
	/**
	 * 重置aid（重新申请）
	 */
	public void resetAid() {
		aid = null;
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		sp.edit().putString("aid", aid).commit();
		
		requestAid();
	}
	
	private void requestAid() {
		final Context mContext = context;
		// TODO 硬件识别号
		RegisterRequest request = new RegisterRequest("", new RequestCallback() {
			@Override
			public void requestFinished(FARequest request) {
				RegisterRequest r = (RegisterRequest) request;
				if(r.succeed && (r.result == RegisterRequest.STATE_SUCCEED || r.result == RegisterRequest.STATE_USER_EXIST)) {
					aid = r.aid;
					Log.v("", "requestAid aid:" + aid);
					// 加密存储账号
					try {
						String encodeAid = DES.encryptDES(aid,LOCAL_ENCODE_KAY);
//						String encodeAid = aes.encrypt(aid).toString();
						
						SharedPreferences sp = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
						sp.edit().putString("aid", encodeAid).commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 上传软硬件信息
					SystemInfo sysInfo = new SystemInfo();
					AndroidSysInfo info = sysInfo.GetAndroidSystemInfo();
					saveData(info.hwInfo(), info.sysInfo(), null);
				}
			}
		});
		new Thread(request).start();
	}
	
	public FARequestManager() {
	}
	
	
	/**
	 * 查询游戏信息
	 * @param appid
	 * @param callback
	 */
/*	public void gameInfo(String appid, RequestCallback callback) {
		if (aid == null) {
			if (callback != null)
				callback.requestFinished(null);
		}
		else {
			GameInfoRequest request = new GameInfoRequest(aid, appid, callback);
			new Thread(request).start();
		}
	}*/
	
	
	/**
	 * 游戏点赞
	 * @param appid
	 * @param callback
	 */
	public void LikeGame(String appid, RequestCallback callback) {
		if (aid == null) {
			if (callback != null)
				callback.requestFinished(null);
		}
		else {
			LikeRequest request = new LikeRequest(aid, appid, callback);
			new Thread(request).start();
		}
	}
	
	/**
	 * 购买游戏
	 * @param appid
	 * @param callback
	 */
	/*public void buyApp(String appid, RequestCallback callback) {
		if (aid == null) {
			if (callback != null)
				callback.requestFinished(null);
		}
		else {
			BuyRequest request = new BuyRequest(aid, appid, callback);
			new Thread(request).start();
		}
	}*/
	
	/**
	 * 上传软硬件信息
	 * @param hardInfo	硬件信息
	 * @param softInfo	软件信息
	 * @param callback	回调
	 */
	public void saveData(String hardInfo, String softInfo, RequestCallback callback) {
		if (aid == null) {
			if (callback != null)
				callback.requestFinished(null);
		}
		else {
			SaveDataRequest request = new SaveDataRequest(aid, hardInfo, softInfo, callback);
			new Thread(request).start();
		}
	}
	
	/**
	 * 游戏时间统计
	 * @param appid
	 * @param time
	 * @param callback
	 */
	/*public void gameTime(String appid, int time, RequestCallback callback) {
		if (aid == null) {
			if (callback != null)
				callback.requestFinished(null);
		}
		else {
			GameTimeRequest request = new GameTimeRequest(aid, appid, time, callback);
			new Thread(request).start();
		}
	}*/
	
	/**
	 * 大厅时间统计,本单例初始化时记录了开始时间，调用此方法时自动计算时间
	 * @param appid
	 * @param time
	 * @param callback
	 */
	/*public void faneTime() {
		if (aid != null && startTime > 0) {
			long time = System.currentTimeMillis() - startTime;
			FaneTimeRequest request = new FaneTimeRequest(aid, (int)(time/60000), null);
			new Thread(request).start();
		}
	}*/
	
	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}
}

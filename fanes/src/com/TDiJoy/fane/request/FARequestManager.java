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
	 * init ��ѯ�����û��˺ţ�û����ע��
	 */
	public void initWithContext(Context context) {
		this.context = context;
		startTime = System.currentTimeMillis();
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		String encodeAid = sp.getString("aid", null);
		aid = null;
		// mark !!!��ӡ���˺����ڲ���!!!
		Log.v("fanes", "load aid : " + encodeAid);
		// �����˺�
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
	 * ����aid���������룩
	 */
	public void resetAid() {
		aid = null;
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		sp.edit().putString("aid", aid).commit();
		
		requestAid();
	}
	
	private void requestAid() {
		final Context mContext = context;
		// TODO Ӳ��ʶ���
		RegisterRequest request = new RegisterRequest("", new RequestCallback() {
			@Override
			public void requestFinished(FARequest request) {
				RegisterRequest r = (RegisterRequest) request;
				if(r.succeed && (r.result == RegisterRequest.STATE_SUCCEED || r.result == RegisterRequest.STATE_USER_EXIST)) {
					aid = r.aid;
					Log.v("", "requestAid aid:" + aid);
					// ���ܴ洢�˺�
					try {
						String encodeAid = DES.encryptDES(aid,LOCAL_ENCODE_KAY);
//						String encodeAid = aes.encrypt(aid).toString();
						
						SharedPreferences sp = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
						sp.edit().putString("aid", encodeAid).commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
					// �ϴ���Ӳ����Ϣ
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
	 * ��ѯ��Ϸ��Ϣ
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
	 * ��Ϸ����
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
	 * ������Ϸ
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
	 * �ϴ���Ӳ����Ϣ
	 * @param hardInfo	Ӳ����Ϣ
	 * @param softInfo	�����Ϣ
	 * @param callback	�ص�
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
	 * ��Ϸʱ��ͳ��
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
	 * ����ʱ��ͳ��,��������ʼ��ʱ��¼�˿�ʼʱ�䣬���ô˷���ʱ�Զ�����ʱ��
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

package com.TDiJoy.fane.request;

import java.util.LinkedHashMap;

import org.json.JSONException;

public class SaveDataRequest extends FARequest {
	public static int STATE_SUCCEED = 1;
	public static int STATE_PWD_WRONG = 2;
	public static int STATE_USER_NOT_EXIST = 3;
	
	public int result;
	
	private RequestCallback callback;
	
	/**
	 * 保存软硬件信息
	 * @param hid			用户识别码
	 * @param hardInfo		硬件信息
	 * @param softInfo		软件信息
	 * @param callback		回调
	 */
	public SaveDataRequest(String hid, String hardInfo, String softInfo, RequestCallback callback) {
		this.callback = callback;
		httpMethod = HttpMethod.POST;
		url = HOST + "savedata.php";
		params = new LinkedHashMap<String, String>();
		params.put("vid", PLAT_ID);
		params.put("aid", hid);
		params.put("hinf", hardInfo);
		params.put("sinf", softInfo);
		addSign();
		decodeJson = true;
	}


	@Override
	public void run() {
		super.run();
		if (succeed) {
			try {
				result = json.getInt("rc");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (callback != null)
			callback.requestFinished(this);
	}
}

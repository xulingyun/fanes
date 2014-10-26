package com.TDiJoy.fane.request;

import java.util.LinkedHashMap;

import org.json.JSONException;

public class RegisterRequest extends FARequest {
	public static int STATE_SUCCEED = 1;
	public static int STATE_PWD_WRONG = 2;
	public static int STATE_USER_EXIST = 3;
	
	public int result;
	public String aid;
	
	private RequestCallback callback;
	/**
	 * 账号申请
	 * @param hid		用户识别号（硬件id）
	 * @param runnable	回调
	 */
	public RegisterRequest(String hid, RequestCallback callback) {
		this.callback = callback;
		httpMethod = HttpMethod.POST;
		url = HOST + "accapp.php";
		params = new LinkedHashMap<String, String>();
		params.put("vid", PLAT_ID);
		params.put("hid", hid);
		addSign();
		decodeJson = true;
	}


	@Override
	public void run() {
		super.run();
		if (succeed) {
			try {
				result = json.getInt("rc");
				aid = json.getString("aid");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (callback != null)
			callback.requestFinished(this);
	}
}

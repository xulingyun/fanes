package com.TDiJoy.fane.request;

import java.util.LinkedHashMap;

import org.json.JSONException;

public class LikeRequest extends FARequest {
	public static int STATE_SUCCEED = 1;
	public static int STATE_PWD_WRONG = 2;
	public static int STATE_USER_NOT_EXIST = 3;
	public static int STATE_GAME_NOT_EXIST = 4;
	
	public int result;
	
	private RequestCallback callback;
	
	/**
	 * 游戏点赞
	 * @param hid		用户识别号
	 * @param appid		游戏编号
	 * @param runnable	回调
	 */
	public LikeRequest(String hid, String appid, RequestCallback callback) {
		this.callback = callback;
		httpMethod = HttpMethod.POST;
		url = HOST + "papp.php";
		params = new LinkedHashMap<String, String>();
		params.put("vid", PLAT_ID);
		params.put("aid", hid);
		params.put("appid", appid);
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

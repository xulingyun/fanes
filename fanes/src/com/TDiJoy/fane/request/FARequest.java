package com.TDiJoy.fane.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.util.MD5;

public class FARequest implements Runnable, Constants{
	private static final int REQUEST_TIMEOUT = 10*1000;
	private static final int SO_TIMEOUT = 10*1000;

	protected Map<String, String> params;
	protected HttpMethod httpMethod;
	protected String url;
	protected String jsonStr;
	protected JSONObject json;
	
	protected boolean decodeJson = false;
	/**
	 * 成功返回数据+成功解析json
	 */
	public boolean succeed = false;
	
	// mark 服务器地址
	protected static String HOST;
	// mark 大厅版本号
	protected static String PLAT_ID;;
	// mark 签名密钥
	protected static String SIGN_KEY;;
	
	public static enum HttpMethod {
		GET, POST
	}
	
	static {
		// mark ---密钥区分---
		PLAT_ID = PLAT_TYPE;
		SIGN_KEY = "TX8R2U41FL";
		if (PLAT_TYPE == VER_HUI_TONG) {
			SIGN_KEY = "G6WQG8LQ";
		}
		else if (PLAT_TYPE == VER_KBL) {
			SIGN_KEY = "G9BV4K9A";
		}
		else if (PLAT_TYPE == VER_JX_1) {
			SIGN_KEY = "I81XIEQHUZ";
		}
		else if (PLAT_TYPE == VER_ALL_U) {
			SIGN_KEY = "L9MWS0R5VE";
		}
		else if (PLAT_TYPE == VER_LETV) {
			SIGN_KEY = "AJSU1BODWQ";
		}
		HOST = "http://ott.winside.cn/AndroidApi/";
//		HOST = "http://183.57.44.130:8543/ottservice4client";
//		HOST = "http://ott.winside.cn/";
	}
	
	@Override
	public void run() {
		jsonStr = null;
		json = null;
		BufferedReader reader = null;
		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		    HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpResponse response = client.execute(getRequest(url, params, httpMethod));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuilder sb = new StringBuilder();
				for (String s = reader.readLine(); s != null; s = reader
						.readLine()) {
					sb.append(s);
				}
				jsonStr = sb.toString();
				Log.v("", "jsonStr : " + jsonStr);
				if (decodeJson) { 
					json = new JSONObject(jsonStr);
				}
				succeed = true;
			}
		} catch (ClientProtocolException e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
		} catch (IOException e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
	}

	protected static HttpUriRequest getRequest(String url,
			Map<String, String> params, HttpMethod method) {
		
		Log.v("", "FARequest getRequest URL = "+url);
		if (method.equals(HttpMethod.POST)) {
			List<NameValuePair> listParams = new ArrayList<NameValuePair>();
			if (params != null) {
				for (String name : params.keySet()) {
					listParams.add(new BasicNameValuePair(name, params
							.get(name)));
				}
			}
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						listParams, "utf-8");
				HttpPost request = new HttpPost(url);
				request.setEntity(entity);
				return request;
			} catch (UnsupportedEncodingException e) {
				throw new java.lang.RuntimeException(e.getMessage(), e);
			}
		} else {
			if (url.indexOf("?") < 0) {
				url += "?";
			}
			if (params != null) {
				for (String name : params.keySet()) {
					try {
						url += "&" + name + "="
								+ URLEncoder.encode(params.get(name), "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			HttpGet request = new HttpGet(url);
			Log.v("", "FARequest  URL = "+url);
			return request;
		}
	}
	
	/**
	 * 增加签名
	 */
	protected void addSign() {
		try {
			String encodeStr = "";
			Set<String> keys = params.keySet();
			for (String key : keys) {
				encodeStr += params.get(key);
			}
			encodeStr += SIGN_KEY;	
//			Log.v("encodeStr", encodeStr);
			MD5 md5 = new MD5(encodeStr.getBytes());
			String signStr = MD5.toHex(md5.doFinal()).toLowerCase(Locale.US);
//			Log.v("signStr", signStr);
			params.put("sign", signStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

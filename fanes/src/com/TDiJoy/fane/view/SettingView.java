package com.TDiJoy.fane.view;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.util.ImageUtils;
import com.TDiJoy.fane.util.StorageUtil;
import com.TDiJoy.fane.util.Unit;

/**
 *系统设置 
 *IP 内存 版本 
 */
public class SettingView extends LinearLayout implements KeyBoardDelegate {
	private Context context;
	private TextView tvIp;
	private TextView tvID;
	private TextView tvPlatformVersion;
	private TextView tvSysVersion;
	private TextView tvInTotal;
	private TextView tvInAvail;
	private TextView tvExTotal;
	private TextView tvExAvail;
	private ProgressBar pbIn;
	private ProgressBar pbEx;
	private LinearLayout exStorageLayout;
	private LinearLayout contentLayout;
	private ImageView ivRef;
	
	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.setGravity(Gravity.CENTER);
		this.addView(
				LayoutInflater.from(context).inflate(R.layout.setting_view,
						null), new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		
		tvIp = (TextView) findViewById(R.id.tvIp);
		tvID = (TextView) findViewById(R.id.tvID);
		tvPlatformVersion = (TextView) findViewById(R.id.tvPlatformVersion);
		tvSysVersion = (TextView) findViewById(R.id.tvSysVersion);
		tvInTotal = (TextView) findViewById(R.id.tvInTotal);
		tvInAvail = (TextView) findViewById(R.id.tvInAvail);
		tvExTotal = (TextView) findViewById(R.id.tvExTotal);
		tvExAvail = (TextView) findViewById(R.id.tvExAvail);
		pbIn = (ProgressBar) findViewById(R.id.pbIn);
		pbEx = (ProgressBar) findViewById(R.id.pbEx);
		exStorageLayout = (LinearLayout) findViewById(R.id.exStorageLayout);
		contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
		ivRef = (ImageView) findViewById(R.id.ivRef);
		
		
		refresh();
	}

	@Override
	public void focus(int direction, int coord) {
	}

	@Override
	public void resignFocus() {
	}

	@Override
	public int getOutCoord() {
		return 0;
	}

	public void refresh() {
		boolean sdcardExist = StorageUtil.externalMemoryAvailable();
		long inTotal = StorageUtil.getTotalInternalMemorySize();
		long inAvail = StorageUtil.getAvailableInternalMemorySize();
		
//		String ipStr = getLocalIpAddress();
//		Log.v("sdcardExist", "" + sdcardExist);
//		Log.v("inTotal", "" + getDisplaySize(inTotal));
//		Log.v("inAvail", "" + getDisplaySize(inAvail));
//		Log.v("exTotal", "" + getDisplaySize(exTotal));
//		Log.v("exAvail", "" + getDisplaySize(exAvail));
//		Log.v("ipStr", "" + ipStr);
//		Log.v("v", "" + getPlatformVersion());
//		Log.v("v", "" + getOSVersion());
		
		String ipStr = getLocalIpAddress();
		tvIp.setText(ipStr == null ? "网络未连接" : "IP地址  " +ipStr);
		
		String tvIDStr = StorageUtil.getLocalTvID();
		tvID.setText("智能卡号："+tvIDStr);
		tvPlatformVersion.setText("软件版本  " + getPlatformVersion());
		tvSysVersion.setText("系统版本  " + getOSVersion());
		tvInTotal.setText("机身存储  " + getDisplaySize(inTotal));
		tvInAvail.setText("可用空间  " + getDisplaySize(inAvail));
		
		double inPercent = 1.d * (inTotal - inAvail) / inTotal;
		pbIn.setProgress((int)(inPercent * 100));
		
		if (sdcardExist) {
			exStorageLayout.setVisibility(VISIBLE);
			long exTotal = StorageUtil.getTotalExternalMemorySize();
			long exAvail = StorageUtil.getAvailableExternalMemorySize();
			double exPercent = 1.d * (exTotal - exAvail) / exTotal;
			pbEx.setProgress((int)(exPercent * 100));
			tvExTotal.setText("扩展存储  " + getDisplaySize(exTotal));
			tvExAvail.setText("可用空间  " + getDisplaySize(exAvail));
		}
		else {
			exStorageLayout.setVisibility(GONE);
		}
		
		if (contentLayout.getWidth() > 0) {
			Bitmap refOriBitmap = Bitmap.createBitmap(contentLayout.getWidth(), contentLayout.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(refOriBitmap);
			contentLayout.draw(canvas);
			Bitmap refBitmap = ImageUtils.getReflectionImage(refOriBitmap, Unit.c(60));
			refOriBitmap.recycle();
			ivRef.setImageBitmap(refBitmap);
		}
		else {
			new Thread(){
				@Override
				public void run() {
					while(contentLayout != null && contentLayout.getWidth() == 0) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					Bitmap refOriBitmap = Bitmap.createBitmap(contentLayout.getWidth(), contentLayout.getHeight(), Config.ARGB_8888);
					Canvas canvas = new Canvas(refOriBitmap);
					contentLayout.draw(canvas);
					final Bitmap refBitmap = ImageUtils.getReflectionImage(refOriBitmap, Unit.c(60));
					if (refBitmap != null) {
						refOriBitmap.recycle();
						post(new Runnable() {
							@Override
							public void run() {
								ivRef.setImageBitmap(refBitmap);
							}
						});
					}
				};
			}.start();
		}
		
	}

	
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private String getDisplaySize(long num) {
		String[] unit = { "KB", "MB", "GB", "TB" };
		double dnum = num;
		for (int i = 0; i < unit.length; i++) {
			dnum /= 1024;
			if (dnum < 1024 || i == unit.length - 1) {
				return String.format("%.2f ", dnum) + unit[i];
			}
		}
		return "未知";
	}

	private String getPlatformVersion() {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo;
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "未知";
	}

	private String getOSVersion() {
		return android.os.Build.VERSION.RELEASE;
	}
}

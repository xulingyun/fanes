package com.TDiJoy.fane;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.manager.DownloadStateManager;
import com.TDiJoy.fane.manager.GameInfoManager;
import com.TDiJoy.fane.manager.ImageManager;
import com.TDiJoy.fane.manager.RemoteManager;
import com.TDiJoy.fane.request.FARequestManager;
import com.TDiJoy.fane.util.StorageUtil;
import com.TDiJoy.fane.util.Unit;

public class LoadActivity extends Activity {
	private ImageView ivLogo;
	private boolean isPlaying = false;
	private long time;
	private MediaPlayer mp;
	private String startIntent = null;
	int initResult;
	static boolean enable_boot;
	static LoadActivity  pointer;
	
	private static boolean LOGO_ANIMATION = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		Display mDisplay = getWindowManager().getDefaultDisplay();

		Point p = new Point();
		mDisplay.getSize(p);
		Unit.width = p.x;
		Unit.height = p.y;
		Log.i("Main", "Width = " + p.x);
		Log.i("Main", "Height = " + p.y);
		
		Log.i("winside", "getDeviceInfo = "+StorageUtil.getDeviceInfo(getBaseContext()) );

		
		// mark 接传入参数
		Intent ThridPart_intent = getIntent();
		startIntent = ThridPart_intent.getStringExtra("APPString");
		Log.i("Main", "startIntent = " + startIntent);
		
		ivLogo = (ImageView) findViewById(R.id.ivLogo);
		

//		if (LOGO_ANIMATION) {
//			ivLogo.setBackgroundResource(R.drawable.logo_frame);
//		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				// mark 程序初始化代码
				
				//GameListUpdate 
				initResult = GameInfoManager.initWithContext(getApplicationContext());
				
				ImageManager.sharedInstance();
				
				DownloadStateManager.sharedInstance();
				//本地用户查询  
				FARequestManager.sharedInstance().initWithContext(getApplicationContext());
				RemoteManager.sharedInstance();
				
				int waitTime = LOGO_ANIMATION ? 5500 : 3000;
				while (isPlaying == false
						|| System.currentTimeMillis() - time < waitTime) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if (mp != null) {
					mp.stop();
				}
				// mp.release();
				// ivLogo.getBackground().setCallback(null);*/
				LoadActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (initResult > 0) {
							Intent intent = new Intent();
							intent.setClass(LoadActivity.this,
									MainActivity.class);
							intent.putExtra("APPString", startIntent);
							intent.putExtra("InitResult", initResult);
							LoadActivity.this.startActivity(intent);
							LoadActivity.this.finish();
							overridePendingTransition(R.anim.fade_in,
									R.anim.fade_out);
						}
						else {
							AlertDialog.Builder builder = new Builder(
									LoadActivity.this);
							builder.setMessage("大厅初始化失败，请检查网络设置！");
							builder.setTitle("提示");
							if(new Intent(android.provider.Settings.ACTION_SETTINGS).resolveActivity(getPackageManager()) != null) {
								builder.setPositiveButton("设置",
										new OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
												finish();
											}
										});
							}
							builder.setNegativeButton("退出",
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											finish();
										}
									});
							builder.create().show();
						}
					}
				});
			}
		}).start();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			if (LOGO_ANIMATION) {
				mp = MediaPlayer.create(getApplicationContext(), R.raw.sound_logo);
				mp.setLooping(false);
				mp.start();
				AnimationDrawable anim = (AnimationDrawable) ivLogo.getBackground();
				anim.start();
			}
			time = System.currentTimeMillis();
			isPlaying = true;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}
}

package com.TDiJoy.fane;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.VideoView;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.manager.GameInfoManager;
import com.TDiJoy.fane.manager.ImageManager;

public class AdControlActivity extends Activity {
	private VideoView videoView;
	private ImageView ivPrice;
	private ImageView ivMask;
	private ImageView ivBg;
	private boolean isWaitingViedo = false;
	private boolean isWaitingBg = false;
	private long startTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ad_control);
		ImageView backImageView = (ImageView) findViewById(R.id.backImageView);
		videoView = (VideoView) findViewById(R.id.videoView1);
		ivPrice = (ImageView) findViewById(R.id.ivPrice);
		ivMask = (ImageView) findViewById(R.id.ivMask);
		ivBg = (ImageView) findViewById(R.id.bgImageView);
		
//		RelativeLayout.LayoutParams lp = (LayoutParams) videoView.getLayoutParams();
//		lp.width = Unit.c(640);
//		lp.height = Unit.c(360);
//		
//		videoView.setLayoutParams(lp);
//		ivMask.setLayoutParams(new RelativeLayout.LayoutParams(Unit.c(640), Unit.c(360)));
		
		final String imagePath = GameInfoManager.sharedCtrl().GetAdImage(3);
		File imagefile = new File(imagePath);
		if (imagefile.exists()) {
			Bitmap bitmap = ImageManager.sharedInstance().bitmapWithPath(imagePath);
			if (bitmap != null) {
				ivBg.setImageBitmap(bitmap);				
			}
		}
		else {
			isWaitingBg = true;
			new Thread() {
				@Override
				public void run() {
					while(isWaitingBg) {
						File file = new File(imagePath);
						if (file.exists()) {
							isWaitingBg = false;
							// 图片下载完成
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Bitmap bitmap = ImageManager.sharedInstance().bitmapWithPath(imagePath);
									if (bitmap != null) {
										ivBg.setImageBitmap(bitmap);
									}
								}
							});
							
							break;
						}
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
		
		// mark 手柄价格设置
		setPrice(GameInfoManager.sharedCtrl().GetRemotePrice());

//		final String path = Environment.getExternalStorageDirectory().getPath()+ "/flash.swf";
		// mark 手柄视频路径设置
		final String path = GameInfoManager.sharedCtrl().GetAdVideo(1);
//		Log.v("", path);
		File file = new File(path);
		if (!file.exists()) {
			// mark 替代手柄视频的图
			ivMask.setImageResource(R.drawable.video_mask);
			// 
			isWaitingViedo = true;
			new Thread() {
				@Override
				public void run() {
					while(isWaitingViedo) {
						File file = new File(path);
						if (file.exists()) {
							isWaitingViedo = false;
							// 视频下载完成
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									ivMask.setImageResource(0);
									playVideo(path);
								}
							});
							
							break;
						}
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
		else {
			playVideo(path);
		}
		backImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AdControlActivity.this.finish();
				AdControlActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});
	}
	
	private void playVideo(final String path) {
		videoView.setVideoPath(path);

		// videoView.setVideoPath("android.resource://com.tdijoy.fanes/"+R.raw.video);

		videoView.start();
		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.setLooping(true);
				mp.start();
				startTime = System.currentTimeMillis();
			}
		});
		videoView
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						// mark 临时做法，广告播放时间大于10s则重播
						if (startTime > 0 &&System.currentTimeMillis() - startTime > 10000) {
							videoView.setVideoPath(path);
							videoView.start();
						}
					}
				});
	}
	
	
	

	@Override
	protected void onPause() {
		super.onPause();
		isWaitingViedo = false;
	}




	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_BACK:
			isWaitingViedo = false;
			isWaitingBg = false;
			this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			return true;
		}
		return true;
	}
	
	private void setPrice(int price) {
		if (price > 0) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ctrl_price).copy(Bitmap.Config.ARGB_8888, true);
			Canvas canvas = new Canvas(bitmap);
			Paint mPaint = new Paint();
			mPaint.setTextAlign(Paint.Align.CENTER);
			mPaint.setAntiAlias(true);
			Typeface font = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC);
			mPaint.setTypeface(font);
			mPaint.setTextSize(42);
			mPaint.setColor(Color.WHITE);
			canvas.drawText(String.valueOf(price), 80, 120, mPaint);
			ivPrice.setImageBitmap(bitmap);
		}
		else {
			ivPrice.setVisibility(View.INVISIBLE);
		}
	}
}

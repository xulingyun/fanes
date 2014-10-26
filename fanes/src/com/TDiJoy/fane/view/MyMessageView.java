package com.TDiJoy.fane.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.util.ImageUtils;
import com.TDiJoy.fane.util.Unit;

/**
 * 我的消息界面
 *
 */
public class MyMessageView extends LinearLayout implements KeyBoardDelegate {

	private Context context;
	
	private LinearLayout contentLayout;
	private ImageView ivRef;
	
	public MyMessageView(Context context) {
		super(context);
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.setGravity(Gravity.CENTER);
		this.addView(
				LayoutInflater.from(context).inflate(R.layout.mymessage_view,
						null), new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		
		
		this.setGravity(Gravity.CENTER);
		
		contentLayout = (LinearLayout) findViewById(R.id.connectAll);
		
		
		/*ivRef = (ImageView) findViewById(R.id.ivRefLock);
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
					
					Bitmap refOriBitmap = null;
					refOriBitmap = Bitmap.createBitmap(contentLayout.getWidth(), contentLayout.getHeight(), Config.ARGB_8888);
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
		}*/
		
	}

	public MyMessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyMessageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void focus(int direction, int coord) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resignFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOutCoord() {
		// TODO Auto-generated method stub
		return 0;
	}

}

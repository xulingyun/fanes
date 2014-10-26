package com.TDiJoy.fane.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.TDiJoy.fane.delegate.CellDelegate;
import com.TDiJoy.fane.delegate.ImageViewDelegate;
import com.TDiJoy.fane.manager.ImageManager;

public class CellViewLite extends LinearLayout implements CellDelegate, ImageViewDelegate{
	public ImageView imageView;
	
	private static int liveCount = 0;
	public CellViewLite(Context context, AttributeSet attrs) {
		super(context, attrs);
		liveCount++;
//		Log.v("", "LiteView alloc " + liveCount);
		
		imageView = new ImageView(context);
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		imageView.setLayoutParams(lp);
		this.addView(imageView);
//		this.setPadding(6, 6, 6, 6);
		
//		imageView.setBackgroundColor(0xffffff);
//		imageView.setBackgroundResource(R.drawable.focus_bg);
//		imageView.setImageResource(R.drawable.cell_small2);
//		imageView.setPadding(8, 8, 8, 8);
	}
	
	@Override
	public void focus() {
//		imageView.setPadding(8, 8, 8, 8);
		this.setBackgroundColor(Color.WHITE);
	}
	
	@Override
	public void resignFocus() {
//		imageView.setPadding(0, 0, 0, 0);
		this.setBackgroundColor(Color.TRANSPARENT);
	}
	
	public void setImageResId(int resId) {
		Bitmap bitmap = ImageManager.sharedInstance().bitmapWithResId(getContext(), resId);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		}
	}

	@Override
	public void setImageWithBitmap(final Bitmap bitmap, boolean asyn) {
		try {
			int i = 0;
			while (this.getWindowToken() == null && i < 20) {
				i++;
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}  
		if (this.getWindowToken() == null) {
//			Log.v("", "this.getWindowToken() == null");
		}
		post(new Runnable() {
			@Override
			public void run() {
				imageView.setImageBitmap(bitmap);
 			}
		});
	}
	
	@Override
	protected void finalize() throws Throwable {
		liveCount--;
//		Log.v("", "LiteView release " + liveCount);
		if (imageView != null && imageView.getDrawable() != null) {
			imageView.getDrawable().setCallback(null);
		}
	}
}

package com.TDiJoy.fane.view;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.ImageViewDelegate;
import com.TDiJoy.fane.delegate.ProgressViewDelegate;
import com.TDiJoy.fane.manager.ImageManager;
import com.TDiJoy.fane.util.ImageUtils;
import com.TDiJoy.fane.util.Unit;

public class CellView extends LinearLayout implements ImageViewDelegate,ProgressViewDelegate{
	public ImageView bgImageView;
	private ImageView refImageView;
	private ImageView ivCtrl0;
	private ImageView ivCtrl1;
	private ImageView ivCtrl2;
	private ImageView ivCtrl3;
	public ProgressBar progress;
	private RelativeLayout bgLayout;
	public LinearLayout coverLayout;
	public LinearLayout titleLayout;
	public LinearLayout cornerLayout;
	public TextView tvTitle;
	public TextView tvWaiting;
	
	public int type;
	public int index;
	
	public boolean needRef;
	public boolean isFocused;
	private boolean showTitle;
	public boolean darkMask = false;
//	public boolean showTitleWhenFocus = true;
	
	
	Context context;
	Bitmap bitmap;
	Bitmap rfBitmap;
	public float focusScale;
	public float focusAdd = 9.f;
	
	private static PorterDuffXfermode DUFF_MODE = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
	
	private boolean isWaitingViedo = false;

	Paint mPaint = new Paint();
	
	private static int liveCount = 0;
	
	public CellView(Context context, AttributeSet attrs) {
		super(context, attrs);
		liveCount++;
//		Log.v("", "CellView alloc " + liveCount);
		this.context = context;
		this.setOrientation(VERTICAL);
		this.setLayoutParams(new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		
		this.addView(LayoutInflater.from(context).inflate(R.layout.common_cell, null), new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		bgImageView = (ImageView) findViewById(R.id.bgImageView);
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		bgImageView.setLayoutParams(lp);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvWaiting = (TextView) findViewById(R.id.tvWaiting);
		bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
		progress = (ProgressBar) findViewById(R.id.progressBar1);
//		tvTitle.setVisibility(INVISIBLE);
		ivCtrl0 = (ImageView) findViewById(R.id.ivCtrl0);
		ivCtrl1 = (ImageView) findViewById(R.id.ivCtrl1);
		ivCtrl2 = (ImageView) findViewById(R.id.ivCtrl2);
		ivCtrl3 = (ImageView) findViewById(R.id.ivCtrl3);
		
		coverLayout = (LinearLayout) findViewById(R.id.coverLayout);
		titleLayout = (LinearLayout) findViewById(R.id.titleLayout);
		cornerLayout = (LinearLayout) findViewById(R.id.cornerLayout);
		
//		bgImageView = new ImageView(context);
//		this.addView(bgImageView);
		refImageView = new ImageView(context);
		// mark 去掉倒影的间距
//		refImageView.setPadding(0, 5, 0, 0);
		this.addView(refImageView);
		refImageView.setVisibility(GONE);
		refImageView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		refImageView.setAdjustViewBounds(false);
		refImageView.setScaleType(ScaleType.FIT_XY);
		this.needRef = false;
		isFocused = false;
		showTitle = true;
//		this.showProgress();
	}
	
	public void reset() {
//		if (bitmap != null && !bitmap.isRecycled()) {
//			bgImageView.setImageBitmap(null);
//			bitmap.recycle();
//			bitmap = null;
//		}
//		if (rfBitmap != null && !rfBitmap.isRecycled()) {
//			refImageView.setImageBitmap(null);
//			rfBitmap.recycle();
//			rfBitmap = null;
//		}
		titleLayout.setVisibility(INVISIBLE);
		bgLayout.setBackgroundResource(0);
		bgLayout.setPadding(0, 0, 0, 0);
		refImageView.setVisibility(GONE);
		progress.setVisibility(GONE);
		tvWaiting.setVisibility(GONE);
		coverLayout.removeAllViews();
		isFocused = false;
		needRef = false;
		isWaitingViedo = false;
		darkMask = false;
		showTitle = true;
		coverLayout.setBackgroundColor(0x00000000);
		cornerLayout.removeAllViews();
//		showTitleWhenFocus = true;
//		this.invalidate();
//		refreshLayout();
	}
	
	public void setNeedRef(boolean needRef) {
		if (this.needRef != needRef) {
			this.needRef = needRef;
			if (needRef) {
				refImageView.setVisibility(VISIBLE);
			}
			else {
				refImageView.setVisibility(GONE);
			}
		}
	}
	
	public void setTitleVisible(boolean visible) {
		if (visible) {
			showTitle = true;
			titleLayout.setVisibility(VISIBLE);
		}
		else {
			showTitle = false;
			titleLayout.setVisibility(GONE);
		}
	}
	
	public void setBitmap(Bitmap bm) {
		if(bm == null || bm.isRecycled())
			return;
//		if (bitmap != null && !bitmap.isRecycled())
//			bitmap.recycle();
		bitmap = bm;
		bgImageView.setImageBitmap(bm);
		if (this.needRef) {
//			if (rfBitmap != null && !rfBitmap.isRecycled())
//				rfBitmap.recycle();
			rfBitmap = ImageUtils.getReflectionImage(bm, Unit.c(60));
			if (rfBitmap != null)
				refImageView.setImageBitmap(rfBitmap);
		}
	}
	
	public void setBgRes(int resid) {
		Bitmap bm = ImageManager.sharedInstance().bitmapWithResId(getContext(), resid);
		if (bm != null) {
			this.setBitmap(bm);
		}
	}
	
	public void focus() {
		if (!isFocused) {
			int height = this.getMeasuredHeight();
			focusScale = (height + focusAdd * 2)/height;
			this.setScaleX(focusScale);
			this.setScaleY(focusScale);
			if (showTitle)
				titleLayout.setVisibility(VISIBLE);
			isFocused = true;
			if (darkMask) {
				coverLayout.setBackgroundColor(0x55000000);
			}
		}
	}

	public void resignFocus() {
		this.setScaleX(1);
		this.setScaleY(1);
		if (showTitle)
			titleLayout.setVisibility(INVISIBLE);
		isFocused = false;
		if (darkMask) {
			coverLayout.setBackgroundColor(0x00000000);
		}
	}
	
	public void showProgress() {
		progress.setVisibility(VISIBLE);
		tvWaiting.setVisibility(GONE);
		refreshLayout();
	}
	
	public void showWaiting() {
		progress.setVisibility(GONE);
		tvWaiting.setVisibility(VISIBLE);
		refreshLayout();
	}
	
	public void hideProgress() {
		progress.setVisibility(GONE);
		tvWaiting.setVisibility(GONE);
		refreshLayout();
	}

	public void refreshLayout() {
		this.requestLayout();
		this.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
	}
	
	public void addCover(int resId, int gravity) {
		ImageView hotImage = new ImageView(context);
		hotImage.setImageResource(resId);
		coverLayout.setGravity(gravity);
		coverLayout.addView(hotImage);
	}
	
	public void setCtrlType(int ctrltype) {
		int byte1 = ctrltype & 1;
		int byte2 = ctrltype >> 1 & 1;
		int byte3 = ctrltype >> 2 & 1;
		int byte4 = ctrltype >> 3 & 1;
		
		int visiblity = byte1 == 1 ? VISIBLE : GONE;
		ivCtrl3.setVisibility(visiblity);
		visiblity = byte2 == 1 ? VISIBLE : GONE;
		ivCtrl0.setVisibility(visiblity);
		visiblity = byte3 == 1 ? VISIBLE : GONE;
		ivCtrl1.setVisibility(visiblity);
		visiblity = byte4 == 1 ? VISIBLE : GONE;
		ivCtrl2.setVisibility(visiblity);
		
	}
	
	public void waitVideo(final String path) {
		if (isWaitingViedo)
			return;
		isWaitingViedo = true;
		coverLayout.removeAllViews();
		final ImageView loadingImage = new ImageView(context);
		loadingImage.setImageResource(R.drawable.icon_play_loading);
		coverLayout.setGravity(Gravity.CENTER);
		coverLayout.addView(loadingImage);
		RotateAnimation animation = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(2000);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setInterpolator(new LinearInterpolator());
		loadingImage.startAnimation(animation);
		this.refreshLayout();
		
		new Thread() {
			@Override
			public void run() {
				while(isWaitingViedo) {
					File file = new File(path);
//					Log.v("", "wait video : " + file.exists());
					if (file.exists()) {
						isWaitingViedo = false;
						post(new Runnable() {
							@Override
							public void run() {
								loadingImage.clearAnimation();
								coverLayout.removeAllViews();
								addCover(R.drawable.icon_play, Gravity.CENTER);
								CellView.this.refreshLayout();
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

	@Override
	public void setImageWithBitmap(final Bitmap bitmap, boolean asyn) {
		if (bitmap == this.bitmap)
			return;
		if (!asyn) {
			setBitmap(bitmap);
			return;
		}
		synchronized(this){
//			if (this.bitmap != null && !this.bitmap.isRecycled())
//				this.bitmap.recycle();
			this.bitmap = bitmap;
			if (this.needRef) {
//				if (rfBitmap != null && !rfBitmap.isRecycled())
//					rfBitmap.recycle();
				rfBitmap = ImageUtils.getReflectionImage(bitmap,Unit.c(60));
			}
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
//				Log.v("", "this.getWindowToken() == null");
			}
//			try {
//				while (this.getWindowToken() == null) {
//					Thread.sleep(10);
//				}
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}  
			post(new Runnable() {
				@Override
				public void run() {
					bgImageView.setImageBitmap(bitmap);
					if (CellView.this.needRef && rfBitmap != null) {
						refImageView.setImageBitmap(rfBitmap);
					}
				}
			});
		}
	}

	@Override
	public void progressChanged(final float progress) {
//		Log.e("", "progressChanged");
		post(new Runnable() {
			@Override
			public void run() {
				if (progress == -2) {
					Toast.makeText(getContext(), "下载取消", Toast.LENGTH_LONG).show();
					hideProgress();
					return;
				}
				if (progress == -1) {
					Toast.makeText(getContext(), "下载失败", Toast.LENGTH_LONG).show();
					hideProgress();
					return;
				}
				if (progress == -3) {
					Toast.makeText(getContext(), "存储空间不足", Toast.LENGTH_LONG).show();
					hideProgress();
					return;
				}
				if (progress == 0 && CellView.this.progress.getVisibility() == VISIBLE) {
					showWaiting();
				}
				if (progress > 0 &&  CellView.this.progress.getVisibility() == GONE) {
					showProgress();
				}
				if (progress >=0 && progress <= 1) {
					double fakeProgress = Math.sqrt(progress);
					CellView.this.progress.setProgress((int) (fakeProgress * 100));
				}
				if (progress == 1) {
					hideProgress();
//					addInstalledCorner();
				}
				
			}
		});
	}
	
	public void addInstalledCorner() {
		cornerLayout.removeAllViews();
		ImageView cornerImage = new ImageView(getContext());
		cornerImage.setImageResource(R.drawable.installed);
		cornerLayout.addView(cornerImage);
	}

	@Override
	protected void finalize() throws Throwable {
		liveCount--;
//		Log.v("", "CellView "+tvTitle.getText()+" release " + liveCount);
		
//		if (bitmap != null && !bitmap.isRecycled()) {
//			bitmap.recycle();
//			bitmap = null;
//		}
//		if (rfBitmap != null && !rfBitmap.isRecycled()) {
//			rfBitmap.recycle();
//			rfBitmap = null;
//		}
	}
	
//	@Override
//	protected void dispatchDraw(Canvas canvas) {
//		RectF rect = null;
//		if (this.isFocused) {
//			Log.v("", "cell dispatchDraw");
//			mPaint.reset();
//			mPaint.setColor(Color.BLACK);
//			rect = new RectF(0, 0, bgImageView.getMeasuredWidth(), bgImageView.getMeasuredHeight());
//			if (needRef) {
//				mPaint.setShadowLayer(40, 0, 40, 0x99000000);
//			}
//			else {
//				mPaint.setShadowLayer(40, 0, 40, 0x55000000);
//			}
//			canvas.drawRect(rect, mPaint);
//			mPaint.setShadowLayer(30, 0, 30, 0xFF000000);
//			canvas.drawRect(rect, mPaint);
//			
//			mPaint.setShadowLayer(0, 0, 0, 0);
//			mPaint.setXfermode(DUFF_MODE);
//			canvas.drawRect(rect, mPaint);
////			
//			
//		}
//		super.dispatchDraw(canvas);
//		if (this.isFocused) {
//			int w = 3;
//			rect.left -= w;
//			rect.top -= w;
//			rect.right += w;
//			rect.bottom += w;
//			mPaint.setXfermode(null);
//			mPaint.setShadowLayer(0, 0, 0, 0);
//			mPaint.setColor(Color.WHITE);
//			mPaint.setStyle(Paint.Style.STROKE);
//			mPaint.setStrokeWidth(6);
//			canvas.drawRoundRect(rect, 3, 3, mPaint);
//		}
//	}
}

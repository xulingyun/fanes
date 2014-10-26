package com.TDiJoy.fane.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.CellActionDelegate;
import com.TDiJoy.fane.delegate.CellDelegate;
import com.TDiJoy.fane.manager.ImageManager;

public class GameInfoTitleView extends LinearLayout implements CellDelegate{
	private boolean isSelfFocus = false;
	private ImageView btnBuy;
	private ImageView btnBuy2;
	private TextView tvGameTitle;
	public TextView tvCategory;
	public TextView tvSize;
	public TextView tvPrice;
	public TextView tvLanguage;
	public TextView tvAge;
	public TextView tvDevice;
	
	public CellActionDelegate delegate;
	private Context context;
	public boolean isBtnHide = false;
	Animation alpha;
	public GameInfoTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.addView(LayoutInflater.from(context).inflate(R.layout.game_info_title, null), new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		this.setPadding(6, 6, 6, 6);
		btnBuy = (ImageView) findViewById(R.id.ivBuy);
		btnBuy2 = (ImageView) findViewById(R.id.ivBuy2);
		tvGameTitle = (TextView) findViewById(R.id.tvGameTitle);
		tvCategory = (TextView) findViewById(R.id.tvCategory);
		tvSize = (TextView) findViewById(R.id.tvSize);
		tvPrice = (TextView) findViewById(R.id.tvPrice);
		tvLanguage = (TextView) findViewById(R.id.tvLanguage);
		tvAge = (TextView) findViewById(R.id.tvAge);
		tvDevice = (TextView) findViewById(R.id.tvDevice);
		btnBuy.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (delegate != null) {
					delegate.cellDidSelectAtIndex(0, null);
				}
			}
		});
		
		alpha = new AlphaAnimation(0.3f, 1.f);
		alpha.setDuration(500);
		alpha.setRepeatMode(Animation.REVERSE);
		alpha.setRepeatCount(Animation.INFINITE);
		btnBuy2.setAnimation(alpha);
		alpha.startNow();
		
		hideButton();
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (isSelfFocus && isBtnHide) {
			Paint mPaint = new Paint();
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(12);
			mPaint.setColor(Color.WHITE);
			int width = this.getWidth();
			int height = this.getHeight();
			canvas.drawRect(0, 0, width, height, mPaint);
		}
	}
	
	@Override
	public void focus() {
//		isSelfFocus = true;
		refreshBtnDisplay();
//		this.invalidate();
	}
	@Override
	public void resignFocus() {
		isSelfFocus = false;
		refreshBtnDisplay();
//		this.invalidate();
	}
	
	private void refreshBtnDisplay() {
		if (isSelfFocus) {
//			btnBuy.setImageResource(R.drawable.btn_buy_focus);
			Bitmap bitmap = ImageManager.sharedInstance().bitmapWithResId(getContext(), R.drawable.btn_buy_focus);
			if (bitmap != null) {
				btnBuy.setImageBitmap(bitmap);
			}
//			alpha.cancel();
		}
		else {
//			btnBuy.setImageResource(R.drawable.btn_buy_normal);
			Bitmap bitmap = ImageManager.sharedInstance().bitmapWithResId(getContext(), R.drawable.btn_buy_normal);
			if (bitmap != null) {
				btnBuy.setImageBitmap(bitmap);
			}
//			alpha.startNow();
		}
	}
	
	public void setTitle(String title) {
		tvGameTitle.setText(title);
	}
	
	public void hideButton() {
		isBtnHide = true;
		btnBuy.setVisibility(GONE);
		btnBuy2.setVisibility(GONE);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int currentWidth = tvGameTitle.getWidth();
		int currentHeight = tvGameTitle.getHeight();
		
		if (currentWidth == 0 || currentHeight == 0) {
			return;
		}
		
		tvGameTitle.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		
//		Log.v("", String.format("tvGameIntro measure  w : %d h: %d", currentWidth, currentHeight));
//		Log.v("", String.format("tvGameIntro w : %d h: %d", tvGameTitle.getWidth(), tvGameTitle.getHeight()));
		
		int measureWidth = tvGameTitle.getMeasuredWidth();
		float textSize = tvGameTitle.getTextSize();
		while (measureWidth > currentWidth && textSize > 10) {
			textSize--;
			tvGameTitle.setTextSize(textSize);
			tvGameTitle.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			measureWidth = tvGameTitle.getMeasuredWidth();
		}
		
	}
}

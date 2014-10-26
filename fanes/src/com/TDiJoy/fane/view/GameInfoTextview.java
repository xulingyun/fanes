package com.TDiJoy.fane.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.CellDelegate;

public class GameInfoTextview extends LinearLayout implements CellDelegate{
	private boolean isSelfFocus = false;
	private TextView tvGameIntro;
	
	public GameInfoTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.addView(LayoutInflater.from(context).inflate(R.layout.game_info_intro, null), new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		this.setPadding(6, 6, 6, 6);
		tvGameIntro = (TextView) findViewById(R.id.tvGameTitle);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (isSelfFocus) {
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
		isSelfFocus = true;
		this.invalidate();
	}
	
	@Override
	public void resignFocus() {
		isSelfFocus = false;
		this.invalidate();
	}
	
	public void setText(String text) {
		// mark 设置游戏简介，横向拉伸适应大小
		tvGameIntro.setText(text);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int currentWidth = tvGameIntro.getWidth();
		int currentHeight = this.getHeight() - tvGameIntro.getTop();
		
		if (currentWidth == 0 || currentHeight == 0) {
			return;
		}
//		Log.v("", String.format("tvGameIntro measure  w : %d h: %d", currentWidth, currentHeight));
//		Log.v("", String.format("parent top : %d h: %d", tvGameIntro.getTop(), this.getHeight()));
		
		tvGameIntro.measure(MeasureSpec.makeMeasureSpec(currentWidth, MeasureSpec.EXACTLY), MeasureSpec.UNSPECIFIED);
		int measureHeight = tvGameIntro.getMeasuredHeight();
//		Log.v("", String.format("tvGameIntro measureHeight %d", measureHeight));
		
		while (measureHeight > currentHeight) {
			currentWidth += 20;
			tvGameIntro.measure(MeasureSpec.makeMeasureSpec(currentWidth, MeasureSpec.EXACTLY), MeasureSpec.UNSPECIFIED);
			measureHeight = tvGameIntro.getMeasuredHeight();
//			Log.v("", String.format("tvGameIntro measureHeight %d", measureHeight));
			
		}
		LayoutParams lp = (LayoutParams) this.getLayoutParams();
		lp.width = this.getWidth() + (currentWidth - tvGameIntro.getWidth());
		lp.height = LayoutParams.MATCH_PARENT;
		this.setLayoutParams(lp);
	}
}

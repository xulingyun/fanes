package com.TDiJoy.fane.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.CellDelegate;

public class VerticalScrollView extends LinearLayout implements CellDelegate{

	public LinearLayout contentLayout;
	private boolean isSelfFocus;
	private int currentChildIndex;
	private ScrollView scrollView;
	
	public VerticalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		scrollView = (ScrollView) LayoutInflater.from(context).inflate(R.layout.vertical_scroll_view, null);
		this.addView(scrollView, new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		this.setPadding(3, 6, 3, 6);
		contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
		currentChildIndex = 0;
	}

	public void addSubview(View view) {
		contentLayout.addView(view);
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
	
	public boolean ScrollUp() {
		if (currentChildIndex <= 0)
			return false;
		currentChildIndex--;
		View child = contentLayout.getChildAt(currentChildIndex);
		int diff = (getHeight() - child.getHeight())/2 - 6;
		scrollView.smoothScrollTo(0, child.getTop() - diff);
		return true;
	}
	
	public boolean ScrollDown() {
		if (currentChildIndex >= contentLayout.getChildCount() - 1)
			return false;
		currentChildIndex++;
		View child = contentLayout.getChildAt(currentChildIndex);
		int diff = (getHeight() - child.getHeight())/2 - 6;;
		scrollView.smoothScrollTo(0, child.getTop() - diff);
		return true;
	}
}

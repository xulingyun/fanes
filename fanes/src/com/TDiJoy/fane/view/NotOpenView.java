package com.TDiJoy.fane.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;

public class NotOpenView extends LinearLayout implements KeyBoardDelegate{

	public NotOpenView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.addView(LayoutInflater.from(context).inflate(R.layout.not_open_view, null), new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}

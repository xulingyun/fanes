package com.TDiJoy.fane.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.HorizontalScrollView;

@SuppressLint("Instantiatable")
public class CHorizontalScrollView extends HorizontalScrollView {
	public CHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return false;
	}
}

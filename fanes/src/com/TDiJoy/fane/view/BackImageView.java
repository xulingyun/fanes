package com.TDiJoy.fane.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;

@SuppressLint("Instantiatable")
public class BackImageView extends ImageView implements KeyBoardDelegate {
	private Context context;
	public BackImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.setImageResource(R.drawable.menu_back_normal);
		this.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((Activity)BackImageView.this.context).finish();
				((Activity)BackImageView.this.context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}});
	}
	@Override
	public void focus(int direction, int coord) {
		this.setImageResource(R.drawable.menu_back_focus);
	}
	@Override
	public void resignFocus() {
		this.setImageResource(R.drawable.menu_back_normal);
	}
	@Override
	public int getOutCoord() {
		return 0;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			((Activity)context).finish();
			((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			return true;
		}
		return false;
	}
}

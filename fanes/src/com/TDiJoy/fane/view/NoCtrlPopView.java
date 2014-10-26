package com.TDiJoy.fane.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.delegate.PageChangeDelegate;

public class NoCtrlPopView extends PopupWindow implements KeyBoardDelegate,OnClickListener{
	private ImageView ivBack;
	private ImageView ivBuyCtrl;
	public int currentState;
	public PageChangeDelegate delegate;
	
	public static final int STATE_BACK = 0;
	public static final int STATE_BUY = 1;
	
	public NoCtrlPopView(Context context) {
		this.setContentView(LayoutInflater.from(context).inflate(R.layout.no_ctrl_pop_view, null));
		this.setWidth(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		ivBack = (ImageView) this.getContentView().findViewById(R.id.ivBack);
		ivBuyCtrl = (ImageView) this.getContentView().findViewById(R.id.ivBuyCtrl);
		ivBack.setOnClickListener(this);
		ivBuyCtrl.setOnClickListener(this);
		currentState = 0;
		refreshDisplay();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			currentState = currentState <= 0 ? 0 : currentState - 1;
			refreshDisplay();
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			currentState = currentState >= 1 ? 1 : currentState + 1;
			refreshDisplay();
			return true;
		}
		return false;
	}

	@Override
	public void focus(int direction, int coord) {
		currentState = 0;
		refreshDisplay();
	}

	@Override
	public void resignFocus() {
	}

	@Override
	public int getOutCoord() {
		return 0;
	}
	
	private void refreshDisplay() {
		ivBack.setImageResource(currentState == 0 ? R.drawable.btn_back_focus : R.drawable.btn_back_normal);
		ivBuyCtrl.setImageResource(currentState == 1 ? R.drawable.btn_buy2_focus : R.drawable.btn_buy2_normal);
	}

	@Override
	public void onClick(View v) {
		if (v == ivBack) {
			currentState = 0;
			refreshDisplay();
		}
		else {
			currentState = 1;
			refreshDisplay();
		}
		if (delegate != null)
			delegate.pageChangeTo(currentState);
	}
}

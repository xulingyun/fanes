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

/**
 * 没有购买包月或者包月过期弹出窗口
 *
 */
public class NoRechargePopView extends PopupWindow implements KeyBoardDelegate,OnClickListener{
	private ImageView ivBack;
	//包月按钮
	private ImageView ivRechargeMCtrl;
	//包年按钮
	private ImageView ivRechargeYCtrl;
	public int currentState;
	public PageChangeDelegate delegate;
	
	public static final int STATE_BACK = 0;
	public static final int STATE_BUYMONTH = 1;
	public static final int STATE_BUYYEAR = 2;
	
	public NoRechargePopView(Context context) {
		this.setContentView(LayoutInflater.from(context).inflate(R.layout.no_recharge_pop_view, null));
		this.setWidth(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		
		ivBack = (ImageView) this.getContentView().findViewById(R.id.ivRechargeBack);
		ivBack.setOnClickListener(this);
		
		ivRechargeMCtrl = (ImageView) this.getContentView().findViewById(R.id.ivRechargeBuyMonth);
		ivRechargeYCtrl = (ImageView) this.getContentView().findViewById(R.id.ivRechargeBuyYear);
	
		ivRechargeMCtrl.setOnClickListener(this);
		ivRechargeYCtrl.setOnClickListener(this);
		
		
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
			currentState = currentState >= 2 ? 0 : currentState + 1;
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
		ivBack.setImageResource(currentState == 0 ? R.drawable.recharge_buy_back2 : R.drawable.recharge_buy_back1);
		ivRechargeMCtrl.setImageResource(currentState == 1 ? R.drawable.recharge_buy_month2 : R.drawable.recharge_buy_month1);
		ivRechargeYCtrl.setImageResource(currentState == 2 ? R.drawable.recharge_buy_year2 : R.drawable.recharge_buy_year1);
	}

	@Override
	public void onClick(View v) {
		if (v == ivBack) {
			currentState = 0;
			refreshDisplay();
		}else if (v == ivRechargeMCtrl){
			currentState = 1;
			refreshDisplay();
		}else{
			currentState = 2;
			refreshDisplay();
		}
		if (delegate != null)
			delegate.pageChangeTo(currentState);
	}
}

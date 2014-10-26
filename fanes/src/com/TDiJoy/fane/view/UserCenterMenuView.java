package com.TDiJoy.fane.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.delegate.PageChangeDelegate;
import com.TDiJoy.fane.manager.KeyBoardManager;

public class UserCenterMenuView extends LinearLayout implements KeyBoardDelegate, OnClickListener{
	
	private int currentIndex;
	
	private ImageView ivMenu0;
	private ImageView ivMenu1;
	private ImageView ivMenu2;
//	/*
	private ImageView ivMenu3;
	private ImageView ivMenu4;
//	*/
//	private ImageView ivMenu5;
	
	private int[][] images = {	{R.drawable.user_menu1_1, R.drawable.user_menu1_2, R.drawable.user_menu1_3}, 
								{R.drawable.user_menu2_1, R.drawable.user_menu2_2, R.drawable.user_menu2_3},
								{R.drawable.user_menu3_1, R.drawable.user_menu3_2, R.drawable.user_menu3_3},
								{R.drawable.user_menu4_1, R.drawable.user_menu4_2, R.drawable.user_menu4_3},
								{R.drawable.user_menu5_1, R.drawable.user_menu5_2, R.drawable.user_menu5_3},
								};
	
	private static final int STATE_NORMAL = 	0;
	private static final int STATE_FOCUS = 		1;
	private static final int STATE_SELECTED = 	2;
	
	private ImageView[] menus;
	
	public PageChangeDelegate activity = null;
	
	public UserCenterMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.setGravity(Gravity.CENTER);
		this.addView(LayoutInflater.from(context).inflate(R.layout.top_menu, null), new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		ivMenu0 = (ImageView) findViewById(R.id.ivMenu0);
		ivMenu1 = (ImageView) findViewById(R.id.ivMenu1);
		ivMenu2 = (ImageView) findViewById(R.id.ivMenu2);
		ivMenu3 = (ImageView) findViewById(R.id.ivMenu3);
		ivMenu4 = (ImageView) findViewById(R.id.ivMenu4);
		
//		ivMenu5 = (ImageView) findViewById(R.id.ivMenu5);
		
		ImageView[] temmenus = {ivMenu0, ivMenu1, ivMenu2, ivMenu3, ivMenu4/*ivMenu5*/};
		menus = temmenus;
		
		currentIndex = 0;
		
//		for (int i = 0; i < /**/4/*6*/; i++) {
			for (int i = 0; i < menus.length; i++) {
			this.setMenuState(i, STATE_NORMAL);
			menus[i].setOnClickListener(this);
		}
	}
	
	private void setMenuState(int index, int state) {
//		menus[index].setBackgroundResource(images[index][state]);
		menus[index].setImageResource(images[index][state]);
	}
	
	@Override
	public void focus(int direction, int coord) {
		this.setMenuState(currentIndex, STATE_FOCUS);
	}
	
	@Override
	public void	resignFocus() {
		this.setMenuState(currentIndex, STATE_SELECTED);
	}

	@Override
	public int getOutCoord() {
		return 0;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			return false;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return false;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (currentIndex <= 0) {
				return false;
			}
			else {
				this.setMenuState(currentIndex, STATE_NORMAL);
				currentIndex--;
				this.setMenuState(currentIndex, STATE_FOCUS);
				if (activity != null) {
					activity.pageChangeTo(currentIndex);
				}
				return true;
			}
		case KeyEvent.KEYCODE_DPAD_RIGHT:
//			if (currentIndex >= 3/*5*/) {
			if (currentIndex >= menus.length-1) {
				return false;
			}
			else {
				this.setMenuState(currentIndex, STATE_NORMAL);
				currentIndex++;
				this.setMenuState(currentIndex, STATE_FOCUS);
				if (activity != null) {
					activity.pageChangeTo(currentIndex);
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		for(int i = 0; i < 6; i++) {
			if (v == menus[i]) {
				KeyBoardManager.sharedInstance().focusKeyBoard(this);
				if (currentIndex >= 0 && currentIndex < 6) {
					this.setMenuState(currentIndex, STATE_NORMAL);
				}
				currentIndex = i;
				this.setMenuState(currentIndex, STATE_FOCUS);
				activity.pageChangeTo(currentIndex);
				break;
			}
		}
	}

}

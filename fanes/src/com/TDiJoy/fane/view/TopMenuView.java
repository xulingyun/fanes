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

public class TopMenuView extends LinearLayout implements KeyBoardDelegate, OnClickListener{
	
	private int currentIndex;
	
	private ImageView ivMenu0;
	private ImageView ivMenu1;
	private ImageView ivMenu2;
//	/*
	private ImageView ivMenu3;
	private ImageView ivMenu4;
//	*/
//	private ImageView ivMenu5;
	
	private int[][] images = {	{R.drawable.menu0_normal, R.drawable.menu0_focus, R.drawable.menu0_selected}, 
								{R.drawable.menu1_normal, R.drawable.menu1_focus, R.drawable.menu1_selected}, 
								{R.drawable.menu6_normal, R.drawable.menu6_focus, R.drawable.menu6_selected},
								{R.drawable.menu2_normal, R.drawable.menu2_focus, R.drawable.menu2_selected}, 
								{R.drawable.menu7_normal, R.drawable.menu7_focus, R.drawable.menu7_selected}, 
//								
								
								
								/*
//								  {R.drawable.menu3_normal, R.drawable.menu3_focus, R.drawable.menu3_selected}, 
//								{R.drawable.menu4_normal, R.drawable.menu4_focus, R.drawable.menu4_selected},
//								*/
//								{R.drawable.menu5_normal, R.drawable.menu5_focus, R.drawable.menu5_selected}
								};
	
	private static final int STATE_NORMAL = 	0;
	private static final int STATE_FOCUS = 		1;
	private static final int STATE_SELECTED = 	2;
	
	private ImageView[] menus;
	
	public PageChangeDelegate mainActivity = null;
	
	public TopMenuView(Context context, AttributeSet attrs) {
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
				if (mainActivity != null) {
					mainActivity.pageChangeTo(currentIndex);
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
				if (mainActivity != null) {
					mainActivity.pageChangeTo(currentIndex);
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
				mainActivity.pageChangeTo(currentIndex);
				break;
			}
		}
	}

}

package com.TDiJoy.fane.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.delegate.PageChangeDelegate;
import com.TDiJoy.fane.model.GameType;
import com.TDiJoy.fane.util.Constants;

public class GameTypeMenuView extends LinearLayout implements Constants,KeyBoardDelegate,OnClickListener{
	private int[][] images = {
			{R.drawable.menu_type_hot_normal, R.drawable.menu_type_hot_focus, R.drawable.menu_type_hot_selected}, 
			{R.drawable.menu_type_reco_normal, R.drawable.menu_type_reco_focus, R.drawable.menu_type_reco_selected}, 
			{R.drawable.menu_type_1_normal, R.drawable.menu_type_1_focus, R.drawable.menu_type_1_selected}, 
			{R.drawable.menu_type_2_normal, R.drawable.menu_type_2_focus, R.drawable.menu_type_2_selected},  
			{R.drawable.menu_type_3_normal, R.drawable.menu_type_3_focus, R.drawable.menu_type_3_selected},  
			{R.drawable.menu_type_4_normal, R.drawable.menu_type_4_focus, R.drawable.menu_type_4_selected},  
			{R.drawable.menu_type_5_normal, R.drawable.menu_type_5_focus, R.drawable.menu_type_5_selected},  
			{R.drawable.menu_type_6_normal, R.drawable.menu_type_6_focus, R.drawable.menu_type_6_selected},  
			{R.drawable.menu_type_7_normal, R.drawable.menu_type_7_focus, R.drawable.menu_type_7_selected},  
			{R.drawable.menu_type_8_normal, R.drawable.menu_type_8_focus, R.drawable.menu_type_8_selected},  
			{R.drawable.menu_type_9_normal, R.drawable.menu_type_9_focus, R.drawable.menu_type_9_selected},  
			{R.drawable.menu_type_10_normal, R.drawable.menu_type_10_focus, R.drawable.menu_type_10_selected},  
			{R.drawable.menu_type_11_normal, R.drawable.menu_type_11_focus, R.drawable.menu_type_11_selected},
			{R.drawable.menu_type_12_normal, R.drawable.menu_type_12_focus, R.drawable.menu_type_12_selected}};

	private Context context;
	private LinearLayout contentLayout;
	private List<ImageView> menus;
	private int currentFocusIndex;
	private boolean isSelfFocus;
	private HorizontalScrollView scrollView;
	
	public PageChangeDelegate activity = null;
	
	public GameTypeMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.setOrientation(VERTICAL);
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.addView(LayoutInflater.from(context).inflate(R.layout.game_type_menu, null), new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
		scrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
		menus = new ArrayList<ImageView>();
		
		
		
//		currentFocusIndex = 0;
		isSelfFocus = false;
//		refreshMenuDisplay();
	}
	
	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		checkScroll();
	}



	public void setGameTypeList(List<GameType> list) {
		contentLayout.removeAllViews();
		menus.clear();
		for (GameType gameType : list) {
			this.addMenu(gameType.type);
		}
		currentFocusIndex = 0;
		refreshMenuDisplay();
	}
	
	public void setCurrentType(int type) {
		for (int i = 0; i < menus.size(); i++) {
			ImageView menu = menus.get(i);
			MenuTag tag = (MenuTag) menu.getTag();
			if (tag.type == type) {
				currentFocusIndex = i;
				break;
			}
		}
		refreshMenuDisplay();
		checkScroll();
	}
	
	private class MenuTag {
		public int type;
		public int resIndex;
	}
	
	
	private void addMenu(int type) {
		int resIndex = 0;
		switch(type) {
		case GAME_TYPE_HOT:
			resIndex = 0;
			break;
		case GAME_TYPE_RECO:
			resIndex = 1;
			break;
		case GAME_TYPE_1:
			resIndex = 2;
			break;
		case GAME_TYPE_2:
			resIndex = 3;
			break;
		case GAME_TYPE_3:
			resIndex = 4;
			break;
		case GAME_TYPE_4:
			resIndex = 5;
			break;
		case GAME_TYPE_5:
			resIndex = 6;
			break;
		case GAME_TYPE_6:
			resIndex = 7;
			break;
		case GAME_TYPE_7:
			resIndex = 8;
			break;
		case GAME_TYPE_8:
			resIndex = 9;
			break;
		case GAME_TYPE_9:
			resIndex = 10;
			break;
		case GAME_TYPE_10:
			resIndex = 11;
			break;
		case GAME_TYPE_11:
			resIndex = 12;
			break;
		}
		MenuTag tag = new MenuTag();
		tag.resIndex = resIndex;
		tag.type = type;
		
		ImageView imageView = new ImageView(context);
		imageView.setTag(tag);
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 0, 0, 0);
		imageView.setLayoutParams(lp);
		imageView.setOnClickListener(this);
		
		contentLayout.addView(imageView);
		menus.add(imageView);
	}
	
	private void refreshMenuDisplay() {
		for (int i = 0; i < menus.size(); i++) {
			ImageView menu = menus.get(i);
			MenuTag tag = (MenuTag) menu.getTag();
			if (i == currentFocusIndex) {
				if (this.isSelfFocus)
					menu.setImageResource(images[tag.resIndex][1]);
				else
					menu.setImageResource(images[tag.resIndex][2]);
			}
			else {
				menu.setImageResource(images[tag.resIndex][0]);
			}
		}
	}
	
	/**
	 * index 不做溢出检查
	 * @param index
	 * @param state
	 */
	private void setMenuState(int index, int state) {
		ImageView menu = menus.get(index);
		MenuTag tag = (MenuTag) menu.getTag();
		menu.setImageResource(images[tag.resIndex][state]);
	}
	
	public int moveLeft() {
		int newIndex = currentFocusIndex - 1;
		if (newIndex < 0)
			return GAME_TYPE_NONE;
		
		setMenuState(currentFocusIndex, 0);
		currentFocusIndex = newIndex;
		setMenuState(currentFocusIndex, 2);
		
		ImageView menu = menus.get(currentFocusIndex);
		MenuTag tag = (MenuTag) menu.getTag();
		checkScroll();
		return tag.type;
	}
	
	public int moveRight() {
		int newIndex = currentFocusIndex + 1;
		if (newIndex >= menus.size())
			return GAME_TYPE_NONE;
		
		setMenuState(currentFocusIndex, 0);
		currentFocusIndex = newIndex;
		setMenuState(currentFocusIndex, 2);
		
		ImageView menu = menus.get(currentFocusIndex);
		MenuTag tag = (MenuTag) menu.getTag();
		checkScroll();
		return tag.type;
	}
	
	

	@Override
	public void focus(int direction, int coord) {
		isSelfFocus = true;
		refreshMenuDisplay();
	}

	@Override
	public void resignFocus() {
		isSelfFocus = false;
		refreshMenuDisplay();
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
			int newIndex = currentFocusIndex - 1;
			if (newIndex < 0)
				return false;
			setMenuState(currentFocusIndex, 0);
			currentFocusIndex = newIndex;
			setMenuState(currentFocusIndex, 1);
			checkScroll();
			// call back
			typeChangedDelegate();
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			int rightIndex = currentFocusIndex + 1;
			if (rightIndex >= menus.size())
				return false;
			setMenuState(currentFocusIndex, 0);
			currentFocusIndex = rightIndex;
			setMenuState(currentFocusIndex, 1);
			checkScroll();
			// call back
			typeChangedDelegate();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void checkScroll() {
		ImageView menu = menus.get(currentFocusIndex);
		int[] location = new int[2];  
		menu.getLocationOnScreen(location);
		int checkWidth = 100;
		if (location[0] < checkWidth) {
			scrollView.smoothScrollBy(location[0] - checkWidth, 0);
		}
		else if (location[0] + menu.getMeasuredWidth() > getWidth() - checkWidth) {
			scrollView.smoothScrollBy(location[0] + menu.getMeasuredWidth() - (getWidth()- checkWidth), 0);
		}
	}
	
	private void typeChangedDelegate() {
		if (activity != null) {
			ImageView menu = menus.get(currentFocusIndex);
			MenuTag tag = (MenuTag) menu.getTag();
			activity.pageChangeTo(tag.type);
		}
	}

	@Override
	public void onClick(View v) {
		int newIndex = getAryIndexByMenu(v);
		if (newIndex != currentFocusIndex) {
			setMenuState(currentFocusIndex, 0);
			currentFocusIndex = newIndex;
			setMenuState(currentFocusIndex, 1);
			checkScroll();
			typeChangedDelegate();
		}
	}
	
	public int getAryIndexByMenu(View menu) {
		for (int i = 0; i < menus.size(); i++) {
			ImageView temMenu = menus.get(i);
			if (temMenu == menu) {
				return i;
			}
		}
		return -1;
	}



	@Override
	protected void finalize() throws Throwable {
		activity = null;
		context = null;
	}
	
	
}

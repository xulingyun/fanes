package com.TDiJoy.fane.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.TDiJoy.fane.AdControlActivity;
import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.CellActionDelegate;
import com.TDiJoy.fane.delegate.CellDelegate;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.manager.GameInfoManager;
import com.TDiJoy.fane.manager.ImageManager;
import com.TDiJoy.fane.manager.KeyBoardManager;
import com.TDiJoy.fane.model.GameInfo;
import com.TDiJoy.fane.util.StringUtils;
import com.TDiJoy.fane.util.Unit;

public class GameIntroView extends LinearLayout implements KeyBoardDelegate, CellActionDelegate, OnClickListener{
	private Context context;
	private LinearLayout contentLayout;
	private CHorizontalScrollView scrollView;
	private GameInfoTitleView titleView;
	private GameInfoTextview textView;
	private VerticalScrollView verticalScrollView;
	
	private List<String> images;
	
	private int currentFocusIndex;
	private boolean isSelfFocus;
	
	public GameIntroView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.addView(LayoutInflater.from(context).inflate(R.layout.horizontal_scroll_view, null), new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		this.setPadding(0, 10, 0, 10);
		contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
		scrollView = (CHorizontalScrollView) findViewById(R.id.horizontalScrollView1);
		
		verticalScrollView = new VerticalScrollView(context, null);
		
		titleView = new GameInfoTitleView(context, null);
		titleView.hideButton();
		textView = new GameInfoTextview(context, null);
		
		titleView.setOnClickListener(this);
		textView.setOnClickListener(this);
		
		LayoutParams lp = (LayoutParams) titleView.getLayoutParams();
		lp.setMargins(30, 0, 0, 0);
		titleView.setLayoutParams(lp);
	
		lp = (LayoutParams) textView.getLayoutParams();
		lp.setMargins(0, 0, 10, 0);
		textView.setLayoutParams(lp);
		currentFocusIndex = 0;
		isSelfFocus = false;

		titleView.delegate = this;
	}
	
	public void setdata(GameInfo gameInfo) {
		titleView.setTitle(gameInfo.name);
		textView.setText(StringUtils.readTxt(gameInfo.desc));
		titleView.tvCategory.setText(StringUtils.getCategory(gameInfo.category));
		titleView.tvSize.setText(StringUtils.getSize(gameInfo.size));
		titleView.tvPrice.setText(StringUtils.getPrice(gameInfo.price));
		titleView.tvLanguage.setText(StringUtils.getLanguage(gameInfo.language));
		titleView.tvAge.setText(StringUtils.getAge(gameInfo.age));
		titleView.tvDevice.setText(StringUtils.getCtrlType(gameInfo.ctrltype));
		if (!gameInfo.isFlingEnable())
			titleView.hideButton();
		
		// mark 获取截图文件路径
		List<String> images = new ArrayList<String>();
		for(int i = 0; i < gameInfo.preview_number; i++) {
			images.add(GameInfoManager.sharedCtrl().GetGamePreview(gameInfo.uuid, i+1));
		}
		setList(images);
	}
	
	public void release() {
		for(int i = 0; i < verticalScrollView.contentLayout.getChildCount(); i++) {
			View child = verticalScrollView.contentLayout.getChildAt(i);
			if (child.getClass().equals(CellViewLite.class)) {
				ImageManager.sharedInstance().cancelImage((CellViewLite)child);
			}
		}
	}
	
	public void setGameIntro(String intro){
		textView.setText(intro);
	}
	
	public void setList(List<String> images) {
		this.images = images;
		buildScrollContent();
	}
	
	private void buildScrollContent() {
		contentLayout.removeAllViews();
		contentLayout.addView(titleView);
		
		for (int i = 0; i < images.size(); i++) {
			String image = images.get(i);
			CellViewLite cell = new CellViewLite(context, null);
			LayoutParams lp = new LayoutParams((int)(Unit.c(596) * 0.8), (int)(Unit.c(507) * 0.8));
			lp.setMargins(0, 0, 0, i == images.size() - 1 ? 0 : 12);
//			cell.setPadding(0, 0, 0, i == images.size() - 1 ? 0 : 12);
			cell.setLayoutParams(lp);
			cell.setImageResId(R.drawable.bg_empty_3);
			verticalScrollView.addSubview(cell);
			ImageManager.sharedInstance().setImage(context, cell, image, R.drawable.bg_empty_3);
			cell.setOnClickListener(this);
		}
		contentLayout.addView(verticalScrollView);
		
		contentLayout.addView(textView);
	}
	
	private void refreshDisplay() {
		int childCount = contentLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			CellDelegate cell = (CellDelegate) contentLayout.getChildAt(i);
			if (isSelfFocus && i == currentFocusIndex)
				cell.focus();
			else
				cell.resignFocus();
		}
		this.invalidate();
	}
	
	
	@Override
	public void focus(int direction, int coord) {
		isSelfFocus = true;
		refreshDisplay();
	}
	@Override
	public void resignFocus() {
		isSelfFocus = false;
		refreshDisplay();
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
			if (currentFocusIndex == 0 && !titleView.isBtnHide) {
				cellDidSelectAtIndex(0, null);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			return currentFocusIndex == 1 && verticalScrollView.ScrollUp();
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return currentFocusIndex == 1 && verticalScrollView.ScrollDown();
		case KeyEvent.KEYCODE_DPAD_LEFT:
			int newIndex = currentFocusIndex - 1;
			if (newIndex < 0)
				return false;
			currentFocusIndex = newIndex;
			refreshDisplay();
			checkScroll();
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			int rightIndex = currentFocusIndex + 1;
			if (rightIndex >= contentLayout.getChildCount())
				return false;
			currentFocusIndex = rightIndex;
			refreshDisplay();
			checkScroll();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void checkScroll() {
		View cell = contentLayout.getChildAt(currentFocusIndex);
		int cellLeft = cell.getLeft() - scrollView.getScrollX();
		int cellRight = cell.getRight() - scrollView.getScrollX();
		int parentWidth = this.getWidth();
		int checkWidth = 250;
		if (cellLeft < checkWidth) {
			scrollView.smoothScrollBy(cellLeft - checkWidth, 0);
		}
		else if (cellRight > parentWidth - checkWidth) {
			scrollView.smoothScrollBy(cellRight - (parentWidth- checkWidth), 0);
		}
	}

	@Override
	public void cellDidSelectAtIndex(int index, View cell) {
		Intent intent = new Intent();
		intent.setClass(context, AdControlActivity.class);
		context.startActivity(intent);
		((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	public void onClick(View v) {
		if (!isSelfFocus) {
			KeyBoardManager.sharedInstance().focusKeyBoard(this);
		}
		int childCount = contentLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			CellDelegate cell = (CellDelegate) contentLayout.getChildAt(i);
			if (isSelfFocus && cell == v) {
				cell.focus();
				currentFocusIndex = i;
			}
			else
				cell.resignFocus();
		}
		this.invalidate();
		checkScroll();
	}
}

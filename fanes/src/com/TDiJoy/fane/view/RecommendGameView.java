package com.TDiJoy.fane.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.CellActionDelegate;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.manager.GameInfoManager;
import com.TDiJoy.fane.manager.ImageManager;
import com.TDiJoy.fane.model.GameInfo;
import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.util.Unit;

public class RecommendGameView extends LinearLayout implements Constants,KeyBoardDelegate,OnClickListener{
	private Context context;
	private LinearLayout contentLayout;
	private CHorizontalScrollView scrollView;
	public List<GameInfo> list;
	private int currentFocusIndex;
	private boolean isSelfFocus;
	public CellActionDelegate delegate;
	public RecommendGameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.addView(LayoutInflater.from(context).inflate(R.layout.horizontal_scroll_view, null), new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
		scrollView = (CHorizontalScrollView) findViewById(R.id.horizontalScrollView1);
		isSelfFocus = false;
	}
	
	public void setList(List<GameInfo> list) {
		currentFocusIndex = 0;
		contentLayout.removeAllViews();
		this.list = list;
		int i = 0;
		for (GameInfo game : list) {
			addCell(game, i);
			i++;
		}
		
		if (contentLayout.getChildCount() > 0) {
			CellViewLite cell = (CellViewLite) contentLayout.getChildAt(0);
			LayoutParams lp = (LayoutParams) cell.getLayoutParams();
			lp.setMargins(Unit.c(30), 0, 0, 0);
		}
		
		if (contentLayout.getChildCount() > 1) {
			CellViewLite cell = (CellViewLite) contentLayout.getChildAt(contentLayout.getChildCount() - 1);
			LayoutParams lp = (LayoutParams) cell.getLayoutParams();
			lp.setMargins(0, 0, Unit.c(30), 0);
		}
		refreshDisplay();
	}
	
	private void addCell(GameInfo game, int tag) {
		CellViewLite cell = new CellViewLite(context, null);
		cell.setTag(tag);
		int width = Unit.c(130);
		width = width > 180 ? 180 : width;
		LayoutParams lp = new LayoutParams(width, width);
		lp.setMargins(0, 0, 0, 0);
		cell.setPadding(6, 6, 6, 6);
		cell.setLayoutParams(lp);
		cell.setImageResId(R.drawable.bg_empty_0);
		String imagePath = GameInfoManager.sharedCtrl().GetGameIcon(game.uuid, 1);
		ImageManager.sharedInstance().setImage(context, cell, imagePath, R.drawable.bg_empty_0);
		cell.setOnClickListener(this);
		contentLayout.addView(cell);
	}
	
	private void refreshDisplay() {
		int childCount = contentLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			CellViewLite cell = (CellViewLite) contentLayout.getChildAt(i);
			if (isSelfFocus && i == currentFocusIndex)
				cell.focus();
			else
				cell.resignFocus();
		}
		this.invalidate();
	}
	
	
	@Override
	public void onClick(View v) {
		CellViewLite cell = (CellViewLite) v;
		currentFocusIndex = (Integer) cell.getTag();
		refreshDisplay();
		checkScroll();
		if (delegate != null) {
			delegate.cellDidSelectAtIndex(currentFocusIndex, null);
		}
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
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if (delegate != null) {
				delegate.cellDidSelectAtIndex(currentFocusIndex, null);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			return false;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return false;
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
		CellViewLite cell = (CellViewLite) contentLayout.getChildAt(currentFocusIndex);
		int cellLeft = cell.getLeft() - scrollView.getScrollX();
		int cellRight = cell.getRight() - scrollView.getScrollX();
		int parentWidth = this.getWidth();
		int checkWidth = Unit.c(200);
		if (cellLeft < checkWidth) {
			scrollView.smoothScrollBy(cellLeft - checkWidth, 0);
		}
		else if (cellRight > parentWidth - checkWidth) {
			scrollView.smoothScrollBy(cellRight - (parentWidth- checkWidth), 0);
		}
	}
	
}

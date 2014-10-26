package com.TDiJoy.fane.view;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.GameInfoMenuDelegate;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;

@SuppressLint("Instantiatable")
public class GameInfoMenuView extends LinearLayout implements KeyBoardDelegate,OnClickListener{
	public static final int TYPE_DOWNLOAD = 0;
	public static final int TYPE_PLAY = 1;
	
	public static final int MENU_START = 0;
	public static final int MENU_DOWNLOAD = 1;
	public static final int MENU_BACK = 2;
	public static final int MENU_LIKE = 3;
	public static final int MENU_DELETE = 4;
	
	ImageView btnAction;
	ImageView btnBack;
	ImageView btnLike;
	ImageView btnDelete;
	TextView tvLikeCount;
	TextView tvDownCount;
	
	public int currentFocusIndex;
	int currentType;
	public boolean isLikeEnable = true;
	public boolean isSelfFocus = false;
	public boolean isDownloading = false;
	
	public GameInfoMenuDelegate delegate;
	
	int[][] images = {
			{R.drawable.btn_download_normal, R.drawable.btn_download_focus},
			{R.drawable.btn_back_normal, R.drawable.btn_back_focus},
			{R.drawable.btn_like_normal, R.drawable.btn_like_focus, R.drawable.btn_like_disable},
			{R.drawable.btn_delete_normal, R.drawable.btn_delete_focus},
			{R.drawable.btn_start_normal, R.drawable.btn_start_focus}
	};
	int[][] downloadingImages = {
			{R.drawable.btn_canceldownload_normal, R.drawable.btn_canceldownload_focus},
			{R.drawable.btn_back_normal, R.drawable.btn_back_focus},
			{R.drawable.btn_like_normal, R.drawable.btn_like_focus, R.drawable.btn_like_disable},
			{R.drawable.btn_delete_normal, R.drawable.btn_delete_focus},
			{R.drawable.btn_start_normal, R.drawable.btn_start_focus}
	};
	
	List<ImageView> btns;
	
	public GameInfoMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.addView(LayoutInflater.from(context).inflate(R.layout.game_info_menu, null), new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		btnAction = (ImageView) findViewById(R.id.btnAction);
		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnLike = (ImageView) findViewById(R.id.btnLike);
		btnDelete = (ImageView) findViewById(R.id.btnDelete);
		tvLikeCount = (TextView) findViewById(R.id.tvGameTitle);
		tvDownCount = (TextView) findViewById(R.id.tvDownCount);
		
		btnAction.setTag(0);
		btnBack.setTag(1);
		btnLike.setTag(2);
		btnDelete.setTag(3);
		
		btnAction.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnLike.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		
		btns = new ArrayList<ImageView>();
		btns.add(btnAction);
		btns.add(btnBack);
		btns.add(btnLike);
		btns.add(btnDelete);
		
		setType(TYPE_DOWNLOAD);
	}
	
	/**
	 * 	
	 * @param type 0:下载游戏 1:开始游戏
	 */
	public void setType(int type) {
		currentFocusIndex = 0;
		currentType = type;
		if (type == TYPE_DOWNLOAD) {
			btnLike.setVisibility(INVISIBLE);
			btnDelete.setVisibility(INVISIBLE);
		}
		else {
			btnLike.setVisibility(VISIBLE);
			btnDelete.setVisibility(VISIBLE);
		}
		refreshBtnState();
	}
	
	public void setState(boolean didBuy, final boolean didLike, final int downloadCount, final int likeCount) {
		try {
			int i = 0;
			while (this.getWindowToken() == null && i < 20) {
				i++;
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		post(new Runnable() {
			
			@Override
			public void run() {
				tvLikeCount.setText(String.valueOf(likeCount));
				tvDownCount.setText(String.valueOf(downloadCount));
				if (didLike)
					isLikeEnable = false;
				else
					isLikeEnable = true;
				refreshBtnState();
			}
		});
	}
	
	public void refreshBtnState() {
		for(int i = 0; i < btns.size(); i++) {
			int btnIndex = i;
			int imageIndex = 0;
			if (i == 0 && currentType == TYPE_PLAY) {
				btnIndex = 4;
			}
			if (i == currentFocusIndex && isSelfFocus) {
				imageIndex = 1;
			}
			
			if (i == 2 && !isLikeEnable) {
				imageIndex = 2;
			}
			ImageView imageView = btns.get(i);
			imageView.setImageResource(isDownloading ? downloadingImages[btnIndex][imageIndex] : images[btnIndex][imageIndex]);
		}
	}
	
	
	
	
	@Override
	public void focus(int direction, int coord) {
		isSelfFocus = true;
		refreshBtnState();
	}
	@Override
	public void resignFocus() {
		isSelfFocus = false;
		refreshBtnState();
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
			switch(currentFocusIndex) {
			case 0:
				if (currentType == TYPE_DOWNLOAD)
					menuDidSelect(MENU_DOWNLOAD);
				else {
					menuDidSelect(MENU_START);
				}
				break;
			case 1:
				menuDidSelect(MENU_BACK);
				break;
			case 2:
				menuDidSelect(MENU_LIKE);
				break;
			case 3:
				menuDidSelect(MENU_DELETE);
				break;
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			return false;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return false;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			int newIndex = currentFocusIndex - 1;
			if (newIndex == 2 && !isLikeEnable)
				newIndex--;
			if (newIndex < 0)
				return false;
			currentFocusIndex = newIndex;
			refreshBtnState();
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			int rightIndex = currentFocusIndex + 1;
			if (rightIndex == 2 && currentType == TYPE_DOWNLOAD)
				return true;
			if (rightIndex == 2 && !isLikeEnable)
					rightIndex++;
			if (rightIndex >= btns.size())
				return false;
			currentFocusIndex = rightIndex;
			refreshBtnState();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		int newIndex = (Integer) v.getTag();
		if (newIndex == 2 && !isLikeEnable)
			return;
		currentFocusIndex = newIndex;
		refreshBtnState();
		
		switch(currentFocusIndex) {
		case 0:
			if (currentType == TYPE_DOWNLOAD)
				menuDidSelect(MENU_DOWNLOAD);
			else {
				menuDidSelect(MENU_START);
			}
			break;
		case 1:
			menuDidSelect(MENU_BACK);
			break;
		case 2:
			menuDidSelect(MENU_LIKE);
			break;
		case 3:
			menuDidSelect(MENU_DELETE);
			break;
		}
	}
	
	private void menuDidSelect(int menuIndex) {
		if (delegate == null)
			return;
		switch(menuIndex) {
		case MENU_START:
			delegate.menuStartGame();
			break;
		case MENU_DOWNLOAD:
			delegate.menuDownloadGame();
			break;
		case MENU_BACK:
			delegate.menuBack();
			break;
		case MENU_LIKE:
			delegate.menuLikeGame();
			break;
		case MENU_DELETE:
			delegate.menuDeleteGame();
			break;
		}
	}
}

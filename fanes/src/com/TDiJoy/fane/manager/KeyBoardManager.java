package com.TDiJoy.fane.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import android.util.Log;
import android.view.KeyEvent;

import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.util.Constants;

public class KeyBoardManager implements Constants{
	/**
	 * singleton
	 */
	private static KeyBoardManager _sharedManager = null;
	public static KeyBoardManager sharedInstance() {
		if (_sharedManager == null) {
			_sharedManager = new KeyBoardManager();
		}
		return _sharedManager;
	}
	
	/**
	 * methods
	 */
	public long lastKeyTime = 0;
	
	private List<KeyBoardDelegate> keyBoardList;
	private Stack<KeyBoardDelegate> keyBoardStack;
	public KeyBoardDelegate currentFocusKeyBoard;
	private KeyBoardDelegate lastFocusKeyBoard;
	
	private HashMap<KeyBoardDelegate, KeyBoardDelegate> rLeft;
	private HashMap<KeyBoardDelegate, KeyBoardDelegate> rUp;
	private HashMap<KeyBoardDelegate, KeyBoardDelegate> rRight;
	private HashMap<KeyBoardDelegate, KeyBoardDelegate> rDown;
	
	public KeyBoardManager() {
		keyBoardList = new ArrayList<KeyBoardDelegate>();
		keyBoardStack = new Stack<KeyBoardDelegate>();
		rLeft = new HashMap<KeyBoardDelegate, KeyBoardDelegate>();
		rUp = new HashMap<KeyBoardDelegate, KeyBoardDelegate>();
		rRight = new HashMap<KeyBoardDelegate, KeyBoardDelegate>();
		rDown = new HashMap<KeyBoardDelegate, KeyBoardDelegate>();
		
	}
	/**
	 * 保存当前激活的界面，一般切换activity时调用
	 */
	public void saveKeyBoardDelegate() {
		if (currentFocusKeyBoard != null) {
			keyBoardStack.push(currentFocusKeyBoard);
		}
	}
	/**
	 * 恢复上一个激活的界面
	 */
	public void loadKeyBoardDelegate() {
		KeyBoardDelegate k = keyBoardStack.pop();
		if (k != null) {
			focusKeyBoard(k);
		}
	}
	
	public void addKeyBoardDelegate(KeyBoardDelegate keyBoard) {
		keyBoardList.add(keyBoard);
	}
	
	public void removeKeyBoardDelegate(KeyBoardDelegate keyBoard) {
		keyBoardList.remove(keyBoard);
	}
	
	/**
	 * 增加不同界面之间的按键联系
	 * @param k1		起始界面
	 * @param k2		目标界面
	 * @param direction	方向
	 */
	public void setKeyBoardRelation(KeyBoardDelegate k1, KeyBoardDelegate k2, int direction) {
		if (keyBoardIndex(k1) < 0 || keyBoardIndex(k2) < 0)
			return;
		switch (direction) {
		case DIRECTION_LEFT:
			rLeft.put(k1, k2);
			break;
		case DIRECTION_TOP:
			rUp.put(k1, k2);
			break;
		case DIRECTION_RIGHT:
			rRight.put(k1, k2);
			break;
		case DIRECTION_BOTTOM:
			rDown.put(k1, k2);
			break;
		}
	}
	
	public void removeKeyBoardRelation(KeyBoardDelegate k1, int direction) {
		switch (direction) {
		case DIRECTION_LEFT:
			rLeft.remove(k1);
			break;
		case DIRECTION_TOP:
			rUp.remove(k1);
			break;
		case DIRECTION_RIGHT:
			rRight.remove(k1);
			break;
		case DIRECTION_BOTTOM:
			rDown.remove(k1);
			break;
		}
	}
	
	public void removeKeyBoardRelations(KeyBoardDelegate k1) {
		rLeft.remove(k1);
		rUp.remove(k1);
		rRight.remove(k1);
		rDown.remove(k1);
	}
	
	public void focusKeyBoard(KeyBoardDelegate keyBoard) {
		if (keyBoard == null || keyBoard == currentFocusKeyBoard)
			return;
		lastFocusKeyBoard = currentFocusKeyBoard;
		currentFocusKeyBoard = keyBoard;
		if (lastFocusKeyBoard != null)
			lastFocusKeyBoard.resignFocus();
		lastFocusKeyBoard = null;
		currentFocusKeyBoard.focus(0, 0);
	}
	
	public void log() {
		Log.v("", "keyBoardList : " + keyBoardList.size() + " keyBoardStack : " + keyBoardStack.size());
	}
	
	/**
	 * activity的按键事件传入
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		this.log();
		if (currentFocusKeyBoard == null)
			return false;
//		Log.v("keykey", "key : " + keyCode);
		long nowTime = System.currentTimeMillis();
		if (nowTime - lastKeyTime < 200) {
			return true;
		}
		lastKeyTime = nowTime;
		
		boolean handle = currentFocusKeyBoard.onKeyDown(keyCode, event);
		// 下层已处理
		if (handle) {
			return true;
		}
		// 下层未处理
		KeyBoardDelegate nextKeyBoard = null;
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			nextKeyBoard = rUp.get(currentFocusKeyBoard);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			nextKeyBoard = rDown.get(currentFocusKeyBoard);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			nextKeyBoard = rLeft.get(currentFocusKeyBoard);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			nextKeyBoard = rRight.get(currentFocusKeyBoard);
			break;
		}
		if (nextKeyBoard == null)
			return false;
		this.focusKeyBoard(nextKeyBoard);
		return true;
	}
	
	/**
	 * private methods
	 */
	private int keyBoardIndex(KeyBoardDelegate keyBoard){
		for (int i = 0; i < keyBoardList.size(); i++) {
			KeyBoardDelegate k = keyBoardList.get(i);
			if (keyBoard == k) {
				return i;
			}
		}
		return -1;
	}
	
}

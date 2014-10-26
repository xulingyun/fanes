package com.TDiJoy.fane.delegate;

import android.view.KeyEvent;

public interface KeyBoardDelegate {
	public boolean onKeyDown(int keyCode, KeyEvent event);
	public void focus(int direction, int coord);
	public void resignFocus();
	public int getOutCoord();
}

package com.TDiJoy.fane.layoutlogic;

import android.graphics.Point;

import com.TDiJoy.fane.view.CellView;

public interface LayoutLogic {
	public int getCellNum();
	public int getGroupWidth();
	public int getGroupHeight();
	public Point calculatePositionByIndex(int index);
	public void layoutCellWithIndex(CellView cell, int index);
	public int offsetWithIndexDirection(int index, int direction);
	/**
	 * ����cell��index�����鳤
	 * @param index
	 * @return
	 */
	public int getGroupWidthByIndex(int index);
}

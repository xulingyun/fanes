package com.TDiJoy.fane.layoutlogic;

import android.graphics.Point;
import android.widget.RelativeLayout;

import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.util.Unit;
import com.TDiJoy.fane.view.CellView;

public class GameLayoutLogic implements LayoutLogic, Constants{
	
	private int[][] celloffset = {{-3, 0, 3, 1}, {-2, -1, 1, 0}, {-1, -2, 2, 0}};
	
	@Override
	public int getCellNum() {
		return 3;
	}

	@Override
	public int getGroupWidth() {
		return Unit.c(422 + 9);
	}
	
	@Override
	public int getGroupHeight() {
		return Unit.c(422 + 9 + 20 + 70);
	}
	
	@Override
	public Point calculatePositionByIndex(int index) {
		index %= getCellNum();
		int gap = 9;
		int add = 15;
		switch (index) {
		case 0 :
			return new Point(0, Unit.c(add));
		case 1 :
			return new Point(0, Unit.c(206 + gap + add));
		case 2 :
			return new Point(Unit.c(206 + gap), Unit.c(206 + gap + add));
		}
		return null;
	}

	@Override
	public void layoutCellWithIndex(CellView cell, int index) {
		index %= getCellNum();
		switch (index) {
		case 0 :
			cell.type = CELL_TYPE_LONG;
			cell.setNeedRef(false);
			break;
		case 1 :
			cell.type = CELL_TYPE_SMALL;
			cell.setNeedRef(true);
			break;
		case 2 :
			cell.type = CELL_TYPE_SMALL;
			cell.setNeedRef(true);
			break;
		}
		
		int width = 206;
		int height = 206;
		switch (cell.type) {
		case CELL_TYPE_SMALL:
			break;
		case CELL_TYPE_LONG:
			width = 422;
			height = 206;
			break;
		case CELL_TYPE_TALL:
			width = 298;
			height = 421;
			break;
		}
		cell.bgImageView.setLayoutParams(new RelativeLayout.LayoutParams(Unit.c(width), Unit.c(height)));
		
	}

	@Override
	public int offsetWithIndexDirection(int index, int direction) {
		index %= getCellNum();
		return celloffset[index][-1 - direction];
	}

	@Override
	public int getGroupWidthByIndex(int index) {
		switch (index) {
		case 1 :
			return Unit.c(206 + 9);
		}
		return Unit.c(422 + 9);
	}
}

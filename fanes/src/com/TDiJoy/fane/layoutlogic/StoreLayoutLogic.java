package com.TDiJoy.fane.layoutlogic;

import android.graphics.Point;
import android.widget.RelativeLayout;

import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.util.Unit;
import com.TDiJoy.fane.view.CellView;

public class StoreLayoutLogic implements LayoutLogic, Constants{
	
	private int[][] celloffset = {{-1, 0, 1, 0}, {-1, 0, 3, 1}, {-2, -1, 1, 0}, {-1, -2, 2, 0}, {-3, 0, 2, 1}, {-2, -1, 2, 0}, {-2, 0, 1, -1}};
	
	@Override
	public int getCellNum() {
		return 7;
	}

	@Override
	public int getGroupWidth() {
		return Unit.c(298 + 206*2 + 422 + 9*4);
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
			return new Point(Unit.c(298 + gap), Unit.c(add));
		case 2 :
			return new Point(Unit.c(298 + gap), Unit.c(206 + gap + add));
		case 3 :
			return new Point(Unit.c(298 + 206 + gap * 2), Unit.c(206 + gap + add));
		case 4 :
			return new Point(Unit.c(298 + 422 + gap * 2), Unit.c(0 + add));
		case 5 :
			return new Point(Unit.c(298 + 422 + gap * 2), Unit.c(206 + gap + add));
		case 6 :
			return new Point(Unit.c(298 + 422 + + 206 + gap * 3), Unit.c(0 + add));
		}
		return null;
	}

	@Override
	public void layoutCellWithIndex(CellView cell, int index) {
		index %= getCellNum();
		switch (index) {
		case 0 :
			cell.type = CELL_TYPE_TALL;
			cell.setNeedRef(true);
			break;
		case 1 :
			cell.type = CELL_TYPE_LONG;
			cell.setNeedRef(false);
			break;
		case 2 :
			cell.type = CELL_TYPE_SMALL;
			cell.setNeedRef(true);
			break;
		case 3 :
			cell.type = CELL_TYPE_SMALL;
			cell.setNeedRef(true);
			break;
		case 4 :
			cell.type = CELL_TYPE_SMALL;
			cell.setNeedRef(false);
			break;
		case 5 :
			cell.type = CELL_TYPE_LONG;
			cell.setNeedRef(true);
			break;
		case 6 :
			cell.type = CELL_TYPE_SMALL;
			cell.setNeedRef(false);
			break;
		}
		
		int width = 206;
		int height = 206;
		switch (cell.type) {
		case CELL_TYPE_SMALL:
//			cell.setBgRes(R.drawable.cell_small);
			break;
		case CELL_TYPE_LONG:
			width = 422;
			height = 206;
//			cell.setBgRes(R.drawable.cell_long);
			break;
		case CELL_TYPE_TALL:
			width = 298;
			height = 421;
//			cell.setBgRes(R.drawable.cell_tall);
			break;
		}
		cell.bgImageView.setLayoutParams(new RelativeLayout.LayoutParams(Unit.c(width), Unit.c(height)));
	}

	@Override
	public int offsetWithIndexDirection(int index, int direction) {
		if (index < 0)
			return 0;
		index %= getCellNum();
		return celloffset[index][-1 - direction];
	}

	@Override
	public int getGroupWidthByIndex(int index) {
		switch (index) {
		case 0 :
			return Unit.c(298 + 9);
		case 1 :
		case 2 :
		case 3 :
			return Unit.c(298 + 422 + 9*2);
		case 4 :
			return Unit.c(298 + 422 + 206 + 9*3);
		case 5 :
		case 6 :
		}
		return Unit.c(298 + 206*2 + 422 + 9*4);
	}
}

package com.TDiJoy.fane.layoutlogic;
import android.graphics.Point;
import android.widget.RelativeLayout;

import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.util.Unit;
import com.TDiJoy.fane.view.CellView;

public class AppLayoutLogic implements LayoutLogic, Constants{
	
	private int[][] celloffset = {{-1, 0, 1, 1}, {-2, 0, 2, 1}, {-2, -1, 2, 1}, {-1, -1, 1, 1}, {-1, -1, 1, 1}, {-1, -1, 1, 1}};
	
	@Override
	public int getCellNum() {
		return 14;
	}

	@Override
	public int getGroupWidth() {
		return Unit.c(298 +  206*7 + 9*7);
	}
	
	@Override
	public int getGroupHeight() {
		return Unit.c(422 + 9 + 20 + 70);
	}
	
	@Override
	public Point calculatePositionByIndex(int index) {
		int gap = 9;
		int add = 15;
		if(index==0){
			return new Point(0, Unit.c(add));
		}else{
			int page = (index-1)/2;
			int start = 298 + 9 + page*(206+9);
			int index1 = (index-1)%2;
			switch (index1) {
			case 0:
				return new Point(Unit.c(start), Unit.c(add));
			case 1:
				return new Point(Unit.c(start), Unit.c(206 + gap + add));
			}
			
		}
		return null;
	}

	@Override
	public void layoutCellWithIndex(CellView cell, int index) {
		
		if(index == 0){
			cell.type = CELL_TYPE_TALL;
			cell.setNeedRef(true);
		}else{
			cell.type = CELL_TYPE_SMALL;
			int index1 = (index-1)%2;
			if(index1%2==1){
				cell.setNeedRef(false);
			}
		}
		int width = 206;
		int height = 206;
		switch (cell.type) {
		case CELL_TYPE_SMALL:
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
		if(index ==0){
			return celloffset[index][-1 - direction];
		}else{
			if (index == 1 && direction == DIRECTION_LEFT) {
				return -1;
			}
			if((index-1)%2==0){
				return celloffset[1][-1 - direction];
			}else{
				return celloffset[2][-1 - direction];
			}
		}
		
		/*if (index < 4) {
			return celloffset[index][-1 - direction];
		}
		if (index == 4 && direction == DIRECTION_LEFT) {
			return -3;
		}
		return celloffset[4 + index%2][-1 - direction];*/
	}

	@Override
	public int getGroupWidthByIndex(int index) {
		// TODO 直接返回总长
		return Unit.c(298 +  206*7 + 9*7);
	}
}

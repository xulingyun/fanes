package com.TDiJoy.fane.layoutlogic;

import android.graphics.Point;
import android.widget.RelativeLayout;

import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.util.Unit;
import com.TDiJoy.fane.view.CellView;

/**
 *¶ùÍ¯ÀÖÔ°²¼¾ÖÂß¼­ 
 *
 */
public class ChildLayoutLogic implements LayoutLogic, Constants{
	
	private int[][] celloffset = {{-1, -5, 1, 5}, {-1, -5, 1, 5}, {-1, -5, 1, 5}, {-1, -5, 1, 5}, {-1, -5, 1, 5}
	
	};
	
	@Override
	public int getCellNum() {
		return 10;
	}

	@Override
	public int getGroupWidth() {
		return Unit.c(206 + 206  + 9*2);
	}
	
	@Override
	public int getGroupHeight() {
		return Unit.c(422 + 9 + 20 + 70);
	}
	
	@Override
	public Point calculatePositionByIndex(int index) {
		int gap = 9;
		int add = 15;
		
		if(index<5){
			return new Point(Unit.c(0 +206 *(index%5)+ (index%5)*gap), Unit.c(add));
			
		}else{
			return new Point(Unit.c(0 +206 *(index%5)+(index%5)*gap), Unit.c(206 + gap + add));
		}
//		
//		switch (index) {
//		case 0 :
//			return new Point(Unit.c(0), Unit.c(add));
//		case 1 :
//			return new Point(Unit.c(206 + gap), Unit.c(add));
//		case 2 :
//			return new Point(Unit.c(0), Unit.c(206 + gap + add));
//		case 3 :
//			return new Point(Unit.c(206+ gap), Unit.c(206 + gap + add));
//		}
//		return null;
	}

	@Override
	public void layoutCellWithIndex(CellView cell, int index) {
//		index %= getCellNum();
//		switch (index) {
//		case 0 :
//			cell.type = CELL_TYPE_SMALL;
//			cell.setNeedRef(false);
//			break;
//		case 1 :
//			cell.type = CELL_TYPE_SMALL;
//			cell.setNeedRef(false);
//			break;
//		case 2 :
//			cell.type = CELL_TYPE_SMALL;
//			cell.setNeedRef(true);
//			break;
//		case 3 :
//			cell.type = CELL_TYPE_SMALL;
//			cell.setNeedRef(true);
//			break;

//		}
		
		if(index<5){
			cell.type = CELL_TYPE_SMALL;
			cell.setNeedRef(false);
		}else{
			cell.type = CELL_TYPE_SMALL;
			cell.setNeedRef(true);
		}
		
		int width = 206;
		int height = 206;

		cell.bgImageView.setLayoutParams(new RelativeLayout.LayoutParams(Unit.c(width), Unit.c(height)));
	}

	@Override
	public int offsetWithIndexDirection(int index, int direction) {
		System.out.println("ChildLayoutLogic = "+direction);
		index %= 5;
		return celloffset[index][-1 - direction];
	}

	@Override
	public int getGroupWidthByIndex(int index) {
		
		return Unit.c(206 + 206 + 9*2);
		
//		switch(index) {
//		case 0:
//			return Unit.c(298 + 9);
//		case 1:
//		case 2:
//			return Unit.c(298 + 422 + 9*2);
//		case 3:
//		case 4:
//			return Unit.c(298 + 206 + 422 + 9*3);
//		}
//		return 0;
	}
}

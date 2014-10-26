package com.TDiJoy.fane.layoutlogic;

import android.graphics.Point;
import android.widget.RelativeLayout;

import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.util.Unit;
import com.TDiJoy.fane.view.CellView;

/**
 *�û����Ĳ����߼� 
 *
 */
public class UserCernterLayoutLogic implements LayoutLogic, Constants{
	
	//0Ϊ��� 1Ϊ�ϼ� 2Ϊ�Ҽ� 3Ϊ�¼�
	// ֵΪ��Ϊ�����ƶ����ٸ�cellView ֵΪ��Ϊ�����ƶ����ٸ�cellView
	private int[][] celloffset = {{-1, -1, 2, 1}, {0, -1, 1, 1}, {-2, 0, 1, 1}, {-1, 0, 1, 1}, {-2, -1, 0, 0}};
	
	@Override
	public int getCellNum() {
		return 5;
	}

	@Override
	public int getGroupWidth() {
		return Unit.c(360*2 + 260 +  + 9*2);
	}
	
	@Override
	public int getGroupHeight() {
		return Unit.c(422 + 9 + 20 + 70);
	}
	
	@Override
	public Point calculatePositionByIndex(int index) {
		int gap = 13;
		int add = 15;
		switch (index) {
		case 0 :
			return new Point(Unit.c(0), Unit.c(add));
		case 1 :
			return new Point(Unit.c(0), Unit.c(206+ gap+ add));
		case 2 :
			return new Point(Unit.c(360 +gap), Unit.c(add));
		case 3 :
			return new Point(Unit.c(360+260+ 2*gap), Unit.c(add));
		case 4:
			return new Point(Unit.c(360+260+ 2*gap), Unit.c(206 + gap + add));
			
		}
		return null;
	}

	@Override
	public void layoutCellWithIndex(CellView cell, int index) {
		
		int width = 360;
		int height = 200;
		switch (index) {
		case 0 :
			cell.setNeedRef(false);
			width = 360;
			height = 200;
			break;
		case 1 :
			cell.setNeedRef(true);
			width = 360;
			height = 200;
			break;
		case 2 :
			cell.setNeedRef(true);
			width = 260;
			height = 420;
			break;
		case 3 :
			cell.setNeedRef(false);
			width = 360;
			height = 200;
			break;
		case 4:
			cell.setNeedRef(true);
			width = 360;
			height = 200;
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
		
		return Unit.c(206 + 206 + 9*2);
		
	}
}

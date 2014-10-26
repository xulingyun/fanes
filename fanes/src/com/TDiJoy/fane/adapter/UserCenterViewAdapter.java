package com.TDiJoy.fane.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.TDiJoy.fane.GamesActivity;
import com.TDiJoy.fane.R;
import com.TDiJoy.fane.SysSettingActivity;
import com.TDiJoy.fane.delegate.CellActionDelegate;
import com.TDiJoy.fane.layoutlogic.LayoutLogic;
import com.TDiJoy.fane.model.GameSetting;
import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.view.CellView;

/**
 * 用户中心adapter
 * 按键事件在这个类写cellDidSelectAtIndex
 *
 */
public class UserCenterViewAdapter implements ListAdapter,CellActionDelegate,Constants{
	private List<GameSetting> list;
	private LayoutLogic layoutLogic;
	private Context context;
	public UserCenterViewAdapter(Context context, List<GameSetting> list, LayoutLogic layoutLogic) {
		this.list = list;
		this.layoutLogic = layoutLogic;
		this.context = context;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public GameSetting getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CellView cell = (CellView) convertView;
		if (cell == null) {
			cell = new CellView(parent.getContext(), null);
			cell.setTitleVisible(false);
		}
		layoutLogic.layoutCellWithIndex(cell, position);
		GameSetting gameSetting = this.getItem(position);
		switch (gameSetting.type) {
		case 0:
			cell.setBgRes(R.drawable.user_childlock);
			break;
		case 1:
			cell.setBgRes(R.drawable.user_bill);
			break;
		case 2:
			cell.setBgRes(R.drawable.user_sysinfo);
			break;
		case 3:
			cell.setBgRes(R.drawable.user_mymessage);
			break;
		case 4:
			cell.setBgRes(R.drawable.user_myinfo);
			break;
		}
		
		cell.index = position;
		return cell;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public void cellDidSelectAtIndex(int index, View cell) {
//		Log.v("", "cellDidSelectAtIndex : " + index);
		
		int type = getItem(index).type;
		Intent intent = null;
		
		intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("type", type);
		intent.putExtras(bundle);
        intent.setClass(context, SysSettingActivity.class);
        
	/*	
		switch (type) {
		case 0:
	        
			break;
		case 1:
			
			break;
		case 2:
			intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("type", type);
			intent.putExtras(bundle);
	        intent.setClass(context, SysSettingActivity.class);
			break;
		case 3:
			
			break;
		case 4:
			
			break;

		default:
			break;
		}*/
		/*if (type == GAME_TYPE_CONTROL) {
			intent = new Intent();
			intent.setClass(context, AdControlActivity.class);
		}
		else {
			intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("type", type);
			intent.putExtras(bundle);
	        intent.setClass(context, GamesActivity.class);
		}*/
		if (intent != null) { 
			context.startActivity(intent);
			((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}

	}

}

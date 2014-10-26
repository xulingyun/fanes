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

import com.TDiJoy.fane.GameInfoActivity;
import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.CellActionDelegate;
import com.TDiJoy.fane.layoutlogic.LayoutLogic;
import com.TDiJoy.fane.manager.DownloadStateManager;
import com.TDiJoy.fane.manager.GameInfoManager;
import com.TDiJoy.fane.manager.ImageManager;
import com.TDiJoy.fane.model.GameInfo;
import com.TDiJoy.fane.view.CellView;

public class GameViewAdapter implements ListAdapter,CellActionDelegate{
	private List<GameInfo> list;
	private LayoutLogic layoutLogic;
	private Context context;
	public GameViewAdapter(Context context, List<GameInfo> list, LayoutLogic layoutLogic) {
		this.list = list;
		this.layoutLogic = layoutLogic;
		this.context = context;
	}
	
	public void setList(List<GameInfo> list){
		this.list = list;
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
	public GameInfo getItem(int position) {
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
			cell = new CellView(context, null);
		}
		cell.reset();
		layoutLogic.layoutCellWithIndex(cell, position);
		
		GameInfo gameInfo = getItem(position);
		cell.tvTitle.setText(gameInfo.name);
		cell.setCtrlType(gameInfo.ctrltype);
		// TODO check progress
		if (DownloadStateManager.sharedInstance().getstate(gameInfo.uuid) != -1) {
			cell.showProgress();
			cell.progress.setProgress((int)(DownloadStateManager.sharedInstance().getstate(1) * 100));
			DownloadStateManager.sharedInstance().addProgressListener(cell, gameInfo.uuid);
		}
		else {
			DownloadStateManager.sharedInstance().removeProgressListener(cell);
		}
		
		int index  = position % 3;
		int resId = R.drawable.bg_empty_0;
		String imagePath = "";
		int iconType = 1;
		switch (index) {
		case 0 :
			resId = R.drawable.bg_empty_1;
			iconType = 2;
			break;
		case 1 :
			resId = R.drawable.bg_empty_0;
			iconType = 1;
			break;
		case 2 :
			resId = R.drawable.bg_empty_0;
			iconType = 1;
			break;
		}
		cell.setBgRes(resId);
		imagePath = GameInfoManager.sharedCtrl().GetGameIcon(gameInfo.uuid, iconType);
		ImageManager.sharedInstance().setImage(context, cell, imagePath, resId);
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
		GameInfo game = getItem(index);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("gameinfo", game);
		bundle.putBoolean("needSaveLastKeyboard", true);
		intent.putExtras(bundle);
		intent.setClass(context, GameInfoActivity.class);
		context.startActivity(intent);
		((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

}

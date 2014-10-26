package com.TDiJoy.fane.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class StoreViewAdapter implements ListAdapter,CellActionDelegate{
	private List<GameInfo> list;
	private LayoutLogic layoutLogic;
	private Context context;
	public StoreViewAdapter(Context context, List<GameInfo> list, LayoutLogic layoutLogic) {
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
			cell = new CellView(parent.getContext(), null);
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
		
		int index  = position % 7;
		int resId = R.drawable.bg_empty_0;
		String imagePath = "";
		int iconType = 1;
		switch (index) {
		case 0 :
			resId = R.drawable.bg_empty_2;
			iconType = 3;
			break;
		case 1 :
			resId = R.drawable.bg_empty_1;
			iconType = 2;
			break;
		case 2 :
			resId = R.drawable.bg_empty_0;
			iconType = 1;
			break;
		case 3 :
			resId = R.drawable.bg_empty_0;
			iconType = 1;
			break;
		case 4 :
			resId = R.drawable.bg_empty_0;
			iconType = 1;
			break;
		case 5 :
			resId = R.drawable.bg_empty_1;
			iconType = 2;
			break;
		case 6 :
			resId = R.drawable.bg_empty_0;
			break;
		}
		cell.setBgRes(resId);
		imagePath = GameInfoManager.sharedCtrl().GetGameIcon(gameInfo.uuid, iconType);
		ImageManager.sharedInstance().setImage(context, cell, imagePath, resId);
		
		if(GameInfoManager.sharedCtrl().GetGameInstallCheck(gameInfo.procname)) {
//			Log.v("", "----------- installed ------------" + gameInfo.procname);
			cell.addInstalledCorner();
		}
//		int index  = position % 7;
//		switch (index) {
//		case 0 :
//			cell.setBgRes(R.drawable.cell_tall);
//			break;
//		case 1 :
//			cell.setBgRes(R.drawable.cell_long);
//			break;
//		case 2 :
//			cell.setBgRes(R.drawable.cell_small);
//			break;
//		case 3 :
//			cell.setBgRes(R.drawable.cell_small2);
//			break;
//		case 4 :
//			cell.setBgRes(R.drawable.cell_small);
//			break;
//		case 5 :
//			cell.setBgRes(R.drawable.cell_long2);
//			break;
//		case 6 :
//			cell.setBgRes(R.drawable.cell_small2);
//			break;
//		}
		cell.index = position;
		cell.refreshLayout();
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
		intent.putExtras(bundle);
		intent.setClass(context, GameInfoActivity.class);
		context.startActivity(intent);
		((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

}

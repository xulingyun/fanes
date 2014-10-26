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

public class ChildViewAdapter implements ListAdapter,CellActionDelegate{
	private List<GameInfo> list;
	private LayoutLogic layoutLogic;
	private Context context;
	public ChildViewAdapter(Context context, List<GameInfo> list, LayoutLogic layoutLogic) {
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
		if (position < 0)
			return null;
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
		if (gameInfo != null) {
			cell.tvTitle.setText(gameInfo.name);
			cell.setCtrlType(gameInfo.ctrltype);
			// TODO check progress
			//是否在下载
			if (DownloadStateManager.sharedInstance().getstate(gameInfo.uuid) != -1) {
				cell.showProgress();
				cell.progress.setProgress((int)(DownloadStateManager.sharedInstance().getstate(1) * 100));
				DownloadStateManager.sharedInstance().addProgressListener(cell, gameInfo.uuid);
			}
			else {
				DownloadStateManager.sharedInstance().removeProgressListener(cell);
			}
			//检查是否已经安装
			if(GameInfoManager.sharedCtrl().GetGameInstallCheck(gameInfo.procname)) {
				cell.addInstalledCorner();
			}
		}
		
//		int index  = position % 5;
		int resId = R.drawable.bg_empty_0;
		String imagePath = "";
		int iconType = 1;
//		switch (index) {
//		case 0 :
//			resId = R.drawable.bg_empty_2;
////			cell.addCover(R.drawable.icon_play, Gravity.CENTER);
//			cell.setTitleVisible(false);
//			cell.darkMask = true;
//			iconType = 3;
//			break;
//		case 1 :
//			resId = R.drawable.bg_empty_1;
//			iconType = 2;
//			break;
//		case 2 :
//			resId = R.drawable.bg_empty_0;
//			iconType = 1;
//			break;
//		case 3 :
//			resId = R.drawable.bg_empty_1;
//			iconType = 2;
//			break;
//		case 4 :
//			resId = R.drawable.bg_empty_0;
//			iconType = 1;
//			break;
//		}
//		iconType = 1;
		cell.setBgRes(resId);
		if (gameInfo != null) {
			imagePath = GameInfoManager.sharedCtrl().GetGameIcon(gameInfo.uuid, iconType);
		}
		else {
			//1首页视频预览
//			imagePath = GameInfoManager.sharedCtrl().GetAdImage(1);
		}
		ImageManager.sharedInstance().setImage(context, cell, imagePath, resId);
		
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
	public void cellDidSelectAtIndex(int index, final View cell) {
//		if (index == 0) {
//			// mark 首页视频路径设置
//			final String path = GameInfoManager.sharedCtrl().GetAdVideo(2);
//			File file = new File(path);
//			if (file.exists()) {
//				// 视频已下载
//				Intent intent = new Intent();
//				intent.setClass(context, FullVideoActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putString("path", path);
//				intent.putExtras(bundle);
//				context.startActivity(intent);
//				((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//			}
//			else {
//				// 视频未下载
//				cell.post(new Runnable() {
//					@Override
//					public void run() {
//						((CellView)cell).waitVideo(path);
//					}
//				});
//
//			}
//		}
//		else {
			GameInfo game = getItem(index);
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("gameinfo", game);
			bundle.putBoolean("needSaveLastKeyboard", true);
			intent.putExtras(bundle);
			intent.setClass(context, GameInfoActivity.class);
			context.startActivity(intent);
			((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//		}
	}

}

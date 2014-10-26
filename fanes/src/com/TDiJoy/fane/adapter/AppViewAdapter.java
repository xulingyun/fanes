package com.TDiJoy.fane.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.TDiJoy.fane.AdControlActivity;
import com.TDiJoy.fane.GamesActivity;
import com.TDiJoy.fane.R;
import com.TDiJoy.fane.delegate.CellActionDelegate;
import com.TDiJoy.fane.layoutlogic.LayoutLogic;
import com.TDiJoy.fane.manager.GameInfoManager;
import com.TDiJoy.fane.manager.ImageManager;
import com.TDiJoy.fane.model.GameInfo;
import com.TDiJoy.fane.model.GameType;
import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.view.CellView;

/**
 * 应用商城adapter
 *
 */
public class AppViewAdapter implements ListAdapter,CellActionDelegate,Constants{
	private List<GameType> list;
	private LayoutLogic layoutLogic;
	private Context context;
	public AppViewAdapter(Context context, List<GameType> list, LayoutLogic layoutLogic) {
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
	public GameType getItem(int position) {
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
		GameType gameType = this.getItem(position);
		switch (gameType.type) {
		case GAME_TYPE_HOT:
			// mark 一周热门背景图
			cell.setBgRes(R.drawable.bg_empty_2);
			List<GameInfo> list = GameInfoManager.sharedCtrl().GetGamelist_Popular();
			GameInfo gameInfo = list.size() > 0 ? list.get(0) : null;
			if (gameInfo != null) {
				String imagePath = GameInfoManager.sharedCtrl().GetGameIcon(gameInfo.uuid, 3);
				ImageManager.sharedInstance().setImage(context, cell, imagePath, R.drawable.bg_empty_2);
			}
			cell.addCover(R.drawable.game_type_hot, Gravity.BOTTOM);
			break;
		case GAME_TYPE_CONTROL:
			cell.setBgRes(R.drawable.bg_empty_1);
			String imagePath = GameInfoManager.sharedCtrl().GetAdImage(2);
			ImageManager.sharedInstance().setImage(context, cell, imagePath, R.drawable.bg_empty_1);
			break;
		case GAME_TYPE_RECO:
			cell.setBgRes(R.drawable.game_type_reco);
			break;
		case GAME_TYPE_1:
			cell.setBgRes(R.drawable.game_type_1);
			break;
		case GAME_TYPE_2:
			cell.setBgRes(R.drawable.game_type_2);
			break;
		case GAME_TYPE_3:
			cell.setBgRes(R.drawable.game_type_3);
			break;
		case GAME_TYPE_4:
			cell.setBgRes(R.drawable.game_type_4);
			break;
		case GAME_TYPE_5:
			cell.setBgRes(R.drawable.game_type_5);
			break;
		case GAME_TYPE_6:
			cell.setBgRes(R.drawable.game_type_6);
			break;
		case GAME_TYPE_7:
			cell.setBgRes(R.drawable.game_type_7);
			break;
		case GAME_TYPE_8:
			cell.setBgRes(R.drawable.game_type_8);
			break;
		case GAME_TYPE_9:
			cell.setBgRes(R.drawable.game_type_9);
			break;
		case GAME_TYPE_10:
			cell.setBgRes(R.drawable.game_type_10);
			break;
		case GAME_TYPE_11:
			cell.setBgRes(R.drawable.game_type_11);
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
		if (type == GAME_TYPE_CONTROL) {
			intent = new Intent();
			intent.setClass(context, AdControlActivity.class);
		}
		else {
			intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("type", type);
			intent.putExtras(bundle);
	        intent.setClass(context, GamesActivity.class);
		}
		if (intent != null) { 
			context.startActivity(intent);
			((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}

	}

}

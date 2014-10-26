package com.TDiJoy.fane;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.TDiJoy.fane.R;
import com.TDiJoy.fane.adapter.StoreViewAdapter;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.delegate.PageChangeDelegate;
import com.TDiJoy.fane.layoutlogic.StoreLayoutLogic;
import com.TDiJoy.fane.manager.DownloadStateManager;
import com.TDiJoy.fane.manager.GameInfoManager;
import com.TDiJoy.fane.manager.ImageManager;
import com.TDiJoy.fane.manager.KeyBoardManager;
import com.TDiJoy.fane.manager.RemoteManager;
import com.TDiJoy.fane.model.GameInfo;
import com.TDiJoy.fane.model.GameType;
import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.view.BackImageView;
import com.TDiJoy.fane.view.ContentListView;
import com.TDiJoy.fane.view.GameTypeMenuView;


public class GamesActivity extends Activity implements Constants,PageChangeDelegate{
	private LinearLayout contentLayout;
	private GameTypeMenuView menuView;
	private BackImageView backImageView;
	private StoreViewAdapter storeViewAdapter;
	private ContentListView storeListView;
	private TextView tvTime;
	private TextView tvDate;
	private TextView tvWeak;
	private List<GameInfo> gameList;
	private ImageView ivRemote;
	
	private Thread timeUpdateThread;
	private boolean needUpdate = true;
	private boolean hasRemote = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games);
		
		// time
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvWeak = (TextView) findViewById(R.id.tvWeakDay);
		ivRemote = (ImageView) findViewById(R.id.ivRemote);

		final SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm");
		final SimpleDateFormat formatterDate = new SimpleDateFormat("MM月d日");
		final SimpleDateFormat formatterWeak = new SimpleDateFormat("EEEE");      
//		final DateFormat formatter = SimpleDateFormat.getTimeInstance();

		timeUpdateThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(needUpdate) {
					Date curDate = new Date(System.currentTimeMillis());
					final String strTime = formatterTime.format(curDate);
					final String strDate = formatterDate.format(curDate);
					final String strWeak = formatterWeak.format(curDate);
					GamesActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							tvTime.setText(strTime);
							tvDate.setText(strDate);
							tvWeak.setText(strWeak);
							int num = RemoteManager.sharedInstance().ipcGetRemoteNumber(getApplicationContext());
							if (num > 0) {
								if (!hasRemote) {
									hasRemote = true;
									ivRemote.setImageResource(R.drawable.icon_connect);
								}	
							}
							else {
								if (hasRemote) {
									hasRemote = false;
									ivRemote.setImageResource(R.drawable.icon_disconnect);
								}	
							}
						}
					});
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		timeUpdateThread.start();
		//
		contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
		backImageView = (BackImageView) findViewById(R.id.backImageView1);
		menuView = new GameTypeMenuView(getApplicationContext(), null);
		menuView.activity = this;
		contentLayout.addView(menuView);
		gameList = new ArrayList<GameInfo>();
		StoreLayoutLogic layoutLogic = new StoreLayoutLogic();
		storeListView = new ContentListView(getApplicationContext(), null, layoutLogic);
		storeViewAdapter = new StoreViewAdapter(this, gameList, layoutLogic);
		storeListView.setAdapter(storeViewAdapter);
		contentLayout.addView(storeListView);
		
		// data
		Bundle bundle = getIntent().getExtras();
		int type = bundle.getInt("type");
//		Log.v("", "get type : "+ type);
		
		refreshListWithType(type, true, true);
		
		List<GameType> typeList = new ArrayList<GameType>();
		GameType typeHot = new GameType();
		typeHot.type = GAME_TYPE_HOT;
		typeList.add(typeHot);
		GameType typeReco = new GameType();
		typeReco.type = GAME_TYPE_RECO;
		typeList.add(typeReco);
		
		List<Integer> typeIds = GameInfoManager.sharedCtrl().GetGameTypeList();
		for (Integer typeId : typeIds) {
			GameType temtype = new GameType();
			temtype.type = typeId;
			typeList.add(temtype);
		}
		
		menuView.setGameTypeList(typeList);
		
		menuView.setCurrentType(type);
		//
		
		KeyBoardManager manager = KeyBoardManager.sharedInstance();
		// 保存上一个激活界面
		manager.saveKeyBoardDelegate();
		
		manager.addKeyBoardDelegate(menuView);
		manager.addKeyBoardDelegate(storeListView);
		manager.addKeyBoardDelegate(backImageView);
		
		manager.setKeyBoardRelation(storeListView, menuView, DIRECTION_TOP);
		manager.setKeyBoardRelation(menuView, storeListView, DIRECTION_BOTTOM);
		
		manager.setKeyBoardRelation(menuView, backImageView, DIRECTION_TOP);
		manager.setKeyBoardRelation(backImageView, menuView, DIRECTION_BOTTOM);
		
		manager.focusKeyBoard(storeListView);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		GameInfoManager.sharedInstance().isRunningForeground(getApplicationContext());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		GameInfoManager.sharedInstance().isRunningForeground(getApplicationContext());
//		Log.v("", "gamesactivity resume");
		GameInfoManager.sharedInstance().checkInstall();
		storeListView.refreshAllView();
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handle = KeyBoardManager.sharedInstance().onKeyDown(keyCode, event);
		if (handle)
			return true;
		KeyBoardDelegate curDelegate = KeyBoardManager.sharedInstance().currentFocusKeyBoard;
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (curDelegate == storeListView) {
				int type = menuView.moveLeft();
				if (type != GAME_TYPE_NONE) {
					refreshListWithType(type, false, true);
				}
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (curDelegate == storeListView) {
				int type = menuView.moveRight();
				if (type != GAME_TYPE_NONE) {
					refreshListWithType(type, true, true);
				}
			}
			break;
		case KeyEvent.KEYCODE_BACK:
			this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
//		return handle ? handle : super.onKeyDown(keyCode, event);
		return true;
	}

	@Override
	public void pageChangeTo(int page) {
		// page -> 游戏类型
		refreshListWithType(page, true, false);
	}
	
	private void refreshListWithType(int type, boolean left, boolean focus) {
		gameList.clear();
		// test
//		for (int i = 0; i < type + 7; i++) {
//			gameList.add(new GameInfo());
//		}
		//
		List<GameInfo> newList = null;
		switch (type) {
		case GAME_TYPE_HOT:
			newList = GameInfoManager.sharedCtrl().GetGamelist_Popular();
			break;
		case GAME_TYPE_RECO:
			newList = GameInfoManager.sharedCtrl().GetGamelist_Recommended();
			break;
		default :
			newList = GameInfoManager.sharedCtrl().GetGamelist_Category(type);
		}
		if (newList != null) {
			gameList.addAll(newList);
		}
		//
		if (left) {
			storeListView.reset();
		}
		else {
			storeListView.resetToEnd();
		}
		
		if (focus) {
			storeListView.focus(0, 0);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		KeyBoardManager manager = KeyBoardManager.sharedInstance();
		manager.removeKeyBoardDelegate(menuView);
		manager.removeKeyBoardDelegate(storeListView);
		manager.removeKeyBoardDelegate(backImageView);
		manager.removeKeyBoardRelations(menuView);
		manager.removeKeyBoardRelations(storeListView);
		manager.removeKeyBoardRelations(backImageView);
		
		manager.loadKeyBoardDelegate();
		manager.log();
		ImageManager.sharedInstance().clear();
		
		DownloadStateManager.sharedInstance().clear();
		needUpdate = false;
	}
	
	
}

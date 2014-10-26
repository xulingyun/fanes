package com.TDiJoy.fane;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.TDiJoy.fane.delegate.CellActionDelegate;
import com.TDiJoy.fane.delegate.GameInfoMenuDelegate;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.delegate.PageChangeDelegate;
import com.TDiJoy.fane.manager.DownloadStateManager;
import com.TDiJoy.fane.manager.GameInfoManager;
import com.TDiJoy.fane.manager.KeyBoardManager;
import com.TDiJoy.fane.manager.RemoteManager;
import com.TDiJoy.fane.model.GameInfo;
import com.TDiJoy.fane.request.BuyRequest;
import com.TDiJoy.fane.request.FARequest;
import com.TDiJoy.fane.request.FARequestManager;
import com.TDiJoy.fane.request.GameInfoRequest;
import com.TDiJoy.fane.request.GameTimeRequest;
import com.TDiJoy.fane.request.LikeRequest;
import com.TDiJoy.fane.request.RequestCallback;
import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.util.StringUtils;
import com.TDiJoy.fane.view.GameInfoMenuView;
import com.TDiJoy.fane.view.GameIntroView;
import com.TDiJoy.fane.view.NoCtrlPopView;
import com.TDiJoy.fane.view.NoRechargePopView;
import com.TDiJoy.fane.view.RecommendGameView;


public class GameInfoActivity extends Activity implements Constants,GameInfoMenuDelegate,CellActionDelegate,PageChangeDelegate{
	private LinearLayout contentLayout;
	private LinearLayout footLayout;
	private RecommendGameView recommendGameView;
	private GameInfoMenuView menuView;
	private GameIntroView gameIntroView;
	private NoCtrlPopView popView;
	private NoRechargePopView noRechargepopView;
	
	//是否在包月期间
	private boolean isInOrder = true;
	
	GameInfo game;
	boolean needCheckFinish = false;
	
	private long startTime = -1;
	private boolean didBuy = false;
	private boolean getInfoSucceed = false;
	
	private boolean firstKey = true;
	
	private boolean needSaveLastKeyboard = true;
	
	private Handler handler =new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			gameIntroView.setGameIntro(StringUtils.readTxt(game.desc));
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_info);
		Bundle bundle = getIntent().getExtras();
		game = (GameInfo) bundle.getSerializable("gameinfo");
		needSaveLastKeyboard = bundle.getBoolean("needSaveLastKeyboard");
		
		new Thread(){
			@Override
			public void run(){
				GameInfoManager.sharedCtrl().GameResourceUpdate(game.uuid);
				handler.sendEmptyMessage(0);
			}
		}.start();

		contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
		footLayout = (LinearLayout) findViewById(R.id.footLayout);
		menuView = (GameInfoMenuView) findViewById(R.id.gameInfoMenuView1);
		menuView.delegate = this;
		
		
		if (GameInfoManager.sharedCtrl().GetGameInstallCheck(game.uuid))
			menuView.setType(GameInfoMenuView.TYPE_PLAY);
		
		recommendGameView = new RecommendGameView(this, null);
		footLayout.addView(recommendGameView);
		
		gameIntroView = new GameIntroView(this, null);
		contentLayout.addView(gameIntroView);
		
		gameIntroView.setdata(game);
		
		List<GameInfo> recoList = GameInfoManager.sharedCtrl().GetGamelist_GuessYouLike(game.uuid);
//		Log.v("", "game.uuid : " + game.uuid);
//		for (GameInfo gameinfo : recoList) {
//			Log.v("", "reco : " + gameinfo.uuid);
//		}
		recommendGameView.setList(recoList);
		recommendGameView.delegate = this;
		
		/*FARequestManager.sharedInstance().gameInfo(String.valueOf(game.uuid), new RequestCallback() {
			
			@Override
			public void requestFinished(FARequest request) {
				GameInfoRequest r = (GameInfoRequest) request;
				if(r != null && r.succeed && r.result == GameInfoRequest.STATE_SUCCEED) {
					getInfoSucceed = true;
					didBuy = r.didBuy;
					menuView.setState(r.didBuy, r.didLike, r.downloadCount, r.likeCount);
				}
				if (r != null && r.result == GameInfoRequest.STATE_USER_NOT_EXIST) {
					FARequestManager.sharedInstance().resetAid();
				}
			}
		});*/
	}
	
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Bundle bundle = intent.getExtras();
		game = (GameInfo) bundle.getSerializable("gameinfo");
		
		menuView.setState(false, false,0, 0);
		
		new Thread(){
			@Override
			public void run(){
				GameInfoManager.sharedCtrl().GameResourceUpdate(game.uuid);
				handler.sendEmptyMessage(0);
			}
		}.start();

		if (GameInfoManager.sharedCtrl().GetGameInstallCheck(game.uuid))
			menuView.setType(GameInfoMenuView.TYPE_PLAY);
		else
			menuView.setType(GameInfoMenuView.TYPE_DOWNLOAD);
		
		footLayout.removeView(recommendGameView);
		
		recommendGameView = new RecommendGameView(this, null);
		footLayout.addView(recommendGameView);
		
		contentLayout.removeView(gameIntroView);
		
		gameIntroView = new GameIntroView(this, null);
		contentLayout.addView(gameIntroView);
		
		gameIntroView.setdata(game);
		
		List<GameInfo> recoList = GameInfoManager.sharedCtrl().GetGamelist_GuessYouLike(game.uuid);
//		Log.v("", "game.uuid : " + game.uuid);
//		for (GameInfo gameinfo : recoList) {
//			Log.v("", "reco : " + gameinfo.uuid);
//		}
		recommendGameView.setList(recoList);
		recommendGameView.delegate = this;
		
		/*FARequestManager.sharedInstance().gameInfo(String.valueOf(game.uuid), new RequestCallback() {
			
			@Override
			public void requestFinished(FARequest request) {
				GameInfoRequest r = (GameInfoRequest) request;
				if(r != null && r.succeed && r.result == GameInfoRequest.STATE_SUCCEED) {
					getInfoSucceed = true;
					didBuy = r.didBuy;
					menuView.setState(r.didBuy, r.didLike, r.downloadCount, r.likeCount);
				}
				if (r != null && r.result == GameInfoRequest.STATE_USER_NOT_EXIST) {
					FARequestManager.sharedInstance().resetAid();
				}
			}
		});*/
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// mark 按键延迟
//		if (firstKey && System.currentTimeMillis() - KeyBoardManager.sharedInstance().lastKeyTime < 2000)
//			return true;
//		firstKey = false;
//		Log.v("---", "--- on key down --- " + keyCode);
//		Log.v("", "onKeyDown time : " + System.currentTimeMillis());
		boolean handle = KeyBoardManager.sharedInstance().onKeyDown(keyCode, event);
//		if (System.currentTimeMillis() - createTime < 2000) {
//			return true;
//		}
		if (handle)
			return true;
		KeyBoardDelegate curDelegate = KeyBoardManager.sharedInstance().currentFocusKeyBoard;
		switch (keyCode) {
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if (curDelegate == popView) {
				popView.dismiss();
				KeyBoardManager.sharedInstance().loadKeyBoardDelegate();
				KeyBoardManager.sharedInstance().removeKeyBoardDelegate(popView);
				if (popView.currentState == NoCtrlPopView.STATE_BUY) {
					Intent intent = new Intent();
					intent.setClass(this, AdControlActivity.class);
					this.startActivity(intent);
					this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			}else if(curDelegate == noRechargepopView){
				noRechargepopView.dismiss();
				KeyBoardManager.sharedInstance().loadKeyBoardDelegate();
				KeyBoardManager.sharedInstance().removeKeyBoardDelegate(noRechargepopView);
				if (noRechargepopView.currentState == NoCtrlPopView.STATE_BUY) {  //
					Intent intent = new Intent();
					intent.setClass(this, AdControlActivity.class);
					this.startActivity(intent);
					this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			}
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			break;
		case KeyEvent.KEYCODE_BACK:
			if (curDelegate == popView) {
				popView.dismiss();
				KeyBoardManager.sharedInstance().loadKeyBoardDelegate();
				KeyBoardManager.sharedInstance().removeKeyBoardDelegate(popView);
			}
			else if (curDelegate == noRechargepopView){
				noRechargepopView.dismiss();
				KeyBoardManager.sharedInstance().loadKeyBoardDelegate();
				KeyBoardManager.sharedInstance().removeKeyBoardDelegate(noRechargepopView);
			}
			else {
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				this.finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		}
//		return handle ? handle : super.onKeyDown(keyCode, event);
		return true;
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		RemoteManager.sharedInstance().onResumeMethod();
		// mark 游戏计时
		/*if (startTime > 0) {
			long time = System.currentTimeMillis() - startTime;
			FARequestManager.sharedInstance().gameTime(String.valueOf(game.uuid), (int)(time/60000), new RequestCallback() {
				
				@Override
				public void requestFinished(FARequest request) {
					GameTimeRequest r = (GameTimeRequest) request;
					if (r != null && r.result == GameTimeRequest.STATE_USER_NOT_EXIST) {
						FARequestManager.sharedInstance().resetAid();
					}
				}
			});
		}*/
		//
		KeyBoardManager manager = KeyBoardManager.sharedInstance();
		// 保存上一个激活界面
//		if (needSaveLastKeyboard) {
			manager.saveKeyBoardDelegate();
//		}
		manager.addKeyBoardDelegate(recommendGameView);
		manager.addKeyBoardDelegate(menuView);
		manager.addKeyBoardDelegate(gameIntroView);
		
		manager.setKeyBoardRelation(menuView, gameIntroView, DIRECTION_BOTTOM);
		manager.setKeyBoardRelation(gameIntroView, recommendGameView, DIRECTION_BOTTOM);
		manager.setKeyBoardRelation(recommendGameView, gameIntroView, DIRECTION_TOP);
		manager.setKeyBoardRelation(gameIntroView, menuView, DIRECTION_TOP);
		
		manager.focusKeyBoard(menuView);
		GameInfoManager.sharedInstance().checkInstall();
		if (needCheckFinish && !GameInfoManager.sharedCtrl().GetGameInstallCheck(game.uuid)) {
			this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		
		if (DownloadStateManager.sharedInstance().getstate(game.uuid) != -1) {
			menuView.isDownloading = true;
		}
		else {
			menuView.isDownloading = false;
		}
		
		if (GameInfoManager.sharedCtrl().GetGameInstallCheck(game.uuid))
			menuView.setType(GameInfoMenuView.TYPE_PLAY);
		menuView.refreshBtnState();
	}
	
	

	@Override
	protected void onPause() {
		super.onPause();
		RemoteManager.sharedInstance().onPauseMethod(getApplicationContext());
		KeyBoardManager manager = KeyBoardManager.sharedInstance();
		manager.removeKeyBoardDelegate(recommendGameView);
		manager.removeKeyBoardDelegate(gameIntroView);
		manager.removeKeyBoardDelegate(menuView);
		manager.removeKeyBoardRelations(recommendGameView);
		manager.removeKeyBoardRelations(gameIntroView);
		manager.removeKeyBoardRelations(menuView);
//		if (needSaveLastKeyboard) {
			manager.loadKeyBoardDelegate();
//		}
		manager.log();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		gameIntroView.release();
	}
	
	/**
	 * menu info delegate
	 */

	@Override
	public void menuDownloadGame() {
		// mark 下载游戏
//		Log.v("", "menuDownloadGame");
		if (DownloadStateManager.sharedInstance().getstate(game.uuid) == -1) {
//			if (getInfoSucceed && !didBuy) {
//				// mark 购买
//				FARequestManager.sharedInstance().buyApp(String.valueOf(game.uuid), new RequestCallback() {
//					
//					@Override
//					public void requestFinished(FARequest request) {
//						BuyRequest r = (BuyRequest) request;
//						if (r != null && r.result == BuyRequest.STATE_USER_NOT_EXIST) {
//							FARequestManager.sharedInstance().resetAid();
//						}
//					}
//				});
//			}
			DownloadStateManager.sharedInstance().download(game.uuid);
			this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		else {
			DownloadStateManager.sharedInstance().cancelDownload(game.uuid);
			menuView.isDownloading = false;
			menuView.refreshBtnState();
		}
	}

	@Override
	public void menuStartGame() {
		// mark 开始游戏
//		Log.v("", "menuStartGame");
		
		
		//需要手柄的游戏    没手柄插入
		if (game.ctrltype == 1 && RemoteManager.sharedInstance().ipcGetRemoteNumber(getApplicationContext()) <= 0) {
			
			if(isInOrder){//已经包月
				
				if (popView == null) {
					popView = new NoCtrlPopView(getApplicationContext());
					popView.delegate = this;
				}
				
				KeyBoardManager manager = KeyBoardManager.sharedInstance();
				manager.saveKeyBoardDelegate();
				manager.addKeyBoardDelegate(popView);
				manager.focusKeyBoard(popView);
				popView.showAtLocation(this.contentLayout, Gravity.CENTER, 0, 0);
				
			}else{//没有购买包月
				if (noRechargepopView == null) {
					noRechargepopView = new NoRechargePopView(getApplicationContext());
					noRechargepopView.delegate = this;
				}
				
				KeyBoardManager manager = KeyBoardManager.sharedInstance();
				manager.saveKeyBoardDelegate();
				manager.addKeyBoardDelegate(noRechargepopView);
				manager.focusKeyBoard(noRechargepopView);
				noRechargepopView.showAtLocation(this.contentLayout, Gravity.CENTER, 0, 0);
			}
		}
		else {
			GameInfoManager.sharedCtrl().LaunchGameProg(game.uuid);
			startTime = System.currentTimeMillis();
			RemoteManager.sharedInstance().ipcSetMouse(false);
			RemoteManager.sharedInstance().ipcSetGameConfigFile(game.config);
			RemoteManager.sharedInstance().ipcStop();
		}
		
	}

	@Override
	public void menuLikeGame() {
		// mark 点赞
//		Log.v("", "menuLikeGame");
		if (getInfoSucceed) {
			menuView.isLikeEnable = false;
			menuView.currentFocusIndex = 0;
			menuView.refreshBtnState();
			FARequestManager.sharedInstance().LikeGame(String.valueOf(game.uuid), new RequestCallback() {
				
				@Override
				public void requestFinished(FARequest request) {
					LikeRequest r = (LikeRequest) request;
					if (r != null && r.result == LikeRequest.STATE_USER_NOT_EXIST) {
//						FARequestManager.sharedInstance().resetAid();
					}
				}
			});
		}
	}

	@Override
	public void menuDeleteGame() {
		GameInfoManager.sharedCtrl().ApkUninstall(game.uuid);
		needCheckFinish = true;
	}

	@Override
	public void menuBack() {
		this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	public void cellDidSelectAtIndex(int index, View cell) {
		GameInfo game = recommendGameView.list.get(index);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("gameinfo", game);
		bundle.putBoolean("needSaveLastKeyboard", false);
		intent.putExtras(bundle);
		intent.setClass(this, GameInfoActivity.class);
		this.startActivity(intent);
//		this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	public void pageChangeTo(int page) {
		
		KeyBoardDelegate curDelegate = KeyBoardManager.sharedInstance().currentFocusKeyBoard;
		
		if (curDelegate == popView) {
			popView.dismiss();
			KeyBoardManager.sharedInstance().loadKeyBoardDelegate();
			KeyBoardManager.sharedInstance().removeKeyBoardDelegate(popView);
			if (popView.currentState == NoCtrlPopView.STATE_BUY) {
				Intent intent = new Intent();
				intent.setClass(this, AdControlActivity.class);
				this.startActivity(intent);
				this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		}else if (curDelegate == noRechargepopView) {
			
			noRechargepopView.dismiss();
			KeyBoardManager.sharedInstance().loadKeyBoardDelegate();
			KeyBoardManager.sharedInstance().removeKeyBoardDelegate(noRechargepopView);
//			if (popView.currentState == NoCtrlPopView.STATE_BUY) {
//				Intent intent = new Intent();
//				intent.setClass(this, AdControlActivity.class);
//				this.startActivity(intent);
//				this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//			}
			
		}
	}
	
}

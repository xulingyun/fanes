package com.TDiJoy.fane;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.TDiJoy.fane.adapter.AppViewAdapter;
import com.TDiJoy.fane.adapter.ChildViewAdapter;
import com.TDiJoy.fane.adapter.GameViewAdapter;
import com.TDiJoy.fane.adapter.MainViewAdapter;
import com.TDiJoy.fane.adapter.UserCenterViewAdapter;
import com.TDiJoy.fane.data.GameListCtrl;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.delegate.PageChangeDelegate;
import com.TDiJoy.fane.layoutlogic.AppLayoutLogic;
import com.TDiJoy.fane.layoutlogic.ChildLayoutLogic;
import com.TDiJoy.fane.layoutlogic.GameLayoutLogic;
import com.TDiJoy.fane.layoutlogic.MainLayoutLogic;
import com.TDiJoy.fane.layoutlogic.UserCernterLayoutLogic;
import com.TDiJoy.fane.manager.DownloadStateManager;
import com.TDiJoy.fane.manager.GameInfoManager;
import com.TDiJoy.fane.manager.KeyBoardManager;
import com.TDiJoy.fane.manager.RemoteManager;
import com.TDiJoy.fane.model.GameInfo;
import com.TDiJoy.fane.model.GameSetting;
import com.TDiJoy.fane.model.GameType;
import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.view.ContentListView;
import com.TDiJoy.fane.view.TopMenuView;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends Activity implements KeyBoardDelegate,
		PageChangeDelegate, Constants {
	//顶部的按钮
	private TopMenuView topMenuView;
	//中间的内容
	private LinearLayout contentLayout;

	private ContentListView mainListView;
	private ContentListView gameListView;
	private ContentListView childListView; //儿童乐园
	private ContentListView appListView;
	private ContentListView userCenterListView;  //测试用的 用户中心界面 
//	private SettingView settingView;    //老版本的设置界面
	private ImageView ivRemote;

	private MainViewAdapter mainViewAdapter;
	private GameViewAdapter gameViewAdapter;
	private ChildViewAdapter childViewAdapter;
	private AppViewAdapter appViewAdapter;

	private TextView tvTime;
	private TextView tvDate;
	private TextView tvWeak;

	private boolean needUpdate = true;

	private KeyBoardDelegate currentListView;

	private boolean hasRemote = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvWeak = (TextView) findViewById(R.id.tvWeakDay);
		ivRemote = (ImageView) findViewById(R.id.ivRemote);

		final SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm",Locale.CHINA);
		final SimpleDateFormat formatterDate = new SimpleDateFormat("MM月d日",Locale.CHINA);
		final SimpleDateFormat formatterWeak = new SimpleDateFormat("EEEE",Locale.CHINA);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (needUpdate) {
					Date curDate = new Date(System.currentTimeMillis());
					final String strTime = formatterTime.format(curDate);
					final String strDate = formatterDate.format(curDate);
					final String strWeak = formatterWeak.format(curDate);
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							tvTime.setText(strTime);
							tvDate.setText(strDate);
							tvWeak.setText(strWeak);
							int num = RemoteManager.sharedInstance()
									.ipcGetRemoteNumber(getApplicationContext());
							if (num > 0) {
								if (!hasRemote) {
									hasRemote = true;
									ivRemote.setImageResource(R.drawable.icon_connect);
								}
							} else {
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
		}).start();

		topMenuView = new TopMenuView(this, null);
		topMenuView.mainActivity = this;
		contentLayout.addView(topMenuView);

		final GameListCtrl ctrl = GameInfoManager.sharedCtrl();

		List<GameInfo> games = ctrl.GetGamelist_Home();
		MainLayoutLogic layoutLogic = new MainLayoutLogic();
		mainListView = new ContentListView(getApplicationContext(), null, layoutLogic);
		mainViewAdapter = new MainViewAdapter(this, games, layoutLogic);
		mainListView.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		contentLayout.addView(mainListView);
		mainListView.setAdapter(mainViewAdapter);

		currentListView = mainListView;

		// 我的游戏列表
		List<GameInfo> myGame = new ArrayList<GameInfo>();
		GameLayoutLogic gameLayoutLogic = new GameLayoutLogic();
		gameListView = new ContentListView(getApplicationContext(), null, gameLayoutLogic);
		gameViewAdapter = new GameViewAdapter(this, myGame, gameLayoutLogic);
		gameListView.setAdapter(gameViewAdapter);
		contentLayout.addView(gameListView);
		gameListView.setVisibility(View.GONE);
		
		// 儿童乐园游戏列表
		List<GameInfo> childGame = ctrl.GetGamelist_IptvGame();
		ChildLayoutLogic childLayoutLogic = new ChildLayoutLogic();
		childListView = new ContentListView(getApplicationContext(), null, childLayoutLogic);
		childViewAdapter = new ChildViewAdapter(this, childGame, childLayoutLogic);
		childListView.setAdapter(childViewAdapter);
		contentLayout.addView(childListView);
		childListView.setVisibility(View.GONE);
		
		
		// 游戏类型列表
		List<GameType> typeList = new ArrayList<GameType>();
		GameType typeHot = new GameType();
		typeHot.type = GAME_TYPE_HOT;
		typeList.add(typeHot);
		GameType typeReco = new GameType();
		typeReco.type = GAME_TYPE_RECO;
		typeList.add(typeReco);

		List<Integer> typeIds = ctrl.GetGameTypeList();
		for (Integer typeId : typeIds) {
			GameType temtype = new GameType();
			temtype.type = typeId;
			typeList.add(temtype);
		}

		refreshMyGameList();

		AppLayoutLogic appLayoutLogic = new AppLayoutLogic();
		appListView = new ContentListView(getApplicationContext(), null, appLayoutLogic);
		appViewAdapter = new AppViewAdapter(this, typeList, appLayoutLogic);
		appListView.setAdapter(appViewAdapter);
		contentLayout.addView(appListView);
		appListView.setVisibility(View.GONE);
		
		
		
		//用ContentListView创建的用户中心  测试
		List<GameSetting> userCenterList = new ArrayList<GameSetting>();
		GameSetting gameSetting0 = new GameSetting(0);
		GameSetting gameSetting1 = new GameSetting(1);
		GameSetting gameSetting2 = new GameSetting(2);
		GameSetting gameSetting3 = new GameSetting(3);
		GameSetting gameSetting4 = new GameSetting(4);
		
		userCenterList.add(gameSetting0);
		userCenterList.add(gameSetting1);
		userCenterList.add(gameSetting2);
		userCenterList.add(gameSetting3);
		userCenterList.add(gameSetting4);
		
		UserCernterLayoutLogic userLayoutLogic  = new UserCernterLayoutLogic();
		userCenterListView = new ContentListView(getApplicationContext(), null, userLayoutLogic);
		UserCenterViewAdapter userCenterAdapter = new UserCenterViewAdapter(this, userCenterList, userLayoutLogic);
		userCenterListView.setAdapter(userCenterAdapter);
		contentLayout.addView(userCenterListView);
		userCenterListView.setVisibility(View.GONE);

		// keyboard manager
		KeyBoardManager manager = KeyBoardManager.sharedInstance();
		manager.addKeyBoardDelegate(topMenuView);
		manager.addKeyBoardDelegate(mainListView);
		manager.addKeyBoardDelegate(gameListView);
		manager.addKeyBoardDelegate(childListView);
		manager.addKeyBoardDelegate(appListView);
		manager.addKeyBoardDelegate(userCenterListView);

		manager.setKeyBoardRelation(mainListView, topMenuView, DIRECTION_TOP);
		manager.setKeyBoardRelation(gameListView, topMenuView, DIRECTION_TOP);
		manager.setKeyBoardRelation(childListView, topMenuView, DIRECTION_TOP);
		manager.setKeyBoardRelation(appListView, topMenuView, DIRECTION_TOP);
		manager.setKeyBoardRelation(userCenterListView, topMenuView, DIRECTION_TOP);
		manager.setKeyBoardRelation(topMenuView, mainListView, DIRECTION_BOTTOM);

		manager.focusKeyBoard(topMenuView);

		new Thread() {
			@Override
			public void run() {
				boolean isOver = false;
				while (!isOver) {
					int state = ctrl.GetSelfUpdateState();
					if (state == -1) {
						isOver = true;
					} else if (state == 2) {
						Looper.prepare();
						AlertDialog.Builder builder = new Builder(
								MainActivity.this);
						builder.setMessage("新版游戏大厅已下载，是否更新？");
						builder.setTitle("提示");
						builder.setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
						builder.setPositiveButton("更新", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								ctrl.InstallSelfUpdate();
							}
						});
						builder.create().show();
						Looper.loop();
						isOver = true;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}.start();

		Intent ThridPart_intent = getIntent();
		String s1 = ThridPart_intent.getStringExtra("APPString");
		System.out.println("mainActivity AppString s1 = " +s1);
		if (s1 != null) {
			// mark 从游戏启动，进入游戏界面
			GameInfo gameinfo = GameInfoManager.sharedCtrl()
					.GetGameInfo_ProcessName(s1);
			if (gameinfo != null) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("gameinfo", gameinfo);
				bundle.putBoolean("needSaveLastKeyboard", true);
				intent.putExtras(bundle);
				intent.setClass(this, GameInfoActivity.class);
				startActivity(intent);
				System.out.println("启动游戏详情界面");
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
//			ThridPart_intent.removeExtra("APPString");
		} else {
			// mark 判断联网
			int initResult = ThridPart_intent.getIntExtra("InitResult", 0);
			if (initResult == 2) {
				AlertDialog.Builder builder = new Builder(this);
				builder.setMessage("网络未连接，请检查网络设置！");
				builder.setTitle("提示");
				if (new Intent(android.provider.Settings.ACTION_SETTINGS)
						.resolveActivity(getPackageManager()) != null) {
					builder.setPositiveButton("设置", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							startActivity(new Intent(
									android.provider.Settings.ACTION_SETTINGS));
							finish();
						}
					});
				}
				builder.setNegativeButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);//友盟統計
		RemoteManager.sharedInstance().onPauseMethod(getApplicationContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);//友盟統計 
		
		RemoteManager.sharedInstance().onResumeMethod();
		GameInfoManager.sharedInstance().checkInstall();
		refreshMyGameList();
		if (currentListView == mainListView || currentListView == gameListView ||currentListView == childListView)
			mainListView.refreshAllView();
			childListView.refreshAllView();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
//		Log.v("", "---- onNewIntent ----");
		String s1 = intent.getStringExtra("APPString");
//		Log.v("", "--- s1 : " + s1);
		
		GameInfo gameinfo = GameInfoManager.sharedCtrl()
				.GetGameInfo_ProcessName(s1);
		if (gameinfo != null) {
			Intent newintent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("gameinfo", gameinfo);
			bundle.putBoolean("needSaveLastKeyboard", true);
			newintent.putExtras(bundle);
			newintent.setClass(this, GameInfoActivity.class);
			System.out.println("onNewIntent 启动游戏详情界面");
			startActivity(newintent);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		KeyBoardManager.sharedInstance().log();
		System.out.println("mainactivity onKeyDown  " );
		boolean handle = KeyBoardManager.sharedInstance().onKeyDown(keyCode,
				event);
		if (handle)
			return true;
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("是否退出游戏大厅?");
			builder.setTitle("提示");
			builder.setNegativeButton("退出", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
//					FARequestManager.sharedInstance().faneTime();
					GameInfoManager.sharedCtrl().DownloadApkCancel();
					DownloadStateManager.sharedInstance().destory();
					MainActivity.this.finish();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
			builder.setPositiveButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void focus(int direction, int coord) {
	}

	@Override
	public void resignFocus() {
	}

	@Override
	public int getOutCoord() {
		return 0;
	}

	//切换界面
	@Override
	public void pageChangeTo(int page) {
		KeyBoardManager manager = KeyBoardManager.sharedInstance();
		switch (page) {
		case 0:
			//首页推荐
			if (currentListView != mainListView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = mainListView;
				mainListView.setVisibility(View.VISIBLE);
				manager.setKeyBoardRelation(topMenuView, mainListView,
						DIRECTION_BOTTOM);
			}
			break;

		case 1: //我的游戏
			if (currentListView != gameListView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = gameListView;
				gameListView.setVisibility(View.VISIBLE);
				refreshMyGameList();
				if (gameViewAdapter.getCount() > 0) {
					manager.setKeyBoardRelation(topMenuView, gameListView, DIRECTION_BOTTOM);
				}
				else {
					KeyBoardManager.sharedInstance().removeKeyBoardRelation(topMenuView, DIRECTION_BOTTOM);
				}
			}
			break;

		case 2: //儿童乐园
			if (currentListView != childListView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = childListView;
				childListView.setVisibility(View.VISIBLE);
				manager.setKeyBoardRelation(topMenuView, childListView,
						DIRECTION_BOTTOM);
			}
			break;
		case 3: //应用商店
			if (currentListView != appListView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = appListView;
				appListView.setVisibility(View.VISIBLE);
				manager.setKeyBoardRelation(topMenuView, appListView,
						DIRECTION_BOTTOM);
			}
			break;
		case 4: //个人中心
		case 5:
			
			if (currentListView != userCenterListView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = userCenterListView;
//				userCenterListView.refresh();
				userCenterListView.setVisibility(View.VISIBLE);
				 manager.setKeyBoardRelation(topMenuView, userCenterListView,
				 DIRECTION_BOTTOM);
//				manager.removeKeyBoardRelation(topMenuView, DIRECTION_BOTTOM);
			}
			
			
			
			// if (currentListView != notOpenView) {
			// ((View)currentListView).setVisibility(View.GONE);
			// currentListView = notOpenView;
			// notOpenView.setVisibility(View.VISIBLE);
			// // manager.setKeyBoardRelation(topMenuView, appListView,
			// DIRECTION_BOTTOM);
			// manager.removeKeyBoardRelation(topMenuView, DIRECTION_BOTTOM);
			// }
			
			
			
			//layout布局的用户中心
			/*if (currentListView != userCenterView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = userCenterView;
				userCenterView.refresh();
				userCenterView.setVisibility(View.VISIBLE);
				// manager.setKeyBoardRelation(topMenuView, appListView,
				// DIRECTION_BOTTOM);
				manager.removeKeyBoardRelation(topMenuView, DIRECTION_BOTTOM);
			}
			*/
			//系统设置
/*			if (currentListView != settingView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = settingView;
				settingView.refresh();
				settingView.setVisibility(View.VISIBLE);
				// manager.setKeyBoardRelation(topMenuView, appListView,
				// DIRECTION_BOTTOM);
				manager.removeKeyBoardRelation(topMenuView, DIRECTION_BOTTOM);
			}
*/			break;
		}
	}

	//我的游戏列表刷新
	private void refreshMyGameList() {
		List<GameInfo> myGame = GameInfoManager.sharedCtrl()
				.GetGamelist_Installed();
		if (myGame.size() != gameViewAdapter.getCount()) {
			gameViewAdapter.setList(myGame);
			gameListView.reset();
		}
		if (currentListView == gameListView) {
			if (myGame.size() > 0) {
//				KeyBoardManager.sharedInstance().focusKeyBoard(topMenuView);
				if (KeyBoardManager.sharedInstance().currentFocusKeyBoard != topMenuView) {
					gameListView.focus(0, 0);
				}			
//				KeyBoardManager.sharedInstance().setKeyBoardRelation(topMenuView, gameListView,
//						DIRECTION_BOTTOM);
			}
			else {
				KeyBoardManager.sharedInstance().focusKeyBoard(topMenuView);
				KeyBoardManager.sharedInstance().removeKeyBoardRelation(topMenuView, DIRECTION_BOTTOM);
			}
		}
	}
}

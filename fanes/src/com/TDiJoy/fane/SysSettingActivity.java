package com.TDiJoy.fane;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.delegate.PageChangeDelegate;
import com.TDiJoy.fane.manager.KeyBoardManager;
import com.TDiJoy.fane.manager.RemoteManager;
import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.view.ChildLockView;
import com.TDiJoy.fane.view.MyBillView;
import com.TDiJoy.fane.view.MyInfoView;
import com.TDiJoy.fane.view.MyMessageView;
import com.TDiJoy.fane.view.SettingView;
import com.TDiJoy.fane.view.TopMenuView;
import com.TDiJoy.fane.view.UserCenterMenuView;

//系统设置界面
public class SysSettingActivity extends Activity implements Constants, KeyBoardDelegate,PageChangeDelegate{
	
//	private UserCenterMenuView userCenterMenuView;
	private TopMenuView userCenterMenuView;

	private LinearLayout contentLayout;
	
	private TextView tvTime;
	private TextView tvDate;
	private TextView tvWeak;
	private ImageView ivRemote;
	
	private Thread timeUpdateThread;
	private boolean needUpdate = true;
	private boolean hasRemote = false;
	
	private KeyBoardDelegate currentListView;
	
	private ChildLockView mChildLockView;  //童锁设置
	private MyBillView mMyBillView;  //我的账单
	private SettingView settingView;   //系统信息
	private MyMessageView mMyMessageView;  //我的消息
	private MyInfoView mMyInfoView;  //个人信息
	
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
		
		
		timeUpdateThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(needUpdate) {
					Date curDate = new Date(System.currentTimeMillis());
					final String strTime = formatterTime.format(curDate);
					final String strDate = formatterDate.format(curDate);
					final String strWeak = formatterWeak.format(curDate);
					SysSettingActivity.this.runOnUiThread(new Runnable() {
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
		
		contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
		
		userCenterMenuView = new TopMenuView(getApplicationContext(), null);
		userCenterMenuView.mainActivity = this;
//		userCenterMenuView.activity = this;
		contentLayout.addView(userCenterMenuView);
		
		settingView = new SettingView(getApplicationContext(), null);
		
		mChildLockView = new ChildLockView(this);
		
		mMyInfoView = new MyInfoView(this);
		
		mMyBillView = new MyBillView(this);
		
		mMyMessageView = new MyMessageView(this);
		
		Bundle bundle = getIntent().getExtras();
		int type = bundle.getInt("type");
		switch (type) {
		case 0:
			contentLayout.addView(mChildLockView);
			break;
		case 1:
			contentLayout.addView(mMyBillView);
			break;
		case 2:
			contentLayout.addView(settingView);
			break;
		case 3:
			contentLayout.addView(mMyMessageView);
			break;
		case 4:
			contentLayout.addView(mMyInfoView);
			break;

		default:
			break;
		}
		
		
		
		
		
		KeyBoardManager manager = KeyBoardManager.sharedInstance();
		manager.addKeyBoardDelegate(userCenterMenuView);
	/*	manager.addKeyBoardDelegate(settingView);
		manager.addKeyBoardDelegate(mChildLockView);
		manager.addKeyBoardDelegate(mMyInfoView);
		manager.addKeyBoardDelegate(mMyBillView);
		manager.addKeyBoardDelegate(mMyMessageView);
		
		manager.setKeyBoardRelation(settingView, userCenterMenuView, DIRECTION_TOP);
		
		manager.setKeyBoardRelation(mChildLockView, userCenterMenuView, DIRECTION_TOP);
		
		manager.setKeyBoardRelation(mMyInfoView, userCenterMenuView, DIRECTION_TOP);
		
		manager.setKeyBoardRelation(mMyBillView, userCenterMenuView, DIRECTION_TOP);
		
		manager.setKeyBoardRelation(mMyMessageView, userCenterMenuView, DIRECTION_TOP);
		manager.setKeyBoardRelation(userCenterMenuView, mMyMessageView, DIRECTION_BOTTOM);
		manager.focusKeyBoard(userCenterMenuView);*/
		
		
	}
	
	


	@Override
	public void focus(int direction, int coord) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resignFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getOutCoord() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void pageChangeTo(int page) {
	/*	KeyBoardManager manager = KeyBoardManager.sharedInstance();
		switch (page) {
		case 0:
			//童锁设置
			if (currentListView != settingView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = settingView;
				settingView.setVisibility(View.VISIBLE);
				manager.setKeyBoardRelation(userCenterMenuView, settingView,
						DIRECTION_BOTTOM);
			}
			break;
		case 1:
			//账单设置
			if (currentListView != mMyBillView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = mMyBillView;
				mMyBillView.setVisibility(View.VISIBLE);
				manager.setKeyBoardRelation(userCenterMenuView, mMyBillView,
						DIRECTION_BOTTOM);
			}
			break;
		case 2:
			//系统设置
			if (currentListView != mChildLockView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = mChildLockView;
				mChildLockView.setVisibility(View.VISIBLE);
				manager.setKeyBoardRelation(userCenterMenuView, mChildLockView,
						DIRECTION_BOTTOM);
			}
			break;
		case 3:
			//系统设置
			if (currentListView != mMyMessageView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = mMyMessageView;
				mMyMessageView.setVisibility(View.VISIBLE);
				manager.setKeyBoardRelation(userCenterMenuView, mMyMessageView,
						DIRECTION_BOTTOM);
			}
			break;
		case 4:
			//系统设置
			if (currentListView != mMyInfoView) {
				((View) currentListView).setVisibility(View.GONE);
				currentListView = mMyInfoView;
				mMyInfoView.setVisibility(View.VISIBLE);
				manager.setKeyBoardRelation(userCenterMenuView, mMyInfoView,
						DIRECTION_BOTTOM);
			}
			break;
		}
		*/
	}

}

package com.TDiJoy.fane.manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.util.SparseArray;

import com.TDiJoy.fane.data.GameListCtrl;
import com.TDiJoy.fane.delegate.ProgressViewDelegate;
import com.TDiJoy.fane.model.ProgressCheckEntity;

public class DownloadStateManager implements Runnable{
	/**
	 * singleton
	 */
	private static DownloadStateManager _sharedManager = null;
	public static DownloadStateManager sharedInstance() {
		if (_sharedManager == null) {
			_sharedManager = new DownloadStateManager();
		}
//		_sharedManager.log();
		return _sharedManager;
	}
	/**
	 * 初始化
	 */
	public DownloadStateManager() {
		stateMap = new SparseArray<Float>();
		checkList = new ArrayList<ProgressCheckEntity>();
		new Thread(this).start();
		
		// mark test
//		setState(1, test);
	}
	private SparseArray<Float> stateMap;
	private List<ProgressCheckEntity> checkList;
//	int test = 0;
	private int currentDownloadUuid = -1;
	private boolean isLooping = true;
	
	public void download(int uuid) {
		stateMap.put(uuid, 0.f);
		checkDownload();
	}
	
	public void cancelDownload(int uuid) {
		if (uuid == currentDownloadUuid) {
//			currentDownloadUuid = -1;
			GameInfoManager.sharedCtrl().DownloadApkCancel();
//			stateMap.put(uuid, -2.f);
		}
		else {
			stateMap.remove(uuid);
			ProgressCheckEntity entity = getEntityByUuid(uuid);
			if (entity != null) {
				entity.delegate.get().progressChanged(-2);
				synchronized (checkList) {
					checkList.remove(entity);
				}
			}
		}
//		checkDownload();
	}
	
	public void destory() {
		isLooping = false;
		synchronized (stateMap) {
			stateMap.clear();
		}
		synchronized (checkList) {
			checkList.clear();
		}
		_sharedManager = null;
	}
	private void checkDownload() {
		int state = GameInfoManager.sharedCtrl().DownloadApkGetState();
//		Log.v("download state", ""+state);
		if (currentDownloadUuid != -1) {
			switch (state) {
			case 0:// 未下载
				
				break;
			case 1:// 下载中
				
				break;
			case -1:// 下载失败
				setState(currentDownloadUuid, -1);
				currentDownloadUuid = -1;
				break;
			case 2:// 下载完成
				setState(currentDownloadUuid, 1);
				GameInfoManager.sharedInstance().installGame(currentDownloadUuid);
				currentDownloadUuid = -1;
				break;
			case -3://
				setState(currentDownloadUuid, -3);
				currentDownloadUuid = -1;
				break;
			case -2://
				setState(currentDownloadUuid, -2);
				currentDownloadUuid = -1;
				break;
			}
		}
		
		if (currentDownloadUuid == -1) {
			synchronized (stateMap) {
				for (int i = 0; i < stateMap.size(); i++) {
					int uuid = stateMap.keyAt(i);
					if (stateMap.get(uuid) == 0) {
						// 开始下载
						GameInfoManager.sharedCtrl().DownloadApkStart(uuid);
						currentDownloadUuid = uuid;
						break;
					}
				}
			}
		}
	}
	public void setState(int uuid, float progress) {
//		progress = progress < 0 ? 0 : (progress > 1.f ? 1.f : progress);
		progress = progress > 1.f ? 1.f : progress;
		synchronized (stateMap) {
			stateMap.put(uuid, progress);
		}
	}
	
	public void removeState(int uuid) {
		synchronized (stateMap) {
			stateMap.remove(uuid);
		}
	}
	
	public float getstate(int uuid) {
		return stateMap.get(uuid, -1.f);
	}
	
	public void addProgressListener(ProgressViewDelegate delegate, int uuid) {
		synchronized (checkList) {
			ProgressCheckEntity entity = getEntityByDelegate(delegate);
			if (entity == null) {
				entity = new ProgressCheckEntity();
				entity.delegate = new WeakReference<ProgressViewDelegate>(delegate);
				checkList.add(entity);
			}
			entity.uuid = uuid;
		}
		
	}
	
	public void removeProgressListener(ProgressViewDelegate delegate) {
		synchronized (checkList) {
			ProgressCheckEntity entity = getEntityByDelegate(delegate);
			if (entity != null) {
				checkList.remove(entity);
			}
		}
	}
	
	public void clear() {
		synchronized (checkList) {
			checkList.clear();
		}
	}
	
	private ProgressCheckEntity getEntityByDelegate(ProgressViewDelegate delegate) {
		synchronized (checkList) {
			for (ProgressCheckEntity entity : checkList) {
				if (entity.delegate.get() == delegate)
					return entity;
			}
			return null;
		}
	}
	
	private ProgressCheckEntity getEntityByUuid(int uuid) {
		synchronized (checkList) {
			for (ProgressCheckEntity entity : checkList) {
				if (entity.uuid == uuid)
					return entity;
			}
			return null;
		}
	}
	
	@Override
	public void run() {
		GameListCtrl ctrl = GameInfoManager.sharedCtrl();
//		while (isLooping) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			test++;
//			test = test > 100 ? 0 : test;
//			setState(1, test/100.f);
			if (currentDownloadUuid != -1) {
				synchronized (stateMap) {
					float progress = ctrl.DownloadApkGetProcess();
//					Log.v("", "DownloadApkGetProcess : " + progress);
					stateMap.put(currentDownloadUuid, progress);
				}
				checkDownload();
			}
			
			synchronized (checkList) {
				for (int i = 0; i < checkList.size(); i++) {
					ProgressCheckEntity entity = checkList.get(i);
					ProgressViewDelegate delegate = entity.delegate.get();
					if (delegate == null) {
//						Log.v("null", "progress entity.delegate.get() null");
						checkList.remove(entity);
						i--;
//						continue;
					}
					else {
						float progress = getstate(entity.uuid);
						delegate.progressChanged(progress);
						if (progress == 1) {
							removeState(entity.uuid);
							checkList.remove(entity);
							i--;
						}
						else if (progress == -1 || progress == -3 || progress == -2) {
							removeState(entity.uuid);
							checkList.remove(entity);
							i--;
						}
					}
					entity = null;
					delegate = null;
				}
			}
//			Log.v("", "checkList : " + checkList.size());
//			log();
//		}
			
			if (isLooping) {
				new Thread(this).start();
			}
	}
	
	public void log() {
		Log.v("", "+++++++++++++++++++++++++++++++");
		Log.v("", "checklist size : "+ checkList.size());
		for(int i = 0; i < stateMap.size(); i++) {
			Log.v("", "uuid : "+stateMap.keyAt(i)+"  progress : "+stateMap.valueAt(i));
		}
		Log.v("", "+++++++++++++++++++++++++++++++");
	}

}

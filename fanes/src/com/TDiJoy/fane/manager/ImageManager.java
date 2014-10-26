package com.TDiJoy.fane.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import com.TDiJoy.fane.delegate.ImageViewDelegate;
import com.TDiJoy.fane.model.ImageCheckEntity;

public class ImageManager implements Runnable{
	/**
	 * singleton
	 */
	private static ImageManager _sharedManager = null;
	public static ImageManager sharedInstance() {
		if (_sharedManager == null) {
			_sharedManager = new ImageManager();
		}
		return _sharedManager;
	}
	/**
	 * 初始化
	 */
	public ImageManager() {
		bmpFactoryOptions = new BitmapFactory.Options();  
		bmpFactoryOptions.inSampleSize = 1;
		bmpFactoryOptions.inJustDecodeBounds = false;
		bmpFactoryOptions.inPurgeable = true;
		bmpFactoryOptions.inInputShareable = true;
//		bmpFactoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;  

//		bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_4444;
		
		try {
			BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(bmpFactoryOptions,true);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		list = new ArrayList<ImageCheckEntity>();
		
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		Log.v("", "maxMemory : " + maxMemory);
	    int cacheSize = maxMemory / 16;
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            return bitmap.getByteCount() / 1024;  
	        }
	    };
 
		new Thread(this).start();
	}
	
	private List<ImageCheckEntity> list;
	private BitmapFactory.Options bmpFactoryOptions;
	private LruCache<String, Bitmap> mMemoryCache;  
	public boolean setImage(Context context, final ImageViewDelegate imageViewDelegate, final String imagePath, int resId){
		File imagefile = new File(imagePath);
		if (imagefile.exists()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bitmap bitmap = bitmapWithPath(imagePath);
					if (bitmap != null) {
						imageViewDelegate.setImageWithBitmap(bitmap, true);
					}
				}
			}).start();
			
			
//			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//			imageViewDelegate.setImageWithBitmap(bitmap, false);
			return true;
		}
//		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
//		imageViewDelegate.setImageWithBitmap(bitmap);
		// start check
//		Log.v("", "add check");
		ImageCheckEntity entity = new ImageCheckEntity();
		entity.delegate = new WeakReference<ImageViewDelegate>(imageViewDelegate);
		entity.imagePath = imagePath;
		entity.startTime = System.currentTimeMillis();
		addEntity(entity);
		//
		return false;
	}
	
	//通过resId创建图片
	public Bitmap bitmapWithResId(Context context, int resId) {
		Bitmap bitmap = getBitmapFromMemCache("res" + resId);
		if (bitmap == null) {
			try {
				InputStream is = context.getResources().openRawResource(resId);
				bitmap = BitmapFactory.decodeStream(is, null, bmpFactoryOptions);
//				bitmap = BitmapFactory.decodeResource(context.getResources(), resId, bmpFactoryOptions);
				addBitmapToMemoryCache("res" + resId, bitmap);
			}
			catch (OutOfMemoryError e) {
				Log.e("FANES", "OOM bitmapWithResId");
			}
		}
//		Log.v("", "cache total size " + mMemoryCache.size());
		return bitmap;
	}
	
	public Bitmap bitmapWithPath(String path) {
		Bitmap bitmap = getBitmapFromMemCache(path);
		if (bitmap == null) {
			try {
				InputStream is = new FileInputStream(path);
				bitmap = BitmapFactory.decodeStream(is, null, bmpFactoryOptions);
//				bitmap = BitmapFactory.decodeFile(path, bmpFactoryOptions);
				addBitmapToMemoryCache(path, bitmap);
			}
			catch (OutOfMemoryError e) {
				Log.e("FANES", "OOM bitmapWithPath");
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
//		Log.v("", "cache total size " + mMemoryCache.size());
		return bitmap;
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (key == null || bitmap == null) {
			return;
		}
	    mMemoryCache.put(key, bitmap);
	}
	
	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
	
	public void clear() {
		synchronized(list){
			list.clear();
		}
	}
	
	/**
	 * 取消图片检查替换
	 * @param imageViewDelegate
	 */
	public void cancelImage(ImageViewDelegate imageViewDelegate){
		synchronized(list){
			for (ImageCheckEntity entity : list) {
				if (entity.delegate.get() == imageViewDelegate) {
					removeEntity(entity);
					break;
				}
			}
		}
	}
	
	private void addEntity(ImageCheckEntity entity) {
		synchronized(list){
			list.add(entity);
		}
	}
	
	private void removeEntity(ImageCheckEntity entity) {
		synchronized(list){
			list.remove(entity);
		}
	}
	@Override
	public void run() {
		for(;;){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long nowTime = System.currentTimeMillis();
			synchronized(list) {
				for (int i = 0; i < list.size(); i++) {
					final ImageCheckEntity entity = list.get(i);
					if (entity.delegate.get() == null) {
//						Log.v("null", "image entity.delegate.get() null");
						removeEntity(entity);
						i--;
						continue;
					}
					File imagefile = new File(entity.imagePath);
					if (imagefile.exists()) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								Bitmap bitmap = bitmapWithPath(entity.imagePath);
								if (bitmap != null) {
									entity.delegate.get().setImageWithBitmap(bitmap, true);
								}
							}
						}).start();
						
						removeEntity(entity);
						i--;
						continue;
					}
					if (nowTime - entity.startTime > 10000) {
						removeEntity(entity);
						i--;
					}
				}
			}
			
//			Log.v("XXXX", "image check num: " + list.size());
		}
	}
}

package com.TDiJoy.fane.util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ImageUtils {

	/** 
	  * ��SDCard�϶�ȡͼƬ 
	  * @param pathName 
	  * @return */
	public static Bitmap getBitmapFromSDCard(String pathName) {
		return BitmapFactory.decodeFile(pathName);
	}

	/** 
	  * ����ͼƬ 
	  * @param bitmap 
	  * @param width 
	  * @param height 
	  * @return */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / w, (float) height / h);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	/** 
	  * ��Drawableת��ΪBitmap 
	  * @param drawable 
	  * @return 
	  */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	/** 
	  * ���Բ��ͼƬ 
	  * @param bitmap 
	  * @param roundPx 
	  * @return 
	  */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/** 
	  * ��ô���Ӱ��ͼƬ 
	  * @param bitmap 
	  * @return 
	  */
	public static Bitmap getReflectionImageWithOrigin(Bitmap bitmap) {
		
		// ԭʼͼƬ�ͷ���ͼƬ�м�ļ��
		final int reflectionGap = 0;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		// ��ת  
		Matrix matrix = new Matrix();
		
		// ��һ������Ϊ1��ʾx��������ԭ����Ϊ׼���ֲ��䣬������ʾ���򲻱䡣   
        // �ڶ�������Ϊ-1��ʾy��������ԭ����Ϊ׼���ֲ��䣬������ʾ����ȡ����
		matrix.preScale(1, -1);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		return bitmapWithReflection;
	}
	
	public static Bitmap getReflectionImage(Bitmap bitmap, int height) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int imageHeight = bitmap.getHeight();
		if (height > imageHeight)
			height = imageHeight;

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, imageHeight - height,
				width, height, matrix, false);
		Bitmap bitmapWithReflection = null;
		try {
			bitmapWithReflection = Bitmap.createBitmap(width,
					height, Config.ARGB_8888);
		}
		catch (OutOfMemoryError e) {
			Log.e("FANES", "OOM getReflectionImage");
		}
		if (bitmapWithReflection == null) {
			return null;
		}
		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(reflectionImage, 0, 0, null);
		reflectionImage.recycle();
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, 0, 0,
				bitmapWithReflection.getHeight(), 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, 0, width, bitmapWithReflection.getHeight(), paint);
		return bitmapWithReflection;
	}
	public static int px2dip(Context context, float pxValue){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(pxValue / scale + 0.5f); 
	}
	public static int dip2px(Context context, float dipValue){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(dipValue * scale + 0.5f); 
	}
}

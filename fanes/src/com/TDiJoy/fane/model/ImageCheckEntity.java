package com.TDiJoy.fane.model;

import java.lang.ref.WeakReference;

import com.TDiJoy.fane.delegate.ImageViewDelegate;

public class ImageCheckEntity {
	public WeakReference<ImageViewDelegate> delegate;
	public String imagePath;
	public long startTime;
}

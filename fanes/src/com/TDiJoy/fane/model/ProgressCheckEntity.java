package com.TDiJoy.fane.model;

import java.lang.ref.WeakReference;

import com.TDiJoy.fane.delegate.ProgressViewDelegate;

public class ProgressCheckEntity {
	public WeakReference<ProgressViewDelegate> delegate;
	public int uuid;
}

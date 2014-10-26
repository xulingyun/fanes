package com.TDiJoy.fane.view;

import java.util.LinkedList;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

import com.TDiJoy.fane.delegate.CellActionDelegate;
import com.TDiJoy.fane.delegate.KeyBoardDelegate;
import com.TDiJoy.fane.layoutlogic.LayoutLogic;
import com.TDiJoy.fane.manager.KeyBoardManager;
import com.TDiJoy.fane.util.Constants;
import com.TDiJoy.fane.util.Unit;

@SuppressLint("ViewConstructor")
public class ContentListView extends AdapterView<ListAdapter> implements KeyBoardDelegate,Constants {
	protected ListAdapter mAdapter;
	private int mLeftGroupIndex = -1;
	private int mRightGroupIndex = 0;
	private int mDisplayOffset = 0;
	protected int mCurrentX;
	protected int mNextX;
	private int mMaxX = Integer.MAX_VALUE;
	
	protected Scroller mScroller;
	private GestureDetector mGesture;
	
	public Queue<View> mRemovedViewQueue = new LinkedList<View>();
	
	private LayoutLogic layoutLogic;
	
	private int currentFocusIndex;
	private int currentFocusChildIndex;
	
	private boolean isSelfFocus = false;
	
	private boolean needReset = false;
	private boolean hasLayout = false;
	
	private boolean fromLeft = true;
	
	Runnable refresh;
	Paint mPaint = new Paint();
	PorterDuffXfermode duffmode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
	
	Bitmap shadowCache = null;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public ContentListView(Context context, AttributeSet attrs, LayoutLogic layoutLogic) {
		super(context, attrs);
		this.layoutLogic = layoutLogic;
		
		// mark 自定义viewgroup绘制顺序
		setChildrenDrawingOrderEnabled(true);
		initView();
		this.setLayerType(View.LAYER_TYPE_SOFTWARE,null);//view硬件加速
		this.setClipChildren(false);
		
		mScroller = new Scroller(getContext());
		// touch
		mGesture = new GestureDetector(getContext(), mOnGesture);
		
		refresh = new Runnable() {
			@Override
			public void run() {
				requestLayout();
				invalidate();
			}
		};
	}
	
	private synchronized void initView() {
		mLeftGroupIndex = -1;
		mRightGroupIndex = 0;
		mDisplayOffset = Unit.c(80);
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		currentFocusIndex = -1;
	}
	
	public synchronized void resetToEnd() {
//		Log.v("temlog", "resetToEnd");
		mRemovedViewQueue.clear();
		mScroller.forceFinished(true);
		if (!hasLayout) {
			needReset = true;
			requestLayout();
			return;
		}
		fromLeft = false;
		
		removeAllViewsInLayout();
		int cellNum = layoutLogic.getCellNum();
		int cellCount = getAdapter().getCount();
		int groupCount = (int) Math.ceil(1.f*cellCount/cellNum);
		
		mLeftGroupIndex = groupCount - 1;
		mRightGroupIndex = groupCount;
		
		mNextX = 0;
		
		currentFocusIndex = -1;
		int addWidth = Unit.c(200);
		int totalWidth = getWidth();
		while (addWidth < totalWidth && mLeftGroupIndex >= 0) {
			int groupNum = cellNum;
			if (mLeftGroupIndex == groupCount - 1) {
				groupNum = cellCount%cellNum == 0 ? cellNum : cellCount%cellNum;
			}
			for (int i = 0; i < groupNum; i++) {
				int childIndex = (mLeftGroupIndex + 1) * cellNum - i - 1 - (cellNum - groupNum);
				View child = mAdapter.getView(childIndex, mRemovedViewQueue.poll(), this);
				addAndMeasureChild(child, 0);
			}
			addWidth += layoutLogic.getGroupWidthByIndex(groupNum - 1);
			mLeftGroupIndex--;
		}
		mDisplayOffset = totalWidth - addWidth;
		mCurrentX = (mLeftGroupIndex + 1)*layoutLogic.getGroupWidth() + Unit.c(200) - mDisplayOffset;
		mNextX = mCurrentX;
		mMaxX = mCurrentX + totalWidth;
		requestLayout();
	}
	
	public void refreshAllView() {
		for (int i = 0; i <getChildCount(); i++) {
			CellView child = (CellView) getChildAt(i);
			mAdapter.getView(child.index, child, this);
		}
	}
	
	public synchronized void reset() {
//		Log.v("temlog", "reset");
		fromLeft = true;
		mRemovedViewQueue.clear();
		mScroller.forceFinished(true);
		initView();
		removeAllViewsInLayout();
		requestLayout();
		this.invalidate();
	}
	
	// key board delegate
	@Override
	public void focus(int direction, int coord) {
		isSelfFocus = true;
		if (currentFocusIndex == -1) {
			if (fromLeft) {
				currentFocusIndex = 0;
				currentFocusChildIndex = 0;
			}
			else {
				currentFocusIndex = getAdapter().getCount() - 1;
				currentFocusChildIndex = getChildCount() - 1;
			}
		}
		
		CellView cell = CellViewWithIndex(currentFocusIndex);
		if (cell != null) {
			cell.focus();
			releaseShadowCache();
			invalidate();
		}
	}

	@Override
	public void resignFocus() {
		isSelfFocus = false;
		CellView cell = CellViewWithIndex(currentFocusIndex);
		if (cell != null) {
			cell.resignFocus();
		}
		this.invalidate();
//		this.requestLayout();
	}

	@Override
	public int getOutCoord() {
		return 0;
	}

	//
	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		mAdapter = adapter;
		reset();
	}

	@Override
	public View getSelectedView() {
		return null;
	}

	@Override
	public void setSelection(int position) {	
	}
	
	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (!hasLayout) {
			hasLayout = true;
			if (needReset) {
				resetToEnd();
				return;
			}
		}
		
		if (mAdapter == null) {
			return;
		}
		if (mScroller.computeScrollOffset()) {
			int scrollx = mScroller.getCurrX();
			mNextX = scrollx;
		}
		
		if (mNextX < 0) {
			mNextX = 0;
			mScroller.forceFinished(true);
		}
		if (mNextX > mMaxX) {
			mNextX = mMaxX;
			mScroller.forceFinished(true);
		}

		int dx = mCurrentX - mNextX;
		
		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);

		mCurrentX = mNextX;

		if (!mScroller.isFinished()) {
//			post(new Runnable() {
//				@Override
//				public void run() {
//					requestLayout();
//					invalidate();
//				}
//			});
			post(refresh);
		}
		// mark 重绘
//		invalidate();
	}
	
	private void positionItems(final int dx) {
		if (getChildCount() > 0) {
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			int cellNum = layoutLogic.getCellNum();
			int count = 0;
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				Point p = layoutLogic.calculatePositionByIndex(i);
				int childWidth = child.getMeasuredWidth();
				child.layout(left + p.x, p.y, left + childWidth + p.x, child.getMeasuredHeight() + p.y);
//				Log.v("lll", "--left : " + (left + p.x));
				count++;
				if (count >= cellNum) {
					left += layoutLogic.getGroupWidth();
					count = 0;
							
				}
			}
		}
	}
	
	private void fillList(final int dx) {
		int edge = 0;
		View child = getChildAt(getChildCount() - 1);
		if (child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);

		edge = 0;
		child = getChildAt(0);
		if (child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);

	}
	
	private void fillListRight(int rightEdge, final int dx) {
		int groupNum = calculateGroupNum();
		int cellNum = layoutLogic.getCellNum();
		
		while (rightEdge + dx < getWidth() && mRightGroupIndex < groupNum) {
			for (int i = 0; i < cellNum; i++) {
				int childIndex = mRightGroupIndex * cellNum + i;
				if (childIndex < mAdapter.getCount()) {
//					Log.v("lll", "fill : " + childIndex);
					View child = mAdapter.getView(childIndex, mRemovedViewQueue.poll(), this);
					addAndMeasureChild(child, -1);
				}
				else {
					break;
				}
			}
			// mark 最后一组可能长度多余
			rightEdge += layoutLogic.getGroupWidth();
			
			if (mRightGroupIndex == groupNum - 1) {
				mMaxX = mCurrentX + rightEdge - getWidth() + Unit.c(400);
			}
			if (mMaxX < 0) {
				mMaxX = 0;
			}
			mRightGroupIndex++;
			CellView currentCell = CellViewWithIndex(currentFocusIndex);
			currentFocusChildIndex = getChildIndex(currentCell);
		}
	}
	
	private void fillListLeft(int leftEdge, final int dx) {
		int cellNum = layoutLogic.getCellNum();
		while (leftEdge + dx > 0 && mLeftGroupIndex >= 0) {
			for (int i = 0; i < cellNum; i++) {
				int childIndex = (mLeftGroupIndex + 1) * cellNum - i - 1;
//				Log.v("lll", "fill : " + childIndex);
				View child = mAdapter.getView(childIndex, mRemovedViewQueue.poll(), this);
				addAndMeasureChild(child, 0);
			}
			leftEdge -= layoutLogic.getGroupWidth();
			mLeftGroupIndex--;
			mDisplayOffset -= layoutLogic.getGroupWidth();
			// refresh child index;
			CellView currentCell = CellViewWithIndex(currentFocusIndex);
			currentFocusChildIndex = getChildIndex(currentCell);
		}
	}
	
	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while (child != null && calculateFirstGroupRight() + dx <= 0) {
			mDisplayOffset += layoutLogic.getGroupWidth();
			removeFirstGroup();
//			Log.v("lll", "removeFirstGroup");
			child = getChildAt(0);
		}
		
		child = getChildAt(getChildCount() - 1);
		while (child != null && calculateLastGroupLeft() + dx >= getWidth()) {
			removeLastGroup();
//			Log.v("lll", "removeLastGroup");
			child = getChildAt(getChildCount() - 1);
		}
	}
	
	
	
	// tools
	private int calculateFirstGroupRight() {
		return mDisplayOffset + layoutLogic.getGroupWidth();
	}
	
	private void removeFirstGroup() {
		for (int i = 0; i < layoutLogic.getCellNum(); i++) {
			View child = getChildAt(0);
			if (child != null) {
				mRemovedViewQueue.offer(child);
				removeViewInLayout(child);
			}
		}
		mLeftGroupIndex++;
	}
	
	private int calculateLastGroupLeft() {
		int groupNum = mRightGroupIndex - mLeftGroupIndex;
		if (groupNum <= 0)
			return 0;
		return mDisplayOffset + layoutLogic.getGroupWidth() * (groupNum - 2);
	}
	
	private void removeLastGroup() {
		int cellNum = layoutLogic.getCellNum();
		int lastNum = getChildCount() % cellNum;
		if (lastNum > 0)
			cellNum = lastNum;
		
		for (int i = 0; i < cellNum; i++) {
			View child = getChildAt(getChildCount() - 1);
			if (child != null) {
				mRemovedViewQueue.offer(child);
				removeViewInLayout(child);
			}
		}
		mRightGroupIndex--;
	}
	
	private int calculateGroupNum() {
		int cellNum = layoutLogic.getCellNum();
		int modelCount = mAdapter.getCount();
		return modelCount/cellNum + (modelCount % cellNum > 0 ? 1 : 0);
	}

	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}

		addViewInLayout(child, viewPos, params, true);
		child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
	}
	
	// touch
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = mGesture.onTouchEvent(ev);
		return handled;
	}
	
	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return ContentListView.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// mark 去掉鼠标滑动
			return ContentListView.this.onFling(e1, e2, velocityX, velocityY);
//			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			// mark 去掉鼠标滑动
			synchronized (ContentListView.this) {
				mNextX += (int) distanceX;
			}
			requestLayout();

			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			boolean beforeFocus = ContentListView.this.isSelfFocus;
			KeyBoardManager.sharedInstance().focusKeyBoard(ContentListView.this);
			Rect viewRect = new Rect();
			for (int i = 0; i < getChildCount(); i++) {
				CellView child = (CellView) getChildAt(i);
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop();
				int bottom = child.getBottom();
				viewRect.set(left, top, right, bottom);
				if (viewRect.contains((int) e.getX(), (int) e.getY())) {
					
					CellView currentCell = CellViewWithIndex(currentFocusIndex);
					if (currentCell == child && beforeFocus) {
						CellActionDelegate delegate = (CellActionDelegate)getAdapter();
						delegate.cellDidSelectAtIndex(currentFocusIndex, currentCell);
						return true;
					}
					if (currentCell != null) {
						currentCell.resignFocus();
					}
					child.focus();
					releaseShadowCache();
					ContentListView.this.invalidate();
					
					currentFocusIndex = child.index;
					currentFocusChildIndex = i;
	
					checkScroll(child);
					
//					if (mOnItemClicked != null) {
//						mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
//					}
//					if (mOnItemSelected != null) {
//						mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
//					}
//					break;
					
				}
			}
			return true;
		}

	};
	
	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		synchronized (ContentListView.this) {
			mScroller.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
		}
		requestLayout();
		return true;
	}

	protected boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		return true;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// mark 不分发按键事件
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (currentFocusIndex == -1) {
			currentFocusIndex = 0;
		}
		CellView nextCell = null;
		int nextIndex = 0;
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
//			Log.v("KeyEvent", "KEYCODE_DPAD_CENTER");
			CellActionDelegate delegate = (CellActionDelegate)getAdapter();
			delegate.cellDidSelectAtIndex(currentFocusIndex, CellViewWithIndex(currentFocusIndex));
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			nextIndex = layoutLogic.offsetWithIndexDirection(currentFocusIndex, DIRECTION_TOP);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			nextIndex = layoutLogic.offsetWithIndexDirection(currentFocusIndex, DIRECTION_BOTTOM);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			nextIndex = layoutLogic.offsetWithIndexDirection(currentFocusIndex, DIRECTION_LEFT);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			nextIndex = layoutLogic.offsetWithIndexDirection(currentFocusIndex, DIRECTION_RIGHT);
			break;
			// TODO 测试代码，记得删除
//		case KeyEvent.KEYCODE_0:
//			Log.v("", "test key 0");
//			CellView view = (CellView) getChildAt(0);
//			view.showProgress();
//			view.tvTitle.setText("show");
//			break;
//		case KeyEvent.KEYCODE_9:
//			Log.v("", "test key 9");
//			CellView view2 = (CellView) getChildAt(0);
//			view2.hideProgress();
//			view2.tvTitle.setText("hide");
//			break;
		}
		
		if (nextIndex != 0) {
			nextCell = CellViewWithIndex(currentFocusIndex + nextIndex);
		}
		else {
			invalidate();
			return false;
		}
		
		if (nextCell != null) {
			CellView currentCell = CellViewWithIndex(currentFocusIndex);
			if (currentCell != null) {
				currentCell.resignFocus();
			}
			nextCell.focus();
			releaseShadowCache();
			invalidate();
			
			currentFocusIndex = currentFocusIndex + nextIndex;
			currentFocusChildIndex = getChildIndex(nextCell);
			
			checkScroll(nextCell);
			
			return true;
		}
		else {
			// mark 没有找到下一个cell时
		}
		invalidate();
		return super.onKeyDown(keyCode, event);
	}
	
	private void checkScroll(CellView cell) {
		int[] location = new int[2];  
		cell.getLocationOnScreen(location);
		
		int checkWidth = Unit.c(200);
		int left = (int) (location[0] + (cell.getMeasuredWidth() * cell.focusScale - cell.getWidth())/2);
		if (left < checkWidth) {
			scrollTo(mNextX - (checkWidth - left));
		}
		else if (left + cell.getMeasuredWidth() > getWidth() - checkWidth) {
			scrollTo(mNextX + left + cell.getMeasuredWidth() - (getWidth()- checkWidth));
		}
		
	}
	
	private synchronized CellView CellViewWithIndex(int index) {
		for (int i = 0; i < getChildCount(); i++) {
			CellView view = (CellView) getChildAt(i);
			if (view.index == index)
				return view;
		}
		return null;
	}
	
	public synchronized void scrollTo(int x) {
//		mNextX =  mNextX <= mScroller.getStartX() ? mScroller.getStartX() + 1 : mNextX;
//		mNextX =  mNextX >= mScroller.getFinalX() ? mScroller.getFinalX() - 1 : mNextX;
		if (Math.abs(x - mNextX) <= 3)
			return;
//		Log.v("scrollto", "mNextX : "+mNextX +" x : "+x+"");
		mNextX = mNextX == 0 ? 1 : mNextX;
		if (mNextX > 1)
			mNextX--;
//		mNextX = mNextX == mScroller.getStartX() ? mNextX - 1 : mNextX;
//		Log.v("", String.format("startx : %d toX : %d scroll:%d %d", mNextX, x - mNextX, mScroller.getStartX(), mScroller.getFinalX()));
		mScroller.startScroll(mNextX, 0, x - mNextX, 0, 600);
		requestLayout();
	}
	
	private int getChildIndex(View child) {
		for (int i = 0; i < getChildCount(); i++) {
			if(child == getChildAt(i))
				return i;
		}
		return -1;
	}
	
	private void releaseShadowCache() {
		if (shadowCache != null && !shadowCache.isRecycled()) {
			shadowCache.recycle();
		}
		shadowCache = null;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		boolean drawFocus = currentFocusIndex >= 0 && isSelfFocus;
		CellView cell = null;
		if (drawFocus) {
			mPaint.reset();
			cell = CellViewWithIndex(currentFocusIndex);
			if (cell == null) {
				return;
			}
			if (!cell.isFocused) {
				cell.focus();
				releaseShadowCache();
				ContentListView.this.invalidate();
				return;
			}
			if (cell != null && cell.getMeasuredWidth() > 0 && cell.getMeasuredHeight() > 0) {
				int width = cell.getWidth();
				int height = cell.bgImageView.getHeight();
				int newWidth = (int) (width * cell.focusScale);
				int newHeight = (int) (height * cell.focusScale);
				if (shadowCache == null || shadowCache.isRecycled()) {
					try {
						shadowCache = Bitmap.createBitmap(width + Unit.c(300), height + Unit.c(300), Config.ARGB_8888);
					}
					catch (OutOfMemoryError e) {
							Log.e("FANES", "OOM shadowCache");
					}
					if (shadowCache == null)
						return;
					Canvas sCanvas = new Canvas(shadowCache);

					RectF brect = new RectF(Unit.c(150) - (newWidth - width)/2, (int) (Unit.c(150) - cell.focusAdd), Unit.c(150) - (newWidth - width)/2 + newWidth, (int) (Unit.c(150) - cell.focusAdd + newHeight));
					RectF rect = new RectF(Unit.c(150), Unit.c(150), Unit.c(150) + width, Unit.c(150) + height);
					
					if (!cell.needRef) {
						mPaint.setShadowLayer(Unit.c(40), 0, Unit.c(40), 0x99000000);
					}
					else {
						mPaint.setShadowLayer(Unit.c(40), 0, Unit.c(40), 0x55000000);
					}
					sCanvas.drawRect(rect, mPaint);
					mPaint.setShadowLayer(Unit.c(30), 0, Unit.c(30), 0xFF000000);
					sCanvas.drawRect(rect, mPaint);
					
					mPaint.setShadowLayer(0, 0, 0, 0);
					mPaint.setXfermode(duffmode);
					sCanvas.drawRect(brect, mPaint);
					
					int w = Unit.c(3);
					brect.left -= w;
					brect.top -= w;
					brect.right += w;
					brect.bottom += w;
					mPaint.setXfermode(null);
					mPaint.setShadowLayer(0, 0, 0, 0);
					mPaint.setColor(Color.WHITE);
					mPaint.setStyle(Paint.Style.STROKE);
					mPaint.setStrokeWidth(Unit.c(6));
					sCanvas.drawRoundRect(brect, Unit.c(3), Unit.c(3), mPaint);
				}
				canvas.drawBitmap(shadowCache, cell.getLeft() - Unit.c(150), cell.getTop() - Unit.c(150), mPaint);
			}
		}
	}
	// mark 获取子视图绘制顺序
	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		int newIndex = i;
		if (currentFocusChildIndex >= 0 && currentFocusChildIndex < childCount) {
			newIndex = i < currentFocusChildIndex ? i : (i == childCount - 1 ? currentFocusChildIndex : i + 1);
		}
		return newIndex;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		releaseShadowCache();
	}
	
	
}

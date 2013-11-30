package net.minirenren.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class SlidingView extends ViewGroup {

	private FrameLayout mContainer;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	private static final int SNAP_VELOCITY = 1000;
	private View mMenuView;
	private View mDetailView;

	public SlidingView(Context context) {
		super(context);
		init();
	}

	public SlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlidingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer.measure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContainer.layout(0, 0, width, height);
	}

	private void init() {
		mContainer = new FrameLayout(getContext());
		mContainer.setBackgroundColor(0xff000000);
		mScroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		super.addView(mContainer);
	}

	public void setView(View v) {
		if (mContainer.getChildCount() > 0) {
			mContainer.removeAllViews();
		}
		mContainer.addView(v);
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				// Keep on drawing until the animation has finished.
				invalidate();
			} else {
				clearChildrenCache();
			}
		} else {
			clearChildrenCache();
		}
	}

	private boolean mIsBeingDragged;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mIsBeingDragged = false;
			break;

		case MotionEvent.ACTION_MOVE:
			final float dx = x - mLastMotionX;
			final float xDiff = Math.abs(dx);
			final float yDiff = Math.abs(y - mLastMotionY);
			if (xDiff > mTouchSlop && xDiff > yDiff) {
				mIsBeingDragged = true;
				mLastMotionX = x;
			}
			break;

		}
		return mIsBeingDragged;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			mLastMotionY = y;
			if (getScrollX() == -getMenuViewWidth()
					&& mLastMotionX < getMenuViewWidth()) {
				return false;
			}

			if (getScrollX() == getDetailViewWidth()
					&& mLastMotionX > getMenuViewWidth()) {
				return false;
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsBeingDragged) {
				enableChildrenCache();
				final float deltaX = mLastMotionX - x;
				mLastMotionX = x;
				float oldScrollX = getScrollX();
				float scrollX = oldScrollX + deltaX;

				if (deltaX < 0 && oldScrollX < 0) { // left view
					final float leftBound = 0;
					final float rightBound = -getMenuViewWidth();
					if (scrollX > leftBound) {
						scrollX = leftBound;
					} else if (scrollX < rightBound) {
						scrollX = rightBound;
					}
					// mDetailView.setVisibility(View.INVISIBLE);
					// mMenuView.setVisibility(View.VISIBLE);
				} else if (deltaX > 0 && oldScrollX > 0) { // right view
					final float rightBound = getDetailViewWidth();
					final float leftBound = 0;
					if (scrollX < leftBound) {
						scrollX = leftBound;
					} else if (scrollX > rightBound) {
						scrollX = rightBound;
					}
					// mDetailView.setVisibility(View.VISIBLE);
					// mMenuView.setVisibility(View.INVISIBLE);
				}

				scrollTo((int) scrollX, getScrollY());

			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (mIsBeingDragged) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000);
				int velocityX = (int) velocityTracker.getXVelocity();
				velocityX = 0;
				Log.e("ad", "velocityX == " + velocityX);
				int oldScrollX = getScrollX();
				int dx = 0;
				if (oldScrollX < 0) {
					if (oldScrollX < -getMenuViewWidth() / 2
							|| velocityX > SNAP_VELOCITY) {
						dx = -getMenuViewWidth() - oldScrollX;
					} else if (oldScrollX >= -getMenuViewWidth() / 2
							|| velocityX < -SNAP_VELOCITY) {
						dx = -oldScrollX;
					}
				} else {
					if (oldScrollX > getDetailViewWidth() / 2
							|| velocityX < -SNAP_VELOCITY) {
						dx = getDetailViewWidth() - oldScrollX;
					} else if (oldScrollX <= getDetailViewWidth() / 2
							|| velocityX > SNAP_VELOCITY) {
						dx = -oldScrollX;
					}
				}

				smoothScrollTo(dx);
				clearChildrenCache();

			}

			break;

		}
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}

		return true;
	}

	private int getMenuViewWidth() {
		if (mMenuView == null) {
			return 0;
		}
		return mMenuView.getWidth();
	}

	private int getDetailViewWidth() {
		if (mDetailView == null) {
			return 0;
		}
		return mDetailView.getWidth();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	public View getDetailView() {
		return mDetailView;
	}

	public void setDetailView(View mDetailView) {
		this.mDetailView = mDetailView;
	}

	public View getMenuView() {
		return mMenuView;
	}

	public void setMenuView(View mMenuView) {
		this.mMenuView = mMenuView;
	}

	// void toggle() {
	// int menuWidth = mMenuView.getWidth();
	// int oldScrollX = getScrollX();
	// if (oldScrollX == 0) {
	// smoothScrollTo(-menuWidth);
	// } else if (oldScrollX == -menuWidth) {
	// smoothScrollTo(menuWidth);
	// }
	// }

	/**
	 * �������Ĺر�����ʾ
	 */
	public void showLeftView() {
		int menuWidth = mMenuView.getWidth();
		int oldScrollX = getScrollX();
		if (oldScrollX == 0) {
			smoothScrollTo(-menuWidth);
		} else if (oldScrollX == -menuWidth) {
			smoothScrollTo(menuWidth);
		}
	}

	/**
	 * �Ҳ�����Ĺر�����ʾ
	 */
	public void showRightView() {
		int menuWidth = mDetailView.getWidth();
		int oldScrollX = getScrollX();
		if (oldScrollX == 0) {
			smoothScrollTo(menuWidth);
		} else if (oldScrollX == menuWidth) {
			smoothScrollTo(-menuWidth);
		}
	}

	void smoothScrollTo(int dx) {
		int duration = 500;
		int oldScrollX = getScrollX();
		mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(),
				duration);
		invalidate();
	}

	void enableChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(true);
		}
	}

	void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}

}

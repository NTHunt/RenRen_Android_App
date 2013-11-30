package net.minirenren.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class SlidingMenu extends RelativeLayout {

	private SlidingView mSlidingView;
	private View mMenuView;
	private View mDetailView;

	public SlidingMenu(Context context) {
		super(context);
	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void addViews(View left, View center, View right) {
		setLeftView(left);
		setRightView(right);
		setCenterView(center);
	}

	/**
	 * 添加左侧边栏的view
	 * 
	 * @param view
	 */
	public void setLeftView(View view) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		behindParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);// 在父控件的左边
		addView(view, behindParams);
		mMenuView = view;
	}

	/**
	 * 添加右侧边栏的view
	 * 
	 * @param view
	 */
	public void setRightView(View view) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		behindParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);// 在父控件的右边
		addView(view, behindParams);
		mDetailView = view;
	}

	/**
	 * 添加中间内容的view
	 * 
	 * @param view
	 */
	public void setCenterView(View view) {
		LayoutParams aboveParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mSlidingView = new SlidingView(getContext());
		mSlidingView.setView(view);
		addView(mSlidingView, aboveParams);
		mSlidingView.setMenuView(mMenuView);
		mSlidingView.setDetailView(mDetailView);
		mSlidingView.invalidate();
	}

	public void showLeftView() {
		mSlidingView.showLeftView();
	}

	public void showRightView() {
		mSlidingView.showRightView();
	}
}

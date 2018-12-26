package com.speex.studyview.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * 支持上下反弹效果的ScrollView
 *
 * @author zhy
 */
public class BounceScrollView extends ScrollView {
    private String TAG = this.getClass().getSimpleName();

    private boolean isCalled;

    private Callback mCallback;

    /**
     * 包含的View
     */
    private View mView;
    /**
     * 存储正常时的位置
     */
    private Rect mRect = new Rect();

    /**
     * y坐标
     */
    private int y;

    private boolean isFirst = true;

    public BounceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /***
     * 根据 XML 生成视图工作完成.该函数在生成视图的最后调用，在所有子视图添加完之后. 即使子类覆盖了 onFinishInflate
     * 方法，也应该调用父类的方法，使该方法得以执行.
     */
    @Override
    protected void onFinishInflate() {
        Log.i(TAG, "onFinishInflate 子视图添加完成了");
        if (getChildCount() > 0)
            mView = getChildAt(0);
        Log.i(TAG, "onFinishInflate 子控件的高度: " + mView.getMeasuredHeight() + " ,屏幕高度: " + getHeight());
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "onSizeChanged 子控件的高度: " + mView.getMeasuredHeight() + " ,屏幕高度: " + getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mView != null) {
            commonOnTouch(ev);
        }
        return super.onTouchEvent(ev);
    }

    private void commonOnTouch(MotionEvent ev) {
        int action = ev.getAction();
        int cy = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            /**
             * 跟随手指移动
             */
            case MotionEvent.ACTION_MOVE:
                int dy = cy - y;//移动的差值
                Log.i(TAG, "当前的cy: " + cy + " ,上次的y: " + y + " ,dy: " + dy);
                if (isFirst) {
                    dy = 0;
                    isFirst = false;
                }
                y = cy;//上一次移动位置

                if (isNeedMove()) {
                    if (mRect.isEmpty()) {
                        /**
                         * 记录移动前的位置
                         */
                        mRect.set(mView.getLeft(), mView.getTop(),
                                mView.getRight(), mView.getBottom());
                    }

                    mView.layout(mView.getLeft(), mView.getTop() + 2 * dy / 3,
                            mView.getRight(), mView.getBottom() + 2 * dy / 3);

                    if (shouldCallBack(dy)) {
                        if (mCallback != null) {
                            if (!isCalled) {
                                isCalled = true;
                                resetPosition();
                                mCallback.callback();
                            }
                        }
                    }
                }

                break;
            /**
             * 反弹回去
             */
            case MotionEvent.ACTION_UP:
                if (!mRect.isEmpty()) {
                    resetPosition();
                }
                break;

        }
    }

    /**
     * 当从上往下，移动距离达到一半时，回调接口
     *
     * @return
     */
    private boolean shouldCallBack(int dy) {
        Log.i(TAG, "shouldCallBack: mView.getTop() = " + mView.getTop() + " ,getHeight()/2 = " + getHeight() / 2);
        if (dy > 0 && mView.getTop() > getHeight() / 2)
            return true;
        return false;
    }

    private void resetPosition() {
        Animation animation = new TranslateAnimation(0, 0, mView.getTop(),
                mRect.top);
        animation.setDuration(200);
        animation.setFillAfter(true);
        mView.startAnimation(animation);
        mView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
        mRect.setEmpty();
        isFirst = true;
        isCalled = false;
    }

    /***
     * 是否需要移动布局 inner.getMeasuredHeight():获取的是控件的总高度
     *
     * getHeight()：获取的是屏幕的高度
     *
     * @return
     */
    public boolean isNeedMove() {
        int offset = mView.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        Log.i(TAG, "isNeedMove 子控件的高度: " + mView.getMeasuredHeight() + " ,屏幕高度: " + getHeight() + ",offset: " + offset + ",scrollY: " + scrollY);
        // 0是顶部，后面那个是底部
        if (scrollY == 0 || scrollY == offset) {
            return true;
        }
        return false;
    }

    public void setCallBack(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void callback();
    }

}

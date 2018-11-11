package com.speex.studyview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.speex.studyview.R;

public class QQListView extends ListView {

    private static final String TAG = "QQlistView";

    // private static final int VELOCITY_SANP = 200;
    // private VelocityTracker mVelocityTracker;
    /**
     * 用户滑动的最小距离
     */
    private int touchSlop;

    /**
     * 是否响应滑动
     */
    private boolean isSliding;

    /**
     * 手指按下时的x坐标
     */
    private int xDown;
    /**
     * 手指按下时的y坐标
     */
    private int yDown;
    /**
     * 手指移动时的x坐标
     */
    private int xMove;
    /**
     * 手指移动时的y坐标
     */
    private int yMove;

    private LayoutInflater mInflater;

    private PopupWindow mPopupWindow;
    private int mPopupWindowHeight;
    private int mPopupWindowWidth;

    private Button mDelBtn;
    /**
     * 为删除按钮提供一个回调接口
     */
    private DelButtonClickListener mListener;

    /**
     * 当前手指触摸的View
     */
    private View mCurrentView;

    /**
     * 当前手指触摸的位置
     */
    private int mCurrentViewPos;

    /**
     * 必要的一些初始化
     *
     * @param context
     * @param attrs
     */
    public QQListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = LayoutInflater.from(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        View view = mInflater.inflate(R.layout.delete_btn, null);
        mDelBtn = (Button) view.findViewById(R.id.id_item_btn);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        /**
         * 先调用下measure,否则拿不到宽和高
         */
        mPopupWindow.getContentView().measure(0, 0);
        mPopupWindowHeight = mPopupWindow.getContentView().getMeasuredHeight();
        mPopupWindowWidth = mPopupWindow.getContentView().getMeasuredWidth();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        Log.i(TAG, "dispatchTouchEvent: " + action);
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (action) {

            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "dispatchTouchEvent: ACTION_DOWN");
                xDown = x;
                yDown = y;
                /**
                 * 如果当前popupWindow显示，则直接隐藏，然后屏蔽ListView的touch事件的下传
                 */
                if (mPopupWindow.isShowing()) {
                    dismissPopWindow();
                    Log.d(TAG, "dispatchTouchEvent: ListView不消费按下事件");
                    return false;//拦截事件，停止事件向下传递。将事件返回父控件的onTouchEvent进行处理
                }
                // 获得当前手指按下时的item的位置
                mCurrentViewPos = pointToPosition(xDown, yDown);
                // 获得当前手指按下时的item
                int firstVisiblePosition = getFirstVisiblePosition();
                Log.d(TAG, "dispatchTouchEvent: 当前手指按下时的item的位置 " + mCurrentViewPos + " ,获得当前手指按下时的item:" + firstVisiblePosition);
                View view = getChildAt(mCurrentViewPos - firstVisiblePosition);
                mCurrentView = view;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "dispatchTouchEvent: ACTION_MOVE");
                xMove = x;
                yMove = y;
                int dx = xMove - xDown;
                int dy = yMove - yDown;
                /**
                 * 判断是否是从右到左的滑动
                 */
                if (xMove < xDown && Math.abs(dx) > touchSlop && Math.abs(dy) < touchSlop) {
                    Log.e(TAG, "touchslop = " + touchSlop + " , dx = " + dx + " , dy = " + dy);
                    isSliding = true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        Log.i(TAG, "onTouchEvent: " + action);

        /**
         * 如果是从右到左的滑动才相应
         */
        if (isSliding) {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    Log.i(TAG, "onTouchEvent: ACTION_MOVE");
                    int[] location = new int[2];
                    // 获得当前item的位置x与y
                    mCurrentView.getLocationOnScreen(location);
                    Log.e(TAG, "location: (" + location[0] + " ," + location[1] + ")" + " ,mCurrentView.getWidth() = " + mCurrentView.getWidth()
                            + " ,mCurrentView.getHeight() = " + mCurrentView.getHeight() + " ,mPopupWindowHeight = " + mPopupWindowHeight);
                    // 设置popupWindow的动画
                    mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
                    mPopupWindow.update();
                    mPopupWindow.showAtLocation(mCurrentView, Gravity.LEFT | Gravity.TOP,
                            location[0] + mCurrentView.getWidth(), location[1] + mCurrentView.getHeight() / 2 - mPopupWindowHeight / 2);
                    // 设置删除按钮的回调
                    mDelBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                mListener.clickHappend(mCurrentViewPos);
                                mPopupWindow.dismiss();
                            }
                        }
                    });
                    // Log.e(TAG, "mPopupWindow.getHeight()=" + mPopupWindowHeight);

                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(TAG, "onTouchEvent: ACTION_UP");
                    isSliding = false;

            }
            // 相应滑动期间屏幕itemClick事件，避免发生冲突
            return true;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 隐藏popupWindow
     */
    private void dismissPopWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public void setDelButtonClickListener(DelButtonClickListener listener) {
        mListener = listener;
    }

    public interface DelButtonClickListener {
        public void clickHappend(int position);
    }

}
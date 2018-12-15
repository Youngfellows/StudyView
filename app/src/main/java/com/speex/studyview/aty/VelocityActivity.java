package com.speex.studyview.aty;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VelocityActivity extends AppCompatActivity {

    private TextView mInfo;

    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity;

    private int mPointerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_velocity);
        mInfo = new TextView(this);
        mInfo.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        mInfo.setGravity(Gravity.CENTER);
        mInfo.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

        setContentView(mInfo);

        //Maximum velocity to initiate a fling, as measured in pixels per second
        mMaxVelocity = ViewConfiguration.get(this).getScaledMaximumFlingVelocity();  //注意这里返回的都是每秒的像素单位

        ViewConfiguration.get(this).getScaledTouchSlop();//获得能够视为是手势滑动的最短距离
        ViewConfiguration.get(this).getScaledMinimumFlingVelocity();//获得允许执行一个fling手势动作的最小速度值
        ViewConfiguration.get(this).getScaledMaximumFlingVelocity();//获得允许执行一个fling手势动作的最大速度值
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        //当你需要跟踪触摸屏事件的速度的时候,使用obtain()方法来获得VelocityTracker类的一个实例对象
        //在onTouchEvent回调函数中，使用addMovement(MotionEvent)函数将当前的移动事件传递给VelocityTracker对象
        acquireVelocityTracker(event);
        //final VelocityTracker verTracker = mVelocityTracker;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //求第一个触点的id， 此时可能有多个触点，但至少一个
                mPointerId = event.getPointerId(0);
                break;

            case MotionEvent.ACTION_MOVE:
                //求伪瞬时速度  如果速度小于mMaxVelocity，正常显示。如果大于mMaxVelocity，则显示mMaxVelocity
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                //1000表示像素/秒，1表示像素/毫秒。因为mMaxVelocity是用像素/秒做单位，所以此时用1000

                final float velocityX = mVelocityTracker.getXVelocity(mPointerId);
                final float velocityY = mVelocityTracker.getYVelocity(mPointerId);
                //getXVelocity getYVelocity之前必须先调用computeCurrentVelocity
                recodeInfo(velocityX, velocityY);
                break;

            case MotionEvent.ACTION_UP:
                releaseVelocityTracker();
                break;

            case MotionEvent.ACTION_CANCEL:
                releaseVelocityTracker();
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    //获取VelocityTracker同时把event增加进去
    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    //捕获到UP事件的时候，记得release
    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private static final String sFormatStr = "velocityX=%f\nvelocityY=%f";

    /**
     * 记录当前速度
     *
     * @param velocityX x轴速度
     * @param velocityY y轴速度
     */
    private void recodeInfo(final float velocityX, final float velocityY) {
        final String info = String.format(sFormatStr, velocityX, velocityY);
        Log.d("LiaBin", "info:" + info);
        mInfo.setText(info);
    }
}

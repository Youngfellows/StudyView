package com.speex.studyview.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.speex.studyview.R;

public class PullRefreshListView extends FrameLayout implements OnTouchListener {
    private View view_head;
    private ImageView head_img;
    private TextView head_text;

    //刷新的头部高度
    private int headView_height = -1;
    //刷新的头部当前拉伸高度
    private int now_trans_height;

    //主体listview
    private ListView list;
    private BaseAdapter adapter;

    //保存当前的y坐标
    private float y;
    //保存按下时的y坐标
    private float down_y;
    //是否可以刷新
    private boolean canRefresh = false;
    //是否正在刷新
    private boolean isRefresh = false;
    //是否正在滑动刷新栏
    private boolean isSlide = false;

    private final int MODE_DISS = 0;
    private final int MODE_REFRESH = 1;

    private final String noRefreshText = "   下拉刷新...";
    private final String canRefreshText = "   释放可刷新...";
    private final String nowRefreshText = "   正在刷新...";
    //刷新时的回调事件
    private OnRefreshing refreshCallback;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            float du = (float) msg.obj;
            ViewHelper.setRotation(head_img, du);
        }

        ;
    };

    public PullRefreshListView(Context context) {
        super(context);
        init(context);
        // TODO Auto-generated constructor stub
    }

    public PullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        // TODO Auto-generated constructor stub
    }

    public PullRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        // TODO Auto-generated constructor stub
    }

    //初始化
    private void init(Context context) {
        view_head = inflate(context, R.layout.activity_refresh_head, null);
        head_img = (ImageView) view_head.findViewById(R.id.refresh_head_img);
        head_text = (TextView) view_head.findViewById(R.id.refresh_head_text);
        head_img.setImageResource(R.mipmap.arrow);
        head_text.setText(noRefreshText);

        list = new ListView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        list.setLayoutParams(params);
        list.setOnTouchListener(this);

        this.addView(view_head, 0);
        this.addView(list, 1);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置头部高度，只需要设置一次就可以
        if (headView_height == -1) {
            view_head.measure(widthMeasureSpec, 0);
            headView_height = view_head.getMeasuredHeight();
            now_trans_height = -headView_height;
            ViewHelper.setTranslationY(view_head, now_trans_height);
        }
    }


    //listView 设置OnTouch
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                y = event.getRawY();
                down_y = event.getRawY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //如果是在滑动或者list中的第一项是第一个item,就表示可以滑动
                if (isSlide || isListFrist()) {
                    return move(event);
                } else {
                    y = event.getRawY();
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                up();
                //只有touch的起点在落点附近正负10像素的位置内时，才有返回OnItemClick事件
                float uy = event.getRawY();
                if (uy > down_y + 10 || uy < down_y - 10) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private boolean move(MotionEvent event) {
        float now_y = event.getRawY();
        float cha = now_y - y;
        //如果已经不在滑动且 手势为向上，则将此次事件返回给list
        if (!isSlide && cha < 0) {
            return false;
        }
        now_trans_height += cha;
        //头部已经滑到最顶部，此时被隐藏，不可滑动
        if (now_trans_height < -headView_height) {
            now_trans_height = -headView_height;
            isSlide = false;
        } else {
            isSlide = true;
        }
        this.y = now_y;
        //滑动
        ViewHelper.setTranslationY(view_head, now_trans_height);
        ViewHelper.setTranslationY(list, now_trans_height + headView_height);
        //头部是否已经滑动到可以刷新的阈值
        if (now_trans_height >= 0 && !canRefresh && !isRefresh) {
            canRefresh = true;
            ViewHelper.setRotation(head_img, 180f);
            head_text.setText(canRefreshText);
        } else if (now_trans_height < 0 && canRefresh && !isRefresh) {
            canRefresh = false;
            ViewHelper.setRotation(head_img, 0f);
            head_text.setText(noRefreshText);
        }
        return true;
    }

    private void up() {
        if (canRefresh) {
            //刷新
            new MyTask().execute(MODE_REFRESH);
        } else {
            //隐藏
            new MyTask().execute(MODE_DISS);
        }
        canRefresh = false;
    }

    private boolean isListFrist() {
        int i = list.getFirstVisiblePosition();
        if (i == 0) {
            View v = list.getChildAt(0);
            if (v.getTop() == 0) {
                return true;
            }
        }
        return false;
    }

    //隐藏
    private void diss() {
        head_img.setImageResource(R.mipmap.arrow);
        head_text.setText(nowRefreshText);
        now_trans_height = -headView_height;
        ViewHelper.setRotation(head_img, 0);
        ViewHelper.setTranslationY(view_head, now_trans_height);
        ViewHelper.setTranslationY(list, 0);
    }

    //展示可刷新
    private void show_refresh() {
        isRefresh = true;
        head_text.setText(nowRefreshText);
        now_trans_height = 0;
        ViewHelper.setTranslationY(view_head, now_trans_height);
        ViewHelper.setTranslationY(list, headView_height);
        refreshCallback.callback();
        new Thread(refreshing).start();
    }

    //刷新时的回调事件
    public void setOnRefreshingCallback(OnRefreshing callback) {
        this.refreshCallback = callback;
    }

    //刷新成功时调用此方法
    public void refreshOK() {
        isRefresh = false;
        new MyTask().execute(MODE_DISS);
    }

    //刷新时 调用此runnable来旋转图标
    Runnable refreshing = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            float f = 180;
            while (isRefresh) {
                Message message = new Message();
                message.obj = f;
                message.what = 0;
                handler.sendMessage(message);
                f += 9;
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private class MyTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            // TODO Auto-generated method stub
            int mode = params[0];
            int cha = 0;
            if (mode == MODE_DISS) {
                cha = (-headView_height - now_trans_height) / 50;
            } else {
                cha = (0 - now_trans_height) / 50;
            }
            int i = 0;
            while (i < 50) {
                publishProgress(cha);
                try {
                    Thread.sleep(2);
                } catch (Exception e) {

                }
                i++;
            }
            return mode;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            now_trans_height += values[0];
            ViewHelper.setTranslationY(view_head, now_trans_height);
            ViewHelper.setTranslationY(list, now_trans_height + headView_height);
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            int mode = result;
            if (mode == MODE_DISS) {
                diss();
            } else {
                show_refresh();
            }
        }
    }

    //
    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        list.setAdapter(adapter);

    }

    public void setOnItemClickLintener(OnItemClickListener listener) {
        list.setOnItemClickListener(listener);
    }

    //定义刷新时调用的接口
    public interface OnRefreshing {
        public void callback();
    }
}

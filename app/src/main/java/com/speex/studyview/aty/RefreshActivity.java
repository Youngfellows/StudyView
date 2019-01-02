package com.speex.studyview.aty;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.speex.studyview.R;
import com.speex.studyview.view.PullRefreshListView;

public class RefreshActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private PullRefreshListView refresh;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            refresh.refreshOK();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        refresh = (PullRefreshListView) findViewById(R.id.refreshview);
        refresh.setOnItemClickLintener(this);
        refresh.setAdapter(new MyAdapter(this));
        refresh.setOnRefreshingCallback(new PullRefreshListView.OnRefreshing() {

            @Override
            public void callback() {
                // TODO Auto-generated method stub
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {

                        }
                        handler.sendEmptyMessage(0);
                    }
                }.start();
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 20;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            TextView t;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_list_item, null);
                t = (TextView) convertView.findViewById(R.id.textView1);
                convertView.setTag(t);
            } else {
                t = (TextView) convertView.getTag();
            }
            t.setText("this is " + position);
            return convertView;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        Log.w("family_log", "in onitemclicklistener position  = " + position);
    }
}

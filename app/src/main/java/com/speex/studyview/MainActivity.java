package com.speex.studyview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.speex.studyview.aty.AttrbuteActivity;
import com.speex.studyview.aty.ImageViewActivity;
import com.speex.studyview.aty.ProgressActivity;
import com.speex.studyview.aty.QQListViewActivity;
import com.speex.studyview.aty.VerticalSplashActivity;
import com.speex.studyview.aty.VolumControlActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * QQ滑动删除
     *
     * @param view
     */
    public void qqDel(View view) {
        Intent intent = new Intent(this, QQListViewActivity.class);
        startActivity(intent);
    }

    /**
     * 自定义ViewGroup实现竖向引导界面
     *
     * @param view
     */
    public void verticalSplash(View view) {
        Intent intent = new Intent(this, VerticalSplashActivity.class);
        startActivity(intent);
    }

    /**
     * 自定义属性
     *
     * @param view
     */
    public void attrbute(View view) {
        Intent intent = new Intent(this, AttrbuteActivity.class);
        startActivity(intent);
    }

    /**
     * 自定义属性
     *
     * @param view
     */
    public void attrbute2(View view) {
        Intent intent = new Intent(this, ImageViewActivity.class);
        startActivity(intent);
    }

    public void attrbute3(View view) {
        Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);
    }

    public void volumeControl(View view) {
        Intent intent = new Intent(this, VolumControlActivity.class);
        startActivity(intent);
    }
}

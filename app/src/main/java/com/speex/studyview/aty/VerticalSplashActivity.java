package com.speex.studyview.aty;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.speex.studyview.R;
import com.speex.studyview.view.VerticalLinearLayout;

public class VerticalSplashActivity extends AppCompatActivity {
    private VerticalLinearLayout mMianLayout;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_splash);

        mMianLayout = (VerticalLinearLayout) findViewById(R.id.id_main_ly);
        mMianLayout.setOnPageChangeListener(new VerticalLinearLayout.OnPageChangeListener() {
            @Override
            public void onPageChange(int currentPage) {
				mMianLayout.getChildAt(currentPage);
                Toast.makeText(VerticalSplashActivity.this, "第" + (currentPage + 1) + "页", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "第" + (currentPage + 1) + "页");
            }
        });

    }
}

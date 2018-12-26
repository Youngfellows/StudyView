package com.speex.studyview.aty;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.speex.studyview.R;
import com.speex.studyview.view.BounceScrollView;

import java.util.ArrayList;
import java.util.Arrays;

public class BounceScrollActivity extends AppCompatActivity {
    private ListView mListView;
    private BounceScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bounce_scroll);

        mScrollView = (BounceScrollView) findViewById(R.id.bsv);
        mScrollView.setCallBack(new BounceScrollView.Callback() {
            @Override
            public void callback() {
                Toast.makeText(BounceScrollActivity.this, "you can do something!", Toast.LENGTH_SHORT).show();
            }
        });
        mListView = (ListView) findViewById(R.id.mlv);
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, new ArrayList<String>(
                Arrays.asList("Hello", "World", "Welcome", "Java",
                        "Android", "Lucene", "C++", "C#", "HTML",
                        "Welcome", "Java", "Android", "Lucene", "C++",
                        "C#", "HTML"))));
    }
}

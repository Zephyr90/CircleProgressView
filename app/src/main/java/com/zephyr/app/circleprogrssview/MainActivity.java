package com.zephyr.app.circleprogrssview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.zephyr.app.library.SkipView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.skip_view)
    SkipView mSkipView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSkipView.setOnProgressListener(new SkipView.OnProgressListener() {
            @Override
            public void onCompleted() {
                Toast.makeText(MainActivity.this, "加载完成了...", Toast.LENGTH_LONG).show();
            }
        });
    }
}

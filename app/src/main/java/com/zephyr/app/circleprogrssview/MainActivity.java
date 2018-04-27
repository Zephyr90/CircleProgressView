package com.zephyr.app.circleprogrssview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.zephyr.app.library.SkipView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.skip_view)
    SkipView mSkipView;
    @BindView(R.id.btn_start)
    Button mBtnStart;

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

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSkipView.startAnimation();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int[] outLocation = new int[2];
        mSkipView.getLocationInWindow(outLocation);
        Log.d(TAG, "onCreate: " + Arrays.toString(outLocation));
    }
}

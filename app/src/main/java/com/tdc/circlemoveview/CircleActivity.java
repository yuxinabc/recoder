package com.tdc.circlemoveview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CircleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //目的就是启动Service来打开悬浮窗体
                Intent intent = new Intent(CircleActivity.this,FloatService.class);
                intent.putExtra(FloatService.LAYOUT_RES_ID,R.layout.custom_layout);
                intent.putExtra(FloatService.ROOT_LAYOUT_ID,R.id.float_window_circle);
                startService(intent);
                finish();
            }
        });
    }
}

package com.tdc.circlemoveview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/6/4.
 */
public class FinishActivity extends Activity {

    TextView stop_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_layout);
        stop_tv = (TextView) findViewById(R.id.stop_time_tv);

    }
}

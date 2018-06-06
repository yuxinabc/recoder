package com.tdc.circlemoveview.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tdc.circlemoveview.R;
import com.tdc.circlemoveview.suspend.StopService;

public class RecorderActivity extends AppCompatActivity {

    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int AUDIO_REQUEST_CODE   = 103;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private RecordService recordService;
    private Button startBtn;

    private Button stopBtn;
    private Button settingsBtn;
    private static final int START_COUNTING = 1;
    private static final int COUNT_NUMBER = 4;
    private TextView mStartCount;
    private MyHandler mHandler = new MyHandler();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        setContentView(R.layout.activity_recorder);

        mStartCount = (TextView) findViewById(R.id.count_text);
        stopBtn = (Button) findViewById(R.id.stop_record);
        settingsBtn = (Button) findViewById(R.id.setting);
        mStartCount.setVisibility(View.VISIBLE);

        stopBtn.setEnabled(true);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                /*if (recordService.isRunning()) {
                    recordService.stopRecord();
                    //startBtn.setText(R.string.start_record);
                } else {
                    Intent captureIntent = projectionManager.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
                }*/
                finish();
            }
        });

        Message msg = mHandler.obtainMessage();
        msg.what = START_COUNTING;
        msg.obj = COUNT_NUMBER;
        mHandler.sendMessageDelayed(msg, 4);


       /* settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecorderActivity.this, RecorderSettingsActivity.class);
                startActivity(intent);
            }
        });*/

        if (ContextCompat.checkSelfPermission(RecorderActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(RecorderActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }

        Intent intent = new Intent(this, RecordService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        startService(intent);
        //finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            /*Intent intent = new Intent(this, RecordService.class);
            startService(intent);*/
            //目的就是启动Service来打开悬浮窗体
            Intent intent1 = new Intent(RecorderActivity.this,StopService.class);
            intent1.putExtra(StopService.STOP_LAYOUT_RES_ID,R.layout.stop_layout);
            intent1.putExtra(StopService.STOP_ROOT_LAYOUT_ID,R.id.stop_window_detail);
            //intent.putExtra(StopService.STOP_ROOT_LAYOUT_ID,R)
            startService(intent1);
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            recordService.setMediaProject(mediaProjection);
            recordService.startRecord();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            //startBtn.setText(R.string.stop_record);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE || requestCode == AUDIO_REQUEST_CODE) {

      if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        finish();
      }
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            //stopBtn.setEnabled(true);
            //startBtn.setText(recordService.isRunning() ? R.string.stop_record : R.string.start_record);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    private class MyHandler extends Handler {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case START_COUNTING:
                    int count = (int) msg.obj;
                    mStartCount.setText(count + "");
                    if (count > 0) {
                        Message msg1 = obtainMessage();
                        msg1.what = START_COUNTING;
                        msg1.obj = count - 1;
                        sendMessageDelayed(msg1, 1000);

                    }else {
                        Intent captureIntent = projectionManager.createScreenCaptureIntent();
                        startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
                        //finish();
                        //目的就是启动Service来打开悬浮窗体
                        /*Intent intent = new Intent(RecorderActivity.this,StopService.class);
                        intent.putExtra(StopService.STOP_LAYOUT_RES_ID,R.layout.stop_layout);
                        intent.putExtra(StopService.STOP_ROOT_LAYOUT_ID,R.id.stop_window_detail);
                        //intent.putExtra(StopService.STOP_ROOT_LAYOUT_ID,R)
                        startService(intent);*/
                        //finish();
                    }

                    break;

                default:
                    break;
            }
        }
    };
}

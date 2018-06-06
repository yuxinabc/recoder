package com.tdc.circlemoveview.timer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 描述：计时工具类
 * 默认60s自动停止
 * 作者：gong.xl
 * 创建日期：2018/5/23 下午3:12
 * 修改日期: 2018/5/23
 * 修改备注：
 * 邮箱：gong.xl@belle.com.cn
 */

public class BLPTimeUtil {

    private Timer mTimer;
    private TimerTask mTimerTask;
    private int count;
    private Date now;

    //是否自动停止计时 默认true
    private boolean isAuto = true;
    //是否已经开始计时
    private boolean isStart;
    //计时过程监听事件
    private OnTimeListener mTimeListener;


    //    private TimeHandler mHandler;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mTimeListener.timeStop();
                    break;
                case 1:
                    mTimeListener.timeProceed((String) msg.obj);
                    break;
                case 2:
                    mTimeListener.timeError();
                    break;
            }
        }
    };

    /**
     * @param timeListener 监听事件
     */
    public BLPTimeUtil(OnTimeListener timeListener) {
        mTimeListener = timeListener;
        now = new Date();
        now.setHours(0);
        now.setMinutes(0);
        now.setSeconds(0);
    }

    /**
     * @param isAutoStop   是否需要自动停止
     * @param timeListener 监听事件
     */
    public BLPTimeUtil(boolean isAutoStop, OnTimeListener timeListener) {
        isAuto = isAutoStop;
        Log.w("time", "TimeUtil构造方法 ThreadName = " + Thread.currentThread().getName());
        mTimeListener = timeListener;
        now = new Date();
        now.setHours(0);
        now.setMinutes(0);
        now.setSeconds(0);
    }

    public void start() {
        startTime();
    }

    public void stop() {
        stopTime();
    }

    /**
     * 开始计时任务
     */
    private void startTime() {
        if (!isStart) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {

                    if (mTimeListener != null) {
                        //格式化录音时间
                        Date now2 = new Date(now.getTime() + 1000);
                        now = now2;
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

                        //定时任务处于异步线程，此处使用handler进行与UI线程通信
                        Message message = Message.obtain();
                        message.what = 1;
                        message.obj = formatter.format(now);
                        mHandler.sendMessage(message);
                    }

                    count++;
                    //判断计时时长，并且设置自动停止，当计时满1分钟时自动停止
                    if (count >= 59 && isAuto) {
                        stopTime();
                    }
                }
            };
            mTimer.schedule(mTimerTask, 1000, 1000);
            isStart = true;
        }
    }


    /**
     * 停止计时任务
     */
    private void stopTime() {
        if (isStart) {
            if (mTimer != null && mTimerTask != null) {

                Log.w("time", "BLPTimeUtil stopTime ThreadName = " + Thread.currentThread().getName());
                //取消计时并销毁资源
                mTimer.cancel();
                mTimerTask.cancel();
                mTimer = null;
                mTimerTask = null;

                //判断计时时长
                if (count < 2 && isAuto) {
                    //定时任务处于异步线程，此处使用handler进行与UI线程通信
                    Message message = Message.obtain();
                    message.what = 2;
                    message.obj = "录音时间不能小于2s";
                    mHandler.sendMessage(message);


                } else {
                    Message message = Message.obtain();
                    message.what = 0;
                    mHandler.sendMessage(message);
                }

                isStart = false;

                //重置计数
                count = 0;
                now.setHours(0);
                now.setMinutes(0);
                now.setSeconds(0);
            }
        }
    }
}

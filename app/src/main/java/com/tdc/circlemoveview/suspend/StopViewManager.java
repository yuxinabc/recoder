package com.tdc.circlemoveview.suspend;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tdc.circlemoveview.FloatService;
import com.tdc.circlemoveview.R;
import com.tdc.circlemoveview.service.RecordService;
import com.tdc.circlemoveview.timer.BLPTimeUtil;
import com.tdc.circlemoveview.timer.OnTimeListener;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/6/4.
 */
public class StopViewManager {

    private Context context;
    private StopView mStopView;
    private static StopViewManager manager;
    private WindowManager mWindowManager;
    private BLPTimeUtil blpTimeUtil;

    private StopViewManager(Context  context){
        this.context = context;
        mWindowManager = getWindowManager();
    }



    //通过单例模式获取实例对象
    public static StopViewManager getInstance(Context context) {
        if (null == manager) {
            synchronized (StopViewManager.class) {
                if (null == manager) {
                    manager = new StopViewManager(context);
                }
            }
        }
        return manager;
    }

    /**
     * 如果WindowManager还未创建，则创建新的WindowManager返回。否则返回当前已创建的WindowManager
     *
     * @param
     * @return
     */
    private WindowManager getWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    //在手机屏幕上显示自定义的FloatView
    public void showFloatViewOnWindow(final Context context, int stopLayoutResId, int stopRootLayoutId) {
        if (mStopView == null) {
            mStopView = new StopView(context, stopLayoutResId, stopRootLayoutId);
            mWindowManager.addView(mStopView, mStopView.params);
        }
        final TextView stop_time_tv = (TextView) mStopView.findViewById(R.id.stop_time_tv);
        if(blpTimeUtil==null){
            blpTimeUtil=new BLPTimeUtil(false, new OnTimeListener() {
                @Override
                public void timeProceed(String time) {
                    stop_time_tv.setText(time);
                }

                @Override
                public void timeError() {

                }

                @Override
                public void timeStop() {

                }
            });
            blpTimeUtil.start();
        }

        mStopView.setOnStopListener(new OnStopListener() {
            @Override
            public void onStopClick() {
                remove();
                Toast.makeText(context, "进入onStopClick完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //是否有悬浮窗,有悬浮窗显示在桌面上返回true，没有的话返回false
    public boolean isWindowShowing(){
        return mStopView != null;
    }

    //将悬浮窗移除
    public void removeWindow(){
        if(mStopView != null){
            mWindowManager.removeView(mStopView);
            mStopView = null;
        }
    }

    public void remove(){
        if(blpTimeUtil!=null){
            blpTimeUtil=null;
        }
        context.stopService(new Intent(context, FloatService.class));
        context.stopService(new Intent(context,RecordService.class));
        context.stopService(new Intent(context,StopService.class));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeWindow();
            }
        }, 550);



    }

}

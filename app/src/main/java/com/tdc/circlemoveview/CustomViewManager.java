package com.tdc.circlemoveview;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.tdc.circlemoveview.service.RecorderActivity;

/**
 * description：
 * 作者：chenyao
 * 时间：2018/5/27 11:57
 * 邮箱：
 */
public class CustomViewManager {

    //上下文
    private Context mContext;
    //CustomViewManager单列
    private static CustomViewManager instance;
    //自定义的mFloatView对象
    private CustomView mFloatView;
    //窗口管理类,用于控制在屏幕上添加或移除悬浮窗
    private WindowManager mWindowManager;

    private CustomViewManager(Context context) {
        this.mContext = context;
        mWindowManager = getWindowManager();
    }



    //通过单例模式获取实例对象
    public static CustomViewManager getInstance(Context context) {
        if (null == instance) {
            synchronized (CustomViewManager.class) {
                if (null == instance) {
                    instance = new CustomViewManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 如果WindowManager还未创建，则创建新的WindowManager返回。否则返回当前已创建的WindowManager
     *
     * @param
     * @return
     */
    private WindowManager getWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) mContext
                    .getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    //在手机屏幕上显示自定义的FloatView
    public void showFloatViewOnWindow(Context context, int layoutResId, int rootLayoutId){
        if (mFloatView == null) {
            mFloatView = new CustomView(context, layoutResId,rootLayoutId);
            mWindowManager.addView(mFloatView, mFloatView.params);
        }
        mFloatView.setOnCustomListener(new OnCustomListener() {
                                           @Override
                                           public void onTopClick() {
                                               //Toast.makeText(mContext, "上", Toast.LENGTH_SHORT).show();
                                               Intent intent = new Intent(mContext, RecorderActivity.class);
                                               mContext.startActivity(intent);
                                               remove();
                                           }

                                           @Override
                                           public void onLeftClick() {
                                               Toast.makeText(mContext, "左", Toast.LENGTH_SHORT).show();
                                           }

                                           @Override
                                           public void onRightClick() {
                                               Toast.makeText(mContext, "右", Toast.LENGTH_SHORT).show();
                                           }

                                           @Override
                                           public void onBottomClick() {
                                               Toast.makeText(mContext, "下", Toast.LENGTH_SHORT).show();
                                           }

                                           @Override
                                           public void onCenterClick() {
                                               remove();
                                           }
                                       }
        );
    }


    //是否有悬浮窗,有悬浮窗显示在桌面上返回true，没有的话返回false
    public boolean isWindowShowing(){
        return mFloatView != null;
    }

    //将悬浮窗移除
    public void removeWindow(){
        if(mFloatView != null){
            mWindowManager.removeView(mFloatView);
            mFloatView = null;
        }
    }

    public void remove(){
        mContext.stopService(new Intent(mContext,FloatService.class));
        removeWindow();
    }

}
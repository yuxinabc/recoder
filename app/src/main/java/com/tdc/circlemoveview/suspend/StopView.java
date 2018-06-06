package com.tdc.circlemoveview.suspend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tdc.circlemoveview.OnCustomListener;
import com.tdc.circlemoveview.R;
import com.tdc.circlemoveview.util.ScreenUtils;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2018/6/4.
 */
public class StopView extends LinearLayout implements View.OnClickListener{

    private OnStopListener mListener;
    private TextView stop_tv;

    // 小悬浮窗的宽
    public int viewWidth;
    // 小悬浮窗的高
    public int viewHeight;
    // 系统状态栏的高度
    private static int statusBarHeight;
    // 用于更新小悬浮窗的位置
    private WindowManager windowManager;
    // 小悬浮窗的布局参数
    public WindowManager.LayoutParams params;
    // 记录当前手指位置在屏幕上的横坐标
    private float xInScreen;
    // 记录当前手指位置在屏幕上的纵坐标
    private float yInScreen;
    // 记录手指按下时在屏幕上的横坐标,用来判断单击事件
    private float xDownInScreen;
    // 记录手指按下时在屏幕上的纵坐标,用来判断单击事件
    private float yDownInScreen;
    // 记录手指按下时在小悬浮窗的View上的横坐标
    private float xInView;
    // 记录手指按下时在小悬浮窗的View上的纵坐标
    private float yInView;

    public StopView(Context context) {
        super(context);
        init(context);
    }


    public StopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StopView(Context context, int layoutResId, int rootLayoutId) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(layoutResId, this);
        View view = findViewById(rootLayoutId);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;

        statusBarHeight = getStatusBarHeight();

        params = new WindowManager.LayoutParams();
        // 设置显示类型为phone
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 显示图片格式
        params.format = PixelFormat.RGBA_8888;
        // 设置交互模式
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 设置对齐方式为左上
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.width = viewWidth;
        params.height = viewHeight;
        params.x = ScreenUtils.getScreenWidth(context);
        params.y = ScreenUtils.getScreenHeight(context) / 2;
        init(context);
    }

    //触摸滑动计算
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // 手指按下时记录必要的数据,纵坐标的值都减去状态栏的高度
            case MotionEvent.ACTION_DOWN:
                // 获取相对与小悬浮窗的坐标
                xInView = event.getX();
                yInView = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 时时的更新当前手指在屏幕上的位置
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - statusBarHeight;
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，按下坐标与当前坐标相等，则视为触发了单击事件
                updateViewPosition();
                xInScreen = yInScreen;
                break;
        }
        return true;
    }

    public void setOnStopListener(OnStopListener mListener) {
        this.mListener = mListener;
    }

    private void init(Context context) {
        inflate(context, R.layout.stop_layout, this);
        stop_tv = (TextView) findViewById(R.id.stop_time_tv);
        stop_tv.setText("00:00:00");
        stop_tv.setOnClickListener(this);
    }

    /*
     * 更新小悬浮窗在屏幕中的位置
     */
    private void updateViewPosition() {
        params.x = (int) (xInScreen - xInView);
        params.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, params);
    }


    public int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onStopClick();

        }
    }
}

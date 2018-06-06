package com.tdc.circlemoveview;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tdc.circlemoveview.util.ScreenUtils;

import java.lang.reflect.Field;

/**
 * 描述： TODO
 * 作者： gong.xl
 * 邮箱： gong.xl@belle.com.cn
 * 创建时间： 2018/5/26 19:08
 * 修改时间： 2018/5/26 19:08
 * 修改备注：
 */

public class CustomView extends RelativeLayout implements View.OnClickListener {
    private OnCustomListener mListener;
    TextView mTop;
    TextView mLeft;
    TextView mRight;
    TextView mBottom;
    ImageView mCenter;

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

    public CustomView(Context context) {
        super(context);
        init(context);
    }


    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomView(Context context,int layoutResId, int rootLayoutId) {
        super(context);
        windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // 手指按下时记录必要的数据,纵坐标的值都减去状态栏的高度
            case MotionEvent.ACTION_DOWN:
                // 获取相对与小悬浮窗的坐标
                xInView = event.getX();
                yInView = event.getY();
                // 按下时的坐标位置，只记录一次
                //xDownInScreen = event.getRawX();
                //yDownInScreen = event.getRawY() - statusBarHeight;
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

    private void init(Context context) {
        inflate(context, R.layout.custom_layout, this);
        mTop = (TextView) findViewById(R.id.top);
        mLeft = (TextView) findViewById(R.id.left);
        mRight = (TextView) findViewById(R.id.right);
        mBottom = (TextView) findViewById(R.id.bottom);
        mCenter = (ImageView) findViewById(R.id.center);
        mTop.setOnClickListener(this);
        mLeft.setOnClickListener(this);
        mRight.setOnClickListener(this);
        mBottom.setOnClickListener(this);
        mCenter.setOnClickListener(this);

    }

    public void setOnCustomListener(OnCustomListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.top:
                    mListener.onTopClick();
                    break;
                case R.id.left:
                    mListener.onLeftClick();
                    break;
                case R.id.right:
                    mListener.onRightClick();
                    break;
                case R.id.bottom:
                    mListener.onBottomClick();
                    break;
                case R.id.center:
                    mListener.onCenterClick();
                    break;
            }
        }
    }

    /**
     * 更新小悬浮窗在屏幕中的位置
     */
    private void updateViewPosition() {
        params.x = (int) (xInScreen - xInView);
        params.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, params);
    }

     //获取状态栏的高度
    private int getStatusBarHeight() {
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


}

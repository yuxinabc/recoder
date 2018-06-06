package com.tdc.circlemoveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tdc.circlemoveview.util.DisplayUtil;

public class CircleRelativeLayout extends RelativeLayout {
    private int color;
    private int[] colors;
    private int alpha;

    //分配比例大小，总比例大小为100
    private int[] strPercent = new int[]{25, 25, 25, 25};

    // 圆环颜色
    private int mTopColor;
    private int mLeftColor;
    private int mRightColor;
    private int mBottomColor;

    //边框颜色和标注颜色
    private int[] mColor;

    // 圆环宽度
    private float mStrokeWidth;


    //圆环的画笔
    private Paint cyclePaint;

    //半径
    private float mRadius;

    public CircleRelativeLayout(Context context) {
        super(context);
    }

    public CircleRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setWillNotDraw(false);
    }

    private void init(Context context, AttributeSet attrs) {
        //获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.CircleRelativeLayoutLayout);
        color = array.getColor(R.styleable.CircleRelativeLayoutLayout_background_color, Color.parseColor("#FFFFFF"));
        mTopColor = array.getColor(R.styleable.CircleRelativeLayoutLayout_topColor, Color.parseColor("#FFA100"));
        mLeftColor = array.getColor(R.styleable.CircleRelativeLayoutLayout_leftColor, Color.parseColor("#B7DB10"));
        mRightColor = array.getColor(R.styleable.CircleRelativeLayoutLayout_rightColor, Color.parseColor("#12D890"));
        mBottomColor = array.getColor(R.styleable.CircleRelativeLayoutLayout_bottomColor, Color.parseColor("#0DB8F0"));
        alpha = array.getInteger(R.styleable.CircleRelativeLayoutLayout_background_alpha, 255);
        setColors();
        array.recycle();

        //圆环宽度，默认3dp
        mStrokeWidth = DisplayUtil.dip2px(context, 3);

        mColor = new int[]{mTopColor, mRightColor, mBottomColor, mLeftColor};

        //圆环画笔
        cyclePaint = new Paint();
        cyclePaint.setAntiAlias(true);
        cyclePaint.setStyle(Paint.Style.STROKE);
        cyclePaint.setStrokeWidth(mStrokeWidth);
    }


    @Override
    protected void onDraw(Canvas canvas) { //构建圆形
        super.onDraw(canvas);
        mRadius = getMeasuredWidth();
        //画圆环
        drawCycle(canvas);

        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);

        //绘制内圆
        mPaint.setARGB(alpha, colors[0], colors[1], colors[2]);
        canvas.drawCircle(mRadius / 2, mRadius / 2, mRadius / 2 - mStrokeWidth, mPaint);

    }


    /**
     * 画圆环
     *
     * @param canvas
     */
    private void drawCycle(Canvas canvas) {
        float startPercent = -135;
        float sweepPercent = 0;
        for (int i = 0; i < strPercent.length; i++) {
            cyclePaint.setColor(mColor[i]);
            startPercent = sweepPercent + startPercent;
            //这里采用比例占100的百分比乘于360的来计算出占用的角度，使用先乘再除可以算出值
            sweepPercent = strPercent[i] * 360 / 100;
            canvas.drawArc(new RectF(mStrokeWidth / 2, mStrokeWidth / 2, mRadius - mStrokeWidth / 2, mRadius - mStrokeWidth / 2), startPercent, sweepPercent, false, cyclePaint);
        }
    }


    public void setColor(int color) { //设置背景色
        this.color = color;
        setColors();
        invalidate();
    }

    public void setAlhpa(int alhpa) { //设置透明度
        this.alpha = alhpa;
        invalidate();
    }


    public void setColors() {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        this.colors = new int[]{red, green, blue};
    }

}
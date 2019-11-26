package com.yhd.semicircle;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 半圆形分布图
 * Created by haide.yin(haide.yin@tcl.com) on 2019/7/24 15:38.
 */
public class SemiCircleView extends View {

    private static final String TAG = SemiCircleView.class.getSimpleName();

    /* ********************* 外部设置的属性 *********************** */
    private int backgoundColor = Color.parseColor("#8A398EFF");//底部圆弧的颜色
    private int deepColor = Color.parseColor("#FF398EFF");//加深的颜色
    private float distanceRatio = 0.02f;//宽度百分比
    //动画
    private int animationTime = 1500;//动画持续时间

    /**
     * 需要加深分布点,是一个String[]类表,规则如下
     * String[0]:开始百分比(0-1f)
     * String[1]:扫过的百分比(0-1f)
     * String[0] + String[1] <= 1f
     */
    private List<float[]> deepArray = new ArrayList<>();

    /* ********************* 内部使用的属性 *********************** */
    private Paint circlePaint;//画圆弧的画笔
    private int distance;//圆环的间距
    //动画监听
    private SemiAnimator semiAnimator = new SemiAnimator(animation -> postInvalidate());

    public SemiCircleView(Context context) {
        this(context, null);
    }

    public SemiCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SemiCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //View被窗体移除的时候释放动画资源
        semiAnimator.release();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFoucus) {
        super.onWindowFocusChanged(hasFoucus);
        //View焦点变化
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        semiAnimator.start(animationTime);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {// 分别获取期望的宽度和高度，并取其中较小的尺寸作为该控件的宽和高
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        //裁剪出一个 (长：宽) = (2:1) 的矩形
        int finalWidth = measureWidth;
        int finalHeight = measureHeight;
        if (measureWidth >= measureHeight * 2) {
            finalWidth = measureHeight * 2;
        } else {
            finalHeight = measureWidth / 2;
        }
        setMeasuredDimension(finalWidth, finalHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() > 0 && getHeight() > 0) {
            float phaseS = semiAnimator.getPhaseS();
            distance = (int) (this.distanceRatio * getWidth());//获取圆间距
            circlePaint.setStrokeCap(Paint.Cap.ROUND); // 把每段圆弧改成圆角的
            circlePaint.setStrokeWidth(distance);//设置边界宽度
            circlePaint.setColor(backgoundColor); // 设置底部圆环的颜色
            /* ********************* 开始画背景半圆 *********************** */
            int width = getWidth();
            //矩形区域
            RectF rectF = new RectF(distance / 2, distance / 2, width - distance / 2, width - distance * 1.5f);
            //画半圆弧
            canvas.drawArc(rectF, -180, 180 * phaseS, false, circlePaint);
            /* ********************* 开始画加深的分布区域 *********************** */
            circlePaint.setStrokeCap(Paint.Cap.SQUARE); // 把每段圆弧改成直角的
            circlePaint.setColor(deepColor); // 设置底部圆环的颜色
            if (deepArray != null) {
                for (float[] selectValue : deepArray) {
                    if (selectValue.length >= 2) {
                        float begin = selectValue[0];//开始点
                        float distance = selectValue[1];//扫过角度
                        if (begin < 1f) {
                            //begin + distance 不会超过1f
                            if (begin + distance > 1f) {
                                distance = 1f - begin;
                            }
                            //画半圆弧
                            float startAngle = 180 - 180 * begin * phaseS;
                            float sweepAngle = (180 * distance) * phaseS;
                            canvas.drawArc(rectF, -startAngle, sweepAngle, false, circlePaint);
                        } else {
                            Log.e(TAG, "ivalid data " + Arrays.toString(selectValue));
                        }
                    } else {
                        Log.e(TAG, "ivalid data length " + selectValue.length);
                    }
                }
            }
        } else {
            circlePaint.setShader(null); // 清除上一次的shader
        }
    }

    /**
     * 初始化
     */
    private void init(AttributeSet attrs) {
        //初始化属性
        if (attrs != null) {
            //初始化布局属性
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SemiCircleView, 0, 0);
            //圆环间距
            float distanceRatio = typedArray.getFloat(R.styleable.SemiCircleView_se_distanceRatio, this.distanceRatio);
            if (distanceRatio >= 0 && distanceRatio <= 1.0f) {
                this.distanceRatio = distanceRatio;
            } else {
                Log.e(TAG, "ivalid heightRatio");
            }
            //背景色
            backgoundColor = typedArray.getColor(R.styleable.SemiCircleView_se_backgroungColor, backgoundColor);
            //深色
            deepColor = typedArray.getColor(R.styleable.SemiCircleView_se_deepColor, deepColor);
            //动画时间
            animationTime = typedArray.getColor(R.styleable.SemiCircleView_se_animationTime, animationTime);
        }
        //初始化画笔
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true); // 抗锯齿
        circlePaint.setDither(true); // 防抖动
        circlePaint.setStyle(Paint.Style.STROKE); // 设置绘制的圆为空心


        //设置默认值
        deepArray.add(new float[]{0.1f, 0.1f});
        deepArray.add(new float[]{0.3f, 0.1f});
        deepArray.add(new float[]{0.5f, 0.2f});
        deepArray.add(new float[]{0.8f, 0.1f});
    }

    /**
     * 按进度显示百分比
     *
     * @param deepArray 分布点数据列表
     */
    public void setDeepArray(List<float[]> deepArray) {
        this.deepArray = deepArray;
        circlePaint.setShader(null); // 清除上一次的shader
        semiAnimator.start(animationTime);
    }

    /**
     * 直线绘制持续的动画类
     */
    private class SemiAnimator {

        private float mPhaseS = 1f; //默认动画值0f-1f
        private ValueAnimator.AnimatorUpdateListener mListener;//监听
        private ObjectAnimator objectAnimator;

        private SemiAnimator(ValueAnimator.AnimatorUpdateListener listener) {
            mListener = listener;
        }

        private float getPhaseS() {
            return mPhaseS;
        }

        private void setPhaseS(float phase) {
            mPhaseS = phase;
        }

        /**
         * Y轴动画
         *
         * @param durationMillis 持续时间
         */
        private void start(int durationMillis) {
            release();
            objectAnimator = ObjectAnimator.ofFloat(this, "phaseS", 0f, 1f);
            objectAnimator.setDuration(durationMillis);
            objectAnimator.addUpdateListener(mListener);
            objectAnimator.start();
        }

        /**
         * 释放动画
         */
        private void release() {
            if (objectAnimator != null) {
                objectAnimator.end();
                objectAnimator.cancel();
                objectAnimator = null;
            }
        }
    }
}
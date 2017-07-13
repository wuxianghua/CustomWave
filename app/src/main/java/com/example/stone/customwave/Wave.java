package com.example.stone.customwave;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by stone on 2017/7/13.
 */

public class Wave extends View {
    private int color;
    private String text;
    private Paint mPaint;
    private Paint textPaint;
    private Path path;
    private int mWidth;
    private int mHeight;
    private int textSize = DimentionUtils.sp2px(getContext(), 25);
    private float currentPersent;

    public Wave(Context context) {
        this(context,null);
    }

    public Wave(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Wave(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //获取自定义参数值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Wave);
        //自定义颜色和文字
        color = typedArray.getColor(R.styleable.Wave_color, Color.rgb(41,163,254));
        text = typedArray.getString(R.styleable.Wave_text);
        typedArray.recycle();
        //图形及路径填充画笔（抗锯齿，填充，防抖动）
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        mPaint.setDither(true);
        //文字画笔（抗锯齿，白色，粗体）
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        //闭合波浪路径
        path = new Path();
    }

    public void startMove() {
        final ValueAnimator animator = ValueAnimator.ofFloat(0,1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentPersent = valueAnimator.getAnimatedFraction();
                invalidate();
            }
        });
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    private Path getActionPath(float percent) {
        Path path = new Path();
        int x = -mWidth;
        // 当前x点坐标（根据动画进度水平推移，一个动画周期推移的距离为一个周期的波长）
        x += percent*mWidth;
        //波的起点
        path.moveTo(x,mHeight/2);
        //控制点的相对宽度
        int quadWidth = mWidth/4;
        //控制点的相对高度
        int quadHeight = mHeight/20*3;
        //第一个周期波形
        path.rQuadTo(quadWidth,quadHeight,quadWidth*2,0);
        path.rQuadTo(quadWidth,-quadHeight,quadWidth*2,0);
        //第二个周期的波形
        path.rQuadTo(quadWidth,quadHeight,quadWidth*2,0);
        path.rQuadTo(quadWidth,-quadHeight,quadWidth*2,0);
        //右侧的直线
        path.lineTo(x+mWidth*2,mHeight);
        //下边的直线
        path.lineTo(x,mHeight);
        //自动闭合补出左边的曲线
        path.close();
        return path;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //底部的字
        textPaint.setColor(color);
        drawCenterText(canvas,textPaint,text);
        //上层的字
        textPaint.setColor(Color.WHITE);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        //裁剪成圆形
        Path o = new Path();
        o.addCircle(mWidth/2,mHeight/2,mWidth/2,Path.Direction.CCW);
        canvas.clipPath(o);
        path = getActionPath(currentPersent);
        canvas.drawPath(path,mPaint);
        canvas.clipPath(path);
        drawCenterText(canvas,textPaint,text);
        canvas.restore();
    }

    private void drawCenterText(Canvas canvas, Paint textPaint, String text) {
        Rect rect = new Rect(0,0,mWidth,mHeight);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;

        int centerY = (int)(rect.centerY() - top/2 - bottom/2);
        canvas.drawText(text,rect.centerX(),centerY,textPaint);
    }
}




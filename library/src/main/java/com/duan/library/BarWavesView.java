package com.duan.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by DuanJiaNing on 2017/9/24.
 * 当为控件指定宽和高时，mWaveMaxHeight，mWaveInterval 将由控件计算
 * 不支持 paddding
 */

public class BarWavesView extends View {

    /**
     * 横条颜色
     */
    private int mBarColor;

    /**
     * 横条高度
     */
    private int mBarHeight;

    /**
     * 波浪条最小高度
     */
    private int mWaveMinHeight;

    /**
     * 波浪条极差（最高与最低的差值）
     */
    private int mWaveRange;

    /**
     * 波浪条宽度
     */
    private int mWaveWidth;

    /**
     * 波浪条数量
     */
    private int mWaveNumber;

    /**
     * 波浪条间隔
     */
    private int mWaveInterval;

    private final Paint mPaint = new Paint();

    private int[][] mWaveColors;

    private final static int sDEFAULT_BAR_COLOR = Color.BLACK;
    private final static int sDEFAULT_WAVE_COLOR = Color.RED;

    private final static int sMIN_WAVE_NUMBER = 13;
    private final static int sMIN_BAR_HEIGHT = 0;
    private final static int sMIN_WAVE_HEIGHT = 10;
    private final static int sMIN_WAVE_RANGE = 10;
    private final static int sMIN_WAVE_INTERVAL = 0;
    private final static int sMIN_WAVE_WIDTH = 5;
    private final static int sMIN_WIDTH = sMIN_WAVE_NUMBER * sMIN_WAVE_WIDTH + (sMIN_WAVE_NUMBER - 1) * sMIN_WAVE_INTERVAL;
    private final static int sMIN_HEIGHT = sMIN_WAVE_HEIGHT + sMIN_WAVE_RANGE + sMIN_BAR_HEIGHT;

    public BarWavesView(Context context) {
        super(context);
        mPaint.setAntiAlias(true);
        setWaveColors(sMIN_WAVE_NUMBER, sDEFAULT_WAVE_COLOR);
        mBarColor = sDEFAULT_BAR_COLOR;
    }

    public BarWavesView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarWavesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint.setAntiAlias(true);

        final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BarWavesView, defStyleAttr, 0);

        int tempWaveColor = array.getColor(R.styleable.BarWavesView_waveColor, sDEFAULT_WAVE_COLOR);
        mBarColor = array.getColor(R.styleable.BarWavesView_barColor, sDEFAULT_BAR_COLOR);

        mBarHeight = array.getDimensionPixelSize(R.styleable.BarWavesView_barHeight, sMIN_BAR_HEIGHT);
        mBarHeight = mBarHeight < sMIN_BAR_HEIGHT ? sMIN_BAR_HEIGHT : mBarHeight;

        mWaveRange = array.getDimensionPixelSize(R.styleable.BarWavesView_waveRange, sMIN_WAVE_RANGE);
        mWaveRange = mWaveRange < sMIN_WAVE_RANGE ? sMIN_WAVE_RANGE : mWaveRange;

        mWaveMinHeight = array.getDimensionPixelSize(R.styleable.BarWavesView_waveMinHeight, sMIN_WAVE_HEIGHT);
        mWaveMinHeight = mWaveMinHeight < sMIN_WAVE_HEIGHT ? sMIN_WAVE_HEIGHT : mWaveMinHeight;

        mWaveWidth = array.getDimensionPixelSize(R.styleable.BarWavesView_waveWidth, sMIN_WAVE_WIDTH);
        mWaveWidth = mWaveWidth < sMIN_WAVE_WIDTH ? sMIN_WAVE_WIDTH : mWaveWidth;

        mWaveInterval = array.getDimensionPixelSize(R.styleable.BarWavesView_waveInterval, sMIN_WAVE_INTERVAL);
        mWaveInterval = mWaveInterval < sMIN_WAVE_INTERVAL ? sMIN_WAVE_INTERVAL : mWaveInterval;

        mWaveNumber = array.getInteger(R.styleable.BarWavesView_waveNumber, sMIN_WAVE_NUMBER);
        mWaveNumber = mWaveNumber < sMIN_WAVE_NUMBER ? sMIN_WAVE_NUMBER : mWaveNumber;

        //释放资源
        array.recycle();

        setWaveColors(mWaveNumber, tempWaveColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize < sMIN_WIDTH ? sMIN_WIDTH : widthSize;
            adjustWidth(width);
        } else {//xml中宽度设为warp_content
            width = mWaveWidth * mWaveNumber + mWaveInterval * (mWaveNumber - 1);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize < sMIN_HEIGHT ? sMIN_HEIGHT : heightSize;
            adjustHeight(height);
        } else {
            height = mWaveMinHeight + mWaveRange + mBarHeight;
        }

        setMeasuredDimension(width, height);

    }

    private void adjustWidth(int width) {

        while ((width - mWaveWidth * mWaveNumber) < mWaveInterval * (mWaveNumber - 1)) {
            if (mWaveInterval > sMIN_WAVE_INTERVAL) {
                mWaveInterval--; // 首选调整波浪条间距
            } else {
                if (mWaveWidth > sMIN_WAVE_WIDTH) {
                    mWaveWidth--; // 其次选择调整波浪条宽度
                } else {
                    width++; // 万不得已选择调整设置的宽度
                }
            }
        }

    }

    private void adjustHeight(int height) {

        while (mWaveMinHeight + mWaveRange + mBarHeight > height) {
            if (mBarHeight > sMIN_BAR_HEIGHT) {
                mBarHeight--;
                continue;
            }

            if (mWaveMinHeight > sMIN_WAVE_HEIGHT) {
                mWaveMinHeight--;
                continue;
            }

            if (mWaveRange > sMIN_WAVE_RANGE) {
                mWaveRange--;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawBar(canvas);

        drawWaves(canvas);

    }

    private void drawWaves(Canvas canvas) {

        final Rect rect = new Rect();
        for (int i = 0; i < mWaveNumber; i++) {
            int left = mWaveWidth * i + mWaveInterval * i;
            int right = left + mWaveWidth;

            int bottom = getHeight() - mBarHeight;
            // FIXME
            int top = bottom - (Math.round(mWaveRange) + mWaveMinHeight);
            rect.set(left, top, right, bottom);
            LinearGradient lg = new LinearGradient(
                    rect.left, rect.top,
                    rect.right, rect.top,
                    mWaveColors[i],
                    null,
                    Shader.TileMode.CLAMP
            );
            mPaint.setShader(lg);
            canvas.drawRect(rect, mPaint);
            Log.d(TAG, "drawWaves: " + rect.toString());
        }

    }

    private void drawBar(Canvas canvas) {
        mPaint.setShader(null);
        mPaint.setColor(mBarColor);
        canvas.drawRect(0, getHeight() - mBarHeight, getWidth(), getHeight(), mPaint);
    }

    public void setBarColor(@ColorInt int color) {
        this.mBarColor = color;
        invalidate();
    }

    public void setWaveColor(@ColorInt int color) {
        setWaveColors(mWaveNumber, color);
        invalidate();
    }

    public void setWaveColor(int[][] color) {
        if (color == null || color.length < mWaveNumber || color[0].length < 2) {
            return;
        }
        setWaveColors(mWaveNumber, color);
        invalidate();
    }

    public void setWaveColor(int[] color) {
        if (color == null || color.length < mWaveNumber) {
            return;
        }
        int[][] cs = new int[color.length][2];
        for (int i = 0; i < cs.length; i++) {
            cs[i][0] = color[i];
            cs[i][1] = color[i];
        }
        setWaveColors(cs.length, cs);
        invalidate();

    }

    // len 不能小于 mWaveNumber  数组第二维长度不能小于 2
    private void setWaveColors(int len, int[][] color) {
        mWaveColors = new int[len][color[0].length];
        for (int i = 0; i < mWaveColors.length; i++) {
            for (int j = 0; j < mWaveColors[i].length; j++) {
                mWaveColors[i][j] = color[i][j];
            }
        }
    }

    // len 不能小于 mWaveNumber
    private void setWaveColors(int len, int color) {
        mWaveColors = new int[len][2];
        for (int i = 0; i < mWaveColors.length; i++) {
            mWaveColors[i][0] = color;
            mWaveColors[i][1] = color;
        }
    }
}

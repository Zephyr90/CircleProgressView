package com.zephyr.app.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.zephyr.app.circleprogressview.R;

public class SkipView extends View {
    private static final String TAG = "SkipView";

    private static int CIRCLE_RADIUS = 10;
    private static int CIRCLE_BOUND_COLOR = Color.BLUE;
    private static int TEXT_COLOR = Color.WHITE;
    private static int CIRCLE_BG = Color.GRAY;
    private static int CIRCLE_BOUND_WIDTH = 5;
    private static int DEFUALUT_TEXT_SIZE = 50;
    private static int DEFUALT_TIME = 6;

    private float mCircleRadius;
    private int mCircleBoundColor;
    private int mTextColor;
    private int mCircleBg;
    private float mCircleBoundWidth;
    private float mTextSize;
    private float mViewRadius;
    private float mCenterX;
    private float mCenterY;
    private float mTextBaseLineY;
    private float mTextStartX;
    private float mSwapAngle;
    private boolean isAnimating;
    private int mTime;
    private float[] mTextWidthArray = new float[1];

    private RectF mRectF = new RectF();
    private Path mPath = new Path();
    private Path mBgPath = new Path();
    private Paint mProgressPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private Paint mBgPaint = new Paint();
    private Paint mBgPathPaint = new Paint();
    private OnProgressListener mProgressListener;

    public SkipView(Context context) {
        this(context, null);
    }

    public SkipView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SkipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defSytleRes) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SkipView, defStyleAttr, defSytleRes);
        mCircleRadius = array.getDimensionPixelSize(R.styleable.SkipView_circle_radius, CIRCLE_RADIUS);
        mCircleBoundColor = array.getColor(R.styleable.SkipView_circle_bound_color, CIRCLE_BOUND_COLOR);
        mTextColor = array.getColor(R.styleable.SkipView_text_color, TEXT_COLOR);
        mCircleBg = array.getColor(R.styleable.SkipView_circle_bg, CIRCLE_BG);
        mCircleBoundWidth = array.getDimensionPixelSize(R.styleable.SkipView_circle_bound_width, CIRCLE_BOUND_WIDTH);
        mTextSize = array.getDimensionPixelSize(R.styleable.SkipView_text_size, DEFUALUT_TEXT_SIZE);
        mTime = array.getInteger(R.styleable.SkipView_time, DEFUALT_TIME);

        mProgressPaint.setColor(mCircleBoundColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.BUTT);
        mProgressPaint.setStrokeWidth(mCircleBoundWidth);
        mProgressPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(mCircleBg);
        mBgPaint.setAntiAlias(true);
        mBgPathPaint.setAntiAlias(true);
        mBgPathPaint.setColor(Color.TRANSPARENT);
        mBgPathPaint.setStyle(Paint.Style.STROKE);
        mBgPathPaint.setStrokeWidth(mCircleBoundWidth);
        array.recycle();

        mViewRadius = mCircleRadius + mCircleBoundWidth;
        mCenterX = mViewRadius;
        mCenterY = mViewRadius;

        mRectF.set(mCircleBoundWidth / 2, mCircleBoundWidth / 2, mViewRadius * 2 - mCircleBoundWidth / 2, mViewRadius * 2 - mCircleBoundWidth / 2);

        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        float dy = (metrics.bottom - metrics.top) / 2 - metrics.bottom;
        mTextBaseLineY = mViewRadius + dy;
        mTextPaint.getTextWidths(String.valueOf(mTime), mTextWidthArray);
        mTextStartX = mViewRadius - mTextWidthArray[0] / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(getDimensions(widthMode, widthSize), getDimensions(heightMode, heightSize));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isAnimating) startProgress();
        canvas.drawCircle(mCenterX, mCenterX, mCircleRadius, mBgPaint);
        canvas.drawText(String.valueOf(mTime), mTextStartX, mTextBaseLineY, mTextPaint);

        mBgPath.addCircle(mCenterX, mCenterY, mCircleRadius + mCircleBoundWidth / 2, Path.Direction.CCW);
        mPath.arcTo(mRectF, 0, mSwapAngle, true);
        canvas.drawPath(mBgPath, mBgPathPaint);
        canvas.drawPath(mPath, mProgressPaint);
    }

    private int getDimensions(int mode, int size) {
        int realSize = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                realSize = size;
                break;
            case MeasureSpec.AT_MOST:
                realSize = ((int) Math.min(size, mViewRadius * 2 + 1));
                break;
            default:
                realSize = ((int) Math.min(size, mViewRadius * 2 + 1));
        }
        return realSize;
    }

    public void setProgress(float angle) {
        mSwapAngle = angle;
        invalidate();
    }

    public void setTime(int time) {
        mTime = time;
        mTextPaint.getTextWidths(String.valueOf(mTime), mTextWidthArray);
        mTextStartX = mViewRadius - mTextWidthArray[0] / 2;
        invalidate();
    }

    private void startProgress() {
        final int duration = mTime * 1000;
        ObjectAnimator progressAnim = ObjectAnimator.ofFloat(this, "progress", 0F, 360.0F);
        progressAnim.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator timeAnim = ObjectAnimator.ofInt(this, "time", mTime, 0);
        timeAnim.setInterpolator(new LinearInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.setDuration(duration);
        set.play(progressAnim).with(timeAnim);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPath.close();
                invalidate();
                if (mProgressListener != null) mProgressListener.onCompleted();
            }
        });
        set.start();
        isAnimating = true;
    }

    public void setOnProgressListener(OnProgressListener listener) {
        if (listener == null) throw new RuntimeException("listener is null!");
        mProgressListener = listener;
    }

    public interface OnProgressListener {
        void onCompleted();
    }
}

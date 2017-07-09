package com.demoapplication.starryskydemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 星空漂浮自定义控件
 * Created by raoxuting on 2017/6/22.
 */

public class StarrySkyView extends View {

    private Bitmap backBitmap;//背景图

    private Bitmap earth;//地球

    private Bitmap jupiter;//木星

    private Bitmap mars;//火星

//    private int earthWidth;
//    private int earthHeight;
//
//    private int jupiterWidth;
//    private int jupiterHeight;
//
//    private int marsWidth;
//    private int marsHeight;

    private int mFloatTransLowSpeed;
    private int mFloatTransMidSpeed;
    private int mFloatTransFastSpeed;

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int TOP = 2;
    private static final int BOTTOM = 3;

    private static final float[][] STAR_LOCATION = new float[][]{
            {0.2f, 0.15f}, {0.35f, 0.35f}, {0.5f, 0.66f},
            {0.8f, 0.76f}, {0.4f, 0.82f}, {0.75f, 0.9f},
            {0.2f, 0.3f}, {0.77f, 0.4f}, {0.75f, 0.5f},
            {0.8f, 0.55f}, {0.9f, 0.6f}, {0.1f, 0.7f},
            {0.1f, 0.1f}, {0.7f, 0.8f}, {0.5f, 0.6f}
    };

    private List<StarInfo> mStarInfos = new ArrayList<>();

    private int mTotalWidth;
    private int mTotalHeight;

    //    private Rect mStarOneSrcRect;
//    private Rect mStarTwoSrcRect;
//    private Rect mStarThreeSrcRect;
    private Rect backGroundSrcRect;
    private Rect screenRect;

    /**
     * 绘图的画笔
     */
    private Paint paint;
    /**
     * 画笔
     */
    private ValueAnimator valueAnimator;

    private long currentTime;

    public StarrySkyView(Context context) {
        this(context, null);
    }

    public StarrySkyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarrySkyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化三种星球移动速度
        mFloatTransLowSpeed = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f,
                getResources().getDisplayMetrics());
        mFloatTransMidSpeed = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.75f,
                getResources().getDisplayMetrics());
        mFloatTransFastSpeed = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f,
                getResources().getDisplayMetrics());

        mTotalWidth = getResources().getDisplayMetrics().widthPixels;
        mTotalHeight = getResources().getDisplayMetrics().heightPixels;

        initBitmapInfo();

        paint = new Paint();

        screenRect = new Rect(0, 0, mTotalWidth, mTotalHeight);

        valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(10000);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new AnimatorUpdateListener());

    }

    private class AnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            Log.e("动画值", (int)animation.getAnimatedValue() + "");
            for (int i = 0; i < mStarInfos.size(); i++) {
                setStarFloat(mStarInfos.get(i));
                postInvalidate();
            }
        }
    }

    /**
     * 初始化星球位图
     */
    private void initBitmapInfo() {

        backBitmap = ((BitmapDrawable) getResources().
                getDrawable(R.mipmap.starry_sky)).getBitmap();
        int backWidth = backBitmap.getWidth();
        int backHeight = backBitmap.getHeight();

        earth = ((BitmapDrawable) getResources().
                getDrawable(R.mipmap.earth)).getBitmap();
//        earthWidth = earth.getWidth();
//        earthHeight = earth.getHeight();

        jupiter = ((BitmapDrawable) getResources().
                getDrawable(R.mipmap.jupiter)).getBitmap();
//        jupiterWidth = jupiter.getWidth();
//        jupiterHeight = jupiter.getHeight();

        mars = ((BitmapDrawable) getResources().
                getDrawable(R.mipmap.mars)).getBitmap();
//        marsWidth = mars.getWidth();
//        marsHeight = mars.getHeight();

//        mStarOneSrcRect = new Rect(0, 0, earthWidth, earthHeight);
//        mStarTwoSrcRect = new Rect(0, 0, jupiterWidth, jupiterHeight);
//        mStarThreeSrcRect = new Rect(0, 0, marsWidth, marsHeight);
        backGroundSrcRect = new Rect(0, 0, backWidth, backHeight);

        initStarInfo();
    }

    /**
     * 星球
     */
    private class StarInfo {

        // 缩放比例
        float sizePercent;
        // x位置
        int xLocation;
        // y位置
        int yLocation;
        // 透明度
        float alpha;
        // 漂浮方向
        int direction;
        // 漂浮速度
        int speed;
        //旋转角度
        int rotateAngle;
    }

    /**
     * 获取星球大小
     */
    private float getStarSize(float start, float end) {
        float nextFloat = (float) Math.random();
        if (start < nextFloat && nextFloat < end) {
            return nextFloat;
        } else {
            // 如果不处于想要的数据段，则再随机一次，因为不断递归有风险
            return (float) Math.random();
        }

    }

    /**
     * 初始化星球运行方向
     */
    private int getStarDirection() {
        Random random = new Random();
        int randomInt = random.nextInt(4);
        int direction = 0;
        switch (randomInt) {
            case 0:
                direction = LEFT;
                break;
            case 1:
                direction = RIGHT;
                break;
            case 2:
                direction = TOP;
                break;
            case 3:
                direction = BOTTOM;
                break;
        }
        return direction;
    }

    /**
     * 初始化星球信息
     */
    private void initStarInfo() {

        StarInfo starInfo = null;
        Random random = new Random();
        int mStarCount = 8;
        for (int i = 0; i < mStarCount; i++) {
            // 获取星球大小比例
            float starSize = getStarSize(0.4f, 0.7f);
            // 初始化星球
            float[] starLocation = STAR_LOCATION[i];
            starInfo = new StarInfo();
            starInfo.sizePercent = starSize;

            // 初始化漂浮速度
            int randomSpeed = random.nextInt(3);
            switch (randomSpeed) {
                case 0:
                    starInfo.speed = mFloatTransLowSpeed;
                    break;
                case 1:
                    starInfo.speed = mFloatTransMidSpeed;
                    break;
                case 2:
                    starInfo.speed = mFloatTransFastSpeed;
                    break;
                default:
                    starInfo.speed = mFloatTransMidSpeed;
                    break;
            }

            // 初始化星球透明度
            starInfo.alpha = getStarSize(0.3f, 0.8f);
            // 初始化星球位置
            starInfo.xLocation = (int) (starLocation[0] * mTotalWidth);
            starInfo.yLocation = (int) (starLocation[1] * mTotalHeight);
            Log.e("位置信息", "xLocation = " + starInfo.xLocation + "--yLocation = "
                    + starInfo.yLocation);
            Log.e("大小信息", "stoneSize = " + starSize + "---stoneAlpha = "
                    + starInfo.alpha);
            // 初始化星球方向
            starInfo.direction = getStarDirection();
            //初始化星球旋转角度
            starInfo.rotateAngle = 0;
            mStarInfos.add(starInfo);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //首先绘制背景图
        canvas.drawBitmap(backBitmap, backGroundSrcRect, screenRect, paint);
        //绘制星球
        for (int i = 0; i < mStarInfos.size(); i++) {
            drawStarDynamic(i, mStarInfos.get(i), canvas, paint);
        }
    }

    public void startMoving() {
        valueAnimator.start();
    }

    public void pauseOrContinueMoving() {
        if (valueAnimator.isRunning()) {
            currentTime = valueAnimator.getCurrentPlayTime();
            valueAnimator.cancel();
        } else {
            valueAnimator.setCurrentPlayTime(currentTime);
            valueAnimator.start();
        }
    }

    private void drawStarDynamic(int count, StarInfo starInfo,
                                 Canvas canvas, Paint paint) {

//        float starAlpha = starInfo.alpha;
        int xLocation = starInfo.xLocation;
        int yLocation = starInfo.yLocation;
        float sizePercent = starInfo.sizePercent;
        int rotateAngle = starInfo.rotateAngle;

        xLocation = (int) (xLocation / sizePercent);
        yLocation = (int) (yLocation / sizePercent);

        Bitmap bitmap;
//        Rect srcRect;
//        Rect destRect = new Rect();

        if (count % 3 == 0) {
            bitmap = earth;
//            srcRect = mStarOneSrcRect;
//            destRect.set(xLocation, yLocation, xLocation +
//                    earthWidth, yLocation + earthHeight);
        } else if (count % 2 == 0) {
            bitmap = jupiter;
//            srcRect = mStarTwoSrcRect;
//            destRect.set(xLocation, yLocation, xLocation
//                    + jupiterWidth, yLocation + jupiterHeight);
        } else {
            bitmap = mars;
//            srcRect = mStarThreeSrcRect;
//            destRect.set(xLocation, yLocation, xLocation
//                    + marsWidth, yLocation + marsHeight);
        }

//        paint.setAlpha((int) (starAlpha * 255));
        canvas.save();
        //缩放星球
        canvas.scale(sizePercent, sizePercent);
        Matrix matrix = new Matrix();
        //绘制位置
        matrix.postTranslate(xLocation, yLocation);
        //绘制角度
        matrix.postRotate(rotateAngle, xLocation + bitmap.getWidth() / 2,
                yLocation + bitmap.getHeight() / 2);
//        canvas.drawBitmap(bitmap, srcRect, destRect, paint);
        canvas.drawBitmap(bitmap, matrix, paint);
        canvas.restore();

    }

    /**
     * 动态改变星球位置及旋转角度
     *
     * @param starInfo
     */
    private void setStarFloat(StarInfo starInfo) {
        switch (starInfo.direction) {
            case LEFT:
                starInfo.xLocation -= starInfo.speed;
                if (starInfo.xLocation < 0)
                    starInfo.xLocation = mTotalWidth;
                break;
            case RIGHT:
                starInfo.xLocation += starInfo.speed;
                if (starInfo.xLocation > mTotalWidth)
                    starInfo.xLocation = 0;
                break;
            case TOP:
                starInfo.yLocation -= starInfo.speed;
                if (starInfo.yLocation < 0)
                    starInfo.yLocation = mTotalHeight;
                break;
            case BOTTOM:
                starInfo.yLocation += starInfo.speed;
                if (starInfo.yLocation > mTotalHeight)
                    starInfo.yLocation = 0;
                break;
            default:
                break;
        }
        starInfo.rotateAngle += 1;
        if (starInfo.rotateAngle > 360)
            starInfo.rotateAngle = 0;
    }

}

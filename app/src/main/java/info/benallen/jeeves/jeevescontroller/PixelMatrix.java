package info.benallen.jeeves.jeevescontroller;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.Random;

/**
 * Created by benallen on 29/01/2017.
 */

interface PixelInterface {
    boolean shouldDrawPixel(int x, int y);
}

public class PixelMatrix extends View implements ValueAnimator.AnimatorUpdateListener{
    private static final int CELL_COUNT = 16;
    private static final int BACKGROUND_COLOUR = 0xFCBC00;

    private float mCellPadding;
    private Paint mPixelPaint;
    private float mCellHeight;
    private float mCellWidth;
    private PixelInterface mPixelInterface;
    private ValueAnimator mValueAnimator;

    private int mNumCirclesToDraw = 0;
    private int circleLimit = 35;
    private int circleStart = 10;

    private float limit[][] = new float[CELL_COUNT][CELL_COUNT];
    private float radiuses[][] = new float[CELL_COUNT][CELL_COUNT];
    private float directions[][] = new float[CELL_COUNT][CELL_COUNT];

    public PixelMatrix(Context context) {
        super(context);
        init(context);
    }

    public PixelMatrix(Context context,AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }

    public PixelMatrix(Context context,AttributeSet attrs,int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        init(context);
    }

    public PixelMatrix(Context context,AttributeSet attrs,int defStyleAttr,int defStyleRes) {
        super(context,attrs,defStyleAttr,defStyleRes);
        init(context);
    }

    private void init(Context context) {

        mPixelInterface = new NoConnectionScreen();
        mPixelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPixelPaint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                Random rand = new Random();
                float radius = rand.nextInt(circleLimit) + circleStart;
                radiuses[i][j] = radius;
                limit[i][j] = radius;
            }
        }

        mValueAnimator = ValueAnimator.ofInt(0, CELL_COUNT * CELL_COUNT);
        mValueAnimator.setDuration(6000);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int width = getMeasuredWidth();
        int heigth = getMeasuredHeight();

        int dimen = (width > heigth) ? heigth : width;

        setMeasuredDimension(dimen,dimen);
    }

    @Override
    protected void onSizeChanged(int w,int h,int oldw,int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);

//            mVertCellCount = h / CELL_COUNT;
//            mHorizCellCount = w / CELL_COUNT;

//            mVertCellCount = mHorizCellCount = CELL_COUNT;


        mCellHeight = h / CELL_COUNT;
        mCellWidth = w / CELL_COUNT;

        mCellPadding = mCellHeight * 0.01f;

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        int val = (Integer) animation.getAnimatedValue();
        if (val != mNumCirclesToDraw) {
            mNumCirclesToDraw = val;
            for (int i = 0; i < CELL_COUNT; i++) {
                for (int j = 0; j < CELL_COUNT; j++) {
                    if (radiuses[i][j] == 0) {
                        directions[i][j] = 1;
                    } else if (radiuses[i][j] == limit[i][j]) {
                        directions[i][j] = 0;
                        Random rand = new Random();
                        float newLimit = rand.nextInt(circleLimit) + circleStart;
                        limit[i][j] = newLimit;
                    }


                    if (directions[i][j] == 0) {
                        radiuses[i][j] = radiuses[i][j] - 1;
                    } else {
                        radiuses[i][j] = radiuses[i][j] + 1;
                    }

                }
            }
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cellColour;
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                cellColour = mPixelInterface.shouldDrawPixel(i, j) ? Color.WHITE : Color.rgb(179, 38, 193);
                mPixelPaint.setColor(cellColour);

                float radius = radiuses[i][j];

                canvas.drawCircle(i * mCellWidth + mCellPadding - (radius / 2),j * mCellHeight + mCellPadding - (radius / 2), radius, mPixelPaint);
            }
        }

    }

    public void setFace(PixelInterface pixelInterface) {
        this.mPixelInterface = pixelInterface;
        invalidate();
    }
}


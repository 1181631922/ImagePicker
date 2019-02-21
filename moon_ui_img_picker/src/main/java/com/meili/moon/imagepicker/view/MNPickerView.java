package com.meili.moon.imagepicker.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;


/**
 * Author： fanyafeng
 * Date： 18/1/31 下午4:37
 * Email: fanyafeng@live.cn
 */
public class MNPickerView extends ImageView {
    private final static String TAG = MNPickerView.class.getSimpleName();

    private Context context;

    private TextPaint textPaint;
    private Paint mPaint;

    private int shadeColor = -1;
    private Paint shadePaint;
    private boolean setRoundShade = false;
    private RectF roundRect;
    private String hintText = null;

    private OnOperateListener onOperateListener;
    private OnMNImageViewListener onMNImageViewListener;

    /**
     * 是否有确认按钮
     */
    private boolean hasConfirm;
    private Paint confirmPaint;

    /**
     * 是否有取消按钮
     */
    private boolean hasCancel;
    /**
     * 是否确认选中或者取消
     */
    private boolean isCancel;
    private Paint cancelPaint;
    private boolean canOperate = false;

    private boolean isMultiImg = false;

    public boolean isCanOperate() {
        return canOperate;
    }

    public void setCanOperate(boolean canOperate) {
        this.canOperate = canOperate;
    }

    private Paint statusPaint;

    private Paint strokeStatusPaint;

    private Paint rectPaint;

    private static int dp26;
    private static int dp3;
    private static int storkeWidth = 5;

    public MNPickerView(Context context) {
        super(context);
        init(context);
    }

    public MNPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MNPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(dip2px(context, 16));
        textPaint.setTextAlign(Paint.Align.LEFT);

        confirmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        confirmPaint.setAntiAlias(true);
        confirmPaint.setColor(Color.WHITE);
        confirmPaint.setStrokeWidth(storkeWidth);

        cancelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cancelPaint.setAntiAlias(true);
        cancelPaint.setColor(Color.parseColor("#cecece"));
        cancelPaint.setStrokeWidth(storkeWidth);
        cancelPaint.setStrokeCap(Paint.Cap.ROUND);

        statusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        statusPaint.setAntiAlias(true);

        strokeStatusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeStatusPaint.setAntiAlias(true);
        strokeStatusPaint.setStrokeWidth(2);
        strokeStatusPaint.setStyle(Paint.Style.STROKE);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);

        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setAntiAlias(true);
        rectPaint.setColor(Color.parseColor("#ffb400"));
        rectPaint.setStrokeWidth(4);

        shadePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadePaint.setAntiAlias(true);
        shadePaint.setColor(Color.parseColor("#77000000"));

        roundRect = new RectF();

        if (dp26 == 0) {
            dp26 = dip2px(context, 20);
        }

        if (dp3 == 0) {
            dp3 = dip2px(context, 4f);
        }


    }

    public interface OnOperateListener {
        void OnStatusListener();
    }

    public void setOnOperateListener(OnOperateListener onOperateListener) {
        this.onOperateListener = onOperateListener;
    }

    public boolean isSetRoundShade() {
        return setRoundShade;
    }

    public void setSetRoundShade(boolean setRoundShade) {
        this.setRoundShade = setRoundShade;
        invalidate();
    }

    public void setShadeColor(@ColorInt int shadeColor) {
        this.shadeColor = shadeColor;
        invalidate();
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
        invalidate();
    }

    public void setMultiImg(boolean multiImg) {
        isMultiImg = multiImg;
        invalidate();
    }

    public void setSuccess() {
        setHintText("上传\n成功");
    }

    public void setSuccess(String msg) {
        setHintText(msg);
    }

    public void setFailed() {
        setHintText("上传\n失败");
    }

    public void setFailed(String msg) {
        setHintText(msg);
    }

    public boolean isHasConfirm() {
        return hasConfirm;
    }

    public void setHasConfirm(boolean hasConfirm) {
        this.hasConfirm = hasConfirm;
    }

    public boolean isHasCancel() {
        return hasCancel;
    }

    public void setHasCancel(boolean hasCancel) {
        this.hasCancel = hasCancel;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void resetView() {
        hasConfirm = false;
        isCancel = false;
        hasCancel = false;
        canOperate = false;
        hintText = "";
        shadeColor = Color.TRANSPARENT;
        setRoundShade = false;
        invalidate();
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
        invalidate();
    }

    public interface OnMNImageViewListener {
        void onDetach();

        void onAttach();

        void onDraw(Canvas canvas);

        boolean verifyDrawable(Drawable who);
    }

    public void setOnMNImageViewListener(OnMNImageViewListener onMNImageViewListener) {
        this.onMNImageViewListener = onMNImageViewListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (onMNImageViewListener != null) {
            onMNImageViewListener.onDetach();
        }
        if (getDrawable() != null) {
            getDrawable().setVisible(false, false);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (onMNImageViewListener != null) {
            onMNImageViewListener.onAttach();
        }
        if (getDrawable() != null) {
            getDrawable().setVisible(getVisibility() == VISIBLE, false);
        }
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (onMNImageViewListener != null) {
            onMNImageViewListener.onAttach();
        }
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (onMNImageViewListener != null) {
            onMNImageViewListener.onDetach();
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable dr) {
        if (onMNImageViewListener != null) {
            if (onMNImageViewListener.verifyDrawable(dr)) {
                return true;
            }
        }
        return super.verifyDrawable(dr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (onMNImageViewListener != null) {
            onMNImageViewListener.onDraw(canvas);
        }

        if (shadeColor != -1) {
            canvas.drawColor(shadeColor);
        }

        if (setRoundShade) {
            roundRect.set(0, 0, getWidth(), getHeight());
            canvas.drawRoundRect(roundRect, dip2px(context, 8), dip2px(context, 8), shadePaint);
        }

        if (isMultiImg) {
            shadePaint.setColor(Color.BLACK);
            roundRect.set(0, (getHeight() - dip2px(context, 16)), dip2px(context, 32), getHeight());
            canvas.drawRoundRect(roundRect, dip2px(context, 8), dip2px(context, 8), shadePaint);
            canvas.drawRect(0, (getHeight() - dip2px(context, 16)), dip2px(context, 8), (getHeight() - dip2px(context, 16) / 2), shadePaint);
            canvas.drawRect(dip2px(context, 24), (getHeight() - dip2px(context, 16) / 2), dip2px(context, 32), getHeight(), shadePaint);
        }

        //如果是取消状态
        if (hasCancel) {
            if (isCancel) {
                //如果选中
                strokeStatusPaint.setColor(Color.parseColor("#b5b5b5"));
                canvas.drawCircle(getWidth() - dp26 / 2 - dp3, dp26 / 2 + dp3, dp26 / 2, strokeStatusPaint);

                statusPaint.setColor(Color.parseColor("#7f29292d"));
                canvas.drawCircle(getWidth() - dp26 / 2 - dp3, dp26 / 2 + dp3, dp26 / 2 - 1, statusPaint);

                canvas.drawLine(getWidth() - dp26 + dp26 / 3 - dp3, dp26 - dp26 / 3 + dp3,
                        getWidth() - dp26 / 3 - dp3, dp26 / 3 + dp3, cancelPaint);
                canvas.drawLine(getWidth() - dp26 + dp26 / 3 - dp3, dp26 / 3 + dp3,
                        getWidth() - dp26 / 3 - dp3, dp26 - dp26 / 3 + dp3, cancelPaint);
            } else {
                //如果取消
                strokeStatusPaint.setColor(Color.parseColor("#b5b5b5"));
                canvas.drawCircle(getWidth() - dp26 / 2 - dp3, dp26 / 2 + dp3, dp26 / 2, strokeStatusPaint);

                statusPaint.setColor(Color.parseColor("#7f29292d"));
                canvas.drawCircle(getWidth() - dp26 / 2 - dp3, dp26 / 2 + dp3, dp26 / 2 - 1, statusPaint);
            }
        }

        //如果是选中状态
        if (hasConfirm) {
            if (isCancel) {
                //如果选中
                statusPaint.setColor(Color.parseColor("#054FA0"));
                canvas.drawCircle(getWidth() - dp26 / 2 - dp3, getHeight() - dp26 / 2 - dp3, dp26 / 2, statusPaint);

                canvas.drawLine(getWidth() - dp26 + dp26 / 4 - dp3, getHeight() - dp26 + dp26 / 2 - dp3,
                        getWidth() - dp26 + dp26 / 4 + dp26 / 6 + storkeWidth - 2 - dp3, getHeight() - dp26 + dp26 * 3 / 4 - dp3, confirmPaint);
                canvas.drawLine(getWidth() - dp26 + dp26 / 4 + dp26 / 6 - dp3, getHeight() - dp26 + dp26 * 3 / 4 - dp3,
                        getWidth() - dp26 + dp26 * 3 / 4 - dp3, getHeight() - dp26 + dp26 / 4 - dp3, confirmPaint);
            } else {
                //如果取消
                strokeStatusPaint.setColor(Color.parseColor("#d8d8d8"));
                canvas.drawCircle(getWidth() - dp26 / 2 - dp3, getHeight() - dp26 / 2 - dp3, dp26 / 2, strokeStatusPaint);

                statusPaint.setColor(Color.parseColor("#b2d8d8d8"));
                canvas.drawCircle(getWidth() - dp26 / 2 - dp3, getHeight() - dp26 / 2 - dp3, dp26 / 2 - 1, statusPaint);
            }
        }

        //画文字状态
        if (hintText != null) {
            StaticLayout currentLayout = new StaticLayout(hintText, textPaint, getWidth(), Layout.Alignment.ALIGN_CENTER, 1.2f, 0.0f, false);
            canvas.translate(0, (getHeight() - currentLayout.getHeight()) / 2);
            currentLayout.draw(canvas);
        }

        if (isMultiImg) {
            textPaint.setTextSize(dip2px(context, 10));
            StaticLayout firstImgLayout = new StaticLayout("首图", textPaint, dip2px(context, 32), Layout.Alignment.ALIGN_CENTER, 1f, 0.0f, false);
            float diffValue = (dip2px(context, 16) - firstImgLayout.getHeight()) / 2;
            canvas.translate(0, (getHeight() - dip2px(context, 16) + diffValue));
            firstImgLayout.draw(canvas);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    long downTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "点击事件：" + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (canOperate) {
                    if (hasConfirm) {
                        if (event.getX() < getWidth() && event.getX() > getWidth() - dp26 * 2
                                && event.getY() < getHeight() && event.getY() > getHeight() - dp26 * 2) {
                            if (onOperateListener != null) {
                                Log.e(TAG, "用户按下时间：" + System.currentTimeMillis());
                                downTime = System.currentTimeMillis();
                                return true;
                            }
                        }
                    }
                    if (hasCancel) {
                        if (event.getX() < getWidth() && event.getX() > getWidth() - dp26 * 2
                                && event.getY() > 0 && event.getY() < dp26 * 2) {
                            if (onOperateListener != null) {
                                Log.e(TAG, "用户按下时间：" + System.currentTimeMillis());
                                downTime = System.currentTimeMillis();
                                return true;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "用户抬起时间：" + System.currentTimeMillis());
                if (downTime != 0) {
                    if (onOperateListener != null) {
                        onOperateListener.OnStatusListener();
                    }
                    downTime = 0;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                downTime = 0;
                break;

        }
        return super.onTouchEvent(event);
    }

    int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    int getTextHeight(Paint paint, String str) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.height();
    }

}

package com.xiaocoder.fingerview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


/**
 * @author xiaocoder
 * @email fengjingyu@foxmail.com
 * @description 可以跟着手指移动的view
 */
public class XCFingerView extends RelativeLayout {

    public static final String TAG = "XCMoveView";
    /**
     * down的那一刻，点击的那个点相对屏幕的绝对坐标
     */
    float clickDownX;
    float clickDownY;
    /**
     * fingerView在parentView中的位置
     */
    int viewDownX;
    int viewDownY;
    /**
     * fingerView的parentView的宽度 高度
     */
    int parentWidth;
    int parentHeight;
    /**
     * fingerView的高度 宽度
     */
    int viewWidth;
    int viewHeight;
    /**
     * 允许移除边界多少
     */
    public static final int ALLOW_OUT_OF_BOUNDRY_DISTANCE = 0;
    /**
     * 用于记录值的
     */
    Rect rect = new Rect();

    private GestureDetector mDetector;

    private OnClickListenerPlus mListener;

    public interface OnClickListenerPlus {
        void onClickListenerPlus(View view);
    }

    public void setOnClickListenerPlus(OnClickListenerPlus listener) {
        mListener = listener;
    }

    public XCFingerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDetector = new GestureDetector(getContext(), mGestureListener);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (viewWidth == 0 || viewHeight == 0 || parentWidth == 0 || parentHeight == 0) {
            viewWidth = getWidth();
            viewHeight = getHeight();

            parentWidth = ((ViewGroup) getParent()).getWidth();
            parentHeight = ((ViewGroup) getParent()).getHeight();
        }
    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mListener != null) {
                mListener.onClickListenerPlus(XCFingerView.this);
            }
            //XCLog.dShortToast("单击click");
            return false;
        }

    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 相对于屏幕，含statusbar高度
                clickDownX = event.getRawX();
                clickDownY = event.getRawY();
                viewDownX = getLeft();
                viewDownY = getTop();
                //XCLog.i("down---viewRawX = " + viewRawX + "---viewRawY = " + viewRawY);
                //XCLog.i("down---clickDownX = " + clickDownX + "---clickDownY = " + clickDownY);
                break;
            case MotionEvent.ACTION_MOVE:
                float tempX = event.getRawX();
                float tempY = event.getRawY();
                // xy轴的偏移量
                float offsetX = tempX - clickDownX;
                float offsetY = tempY - clickDownY;

                rect.left = (int) (viewDownX + offsetX);
                rect.top = (int) (viewDownY + offsetY);
                rect.right = rect.left + viewWidth;
                rect.bottom = rect.top + viewHeight;
                layout(rect.left, rect.top, rect.right, rect.bottom);

                //XCLog.i("move---offsetX = " + offsetX + "---offsetY = " + offsetY);
                //XCLog.i("move---rect.left = " + rect.left + "---rect.top = " + rect.top + "---rect.right" + rect.right + "---rect.bottom" + rect.bottom);
                break;
            case MotionEvent.ACTION_UP:
                checkBoundry();
                break;
            default:
                break;
        }

        return true;
    }

    private void checkBoundry() {

        if (rect.top == 0 && rect.left == 0 && rect.right == 0 && rect.bottom == 0) {
            // 刚进界面，点击该view不移动，会跳到屏幕的00坐标
            return;
        }

        int maxWidth = parentWidth + ALLOW_OUT_OF_BOUNDRY_DISTANCE;
        int maxHeight = parentHeight + ALLOW_OUT_OF_BOUNDRY_DISTANCE;

        if (getLeft() < -ALLOW_OUT_OF_BOUNDRY_DISTANCE) {
            // 移出了左边界 -ALLOW_OUT_OF_BOUNDRY_DISTANCE
            rect.set(0, rect.top, viewWidth, rect.bottom);
        }

        if (getRight() > maxWidth) {
            // 移出了右边界 ALLOW_OUT_OF_BOUNDRY_DISTANCE
            rect.set(parentWidth - viewWidth, rect.top, parentWidth, rect.bottom);
        }

        if (getTop() < -ALLOW_OUT_OF_BOUNDRY_DISTANCE) {
            // 移出了上边界 -
            rect.set(rect.left, 0, rect.right, viewHeight);
        }

        if (getBottom() > maxHeight) {
            // 移除了下边界
            rect.set(rect.left, parentHeight - viewHeight, rect.right, parentHeight);
        }

        layout(rect.left, rect.top, rect.right, rect.bottom);
    }

}
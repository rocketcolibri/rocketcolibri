/**
 * Rocket Colibri © 2014
 */

/*
 * This is a modified version of a class from the Android Open Source Project. 
 * The original copyright and license information follows.
 * 
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.hsr.rocketcolibri.view.draggable;

import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
//import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.View;
//import android.view.WindowManager;

/**
 * A DragView is a special view used by a DragController. During a drag operation, what is actually moving
 * on the screen is a DragView. A DragView is constructed using a bitmap of the view the user really
 * wants to move.
 *
 * @author Artan Veliju
 */

public class DragView extends View 
{
    // Number of pixels to add to the dragged item for scaling.  Should be even for pixel alignment.
    private static final int DRAG_SCALE = 0;   // In Launcher, value is 40

    private Bitmap mBitmap;
    private Paint mPaint;
    private Paint mBgPaint;
    private int mRegistrationX;
    private int mRegistrationY;

    private float mAnimationScale = 1.0f;
    private AbsoluteLayout mWindowManager;
    private LayoutParams mLayoutParams;
//    private WindowManager.LayoutParams mLayoutParams;
//    private WindowManager mWindowManager;

    /**
     * Construct the drag view.
     * <p>
     * The registration point is the point inside our view that the touch events should
     * be centered upon. </p>
     *
     * @param context A context
     * @param bitmap The view that we're dragging around.  We scale it up when we draw it.
     * @param registrationX The x coordinate of the registration point.
     * @param registrationY The y coordinate of the registration point.
     */
    public DragView(Context context, AbsoluteLayout parent, Bitmap bitmap, int registrationX, int registrationY,
            int left, int top, int width, int height) {
        super(context);

//        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);        
        mWindowManager = parent;
        
        Matrix scale = new Matrix();
        float scaleFactor = width;
        scaleFactor = (scaleFactor + DRAG_SCALE) / scaleFactor;
        scale.setScale(scaleFactor, scaleFactor);
        mBitmap = Bitmap.createBitmap(bitmap, left, top, width, height, scale, true);

        // The point in our scaled bitmap that the touch events are located
        mRegistrationX = registrationX + (DRAG_SCALE / 2);
        mRegistrationY = registrationY + (DRAG_SCALE / 2);
        
        mLayoutParams = new LayoutParams(width, height, mRegistrationX, mRegistrationY);
        
        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBgPaint.setColor(0x88dd0011);
        mBgPaint.setAlpha(50);
//        RocketColibri.setDefaultViewSettings(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBgPaint);
        
        float scale = mAnimationScale;
        if (scale < 0.999f) { // allow for some float error
            float width = mBitmap.getWidth();
            float offset = (width-(width*scale))/2;
            canvas.translate(offset, offset);
            canvas.scale(scale, scale);
        }
        canvas.drawBitmap(mBitmap, 0f, 0f, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBitmap.recycle();
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
        invalidate();
    }

    /**
     * Create a window containing this view and show it.
     *
     * @param windowToken obtained from v.getWindowToken() from one of your views
     * @param touchX the x coordinate the user touched in screen coordinates
     * @param touchY the y coordinate the user touched in screen coordinates
     */
    public void show(IBinder windowToken, int touchX, int touchY) {
//        WindowManager.LayoutParams lp;
//        int pixelFormat;
//
//        pixelFormat = PixelFormat.TRANSLUCENT;

//        lp = new WindowManager.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                touchX-mRegistrationX, touchY-mRegistrationY,
//                WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//                    /*| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM*/,
//                pixelFormat);
//        lp.token = mStatusBarView.getWindowToken();
//        lp.gravity = Gravity.LEFT | Gravity.TOP;
//        lp.token = windowToken;
//        lp.setTitle("DragView");
//        mLayoutParams = lp;
        mLayoutParams.x = touchX-mRegistrationX;
        mLayoutParams.y = touchY-mRegistrationY;
        mWindowManager.addView(this, mLayoutParams);

    }
    
    /**
     * <p>Move the window containing this view.</p>
     *
     * @param touchX the x coordinate the user touched in screen coordinates
     * @param touchY the y coordinate the user touched in screen coordinates
     */
    void move(int touchX, int touchY) {
        // This is what was done in the Launcher code.
//        WindowManager.LayoutParams lp = mLayoutParams;

    	mLayoutParams.x = calculateXPosition(touchX - mRegistrationX);
    	mLayoutParams.y = calculateYPosition(touchY - mRegistrationY);

    	mWindowManager.updateViewLayout(this, mLayoutParams);
    }

    /**
     * <p>The widgets must stay in visible area on the screen,
     * this method calculates the x position within range</p>
     * 
     * @param actualXPosition
     * @return
     */
    public int calculateXPosition(int actualXPosition) {
    	int maxWidth = mWindowManager.getWidth();
        int newXPosition = actualXPosition;

    	if (newXPosition + mLayoutParams.width > maxWidth) {
    		newXPosition = maxWidth - mLayoutParams.width;
    	}

    	if (newXPosition < 0) {
    		newXPosition = 0;
    	}

    	return newXPosition;
    }

    /**
     * <p>The widgets must stay in visible area on the screen,
     * this method calculates the y position within range</p>
     * 
     * @param actualXPosition
     * @return
     */
    public int calculateYPosition(int actualYPosition) {
    	int maxHeight = mWindowManager.getHeight();
        int newYPosition = actualYPosition;

    	if (newYPosition + mLayoutParams.height > maxHeight) {
    		newYPosition = maxHeight - mLayoutParams.height;
    	}

    	if (newYPosition < 0) {
    		newYPosition = 0;
    	}

    	return newYPosition;
    }

    void remove() {
        mWindowManager.removeView(this);
    }
}
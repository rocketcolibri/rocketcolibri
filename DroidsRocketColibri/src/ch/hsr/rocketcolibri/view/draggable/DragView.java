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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.IBinder;
import android.view.View;

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
    private final int DRAG_SCALE = 0;   // In Launcher, value is 40
    private HelplineDrawer tHelplineDrawer;
    private Bitmap tBitmap;
    private Paint mPaint;
    private int tRegistrationX;
    private int tRegistrationY;
    private int[] tTouchXY = new int[2];

    private float mAnimationScale = 1.0f;
    private AbsoluteLayout tParent;
    private LayoutParams tParentLP;
    private LayoutParams tLayoutParams;

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
        tParent = parent;
        tParentLP = (LayoutParams) parent.getLayoutParams();
        tParentLP = new LayoutParams(tParent.getWidth(), tParent.getHeight(), tParentLP.x, tParentLP.y);
        Matrix scale = new Matrix();
        float scaleFactor = width;
        scaleFactor = (scaleFactor + DRAG_SCALE) / scaleFactor;
        scale.setScale(scaleFactor, scaleFactor);
        tBitmap = Bitmap.createBitmap(bitmap, left, top, width, height, scale, true);

        // The point in our scaled bitmap that the touch events are located
        tRegistrationX = registrationX + (DRAG_SCALE / 2);
        tRegistrationY = registrationY + (DRAG_SCALE / 2);
        
        tLayoutParams = new LayoutParams(width, height, tRegistrationX, tRegistrationY);
        setLayoutParams(tLayoutParams);
        tHelplineDrawer = new HelplineDrawer(context, parent, this, 50, 1, 18, Color.CYAN, Color.YELLOW);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(tBitmap.getWidth(), tBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw background
//        canvas.drawRect(0, 0, getWidth(), getHeight(), mBgPaint);
        
        float scale = mAnimationScale;
        if (scale < 0.999f) { // allow for some float error
            float width = tBitmap.getWidth();
            float offset = (width-(width*scale))/2;
            canvas.translate(offset, offset);
            canvas.scale(scale, scale);
        }
        canvas.drawBitmap(tBitmap, 0f, 0f, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        tBitmap.recycle();
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
        tLayoutParams.x = touchX-tRegistrationX;
        tLayoutParams.y = touchY-tRegistrationY;
        tParent.addView(this, tLayoutParams);
        tHelplineDrawer.addLinesTo(tParent);
    }
    
    /**
     * <p>Move the window containing this view.</p>
     *
     * @param touchX the x coordinate the user touched in screen coordinates
     * @param touchY the y coordinate the user touched in screen coordinates
     */
    void move(int touchX, int touchY) {
    	tTouchXY[0]=touchX - tRegistrationX;tTouchXY[1]=touchY - tRegistrationY;
    	makeSureXYAreOnVisibleArea();
    	tHelplineDrawer.drawAndFillStickyPosition(tTouchXY);
    	tLayoutParams.x = tTouchXY[0];tLayoutParams.y = tTouchXY[1];
    	tParent.updateViewLayout(this, tLayoutParams);
    }

    /**
     * <p>The widgets must stay in visible area on the screen,
     * this method calculates the x and y position within range</p>
     */
    public void makeSureXYAreOnVisibleArea() {
    	if (tTouchXY[0] + tLayoutParams.width > tParentLP.width) {
    		tTouchXY[0] = tParentLP.width - tLayoutParams.width;
    	}else if (tTouchXY[0] < 0) {
    		tTouchXY[0] = 0;
    	}
    	if (tTouchXY[1] + tLayoutParams.height > tParentLP.height) {
    		tTouchXY[1] = tParentLP.height - tLayoutParams.height;
    	}else if (tTouchXY[1] < 0) {
    		tTouchXY[1] = 0;
    	}
    }
    
    void remove() {
    	tHelplineDrawer.removeFrom(tParent);
        tParent.removeView(this);
    }
}
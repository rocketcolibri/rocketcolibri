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
//import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
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
    private static final int HELPLINE_OFFSET = 20;
    private View topHorizontalLine;
    private View leftVerticalLine;
    private View bottomHorizontalLine;
    private View rightVerticalLine;
    private int[] tXOfViewsInParent;
    private int[] tYOfViewsInParent;
    private boolean loopSuccess;

    private Bitmap tBitmap;
    private Paint mPaint;
//    private Paint mBgPaint;
    private int tRegistrationX;
    private int tRegistrationY;

    private float mAnimationScale = 1.0f;
    private AbsoluteLayout tWindowManager;
    private LayoutParams tLayoutParams;
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
        tWindowManager = parent;
        tXOfViewsInParent = new int[tWindowManager.getChildCount()*2];
        tYOfViewsInParent = new int[tWindowManager.getChildCount()*2];
        View childView = null;
        for(int i = 0, j = 0; j < tWindowManager.getChildCount();++j,i+=2){
        	childView = tWindowManager.getChildAt(j);
        	tXOfViewsInParent[i] = (int) childView.getX();
        	tXOfViewsInParent[i+1] = (int) childView.getX()+childView.getWidth();
        	tYOfViewsInParent[i] = (int) childView.getY();
        	tYOfViewsInParent[i+1] = (int) childView.getY()+childView.getHeight();
        }
        
        topHorizontalLine = createLine(context, Color.RED, tWindowManager.getWidth(), 1);
        bottomHorizontalLine = createLine(context, Color.RED, tWindowManager.getWidth(), 1);
        leftVerticalLine = createLine(context, Color.RED, 1, tWindowManager.getWidth());
        rightVerticalLine = createLine(context, Color.RED, 1, tWindowManager.getWidth());
        
        
        Matrix scale = new Matrix();
        float scaleFactor = width;
        scaleFactor = (scaleFactor + DRAG_SCALE) / scaleFactor;
        scale.setScale(scaleFactor, scaleFactor);
        tBitmap = Bitmap.createBitmap(bitmap, left, top, width, height, scale, true);

        // The point in our scaled bitmap that the touch events are located
        tRegistrationX = registrationX + (DRAG_SCALE / 2);
        tRegistrationY = registrationY + (DRAG_SCALE / 2);
        
        tLayoutParams = new LayoutParams(width, height, tRegistrationX, tRegistrationY);
        
        //TODO do we need the red background? I think not...
//        mBgPaint = new Paint();
//        mBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        mBgPaint.setColor(0x88dd0011);
//        mBgPaint.setAlpha(50);
    }
    
    private View createLine(Context context, int color, int width, int height){
    	View line = new View(context);
        line.setBackgroundColor(color);
        line.setLayoutParams(new LayoutParams(width, height, 0, 0));
        line.setVisibility(View.INVISIBLE);
        return line;
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
        tWindowManager.addView(this, tLayoutParams);
        tWindowManager.addView(topHorizontalLine);
        tWindowManager.addView(leftVerticalLine);
        tWindowManager.addView(rightVerticalLine);
        tWindowManager.addView(bottomHorizontalLine);
    }
    
    /**
     * <p>Move the window containing this view.</p>
     *
     * @param touchX the x coordinate the user touched in screen coordinates
     * @param touchY the y coordinate the user touched in screen coordinates
     */
    void move(int touchX, int touchY) {
    	touchX = calculateXPosition(touchX - tRegistrationX);
    	touchY = calculateYPosition(touchY - tRegistrationY);
    	//left
    	loopSuccess = false;
    	for(int i = 0; i < tXOfViewsInParent.length; ++i){
    		if(touchX+HELPLINE_OFFSET>=tXOfViewsInParent[i] && touchX-HELPLINE_OFFSET<=tXOfViewsInParent[i]){
    			((AbsoluteLayout.LayoutParams)leftVerticalLine.getLayoutParams()).x = tXOfViewsInParent[i];
    			leftVerticalLine.setVisibility(View.VISIBLE);
    			tWindowManager.updateViewLayout(leftVerticalLine, leftVerticalLine.getLayoutParams());
    			touchX = tXOfViewsInParent[i];
    			loopSuccess = true;
    			break;
    		}
    	}
    	if(!loopSuccess)
    		leftVerticalLine.setVisibility(View.INVISIBLE);
    	
    	//top
    	loopSuccess = false;
    	for(int i = 0; i < tYOfViewsInParent.length; ++i){
    		if(touchY+HELPLINE_OFFSET>=tYOfViewsInParent[i] && touchY-HELPLINE_OFFSET<=tYOfViewsInParent[i]){
    			((AbsoluteLayout.LayoutParams)topHorizontalLine.getLayoutParams()).y = tYOfViewsInParent[i];
    			topHorizontalLine.setVisibility(View.VISIBLE);
    			tWindowManager.updateViewLayout(topHorizontalLine, topHorizontalLine.getLayoutParams());
    			touchY = tYOfViewsInParent[i];
    			loopSuccess = true;
    			break;
    		}
    	}
    	if(!loopSuccess)
    		topHorizontalLine.setVisibility(View.INVISIBLE);
    	
    	//right
    	loopSuccess = false;
    	for(int i = 0; i < tXOfViewsInParent.length; ++i){
    		if(touchX+getWidth()+HELPLINE_OFFSET>=tXOfViewsInParent[i] && touchX+getWidth()-HELPLINE_OFFSET<=tXOfViewsInParent[i]){
    			((AbsoluteLayout.LayoutParams)rightVerticalLine.getLayoutParams()).x = tXOfViewsInParent[i];
    			rightVerticalLine.setVisibility(View.VISIBLE);
    			tWindowManager.updateViewLayout(rightVerticalLine, rightVerticalLine.getLayoutParams());
    			touchX = tXOfViewsInParent[i]-getWidth();
    			loopSuccess = true;
    			break;
    		}
    	}
    	if(!loopSuccess)
    		rightVerticalLine.setVisibility(View.INVISIBLE);

    	//bottom
    	loopSuccess = false;
    	for(int i = 0; i < tYOfViewsInParent.length; ++i){
    		if(touchY+getHeight()+HELPLINE_OFFSET>=tYOfViewsInParent[i] && touchY+getHeight()-HELPLINE_OFFSET<=tYOfViewsInParent[i]){
    			((AbsoluteLayout.LayoutParams)bottomHorizontalLine.getLayoutParams()).y = tYOfViewsInParent[i];
    			bottomHorizontalLine.setVisibility(View.VISIBLE);
    			tWindowManager.updateViewLayout(bottomHorizontalLine, bottomHorizontalLine.getLayoutParams());
    			touchY = tYOfViewsInParent[i]-getHeight();
    			loopSuccess = true;
    			break;
    		}
    	}
    	if(!loopSuccess)
    		bottomHorizontalLine.setVisibility(View.INVISIBLE);
    	

    	tLayoutParams.x = touchX;
    	tLayoutParams.y = touchY;
    	tWindowManager.updateViewLayout(this, tLayoutParams);
    }

    /**
     * <p>The widgets must stay in visible area on the screen,
     * this method calculates the x position within range</p>
     * 
     * @param actualXPosition
     * @return
     */
    public int calculateXPosition(int actualXPosition) {
    	int maxWidth = tWindowManager.getWidth();
        int newXPosition = actualXPosition;

    	if (newXPosition + tLayoutParams.width > maxWidth) {
    		newXPosition = maxWidth - tLayoutParams.width;
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
    	int maxHeight = tWindowManager.getHeight();
        int newYPosition = actualYPosition;

    	if (newYPosition + tLayoutParams.height > maxHeight) {
    		newYPosition = maxHeight - tLayoutParams.height;
    	}

    	if (newYPosition < 0) {
    		newYPosition = 0;
    	}

    	return newYPosition;
    }

    void remove() {
    	tWindowManager.removeView(bottomHorizontalLine);
    	tWindowManager.removeView(rightVerticalLine);
    	tWindowManager.removeView(topHorizontalLine);
    	tWindowManager.removeView(leftVerticalLine);
        tWindowManager.removeView(this);
    }
}
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
//import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

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
    private final int HELPLINE_OFFSET = 15;
    private final int HELPLINE_WIDTH = 1;
    private final int HELPDISTANCELINE_WIDTH_AND_HEIGHT = 50;
    private HelplineDrawer tHl;
    private boolean horizontalDistanceTriggert;
    private boolean verticalDistanceTriggert;
    private int target = 0;
    private int i = 0;
    private Bitmap tBitmap;
    private Paint mPaint;
//    private Paint mBgPaint;
    private int tRegistrationX;
    private int tRegistrationY;

    private float mAnimationScale = 1.0f;
    private AbsoluteLayout tParent;
    private LayoutParams tParentLP;
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
        tParent = parent;
        tParentLP = (LayoutParams) parent.getLayoutParams();
        tParentLP = new LayoutParams(tParent.getWidth(), tParent.getHeight(), tParentLP.x, tParentLP.y);
        tHl = new HelplineDrawer(parent, tParentLP, HELPDISTANCELINE_WIDTH_AND_HEIGHT, HELPLINE_WIDTH, HELPLINE_OFFSET, Color.CYAN, Color.YELLOW);
        Matrix scale = new Matrix();
        float scaleFactor = width;
        scaleFactor = (scaleFactor + DRAG_SCALE) / scaleFactor;
        scale.setScale(scaleFactor, scaleFactor);
        tBitmap = Bitmap.createBitmap(bitmap, left, top, width, height, scale, true);

        // The point in our scaled bitmap that the touch events are located
        tRegistrationX = registrationX + (DRAG_SCALE / 2);
        tRegistrationY = registrationY + (DRAG_SCALE / 2);
        
        tLayoutParams = new LayoutParams(width, height, tRegistrationX, tRegistrationY);
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
        tHl.addLinesTo(tParent);
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
    	if(tHl.tLeftDistanceOfViewsInParent.length>0 && Math.abs(tHl.tLeftDistanceOfViewsInParent[i=findClosest(tHl.tLeftDistanceOfViewsInParent, target=touchX)]-target)<=HELPLINE_OFFSET){
    		horizontalDistanceTriggert = true;
    		AbsoluteLayout.LayoutParams lp = ((AbsoluteLayout.LayoutParams)tHl.horizontalDistanceLine.getLayoutParams());
    		lp.x = 0;lp.height=HELPDISTANCELINE_WIDTH_AND_HEIGHT;lp.y=(tLayoutParams.y+(tLayoutParams.height/2-lp.height/2));lp.width=tHl.tLeftDistanceOfViewsInParent[i];
    		tHl.horizontalDistanceLine.setVisibility(View.VISIBLE);
			touchX = tHl.tLeftDistanceOfViewsInParent[i];
    	}else{
    		horizontalDistanceTriggert = false;
    		tHl.horizontalDistanceLine.setVisibility(View.INVISIBLE);
			if(Math.abs(tHl.tXOfViewsInParent[i=findClosest(tHl.tXOfViewsInParent, target=touchX)]-target)<=HELPLINE_OFFSET){
				((AbsoluteLayout.LayoutParams)tHl.leftVerticalLine.getLayoutParams()).x = tHl.tXOfViewsInParent[i];
				tHl.leftVerticalLine.setVisibility(View.VISIBLE);
				touchX = tHl.tXOfViewsInParent[i];
	    	}else tHl.leftVerticalLine.setVisibility(View.INVISIBLE);
    	}
    	//top
    	if(tHl.tTopDistanceOfViewsInParent.length>0 && Math.abs(tHl.tTopDistanceOfViewsInParent[i=findClosest(tHl.tTopDistanceOfViewsInParent, target=touchY)]-target)<=HELPLINE_OFFSET){
    		verticalDistanceTriggert = true;
    		AbsoluteLayout.LayoutParams lp = ((AbsoluteLayout.LayoutParams)tHl.verticalDistanceLine.getLayoutParams());
    		lp.width=HELPDISTANCELINE_WIDTH_AND_HEIGHT;lp.x=tLayoutParams.x+(tLayoutParams.width/2-lp.width/2);lp.y=0;lp.height=tHl.tTopDistanceOfViewsInParent[i];
    		tHl.verticalDistanceLine.setVisibility(View.VISIBLE);
    		touchY = tHl.tTopDistanceOfViewsInParent[i];
    	}else{
    		verticalDistanceTriggert = false;
    		tHl.verticalDistanceLine.setVisibility(View.INVISIBLE);
    		if(Math.abs(tHl.tYOfViewsInParent[i=findClosest(tHl.tYOfViewsInParent, target=touchY)]-target)<=HELPLINE_OFFSET){
    			((AbsoluteLayout.LayoutParams)tHl.topHorizontalLine.getLayoutParams()).y = tHl.tYOfViewsInParent[i];
    			tHl.topHorizontalLine.setVisibility(View.VISIBLE);
    			touchY = tHl.tYOfViewsInParent[i];
        	}else tHl.topHorizontalLine.setVisibility(View.INVISIBLE);
    	}
    	//right
    	if(!horizontalDistanceTriggert && tHl.tRightDistanceOfViewsInParent.length>0 && Math.abs(tHl.tRightDistanceOfViewsInParent[i=findClosest(tHl.tRightDistanceOfViewsInParent, target=touchX+tLayoutParams.width)]-target)<=HELPLINE_OFFSET){
    		AbsoluteLayout.LayoutParams lp = ((AbsoluteLayout.LayoutParams)tHl.horizontalDistanceLine.getLayoutParams());
    		lp.height=HELPDISTANCELINE_WIDTH_AND_HEIGHT;lp.x = tHl.tRightDistanceOfViewsInParent[i];lp.y=(tLayoutParams.y+(tLayoutParams.height/2-lp.height/2));lp.width=tParentLP.width-tHl.tRightDistanceOfViewsInParent[i];
    		tHl.horizontalDistanceLine.setVisibility(View.VISIBLE);
    		touchX = tHl.tRightDistanceOfViewsInParent[i]-tLayoutParams.width;
    	}else{
			if(Math.abs(tHl.tXOfViewsInParent[i=findClosest(tHl.tXOfViewsInParent, target=touchX+tLayoutParams.width)]-target)<=HELPLINE_OFFSET){
				((AbsoluteLayout.LayoutParams)tHl.rightVerticalLine.getLayoutParams()).x = tHl.tXOfViewsInParent[i];
				tHl.rightVerticalLine.setVisibility(View.VISIBLE);
				touchX = tHl.tXOfViewsInParent[i]-tLayoutParams.width;
			}else tHl.rightVerticalLine.setVisibility(View.INVISIBLE);
    	}
    	//bottom
    	if(!verticalDistanceTriggert && tHl.tBottomDistanceOfViewsInParent.length>0 && Math.abs(tHl.tBottomDistanceOfViewsInParent[i=findClosest(tHl.tBottomDistanceOfViewsInParent, target=touchY+tLayoutParams.height)]-target)<=HELPLINE_OFFSET){
    		AbsoluteLayout.LayoutParams lp = ((AbsoluteLayout.LayoutParams)tHl.verticalDistanceLine.getLayoutParams());
    		lp.width=HELPDISTANCELINE_WIDTH_AND_HEIGHT;lp.x=(tLayoutParams.x+(tLayoutParams.width/2-lp.width/2));lp.height=tParentLP.height-tHl.tBottomDistanceOfViewsInParent[i];lp.y=tHl.tBottomDistanceOfViewsInParent[i];
    		tHl.verticalDistanceLine.setVisibility(View.VISIBLE);
    		touchY = tHl.tBottomDistanceOfViewsInParent[i]-tLayoutParams.height;
    	}else{
			if(Math.abs(tHl.tYOfViewsInParent[i=findClosest(tHl.tYOfViewsInParent, target=touchY+tLayoutParams.height)]-target)<=HELPLINE_OFFSET){
				((AbsoluteLayout.LayoutParams)tHl.bottomHorizontalLine.getLayoutParams()).y = tHl.tYOfViewsInParent[i];
				tHl.bottomHorizontalLine.setVisibility(View.VISIBLE);
				touchY = tHl.tYOfViewsInParent[i]-tLayoutParams.height;
			}else tHl.bottomHorizontalLine.setVisibility(View.INVISIBLE);
    	}

    	tLayoutParams.x = touchX;
    	tLayoutParams.y = touchY;
    	tParent.updateViewLayout(this, tLayoutParams);
    }

    /**
     * <p>The widgets must stay in visible area on the screen,
     * this method calculates the x position within range</p>
     * 
     * @param actualXPosition
     * @return
     */
    public int calculateXPosition(int actualXPosition) {
    	int maxWidth = tParentLP.width;
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
    	int maxHeight = tParentLP.height;
        int newYPosition = actualYPosition;

    	if (newYPosition + tLayoutParams.height > maxHeight) {
    		newYPosition = maxHeight - tLayoutParams.height;
    	}

    	if (newYPosition < 0) {
    		newYPosition = 0;
    	}

    	return newYPosition;
    }
    
	private int findClosest(int[] a, int key) {
		if (a.length == 0)
			return 0;
		int minIndex = 0;
		int maxIndex = a.length - 1;
		int curIn = (maxIndex + minIndex) / 2;
		while (true) {
			if (a[curIn] == key) {
				return curIn;
			} else if (a[curIn]<key) {
				minIndex = curIn + 1; // its in the upper
				if (minIndex > maxIndex){
					if(minIndex==a.length)return --minIndex;
					if((Math.abs(a[minIndex]-key)) < Math.abs(a[maxIndex]-key)){
						return minIndex;
					}else{
						return curIn + 1;
					}
				}else{
					curIn = minIndex;
				}
			} else {
				maxIndex = curIn - 1; // its in the lower
				if (minIndex > maxIndex){
					if(maxIndex<0)return 0;
					if(Math.abs(a[maxIndex]-key)<(Math.abs(a[minIndex]-key))){
						return maxIndex;
					}else
						return curIn;
				}else{
					curIn = maxIndex;
				}
			}
		}
	}

    void remove() {
    	tHl.removeFrom(tParent);
        tParent.removeView(this);
    }
    
    private int[] listToArray(List<Integer> list){
    	int[] a = new int[list.size()];
    	for(int i = 0; i < list.size();++i){
    		a[i] = list.get(i).intValue();
    	}
    	return a;
    }
    
    private class HelplineDrawer{
        private View topHorizontalLine;
        private View leftVerticalLine;
        private View bottomHorizontalLine;
        private View rightVerticalLine;
        private View horizontalDistanceLine;
        private View verticalDistanceLine;
        private int[] tXOfViewsInParent;
        private int[] tYOfViewsInParent;
        private int[] tLeftDistanceOfViewsInParent;
        private int[] tTopDistanceOfViewsInParent;
        private int[] tRightDistanceOfViewsInParent;
        private int[] tBottomDistanceOfViewsInParent;
        
    	public HelplineDrawer(ViewGroup parent, AbsoluteLayout.LayoutParams tParentLP, int distanceWidth, int lineWidth, int offset, int lineColor, int distanceColor) {
            tXOfViewsInParent = new int[parent.getChildCount()*2];
            tYOfViewsInParent = new int[parent.getChildCount()*2];
            List<Integer> leftDistanceOfViewsInParent = new ArrayList<Integer>();
            List<Integer> rightDistanceOfViewsInParent = new ArrayList<Integer>();
            List<Integer> topDistanceOfViewsInParent = new ArrayList<Integer>();
            List<Integer> bottomDistanceOfViewsInParent = new ArrayList<Integer>();
            Integer distanceTmp = null;
            AbsoluteLayout.LayoutParams childViewLp = null;
            for(int i = 0, j = 0; j < parent.getChildCount();++j,i+=2){
            	childViewLp = (LayoutParams) parent.getChildAt(j).getLayoutParams();
            	tXOfViewsInParent[i] = childViewLp.x;
            	if(tXOfViewsInParent[i]>tParentLP.x+offset){
            		distanceTmp = Integer.valueOf(tParentLP.width-tXOfViewsInParent[i]);
            		if(tParentLP.width/2>distanceTmp.intValue()){
            			leftDistanceOfViewsInParent.add(distanceTmp);
            		}else{rightDistanceOfViewsInParent.add(distanceTmp);}
            	}
            	tXOfViewsInParent[i+1] = childViewLp.x+childViewLp.width;
            	if(tXOfViewsInParent[i+1]<tParentLP.width-offset){
             		distanceTmp = Integer.valueOf(tParentLP.width-tXOfViewsInParent[i+1]);
             		if(tParentLP.width/2>distanceTmp.intValue()){
             			leftDistanceOfViewsInParent.add(distanceTmp);
             		}else{rightDistanceOfViewsInParent.add(distanceTmp);}
            	}
            	tYOfViewsInParent[i] = childViewLp.y;
            	if(tYOfViewsInParent[i]>tParentLP.y+offset){
             		distanceTmp = Integer.valueOf(tParentLP.height-tYOfViewsInParent[i]);
             		if(tParentLP.height/2>distanceTmp.intValue()){
             			topDistanceOfViewsInParent.add(distanceTmp);
             		}else{bottomDistanceOfViewsInParent.add(distanceTmp);}

            	}
            	tYOfViewsInParent[i+1] = childViewLp.y+childViewLp.height;
            	if(tYOfViewsInParent[i+1]<tParentLP.height-offset)
             		distanceTmp = Integer.valueOf(tParentLP.height-tYOfViewsInParent[i+1]);
             		if(tParentLP.height/2>distanceTmp.intValue()){
             			topDistanceOfViewsInParent.add(distanceTmp);
             		}else{bottomDistanceOfViewsInParent.add(distanceTmp);}
            }
            tLeftDistanceOfViewsInParent = listToArray(leftDistanceOfViewsInParent);
            tRightDistanceOfViewsInParent = listToArray(rightDistanceOfViewsInParent);
            tTopDistanceOfViewsInParent = listToArray(topDistanceOfViewsInParent);
            tBottomDistanceOfViewsInParent = listToArray(bottomDistanceOfViewsInParent);
            Arrays.sort(tLeftDistanceOfViewsInParent);
            Arrays.sort(tTopDistanceOfViewsInParent);
            Arrays.sort(tRightDistanceOfViewsInParent);
            Arrays.sort(tBottomDistanceOfViewsInParent);
            Arrays.sort(tXOfViewsInParent);
            Arrays.sort(tYOfViewsInParent);
            topHorizontalLine = createLine(tParentLP.width, lineWidth, lineColor);
            bottomHorizontalLine = createLine(tParentLP.width, lineWidth, lineColor);
            leftVerticalLine = createLine(lineWidth, tParentLP.height, lineColor);
            rightVerticalLine = createLine(lineWidth, tParentLP.height, lineColor);
            
            horizontalDistanceLine = createDistanceLine(false, distanceWidth, distanceWidth, lineWidth, distanceColor);
            verticalDistanceLine = createDistanceLine(true, distanceWidth, distanceWidth, lineWidth, distanceColor);
		}
    	
        private View createDistanceLine(boolean vertical, int width, int height, int lineWidth, int color){
        	FrameLayout fl = new FrameLayout(getContext());
        	fl.setLayoutParams(new LayoutParams(width, height, 0, 0));
        	fl.setVisibility(View.INVISIBLE);
        	if(vertical){
    	    	fl.addView(createGravityLine(-1, lineWidth, Gravity.TOP, color));
    	    	fl.addView(createGravityLine(-1, lineWidth, Gravity.BOTTOM, color));
    	    	fl.addView(createGravityLine(lineWidth, -1, Gravity.CENTER_HORIZONTAL, color));
        	}else{
    	    	fl.addView(createGravityLine(lineWidth, -1, Gravity.LEFT, color));
    	    	fl.addView(createGravityLine(lineWidth, -1, Gravity.RIGHT, color));
    	    	fl.addView(createGravityLine(-1, lineWidth, Gravity.CENTER_VERTICAL, color));
        	}
        	return fl;
        }
        
        private View createGravityLine(int width, int height, int gravity, int color){
        	View line = new View(getContext());
            line.setBackgroundColor(color);
            line.setLayoutParams(new FrameLayout.LayoutParams(width<1?FrameLayout.LayoutParams.MATCH_PARENT:width,
            		height<1?FrameLayout.LayoutParams.MATCH_PARENT:height, gravity));
            return line;
        }

        private View createLine(int width, int height, int color){
        	View line = new View(getContext());
            line.setBackgroundColor(color);
            line.setLayoutParams(new LayoutParams(width, height, 0, 0));
            line.setVisibility(View.INVISIBLE);
            return line;
        }
    	
    	public void addLinesTo(ViewGroup parent) {
            parent.addView(topHorizontalLine);
            parent.addView(leftVerticalLine);
            parent.addView(rightVerticalLine);
            parent.addView(bottomHorizontalLine);
            parent.addView(horizontalDistanceLine);
            parent.addView(verticalDistanceLine);			
		}

		public void removeFrom(ViewGroup parent){
    		parent.removeView(bottomHorizontalLine);
    		parent.removeView(rightVerticalLine);
    		parent.removeView(topHorizontalLine);
    		parent.removeView(leftVerticalLine);
    		parent.removeView(verticalDistanceLine);
    		parent.removeView(horizontalDistanceLine);
    	}
    }
}
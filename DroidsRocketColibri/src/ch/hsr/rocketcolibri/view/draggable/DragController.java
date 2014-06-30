/**
 * Rocket Colibri © 2014
 */
/*
 * This is a modified version of a class from the Android
 * Open Source Project. The original copyright and license information follows.
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import ch.hsr.rocketcolibri.view.AbsoluteLayout;

/**
 * This class is used to initiate a drag within a view or across multiple views.
 * When a drag starts it creates a special view (a DragView) that moves around the screen
 * until the user ends the drag. As feedback to the user, this object causes the device to
 * vibrate as the drag begins.
 * 
 * @author Artan Veliju
 */

public class DragController {
    private static final String TAG = "DragController";

    /** Indicates the drag is a move.  */
    public static int DRAG_ACTION_MOVE = 0;

    /** Indicates the drag is a copy.  */
    public static int DRAG_ACTION_COPY = 1;

    private static final int VIBRATE_DURATION = 35;

    private static final boolean PROFILE_DRAWING_DURING_DRAG = false;

    private Context tContext;
    private Vibrator tVibrator;

    private final int[] tCoordinatesTemp = new int[2];

    /** Whether or not we're dragging. */
    private boolean tDragging;

    /** X coordinate of the down event. */
    private float tMotionDownX;

    /** Y coordinate of the down event. */
    private float tMotionDownY;

    /** Info about the screen for clamping. */
    private DisplayMetrics tDisplayMetrics = new DisplayMetrics();

    /** Original view that is being dragged.  */
    private View tOriginator;
    private int tOriginIndex;
    /** X offset from the upper-left corner of the cell to where we touched.  */
    private float tTouchOffsetX;

    /** Y offset from the upper-left corner of the cell to where we touched.  */
    private float tTouchOffsetY;

    /** Where the drag originated */
    private IDragSource tDragSource;
    private Object tDragInfo;
    private DragView tDragView;
    private DragLayer tDropTarget;
    private IDragListener tListener;

    /** The window token used as the parent for the DragView. */
    private IBinder tWindowToken;

    private View tMoveTarget;

    private InputMethodManager tInputMethodManager;

    /**
     * Used to create a new DragLayer from XML.
     *
     * @param context The application's context.
     */
    public DragController(Context context) {
        tContext = context;
        tVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        recordScreenSize();

    }

    /**
     * Starts a drag. 
     * It creates a bitmap of the view being dragged. That bitmap is what you see moving.
     * The actual view can be repositioned if that is what the onDrop handle chooses to do.
     * 
     * @param v The view that is being dragged
     * @param source An object representing where the drag originated
     * @param dragInfo The data associated with the object that is being dragged
     * @param dragAction The drag action: either {@link #DRAG_ACTION_MOVE} or
     *        {@link #DRAG_ACTION_COPY}
     */
    public void startDrag(View v, IDragSource source, Object dragInfo, int dragAction) {
        // Start dragging, but only if the source has something to drag.
        boolean doDrag = source.allowDrag ();
        if (!doDrag) return;
        tOriginator = v;

        Bitmap b = getViewBitmap(v);

        if (b == null) {
            // out of memory?
            return;
        }

        v.getLocationOnScreen(tCoordinatesTemp);

        if (dragAction == DRAG_ACTION_MOVE) {
	      	tOriginIndex = tDropTarget.indexOfChild(tOriginator);
	      	tDropTarget.removeView(tOriginator);
        }
        
        startDrag(b, tCoordinatesTemp[0], tCoordinatesTemp[1], 0, 0, b.getWidth(), b.getHeight(),
                source, dragInfo, dragAction);

        b.recycle();

    }

    /**
     * Starts a drag.
     * 
     * @param b The bitmap to display as the drag image.  It will be re-scaled to the
     *          enlarged size.
     * @param screenX The x position on screen of the left-top of the bitmap.
     * @param screenY The y position on screen of the left-top of the bitmap.
     * @param textureLeft The left edge of the region inside b to use.
     * @param textureTop The top edge of the region inside b to use.
     * @param textureWidth The width of the region inside b to use.
     * @param textureHeight The height of the region inside b to use.
     * @param source An object representing where the drag originated
     * @param dragInfo The data associated with the object that is being dragged
     * @param dragAction The drag action: either {@link #DRAG_ACTION_MOVE} or
     *        {@link #DRAG_ACTION_COPY}
     */
    public void startDrag(Bitmap b, int screenX, int screenY,
            int textureLeft, int textureTop, int textureWidth, int textureHeight,
            IDragSource source, Object dragInfo, int dragAction) {
        if (PROFILE_DRAWING_DURING_DRAG) {
            android.os.Debug.startMethodTracing("Launcher");
        }

        // Hide soft keyboard, if visible
        if (tInputMethodManager == null) {
            tInputMethodManager = (InputMethodManager)
                    tContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        tInputMethodManager.hideSoftInputFromWindow(tWindowToken, 0);

        if (tListener != null) {
            tListener.onDragStart(source, dragInfo, dragAction);
        }

        int registrationX = ((int)tMotionDownX) - screenX;
        int registrationY = ((int)tMotionDownY) - screenY;

        tTouchOffsetX = tMotionDownX - screenX;
        tTouchOffsetY = tMotionDownY - screenY;

        tDragging = true;
        tDragSource = source;
        tDragInfo = dragInfo;

        tVibrator.vibrate(VIBRATE_DURATION);
        DragView dragView = tDragView = new DragView(tContext, (AbsoluteLayout) tDropTarget, b, registrationX, registrationY,
                textureLeft, textureTop, textureWidth, textureHeight);
        dragView.show(tWindowToken, (int)tMotionDownX, (int)tMotionDownY);
    }

    /**
     * Draw the view into a bitmap.
     */
    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e(TAG, "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    /**
     * Call this from a drag source view like this:
     *
     * <pre>
     *  @Override
     *  public boolean dispatchKeyEvent(KeyEvent event) {
     *      return mDragController.dispatchKeyEvent(this, event)
     *              || super.dispatchKeyEvent(event);
     * </pre>
     */
    public boolean dispatchKeyEvent(KeyEvent event) {
        return tDragging;
    }

    /**
     * Stop dragging without dropping.
     */
    public void cancelDrag() {
        endDrag();
    }

    private void endDrag() {
        if (tDragging) {
            tDragging = false;
            if (tListener != null) {
                tListener.onDragEnd(tOriginator);
            }
            if (tDragView != null) {
                tDragView.remove();
                tDragView = null;
            }
        }
    }

    /**
     * Call this from a drag source view.
     */
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

//        if (action == MotionEvent.ACTION_DOWN) {
//            recordScreenSize();
//        }

        final int screenX = clamp((int)ev.getRawX(), 0, tDisplayMetrics.widthPixels);
        final int screenY = clamp((int)ev.getRawY(), 0, tDisplayMetrics.heightPixels);

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_DOWN:
                // Remember location of down touch
                tMotionDownX = screenX;
                tMotionDownY = screenY;
//                mLastDropTarget = null;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (tDragging) {
                    drop(screenX, screenY);
                }
                endDrag();
                break;
        }

        return tDragging;
    }

    /**
     * Sets the view that should handle move events.
     */
    void setMoveTarget(View view) {
        tMoveTarget = view;
    }    

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return tMoveTarget != null && tMoveTarget.dispatchUnhandledMove(focused, direction);
    }

    /**
     * Call this from a drag source view.
     */
    public boolean onTouchEvent(MotionEvent ev) {
        if (!tDragging) {
            return false;
        }

        final int action = ev.getAction();
        final int screenX = clamp((int)ev.getRawX(), 0, tDisplayMetrics.widthPixels);
        final int screenY = clamp((int)ev.getRawY(), 0, tDisplayMetrics.heightPixels);

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            // Remember where the motion event started
            tMotionDownX = screenX;
            tMotionDownY = screenY;
            break;
        case MotionEvent.ACTION_MOVE:
            // Update the drag view.  Don't use the clamped pos here so the dragging looks
            // like it goes off screen a little, intead of bumping up against the edge.
        	tDragView.move((int)ev.getRawX(), (int)ev.getRawY());
            //TODO the blow commented code is not needed...
            //I leave it commented to make the cleanup easier
            
//            // Drop on someone?
//            final int[] coordinates = mCoordinatesTemp;
//            IDropTarget dropTarget = findDropTarget(screenX, screenY, coordinates);
//            if (dropTarget != null) {
//                if (mLastDropTarget == dropTarget) {
//                    dropTarget.onDragOver(mDragSource, coordinates[0], coordinates[1],
//                        (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
//                } else {
//                    if (mLastDropTarget != null) {
//                        mLastDropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1],
//                            (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
//                    }
//                    dropTarget.onDragEnter(mDragSource, coordinates[0], coordinates[1],
//                        (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
//                }
//            } else {
//                if (mLastDropTarget != null) {
//                    mLastDropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1],
//                        (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
//                }
//            }
//            mLastDropTarget = dropTarget;

            break;
        case MotionEvent.ACTION_UP:
            if (tDragging) {
                drop(screenX, screenY);
            }
            endDrag();

            break;
        case MotionEvent.ACTION_CANCEL:
            cancelDrag();
        }

        return true;
    }

    private boolean drop(int x, int y) {
    	tDropTarget.addView(tOriginator, tOriginIndex);
        tDropTarget.onDragExit(tDragSource, tCoordinatesTemp[0], tCoordinatesTemp[1],
                (int) tTouchOffsetX, (int) tTouchOffsetY, tDragView, tDragInfo);
        if (tDropTarget.acceptDrop(tDragSource, tCoordinatesTemp[0], tCoordinatesTemp[1],
                (int) tTouchOffsetX, (int) tTouchOffsetY, tDragView, tDragInfo)) {
            tDropTarget.onDrop(tDragSource, tCoordinatesTemp[0], tCoordinatesTemp[1],
                    (int) tTouchOffsetX, (int) tTouchOffsetY, tDragView, tDragInfo);
            tDragSource.onDropCompleted((View) tDropTarget, true);
            return true;
        } else {
            tDragSource.onDropCompleted((View) tDropTarget, false);
            return true;
        }
    }

    /**
     * Get the screen size so we can clamp events to the screen size so even if
     * you drag off the edge of the screen, we find something.
     */
    private void recordScreenSize() {
        ((WindowManager)tContext.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(tDisplayMetrics);
    }

    /**
     * Clamp val to be &gt;= min and &lt; max.
     */
    private static int clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        } else if (val >= max) {
            return max - 1;
        } else {
            return val;
        }
    }

    public void setWindowToken(IBinder token) {
        tWindowToken = token;
    }

    /**
     * Sets the drag listner which will be notified when a drag starts or ends.
     */
    public void setDragListener(IDragListener l) {
        tListener = l;
    }

    /**
     * Remove a previously installed drag listener.
     */
    public void removeDragListener(IDragListener l) {
        tListener = null;
    }

    /**
     * Add a DropTarget to the list of potential places to receive drop events.
     */
    public void setDropTarget(DragLayer target) {
        tDropTarget = target;
    }

}

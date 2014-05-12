/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.popup;
/*
 * Copyright (C) 2007 The Android Open Source Project
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

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;

/**
 * <p>A popup window that can be used to display an arbitrary view. The popup
 * windows is a floating container that appears on top of the current
 * activity.</p>
 * 
 * @see android.widget.AutoCompleteTextView
 * @see android.widget.Spinner
 * @author Artan Veliju
 */
public class PopupWindow {
        
    private Context mContext;
    private AbsoluteLayout mWindowManager;
    
    private boolean mIsShowing;

    private View mContentView;
    private View mPopupView;
    private boolean mFocusable;
    private int mSoftInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
    private boolean mTouchable = true;
    private boolean mClippingEnabled = true;
    private boolean mLayoutInsetDecor = false;

    private int mWidthMode;
    private int mWidth;
    private int mLastWidth;
    private int mHeightMode;
    private int mHeight;
    private int mLastHeight;

    private Drawable mBackground;
    
    private OnDismissListener mOnDismissListener;
    private AbsoluteLayout.LayoutParams tLayoutParams;

    private int mAnimationStyle = -1;

    /**
     * <p>Create a new popup window which can display the <tt>contentView</tt>.
     * The dimension of the window must be passed to this constructor.</p>
     *
     * <p>The popup does not provide any background. This should be handled
     * by the content view.</p>
     *
     * @param contentView the popup's content
     * @param width the popup's width
     * @param height the popup's height
     * @param focusable true if the popup can be focused, false otherwise
     */
    public PopupWindow(AbsoluteLayout parent, View contentView, int width, int height, boolean focusable) {
        mContext = contentView.getContext();
        mWindowManager = parent;
        View view = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
        tLayoutParams = createPopupLayout(null);
        setContentView(contentView);
        contentView.measure(width, height);
        setWidth(contentView.getMeasuredWidth());
        setHeight(contentView.getMeasuredHeight());
        setFocusable(focusable);
    }

    /**
     * <p>Return the drawable used as the popup window's background.</p>
     *
     * @return the background drawable or null
     */
    public Drawable getBackground() {
        return mBackground;
    }

    /**
     * <p>Change the background drawable for this popup window. The background
     * can be set to null.</p>
     *
     * @param background the popup's background
     */
    public void setBackgroundDrawable(Drawable background) {
        mBackground = background;
    }

    /**
     * <p>Return the animation style to use the popup appears and disappears</p>
     *
     * @return the animation style to use the popup appears and disappears
     */
    public int getAnimationStyle() {
        return mAnimationStyle;
    }
    
    /**
     * <p>Change the animation style resource for this popup.</p>
     *
     * <p>If the popup is showing, calling this method will take effect only
     * the next time the popup is shown or through a manual call to one of
     * the {@link #update()} methods.</p>
     *
     * @param animationStyle animation style to use when the popup appears
     *      and disappears.  Set to -1 for the default animation, 0 for no
     *      animation, or a resource identifier for an explicit animation.
     *      
     * @see #update()
     */
    public void setAnimationStyle(int animationStyle) {
        mAnimationStyle = animationStyle;
    }
    
    /**
     * <p>Return the view used as the content of the popup window.</p>
     *
     * @return a {@link android.view.View} representing the popup's content
     *
     * @see #setContentView(android.view.View)
     */
    public View getContentView() {
        return mContentView;
    }

    /**
     * <p>Change the popup's content. The content is represented by an instance
     * of {@link android.view.View}.</p>
     *
     * <p>This method has no effect if called when the popup is showing.</p>
     *
     * @param contentView the new content for the popup
     *
     * @see #getContentView()
     * @see #isShowing()
     */
    public void setContentView(View contentView) {
        if (isShowing()) {
            return;
        }

        mContentView = contentView;

        if (mContext == null && mContentView != null) {
            mContext = mContentView.getContext();
        }
    }

    /**
     * <p>Indicate whether the popup window can grab the focus.</p>
     *
     * @return true if the popup is focusable, false otherwise
     *
     * @see #setFocusable(boolean)
     */
    public boolean isFocusable() {
        return mFocusable;
    }

    /**
     * <p>Changes the focusability of the popup window. When focusable, the
     * window will grab the focus from the current focused widget if the popup
     * contains a focusable {@link android.view.View}.  By default a popup
     * window is not focusable.</p>
     *
     * <p>If the popup is showing, calling this method will take effect only
     * the next time the popup is shown or through a manual call to one of
     * the {@link #update()} methods.</p>
     *
     * @param focusable true if the popup should grab focus, false otherwise.
     *
     * @see #isFocusable()
     * @see #isShowing() 
     * @see #update()
     */
    public void setFocusable(boolean focusable) {
        mFocusable = focusable;
    }

    /**
     * Sets the operating mode for the soft input area.
     *
     * @param mode The desired mode, see
     *        {@link android.view.WindowManager.LayoutParams#softInputMode}
     *        for the full list
     *
     * @see android.view.WindowManager.LayoutParams#softInputMode
     * @see #getSoftInputMode()
     */
    public void setSoftInputMode(int mode) {
        mSoftInputMode = mode;
    }

    /**
     * Returns the current value in {@link #setSoftInputMode(int)}.
     *
     * @see #setSoftInputMode(int)
     * @see android.view.WindowManager.LayoutParams#softInputMode
     */
    public int getSoftInputMode() {
        return mSoftInputMode;
    }
    
    /**
     * <p>Indicates whether the popup window receives touch events.</p>
     * 
     * @return true if the popup is touchable, false otherwise
     * 
     * @see #setTouchable(boolean)
     */
    public boolean isTouchable() {
        return mTouchable;
    }

    /**
     * <p>Changes the touchability of the popup window. When touchable, the
     * window will receive touch events, otherwise touch events will go to the
     * window below it. By default the window is touchable.</p>
     *
     * <p>If the popup is showing, calling this method will take effect only
     * the next time the popup is shown or through a manual call to one of
     * the {@link #update()} methods.</p>
     *
     * @param touchable true if the popup should receive touch events, false otherwise
     *
     * @see #isTouchable()
     * @see #isShowing() 
     * @see #update()
     */
    public void setTouchable(boolean touchable) {
        mTouchable = touchable;
    }

    /**
     * <p>Indicates whether clipping of the popup window is enabled.</p>
     * 
     * @return true if the clipping is enabled, false otherwise
     * 
     * @see #setClippingEnabled(boolean)
     */
    public boolean isClippingEnabled() {
        return mClippingEnabled;
    }

    /**
     * <p>Allows the popup window to extend beyond the bounds of the screen. By default the
     * window is clipped to the screen boundaries. Setting this to false will allow windows to be
     * accurately positioned.</p>
     * 
     * <p>If the popup is showing, calling this method will take effect only
     * the next time the popup is shown or through a manual call to one of
     * the {@link #update()} methods.</p>
     *
     * @param enabled false if the window should be allowed to extend outside of the screen
     * @see #isShowing() 
     * @see #isClippingEnabled()
     * @see #update()
     */
    public void setClippingEnabled(boolean enabled) {
        mClippingEnabled = enabled;
    }

    /**
     * <p>Return this popup's height MeasureSpec</p>
     *
     * @return the height MeasureSpec of the popup
     *
     * @see #setHeight(int)
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * <p>Change the popup's height MeasureSpec</p>
     *
     * <p>If the popup is showing, calling this method will take effect only
     * the next time the popup is shown.</p>
     *
     * @param height the height MeasureSpec of the popup
     *
     * @see #getHeight()
     * @see #isShowing() 
     */
    public void setHeight(int height) {
    	tLayoutParams.height = height;
        mHeight = height;
    }

    /**
     * <p>Return this popup's width MeasureSpec</p>
     *
     * @return the width MeasureSpec of the popup
     *
     * @see #setWidth(int) 
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * <p>Change the popup's width MeasureSpec</p>
     *
     * <p>If the popup is showing, calling this method will take effect only
     * the next time the popup is shown.</p>
     *
     * @param width the width MeasureSpec of the popup
     *
     * @see #getWidth()
     * @see #isShowing()
     */
    public void setWidth(int width) {
    	tLayoutParams.width = width;
        mWidth = width;
    }

    /**
     * <p>Indicate whether this popup window is showing on screen.</p>
     *
     * @return true if the popup is showing, false otherwise
     */
    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * <p>
     * Display the content view in a popup window at the specified location. If the popup window
     * cannot fit on screen, it will be clipped. See {@link android.view.WindowManager.LayoutParams}
     * for more information on how gravity and the x and y parameters are related. Specifying
     * a gravity of {@link android.view.Gravity#NO_GRAVITY} is similar to specifying
     * <code>Gravity.LEFT | Gravity.TOP</code>.
     * </p>
     * 
     * @param parent a parent view to get the {@link android.view.View#getWindowToken()} token from
     * @param gravity the gravity which controls the placement of the popup window
     * @param x the popup's x location offset
     * @param y the popup's y location offset
     */
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (isShowing() || mContentView == null) {
            return;
        }

        mIsShowing = true;

        AbsoluteLayout.LayoutParams p = createPopupLayout(parent.getWindowToken());
       
        preparePopup(p);
        if (gravity == Gravity.NO_GRAVITY) {
            gravity = Gravity.TOP | Gravity.LEFT;
        }

        p.x = x;
        p.y = y;
        if (mHeightMode < 0) p.height = mLastHeight = mHeightMode;
        if (mWidthMode < 0) p.width = mLastWidth = mWidthMode;
        invokePopup(p);
    }

    /**
     * <p> Calculating the best position for the popup menu </p>
     * 
     * @param anchor
     */
    public void showAtBestPosition(View anchor){
        if (isShowing() || mContentView == null) {
            return;
        }
        mIsShowing = true;
        findPosition(anchor);
        mWindowManager.addView(mContentView, tLayoutParams);
    }

    /**
     * <p>Prepare the popup by embedding in into a new ViewGroup if the
     * background drawable is not null. If embedding is required, the layout
     * parameters' height is modified to take into account the background's
     * padding.</p>
     *
     * @param p the layout parameters of the popup's content view
     */
    private void preparePopup(AbsoluteLayout.LayoutParams p) {
        if (mContentView == null || mContext == null  || mWindowManager == null) {
            throw new IllegalStateException("You must specify a valid content view by "
                    + "calling setContentView() before attempting to show the popup.");
        }

        if (mBackground != null) {
            final ViewGroup.LayoutParams layoutParams = mContentView.getLayoutParams();
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (layoutParams != null &&
                    layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            // when a background is available, we embed the content view
            // within another view that owns the background drawable
            PopupViewContainer popupViewContainer = new PopupViewContainer(mContext);
            PopupViewContainer.LayoutParams listParams = new PopupViewContainer.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height
            );
            popupViewContainer.setBackgroundDrawable(mBackground);
            popupViewContainer.addView(mContentView, listParams);

            mPopupView = popupViewContainer;
        } else {
            mPopupView = mContentView;
        }
    }

    /**
     * <p>Invoke the popup window by adding the content view to the window
     * manager.</p>
     *
     * <p>The content view must be non-null when this method is invoked.</p>
     *
     * @param p the layout parameters of the popup's content view
     */
    private void invokePopup(AbsoluteLayout.LayoutParams p) {
        mPopupView.setFitsSystemWindows(mLayoutInsetDecor);
        mWindowManager.addView(mPopupView, p);
    }

    /**
     * <p>Generate the layout parameters for the popup window.</p>
     *
     * @param token the window token used to bind the popup's window
     *
     * @return the layout parameters to pass to the window manager
     */
    private AbsoluteLayout.LayoutParams createPopupLayout(IBinder token) {
        // Generates the layout parameters for the drop down
        // we want a fixed size view located at the bottom left of the anchor
        AbsoluteLayout.LayoutParams p = new AbsoluteLayout.LayoutParams(0, 0, 0, 0);

        // These gravity settings put the view at the top left corner of the
        // screen. The view is then positioned to the appropriate location
        // by setting the x and y offsets to match the anchor's bottom
        // left corner
        p.width = mLastWidth = mWidth;
        p.height = mLastHeight = mHeight;

        return p;
    }
    
    /**
     * <p> For finding the best position for the popup window. The popup
     * window will be placed around the widgets </p>
     * 
     * @param anchor
     */
    private void findPosition(View anchor){
    	int targetPos = 0;
        if ((targetPos=anchor.getTop()-getHeight()) >= 0 && anchor.getLeft()+getWidth() <= mWindowManager.getWidth()) { //top
        	tLayoutParams.x = (int) anchor.getX();
        	tLayoutParams.y = targetPos;
        } else if (anchor.getRight()+getWidth() <= mWindowManager.getWidth()) { //right
        	tLayoutParams.y = (int) anchor.getY();
        	tLayoutParams.x = anchor.getRight();
        } else if (anchor.getBottom()+getHeight() <= mWindowManager.getHeight() && anchor.getLeft()+getWidth() <= mWindowManager.getWidth()) { //bottom
        	tLayoutParams.x = (int) anchor.getX();
        	tLayoutParams.y = anchor.getBottom();
        } else if ((targetPos=anchor.getLeft()-getWidth()) >= 0){ //left
        	tLayoutParams.y = (int) anchor.getY();

        	// The popup window must be visible on the bottom right corner of the display. If
        	// the only available place is on left and popup window is bigger than the widget.
        	if (tLayoutParams.y + getHeight() > mWindowManager.getHeight()) {
        		tLayoutParams.y = mWindowManager.getHeight() - getHeight();
        	}

        	tLayoutParams.x = targetPos;
        }
    }

    /**
     * <p>Dispose of the popup window. This method can be invoked only after
     * {@link #showAsDropDown(android.view.View)} has been executed. Failing that, calling
     * this method will have no effect.</p>
     *
     * @see #showAsDropDown(android.view.View) 
     */
    public void dismiss() {
    	if(isShowing()){
	        mIsShowing = false;
	        mWindowManager.removeView(mContentView); 
	        if (mOnDismissListener != null) {
	            mOnDismissListener.onDismiss();
	        }
    	}
    }

    /**
     * Sets the listener to be called when the window is dismissed.
     * 
     * @param onDismissListener The listener.
     */
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }
    
    /**
     * <p>Updates the position and the dimension of the popup window. Width and
     * height can be set to -1 to update location only.  Calling this function
     * also updates the window with the current popup state as
     * described for {@link #update()}.</p>
     *
     * @param x the new x location
     * @param y the new y location
     * @param width the new width, can be -1 to ignore
     * @param height the new height, can be -1 to ignore
     * @param force reposition the window even if the specified position
     *              already seems to correspond to the LayoutParams
     */
    public void update(int x, int y, int width, int height, boolean force) {
        if (width != -1) {
            mLastWidth = width;
            setWidth(width);
        }

        if (height != -1) {
            mLastHeight = height;
            setHeight(height);
        }

        if (!isShowing() || mContentView == null) {
            return;
        }

        LayoutParams p = (LayoutParams) mPopupView.getLayoutParams();

        boolean update = force;

        final int finalWidth = mWidthMode < 0 ? mWidthMode : mLastWidth;
        if (width != -1 && p.width != finalWidth) {
            p.width = mLastWidth = finalWidth;
            update = true;
        }

        final int finalHeight = mHeightMode < 0 ? mHeightMode : mLastHeight;
        if (height != -1 && p.height != finalHeight) {
            p.height = mLastHeight = finalHeight;
            update = true;
        }

        if (p.x != x) {
            p.x = x;
            update = true;
        }

        if (p.y != y) {
            p.y = y;
            update = true;
        }

        if (update) {
            mWindowManager.updateViewLayout(mPopupView, p);
        }
    }

    /**
     * Listener that is called when this popup window is dismissed.
     */
    public interface OnDismissListener {
        /**
         * Called when this popup window is dismissed.
         */
        public void onDismiss();
    }

    private class PopupViewContainer extends FrameLayout {

        public PopupViewContainer(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (getKeyDispatcherState() == null) {
                    return super.dispatchKeyEvent(event);
                }

                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null) {
                        state.startTracking(event, this);
                    }
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null && state.isTracking(event) && !event.isCanceled()) {
                        dismiss();
                        return true;
                    }
                }
                return super.dispatchKeyEvent(event);
            } else {
                return super.dispatchKeyEvent(event);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            
            if ((event.getAction() == MotionEvent.ACTION_DOWN)
                    && ((x < 0) || (x >= getWidth()) || (y < 0) || (y >= getHeight()))) {
                dismiss();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                dismiss();
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        }

        @Override
        public void sendAccessibilityEvent(int eventType) {
            // clients are interested in the content not the container, make it event source
            if (mContentView != null) {
                mContentView.sendAccessibilityEvent(eventType);
            } else {
                super.sendAccessibilityEvent(eventType);
            }
        }
    }
}
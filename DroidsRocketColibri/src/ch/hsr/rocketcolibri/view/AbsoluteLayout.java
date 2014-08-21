/**
 * Rocket Colibri © 2014
 */
/*
 * This is a modified version of a class from the Android Open Source Project. 
 * The original copyright and license information follows.
 * 
 * Copyright (C) 2006 The Android Open Source Project
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

package ch.hsr.rocketcolibri.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews.RemoteView;

/**
 * @author Artan Veliju
 */

@RemoteView
public class AbsoluteLayout extends RelativeLayout {
    public AbsoluteLayout(Context context) {
        super(context);
    }

    public AbsoluteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsoluteLayout(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
     * a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
     * and with the coordinates (0, 0).
     */
    @Override
    protected RelativeLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
    }

    @Override
    public RelativeLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AbsoluteLayout.LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of LayoutParams. 
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof AbsoluteLayout.LayoutParams;
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {
        public LayoutParams(){
        	super(0,0);
        }
        /**
         * Creates a new set of layout parameters with the specified width,
         * height and location.
         *
         * @param width the width, either {@link #MATCH_PARENT},
                  {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param height the height, either {@link #MATCH_PARENT},
                  {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param x the X location of the child
         * @param y the Y location of the child
         */
        public LayoutParams(int width, int height, int x, int y) {
            super(width, height);
            this.leftMargin = x;
            this.topMargin = y;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
        
        public void setX(int x){
        	this.leftMargin = x;
        }
        
        public void setY(int y){
        	this.topMargin = y;
        }
        
        public int getX(){
        	return this.leftMargin;
        }
        
        public int getY(){
        	return this.topMargin;
        }

        public String debug(String output) {
            return output + "Absolute.LayoutParams={width="
                    + sizeToString(width) + ", height=" + sizeToString(height)
                    + " x=" + this.leftMargin + " y=" + this.topMargin + "}";
        }

      /**
         * Converts the specified size to a readable String.
         *
         * @param size the size to convert
         * @return a String instance representing the supplied size
         *
         * @hide
         */
        protected static String sizeToString(int size) {
            if (size == WRAP_CONTENT) {
                return "wrap-content";
            }
            if (size == MATCH_PARENT) {
                return "match-parent";
            }
            return String.valueOf(size);
        }

        public boolean equals(LayoutParams theParam) {
        	if (this.leftMargin != theParam.leftMargin) {
        		return false;
        	}

        	if (this.topMargin != theParam.topMargin) {
        		return false;
        	}

        	return true;
        }
    }
 
}
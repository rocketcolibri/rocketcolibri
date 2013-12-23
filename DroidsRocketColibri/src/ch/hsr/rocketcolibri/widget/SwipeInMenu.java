package ch.hsr.rocketcolibri.widget;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.R.styleable;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class SwipeInMenu extends FrameLayout{

	private String positionInDefaultPortrait;

	private GestureOverlayView touchContent;
	private float lastPosition;
	private int moveOffset = 100;
	
	private boolean isVisible = false;
	
	public SwipeInMenu(Context context) {
		super(context);
		init(context, null);
	}
	
	public SwipeInMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SwipeInMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		touchContent = new GestureOverlayView(context);
		this.addView(touchContent);
		if (context != null && attrs != null){
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.swipeInMenu);
			positionInDefaultPortrait = a.getString(R.styleable.swipeInMenu_positionInDefaultPortrait);
		}
		initSwiping();
	}
	
	
	private void initPosition(){
		if (this.getViewTreeObserver().isAlive()) {
			this.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
					
//						if(positionInDefaultPortrait.toLowerCase().equals("left")){
//							setTranslationX(percentInDP(dpWidth, positionInPercentX)-getMeasuredWidth()/2);
//						}else if(orientationSide.toLowerCase().equals("right")){
//							setTranslationX(dpWidth-percentInDP(dpWidth, positionInPercentX)-getMeasuredWidth()/2);
//						}else if(positionInDefaultPortrait.toLowerCase().equals("bottom")){
//							
//						}else if(positionInDefaultPortrait.toLowerCase().equals("top")){
//							
//						}
					}
				}
			);
		}
	}
	
	public void initSwiping() {
    	Log.d("ACTION_UP", String.valueOf(getScreenHeight()));
	    float density  = getResources().getDisplayMetrics().density;
	    float dpHeight = getScreenHeight() / density;
	    float dpWidth  = getScreenWidth() / density;

		touchContent.setTranslationY(0);
		touchContent.setTranslationX(0);
		touchContent.setBackgroundColor(Color.GREEN);
		touchContent.setLayoutParams(new LayoutParams(getScreenWidth(), 60));
		setTranslationY(getYOut());
		setTranslationX(0);
		setLayoutParams(new LayoutParams(getScreenWidth(), 200));

		touchContent.setOnTouchListener(new View.OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            final int action = event.getAction();
	            switch (action & MotionEvent.ACTION_MASK) {
	                case MotionEvent.ACTION_DOWN: {
	                    lastPosition = SwipeInMenu.this.getY();
	                    Log.d("move", String.valueOf(lastPosition));
	                    break;
	                }case MotionEvent.ACTION_MOVE:{
	            		Log.d("getTranslationY", String.valueOf(SwipeInMenu.this.getTranslationY()));
	            		Log.d("getY", String.valueOf(SwipeInMenu.this.getY()));
	                    Log.d("ACTION_MOVE yCoord", String.valueOf((int) event.getY()));
	                    SwipeInMenu.this.setY(event.getRawY()-(float)SwipeInMenu.this.getHeight());
	                    Log.d("move", String.valueOf(SwipeInMenu.this.getY()));
	                    break;
	                }
	                case MotionEvent.ACTION_OUTSIDE:{
	                    Log.d("ACTION_OUTSIDE yCoord", String.valueOf((int) event.getY()));
	                    break;
	                }case MotionEvent.ACTION_CANCEL:{
	                	Log.d("ACTION_CANCEL","ACTION_CANCEL");
	                	break;
	                }case MotionEvent.ACTION_HOVER_EXIT:{
	                	Log.d("ACTION_HOVER_EXIT","ACTION_HOVER_EXIT");
	                	break;
	                }case MotionEvent.ACTION_UP:{
	                	Log.d("ACTION_UP", String.valueOf(getScreenHeight()));
	                	Log.d("ACTION_UP","ACTION_HOVER_EXIT" + String.valueOf(lastPosition) +  " " + String.valueOf(SwipeInMenu.this.getTranslationY()));
	                	if(lastPosition+moveOffset<SwipeInMenu.this.getTranslationY()){
	                		animateOut();
	                	}else if(lastPosition-moveOffset>SwipeInMenu.this.getTranslationY()){
	                		animateIn();
	                	}else{
	                		if(isVisible){
	                			animateOut();
	                		}else{
	                			animateIn();
	                		}
	                	}
	                	break;
	                }case MotionEvent.ACTION_MASK:{
	                	Log.d("ACTION_MASK","ACTION_HOVER_EXIT");
	                	break;
	                }case MotionEvent.ACTION_SCROLL:{
	                	Log.d("ACTION_SCROLL","ACTION_HOVER_EXIT");
	                	break;
	                }
	            }
	            return true;

	        }

	    });
		
	}
	
	private void animateOut(){
		final float yTo = getYOut();
		TranslateAnimation animation = createYAnimation(yTo);
    	SwipeInMenu.this.startAnimation(animation);
    	isVisible = true;
	}
	
	private void animateIn(){
		final float yTo = getYIn();
		TranslateAnimation animation = createYAnimation(yTo);
    	SwipeInMenu.this.startAnimation(animation);
    	isVisible = false;
	}
	
	private float getYOut(){
		return percentInDP(getScreenHeight(),80);
	}
	
	private float getYIn(){
		return percentInDP(getScreenHeight(),20);
	}
	
	
	private TranslateAnimation createYAnimation(final float yTo){
		 TranslateAnimation animation = new TranslateAnimation( 0, this.getX() , 0, yTo - SwipeInMenu.this.getY());
		 animation.setDuration(300);
		 animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
                SwipeInMenu.this.setY(yTo);
                TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                anim.setDuration(1);
                startSwipeAnimation(anim);
            }
        });
		return animation;
	}
	
	private float percentInDP(float displayDP, int percent){
		return displayDP/100*percent;
	}
	
	private int getScreenHeight() {
		return getResources().getDisplayMetrics().heightPixels;
	}
	
	private int getScreenWidth(){
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	private int getSwipeInMenuHeight(){
		return getHeight();
	}
	
	private void startSwipeAnimation(Animation animation){
		startAnimation(animation);
	}
	
	public int getOrientationHeight(){
		switch(getResources().getConfiguration().orientation){
			case Configuration.ORIENTATION_PORTRAIT: return getScreenHeight();
			case Configuration.ORIENTATION_LANDSCAPE: return getScreenWidth();
			default: return getScreenHeight();
		}
	}
}

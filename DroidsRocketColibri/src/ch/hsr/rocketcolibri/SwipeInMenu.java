package ch.hsr.rocketcolibri;

import java.util.ArrayList;

import ch.hsr.rocketcolibri.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SwipeInMenu extends FrameLayout{

	private String positionInDefaultPortrait;

	private GestureOverlayView touchContent;
	private boolean beginDragging = false;
	private boolean swipeIn = true;
	private float lastPosition;
	
	private boolean isUp;
	
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
		Log.d("HEEELLOOOOO", "INIT");
		touchContent = new GestureOverlayView(context);
//		((Activity)context).getWindow().addContentView(this, new LayoutParams(3,30));
		this.addView(touchContent);
//		ArrayList<View> childs = new ArrayList<View>(1);
//		childs.add(touchContent);
//		this.addFocusables(childs, 1);
//		this.addTouchables(childs);
		// Get the properties from the resource file.
		if (context != null && attrs != null){
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.swipeInMenu);
			positionInDefaultPortrait = a.getString(R.styleable.swipeInMenu_positionInDefaultPortrait);
		}
		bla();
//		initPosition();

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
	
	public void bla() {
		
	    float density  = getResources().getDisplayMetrics().density;
	    float dpHeight = getResources().getDisplayMetrics().heightPixels / density;
	    float dpWidth  = getResources().getDisplayMetrics().widthPixels / density;

		touchContent.setTranslationY(0);
		touchContent.setTranslationX(0);
		touchContent.setBackgroundColor(Color.BLACK);
		touchContent.setLayoutParams(new LayoutParams(getResources().getDisplayMetrics().widthPixels, 40));
		setTranslationY(getResources().getDisplayMetrics().heightPixels-40);
//		setTranslationY(0);
		setTranslationX(0);
		setLayoutParams(new LayoutParams(getResources().getDisplayMetrics().widthPixels, 200));
		touchContent.setOnDragListener(new View.OnDragListener() {
			
			@Override
			public boolean onDrag(View v, DragEvent event) {
				// TODO Auto-generated method stub
                Log.d("onDrag xCoord", String.valueOf((int) event.getX()));
                Log.d("onDrag yCoord", String.valueOf((int) event.getY()));
				return false;
			}
		});
		touchContent.setOnGenericMotionListener(new View.OnGenericMotionListener() {
			
			@Override
			public boolean onGenericMotion(View v, MotionEvent event) {
                Log.d("onGenericMotion xCoord", String.valueOf((int) event.getX()));
                Log.d("onGenericMotion yCoord", String.valueOf((int) event.getY()));
				return false;
			}
		});
		touchContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
                Log.d("onFocusChange xCoord", String.valueOf(hasFocus));
			}
		});
		touchContent.setOnHoverListener(new View.OnHoverListener() {
			
			@Override
			public boolean onHover(View v, MotionEvent event) {
				// TODO Auto-generated method stub
                Log.d("onHover xCoord", String.valueOf((int) event.getX()));
                Log.d("onHover yCoord", String.valueOf((int) event.getY()));

				return false;
			}
		});
		touchContent.setOnTouchListener(new View.OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            final int action = event.getAction();
//	            Log.d("onTouchonTouchonTouch", String.valueOf((int) event.getX()));
//	            Log.d("onTouchonTouchonTouch", String.valueOf((int) event.getX()));
            	float y = event.getY();

	            switch (action & MotionEvent.ACTION_MASK) {

	                case MotionEvent.ACTION_DOWN: {
//	                    xCoord.setText(String.valueOf((int) event.getX()));
//	                    yCoord.setText(String.valueOf((int) event.getY()));
//	                    Log.d("ACTION_DOWN xCoord", String.valueOf((int) event.getX()));
	                    Log.d("ACTION_DOWN yCoord", String.valueOf((int) event.getY()));
	                    beginDragging = true;
	                    lastPosition = SwipeInMenu.this.getY();
	                    Log.d("move", String.valueOf(lastPosition));
	                    break;
	                }case MotionEvent.ACTION_MOVE:{
	            		Log.d("getTranslationY", String.valueOf(SwipeInMenu.this.getTranslationY()));
	            		Log.d("getY", String.valueOf(SwipeInMenu.this.getY()));
//	                    xCoord.setText(String.valueOf((int) event.getX()));
//	                    yCoord.setText(String.valueOf((int) event.getY()));
//	                    Log.d("ACTION_MOVE xCoord", String.valueOf((int) event.getX()));
	                    Log.d("ACTION_MOVE yCoord", String.valueOf((int) event.getY()));
	                    SwipeInMenu.this.setY(SwipeInMenu.this.getY()+event.getY());
	                    Log.d("move", String.valueOf(SwipeInMenu.this.getY()));
	                    break;
	                }
	                case MotionEvent.ACTION_OUTSIDE:{
//	                    xCoord.setText(String.valueOf((int) event.getX()));
//	                    yCoord.setText(String.valueOf((int) event.getY()));
//	                    Log.d("ACTION_OUTSIDE xCoord", String.valueOf((int) event.getX()));
	                    Log.d("ACTION_OUTSIDE yCoord", String.valueOf((int) event.getY()));
	                    break;
	                }case MotionEvent.ACTION_CANCEL:{
	                	Log.d("ACTION_CANCEL","ACTION_CANCEL");
	                	break;
	                }case MotionEvent.ACTION_HOVER_EXIT:{
	                	Log.d("ACTION_HOVER_EXIT","ACTION_HOVER_EXIT");
	                	break;
	                }case MotionEvent.ACTION_UP:{
	                	Log.d("ACTION_UP", String.valueOf(getResources().getDisplayMetrics().heightPixels));
	                	Log.d("ACTION_UP","ACTION_HOVER_EXIT" + String.valueOf(lastPosition) +  " " + String.valueOf(SwipeInMenu.this.getTranslationY()));
//	                	SwipeInMenu.this.setTranslationY(SwipeInMenu.this.getTranslationY()+event.getY());

	                	if(lastPosition+100<SwipeInMenu.this.getTranslationY()){
	                		animateOut();
	                	}else if(lastPosition-100>SwipeInMenu.this.getTranslationY()){
	                		animateIn();
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
		Log.d("heightPixels", String.valueOf(getResources().getDisplayMetrics().heightPixels));
		Log.d("widthPixels", String.valueOf(getResources().getDisplayMetrics().widthPixels));
		Log.d("ydpi", String.valueOf(getResources().getDisplayMetrics().ydpi));
		Log.d("xdpi", String.valueOf(getResources().getDisplayMetrics().xdpi));

		final float yTo = percentInDP(getResources().getDisplayMetrics().heightPixels,90);
		Log.d("animateIn", String.valueOf("from: "+SwipeInMenu.this.getTranslationY())+ " to: "+String.valueOf(yTo));
		Log.d("getBottom",""+SwipeInMenu.this.getBottom());
		//public TranslateAnimation (int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue)
		TranslateAnimation animation = createYAnimationOut(yTo);
    	SwipeInMenu.this.startAnimation(animation);
	}
	
	private void animateIn(){
		final float yTo = percentInDP(getResources().getDisplayMetrics().heightPixels,30);
		TranslateAnimation animation = createYAnimationOut(yTo);
    	SwipeInMenu.this.startAnimation(animation);
	}
	
	
	public void blaa(){
		 final float direction = (isUp) ? -1 : 1;
//         final float yDelta = getScreenHeight() - (2 * getSwipeInMenuHeight());
         float yTo = percentInDP(getResources().getDisplayMetrics().heightPixels,80);
         final float yDelta = yTo;
         final int layoutTopOrBottomRule = (isUp) ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.ALIGN_PARENT_BOTTOM;

         final Animation animation = new TranslateAnimation(0,0,0, yDelta * direction);

         animation.setDuration(2500);

         animation.setAnimationListener(new AnimationListener() {

             public void onAnimationStart(Animation animation) {
             }

             public void onAnimationRepeat(Animation animation) {
             }

             public void onAnimationEnd(Animation animation) {

                 // fix flicking
                 // Source : http://stackoverflow.com/questions/9387711/android-animation-flicker
                 TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                 anim.setDuration(1);
                 startSwipeAnimation(anim);


//                 //set new params
//                 LayoutParams params = new LayoutParams(v.getLayoutParams());
//                 params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                 params.addRule(layoutTopOrBottomRule);
//                 v.setLayoutParams(params);
             }
         });

         startSwipeAnimation(animation);

         //reverse direction
         isUp = !isUp;
	}
	
	private TranslateAnimation createYAnimationOut(final float yTo){
		Log.d("createYAnimation", " to: "+String.valueOf(yTo));
		 TranslateAnimation animation = new TranslateAnimation( 0, this.getX() , 0, yTo - SwipeInMenu.this.getY());
		 animation.setDuration(500);
//		 animation.setFillAfter( true );
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
		
//		return new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, yTo);
	}
	
	private TranslateAnimation createYAnimationIn(final float yTo){
		Log.d("createYAnimation", " to: "+String.valueOf(yTo));
		 TranslateAnimation animation = new TranslateAnimation( 0, this.getX() , 0, SwipeInMenu.this.getY() - yTo);
		 animation.setDuration(500);
//		 animation.setFillAfter( true );
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
		
//		return new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, yTo);
	}
	
	private float percentInDP(float displayDP, int percent){
		return displayDP/100*percent;
	}
	
	private float getScreenHeight() {
		return getResources().getDisplayMetrics().heightPixels;
	}
	
	private int getSwipeInMenuHeight(){
		return getHeight();
	}
	
	private void startSwipeAnimation(Animation animation){
		startAnimation(animation);
	}
}

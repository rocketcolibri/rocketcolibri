package ch.hsr.rocketcolibri.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class HoldButton extends Button{
	
    private TransitionDrawable tTransition;
    private OnHoldListener tHoldListener;
    
	public HoldButton(Context context) {
		super(context);
	}

	public HoldButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public HoldButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs){
	    int[] attrsArray = new int[] {android.R.attr.drawable};
	    TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
	    Drawable drawableEnd = ta.getDrawable(0);
	    ta.recycle();
	    Drawable[] colors = {getBackground(), drawableEnd};
	    tTransition = new TransitionDrawable(colors);
	    setBackground(tTransition);
	}
	
	public void setOnHoldListener(OnHoldListener hListener){
		tHoldListener = hListener;
		setOnTouchListener(createOnTouchListener());
	}
	
	public interface OnHoldListener{
		void onHoldStart(View v);
		/** @param timeLeft from x>0 to 0, on 0 will be called onHoldEnd*/
		void onHold(int timeLeft, int overallDuration);
		void onHoldEnd(View v);
		void onHoldCanceled();
	}
	
	private OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
		    final int MAX_DURATION = 4000;
		    long duration = 0;
		    long startTime = 0;
		    boolean done = false;
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					done = false;
	        		startTime = System.currentTimeMillis();
	        		tHoldListener.onHoldStart(v);
	        		tTransition.startTransition(MAX_DURATION);
	        		break;
				case MotionEvent.ACTION_MOVE: 
					duration = System.currentTimeMillis() - startTime;
					tHoldListener.onHold((int)(MAX_DURATION-duration), MAX_DURATION);
					if(duration >= MAX_DURATION){
						done = true;
						tHoldListener.onHoldEnd(v);
						tTransition.resetTransition();
					}
					break;
				case MotionEvent.ACTION_UP: 
					if(!done)tHoldListener.onHoldCanceled();
					tTransition.resetTransition();
					break;
		        case MotionEvent.ACTION_CANCEL:
		        	return false;
		        }
				return true;
			}
		};
	}
	
}

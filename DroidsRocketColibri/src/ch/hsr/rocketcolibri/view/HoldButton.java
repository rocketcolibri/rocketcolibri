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
		void onHoldEnd(View v);
	}
	
	private OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
		    final int MAX_DURATION = 4000;
		    long duration = 0;
		    long startTime = 0;
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN: 
	        		startTime = System.currentTimeMillis();
	        		tTransition.startTransition(MAX_DURATION);
	        		break;
				case MotionEvent.ACTION_MOVE: 
					duration = System.currentTimeMillis() - startTime;
					if(duration >= MAX_DURATION){
						tHoldListener.onHoldEnd(v);
						tTransition.resetTransition();
					}
					break;
				case MotionEvent.ACTION_UP: 
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

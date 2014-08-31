package ch.hsr.rocketcolibri.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class HoldImageView extends ImageView{
	
    private TransitionDrawable tTransition;
    private OnHoldListener tHoldListener;
    
	public HoldImageView(Context context) {
		super(context);
	}

	public HoldImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public HoldImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs){
	    int[] attrsArray = new int[] {android.R.attr.src};
	    TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
	    Drawable drawableStart = ta.getDrawable(0);
	    ta.recycle();
	    Drawable[] colors = {drawableStart, new ColorDrawable(Color.RED)};
	    tTransition = new TransitionDrawable(colors);
	    setBackground(tTransition);
	}
	
	public void setOnHoldListener(OnHoldListener hListener){
		tHoldListener = hListener;
		setOnTouchListener(new HoldTouchListener(tTransition, tHoldListener));
	}
}

package ch.hsr.rocketcolibri.view.popup;

import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class PopupView extends ViewGroup{

	private volatile boolean showing;
	private AbsoluteLayout tParent;
	
	public PopupView(Context context, AbsoluteLayout parent) {
		super(context);
		tParent = parent;
		measure(20, 20);
	}
	
	public PopupView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PopupView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		try{
			tParent = (AbsoluteLayout) this.getParent();
		}catch(Exception e){
			throw new IllegalArgumentException("parent must be an AbsoluteLayout");
		}
	}
	
	public void showCloseTo(View view){
		showing = true;
//		if(view.getTop()-getHeight() >= tParent.getTop()){//top is clear
//			layout(view.getLeft(), view.getTop()-getHeight(), view.getLeft()+getWidth(), view.getTop());
//		}else if(view.getRight()+getWidth()<=tParent.getRight()){//right is clear
//			layout(view.getRight(), view.getTop(), view.getRight()+getWidth(), view.getTop());
//		}else if(view.getBottom() + getHeight()<=tParent.getBottom()){//bottom is clear
//		layout(view.getLeft(), view.getBottom(), view.getLeft()+getWidth(), view.getBottom()+getHeight());
//		layout(getWidth(), getWidth(), getWidth(), getWidth());
//		}else if(view.getLeft() - getWidth() >= tParent.getLeft()){//left is clear
//			layout(view.getLeft()-getWidth(), view.getTop(), view.getLeft(), view.getTop()+getHeight());
//		}
//		tParent.updateViewLayout(this, getLayoutParams());
		tParent.addView(this);
	}
	
    public boolean isShowing() {
        return showing;
    }
    
    public void dismiss(){
    	if(showing){
    		tParent.removeView(this);
    		showing = false;
    	}
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	}
	
}

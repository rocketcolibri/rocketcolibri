/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.custimizable;

import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;

/**
 * @author Artan Veliju
 */
public class CustomizableView extends View implements ICustomizableView{
	private boolean tCustomizeModusActive = false;
	private ViewElementConfig tViewElementConfig;
	//empty listener to avoid null check
	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		@Override
		public void customizeModeDeactivated() {
		}
		@Override
		public void customizeModeActivated() {
		}
	};
	
	/**
	 * Constructor for non Android instantiation
	 * @param context
	 * @param vElementConfig
	 */
    public CustomizableView(Context context, ViewElementConfig vElementConfig) {
		super(context);
		tViewElementConfig = vElementConfig;
		setLayoutParams(tViewElementConfig.getLayoutParams());
		setAlpha(tViewElementConfig.getAlpha());
	}
    
	public CustomizableView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomizableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
    @Override
    protected void onDraw(Canvas canvas) {
    	if(!tCustomizeModusActive)return;
        final Drawable foreground = getResources().getDrawable(R.drawable.dragforeground);
        if (foreground != null) {
            foreground.setBounds(0, 0, getRight() - getLeft(), getBottom() - getTop());
 
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();
 
            if ((scrollX | scrollY) == 0) {
                foreground.draw(canvas);
            }
            else {
                canvas.translate(scrollX, scrollY);
                foreground.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }
    }

	@Override
	public void setCustomizeModus(boolean enabled) {
		if(tCustomizeModusActive!=enabled){
			if(enabled){
				tModusChangeListener.customizeModeActivated();
			}else{
				tModusChangeListener.customizeModeDeactivated();
			}
			invalidate();
			tCustomizeModusActive = enabled;
		}
	}
	
	public void setCustomizeModusListener(OnTouchListener customizeModusListener){
		setOnTouchListener(customizeModusListener);
	}
	
	@Override
	public void setModusChangeListener(ModusChangeListener mcl){
		tModusChangeListener = mcl;
	}
	
	@Override
	public ViewElementConfig getViewElementConfig(){
		tViewElementConfig.setLayoutParams((LayoutParams) getLayoutParams());
		tViewElementConfig.setAlpha(getAlpha());
		return tViewElementConfig;
	}
	
	@Override
	public void notifyServiceReady(Service service) {
		
	}
}


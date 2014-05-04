/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.custimizable;

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
	private boolean customizeModusActive = false;
	private ViewElementConfig tViewElementConfig;
	
	/**
	 * Constructor for non Android instantiation
	 * @param context
	 * @param cElementConfig
	 */
    public CustomizableView(Context context, ViewElementConfig cElementConfig) {
		super(context);
		tViewElementConfig = cElementConfig;
		setLayoutParams(tViewElementConfig.getLayoutParams());
		setAlpha(cElementConfig.getAlpha());
	}

	public CustomizableView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomizableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
    @Override
    protected void onDraw(Canvas canvas) {
    	if(!customizeModusActive)return;
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
		if(customizeModusActive!=enabled)
			invalidate();
		customizeModusActive = enabled;
	}
	
	public ViewElementConfig getViewElementConfig(){
		tViewElementConfig.settLayoutParams((LayoutParams) getLayoutParams());
		tViewElementConfig.setAlpha(getAlpha());
		return tViewElementConfig;
	}
}


package ch.hsr.rocketcolibri.view.custimizable;

import ch.hsr.rocketcolibri.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class CustomizableView extends View implements ICustomizableView{
	private boolean customizeModusActive = false;
	
    public CustomizableView(Context context) {
		super(context);
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
}


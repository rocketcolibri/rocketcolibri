package ch.hsr.rocketcolibri.view.resizable.layer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout;
import ch.hsr.rocketcolibri.view.resizable.IResizeListener;

public class ResizableMainLayer extends MyAbsoluteLayout{

	public ResizableMainLayer(Context context, View resizeTarget, final IResizeListener listener) {
		super(context);
		((MyAbsoluteLayout)resizeTarget.getParent()).removeView(resizeTarget);
		
		IResizeListener mainListener = new IResizeListener() {
			
			@Override
			public void done(View resizedView) {
				ResizableMainLayer.this.removeAllViews();
				((ViewGroup)getParent()).removeView(ResizableMainLayer.this);
				listener.done(resizedView);
			}
		};
		setBackgroundColor(Color.TRANSPARENT);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        LayoutParams lp = new LayoutParams(width, height, 0, 0);
        setLayoutParams(lp);
        ResizableBackgroundLayer bgLayer = new ResizableBackgroundLayer(context, lp);
        addView(bgLayer);
        ResizeableTargetLayer targetLayer = new ResizeableTargetLayer(context, resizeTarget, lp, mainListener);
        addView(targetLayer);
	}

}

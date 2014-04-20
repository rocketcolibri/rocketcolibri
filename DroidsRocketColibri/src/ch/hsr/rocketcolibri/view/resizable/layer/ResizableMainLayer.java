package ch.hsr.rocketcolibri.view.resizable.layer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.resizable.IResizeDoneListener;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;

public class ResizableMainLayer extends AbsoluteLayout{

	public ResizableMainLayer(Context context, View resizeTarget, final IResizeDoneListener listener, LayoutParams lp, ResizeConfig config) {
		super(context);
		IResizeDoneListener mainListener = new IResizeDoneListener() {
			
			@Override
			public void done(View resizedView) {
				ResizableMainLayer.this.removeAllViews();
				listener.done(resizedView);
			}
		};
		setBackgroundColor(Color.TRANSPARENT);
        setLayoutParams(lp);
        ResizableBackgroundLayer bgLayer = new ResizableBackgroundLayer(context, lp);
        addView(bgLayer);
        ResizeableTargetLayer targetLayer = new ResizeableTargetLayer(context, resizeTarget, lp, mainListener, config);
        addView(targetLayer);
	}

}

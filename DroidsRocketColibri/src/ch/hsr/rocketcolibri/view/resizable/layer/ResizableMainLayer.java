/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.resizable.layer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.resizable.IResizeDoneListener;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;

/**
 * @author Artan Veliju
 */
public class ResizableMainLayer extends AbsoluteLayout{
	
	private ResizeableTargetLayer tTargetLayer;
	private ResizeMenu tMenu;
	public ResizableMainLayer(Context context, AbsoluteLayout parent, View resizeTarget, final IResizeDoneListener listener, LayoutParams lp, ResizeConfig config) {
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
        tTargetLayer = new ResizeableTargetLayer(context, parent, resizeTarget, lp, mainListener, config);
        addView(tTargetLayer);
        tMenu = new ResizeMenu(context, parent, resizeTarget, tTargetLayer.getMenuListener());
        addView(tMenu);
	}
	
	public void stop(){
		tTargetLayer.stop();
	}

}

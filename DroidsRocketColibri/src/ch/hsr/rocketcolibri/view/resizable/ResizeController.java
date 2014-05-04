/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.resizable;

import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.resizable.layer.ResizableMainLayer;
import android.content.Context;
import android.view.View;

public class ResizeController {
	
	private Context tContext;
	private volatile boolean tIsResizing;
	private AbsoluteLayout tTargetParent;
	private int indexOfResizeTargetOnParent;
	private ResizableMainLayer tViewResizer;
	private IResizeDoneListener tResizeDoneListener;
	private IResizeDoneListener tInternalResizeDoneListener = new IResizeDoneListener() {
		@Override
		public void done(View resizedView) {
			tTargetParent.removeView(tViewResizer);
			tTargetParent.addView(resizedView, indexOfResizeTargetOnParent);
			tIsResizing = false;
			if(tResizeDoneListener!=null)
				tResizeDoneListener.done(resizedView);
		}
	};
	
	public ResizeController(Context context){
		tContext = context;
	}
	
	public void startResize(AbsoluteLayout parent, View resizeTarget){
		startResize(parent, resizeTarget, new ResizeConfig());
	}
	
	public void startResize(AbsoluteLayout parent, View resizeTarget, ResizeConfig rConfg){
		if(tIsResizing)return;
		tTargetParent = parent;
		LayoutParams parentLayoutParams = new AbsoluteLayout.LayoutParams(tTargetParent.getLayoutParams().width, tTargetParent.getLayoutParams().height, 0, 0);
		indexOfResizeTargetOnParent = parent.indexOfChild(resizeTarget);
		tTargetParent.removeViewAt(indexOfResizeTargetOnParent);
		tViewResizer = new ResizableMainLayer(tContext, resizeTarget, tInternalResizeDoneListener, parentLayoutParams, rConfg);
	    tTargetParent.addView(tViewResizer);//add resize view to the targets parent to make it visible
	    tIsResizing = true;
	}
	
	public void setResizeDoneListener(IResizeDoneListener listener){
		tResizeDoneListener = listener;
	}
}

package ch.hsr.rocketcolibri.view.resizable;

import ch.hsr.rocketcolibri.view.MyAbsoluteLayout;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.resizable.layer.ResizableMainLayer;
import android.content.Context;
import android.view.View;

public class ResizeController {
	
	private Context tContext;
	private volatile boolean tIsResizing;
	private MyAbsoluteLayout tTargetParent;
	private ResizableMainLayer tViewResizer;
	private IResizeDoneListener tResizeDoneListener;
	private IResizeDoneListener tInternalResizeDoneListener = new IResizeDoneListener() {
		@Override
		public void done(View resizedView) {
			tTargetParent.removeView(tViewResizer);
			tTargetParent.addView(resizedView);
			tIsResizing = false;
			if(tResizeDoneListener!=null)
				tResizeDoneListener.done(resizedView);
		}
	};
	
	public ResizeController(Context context){
		tContext = context;
	}
	
	public void startResize(MyAbsoluteLayout parent, View resizeTarget){
		if(tIsResizing)return;
		tTargetParent = parent;
		LayoutParams parentLayoutParams = new MyAbsoluteLayout.LayoutParams(tTargetParent.getLayoutParams().width, tTargetParent.getLayoutParams().height, 0, 0);
		tTargetParent.removeView(resizeTarget);
		tViewResizer = new ResizableMainLayer(tContext, resizeTarget, tInternalResizeDoneListener, parentLayoutParams);
	    tTargetParent.addView(tViewResizer);//add resize stuff to the targets parent to make it visible
	    tIsResizing = true;
	}
	
	public void setResizeDoneListener(IResizeDoneListener listener){
		tResizeDoneListener = listener;
	}
}

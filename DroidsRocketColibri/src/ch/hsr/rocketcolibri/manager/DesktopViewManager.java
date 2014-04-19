package ch.hsr.rocketcolibri.manager;

import java.lang.reflect.Constructor;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.manager.listener.CustomizeModusListener;
import ch.hsr.rocketcolibri.menu.CustomizeModusPopupMenu;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.draggable.DragController;
import ch.hsr.rocketcolibri.view.draggable.DragLayer;
import ch.hsr.rocketcolibri.view.draggable.IDragSource;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeController;


/**
 * The DesktopViewManager handles creation, drag&drop and resizing of the
 * Views on the Desktop.
 * @author artvel
 */
public class DesktopViewManager implements IDesktopViewManager{
	private Activity tContext;
	private ResizeController tResizeController;
	private DragController tDragController;
	private MyAbsoluteLayout tRootView;
	private boolean tCustomizeModus;
	private CustomizeModusListener tCustomizeModusListener;
	
	public DesktopViewManager(Activity context, MyAbsoluteLayout rootView){
		tContext = context;
		tRootView = rootView;
		tResizeController = new ResizeController(context);
		tDragController = new DragController(context);
		DragLayer dragLayer = (DragLayer) tRootView;
		dragLayer.setDragController(tDragController);
	    tDragController.addDropTarget (dragLayer);
	    
	    LayoutInflater li = LayoutInflater.from(context);
		LinearLayout ll = (LinearLayout) li.inflate(R.layout.customize_modus_popup, rootView, false);
		
	    tCustomizeModusListener = new CustomizeModusListener(this, new CustomizeModusPopupMenu(this, ll));
	}
	
	@Override
	public void resizeView(View resizeTarget){
		ResizeConfig rConfig = null;
		try{
			rConfig = ((CustomizableView)resizeTarget).getViewElementConfig().getResizeConfig();
			tResizeController.startResize(tRootView, resizeTarget, rConfig);
		}catch(Exception e){
			tResizeController.startResize(tRootView, resizeTarget);
		}
	}
	
	@Override
	public boolean dragView(View dragView){
	    if (tCustomizeModus) {
	        // Make sure the drag was started by a long press as opposed to a long click.
	        // (Note: I got this from the Workspace object in the Android Launcher code. 
	        //  I think it is here to ensure that the device is still in touch mode as we start the drag operation.)
	        if (!dragView.isInTouchMode()) {
	           return false;
	        }
		    // Let the DragController initiate a drag-drop sequence.
		    // I use the dragInfo to pass along the object being dragged.
		    // I'm not sure how the Launcher designers do this.
		    Object dragInfo = dragView;
		    tDragController.startDrag (dragView, (IDragSource) tRootView, dragInfo, DragController.DRAG_ACTION_MOVE);
		    return true;
	    }
	    // If we get here, return false to indicate that we have not taken care of the event.
	    return false;
	}
	
	@Override
	public View createView(ViewElementConfig cElementConfig) throws Exception{
	    Class<?> c = Class.forName(cElementConfig.getClassPath());
	    Constructor<?> cons = c.getConstructor(Context.class, ViewElementConfig.class);
	    CustomizableView view = (CustomizableView)cons.newInstance(tContext, cElementConfig);
	    view.setOnTouchListener(tCustomizeModusListener);
	    tRootView.addView(view);
	    return view;
	}

	@Override
	public boolean isInCustomizeModus() {
		return tCustomizeModus;
	}

	@Override
	public void switchCustomieModus() {
		tCustomizeModus = !tCustomizeModus;
		updateModusOfCustomizableViews();
	}
	
	private void updateModusOfCustomizableViews(){
    	int size = tRootView.getChildCount();
    	ICustomizableView view = null;
    	for(int i = 0; i < size; ++i){
    		try{
    			view = (ICustomizableView) tRootView.getChildAt(i);
    			view.setCustomizeModus(tCustomizeModus);
    		}catch(Exception e){
    		}
    	}
	}

	@Override
	public void release() {
		Log.d("DesktopViewManager", "release");
		tResizeController = null;
		tContext = null;
		tCustomizeModusListener.release();
		tCustomizeModusListener = null;
		tDragController = null;
		tRootView = null;
	}

	@Override
	public View getRootView() {
		//the real root
//		View view = tContext.getWindow().getDecorView().findViewById(android.R.id.content);
		return tRootView;
	}
}

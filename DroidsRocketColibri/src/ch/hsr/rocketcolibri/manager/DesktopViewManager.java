/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.activity.EditChannelActivity;
import ch.hsr.rocketcolibri.manager.listener.CustomizeModusListener;
import ch.hsr.rocketcolibri.manager.listener.ViewChangedListener;
import ch.hsr.rocketcolibri.menu.CustomizeModusPopupMenu;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.draggable.DragController;
import ch.hsr.rocketcolibri.view.draggable.DragLayer;
import ch.hsr.rocketcolibri.view.draggable.IDragListener;
import ch.hsr.rocketcolibri.view.draggable.IDragSource;
import ch.hsr.rocketcolibri.view.resizable.IResizeDoneListener;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeController;


/**
 * The DesktopViewManager handles creation, drag&drop and resizing of the
 * Views on the Desktop.
 * @author Artan Veliju
 */
public class DesktopViewManager implements IDesktopViewManager{
	private Activity tContext;
	private ResizeController tResizeController;
	private DragController tDragController;
	private AbsoluteLayout tRootView;
	private AbsoluteLayout tControlElementParentView;
	private boolean tCustomizeModus;
	private CustomizeModusListener tCustomizeModusListener;
	private ViewChangedListener tViewChangeListener;
	private IDragListener dragListener;
	private CustomizeModusPopupMenu tCustomizeModusPopupMenu;
	
	public DesktopViewManager(Activity context, AbsoluteLayout rootView, AbsoluteLayout controlElementParentView, ViewChangedListener vcListener){
		tContext = context;
		tRootView = rootView;
		tControlElementParentView = controlElementParentView;
		tViewChangeListener = vcListener;
		tResizeController = new ResizeController(context);
		tResizeController.setResizeDoneListener(createResizeDoneListener());
		tDragController = new DragController(context);
		DragLayer dragLayer = (DragLayer) tControlElementParentView;
		dragLayer.setDragController(tDragController);
	    tDragController.addDropTarget (dragLayer);
	    tDragController.setDragListener(createDragListener());
	    LayoutInflater li = LayoutInflater.from(context);
		LinearLayout ll = (LinearLayout) li.inflate(R.layout.customize_modus_popup, null, false);
		tCustomizeModusPopupMenu = new CustomizeModusPopupMenu(this, ll);
	    tCustomizeModusListener = new CustomizeModusListener(this);
	}

	@Override
	public void resizeView(View resizeTarget){
		ResizeConfig rConfig = null;
		try{
			rConfig = ((CustomizableView)resizeTarget).getViewElementConfig().getResizeConfig();
			tResizeController.startResize(tControlElementParentView, resizeTarget, rConfig);
		}catch(Exception e){
			tResizeController.startResize(tControlElementParentView, resizeTarget);
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
		    tDragController.startDrag (dragView, (IDragSource) tControlElementParentView, dragInfo, DragController.DRAG_ACTION_MOVE);
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
	    tControlElementParentView.addView(view);
//	    if(!tCustomizeModus && (null != view.getOperateOverlayView()))
//	    	tControlElementParentView.addView(view.getOperateOverlayView(),0);
	    updateModusOfCustomizableViews();
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
    	int size = tControlElementParentView.getChildCount();
    	
    	List<View>operateOverlayViews = new ArrayList<View>();
    	
    	CustomizableView view = null;
    	for(int i = 0; i < size; ++i){
    		try{
	    			View v = tControlElementParentView.getChildAt(i);
	    			if (v instanceof CustomizableView)	{
		    			view = (CustomizableView) v;
		    			view.setOnTouchListener(tCustomizeModusListener);
		    			view.setCustomizeModus(tCustomizeModus);
		    			// switch View
		    			if(null != view.getOperateOverlayView())
		    				operateOverlayViews.add(view.getOperateOverlayView());
	    			} else{
	    				tControlElementParentView.removeView(v);
	    			}
    			}catch(Exception e){
    			Log.d("DV", "msg");
    		}
    	}
    	// add the new operate overly after setting the 'normal' customized' views
    	for ( View v : operateOverlayViews)	{
    		if(!tCustomizeModus)
    			tControlElementParentView.addView(v, 0);	
    	}
    		
    	
	}

	@Override
	public AbsoluteLayout getRootView() {
		return tRootView;
	}

	@Override
	public AbsoluteLayout getControlElementParentView() {
		return tControlElementParentView;
	}
	
	@Override
	public void startEditActivity(View targetView){
		Intent intent = new Intent(tContext, EditChannelActivity.class);
		tContext.startActivity(intent);
	}
	
	private IResizeDoneListener createResizeDoneListener() {
		return new IResizeDoneListener() {
			@Override
			public void done(View resizedView) {
				viewChanged(resizedView);
			}
		};
	}

	private IDragListener createDragListener() {
		return new IDragListener() {
			@Override
			public void onDragStart(IDragSource source, Object info, int dragAction) {
			}
			@Override
			public void onDragEnd(View targetView) {
				viewChanged(targetView);
			}
		};
	}
	
	private void viewChanged(View view){
		try{
			tViewChangeListener.onViewChange(((CustomizableView)view).getViewElementConfig());
		}catch(Exception e){
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
		tControlElementParentView = null;
		tRootView = null;
	}
	
	@Override
	public CustomizeModusPopupMenu getCustomizeModusPopupMenu(){
		return tCustomizeModusPopupMenu;
	}
	
	@Override
	public void closeSpecialThings(){
		tCustomizeModusPopupMenu.dismiss();
		tResizeController.stopResize();
	}

}

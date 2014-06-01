/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.activity.EditChannelActivity;
import ch.hsr.rocketcolibri.manager.listener.CustomizeModusListener;
import ch.hsr.rocketcolibri.manager.listener.ViewChangedListener;
import ch.hsr.rocketcolibri.menu.CustomizeModusPopupMenu;
import ch.hsr.rocketcolibri.menu.desktop.DesktopMenu;
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
import ch.hsr.rocketcolibri.view.widget.OnChannelChangeListener;
import ch.hsr.rocketcolibri.view.widget.RCWidget;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;


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
	private DesktopMenu tDesktopMenu;
	private OnChannelChangeListener tControlModusListener;
	private Service tService;
	
	public DesktopViewManager(Activity context, AbsoluteLayout rootView, AbsoluteLayout controlElementParentView, OnChannelChangeListener controlModusListener, ViewChangedListener vcListener){
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
	    tDesktopMenu = new DesktopMenu(tContext, this);
	    tControlModusListener = controlModusListener;
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
	public RCWidget createAndAddView(ViewElementConfig vElementConfig) throws Exception{
		RCWidget widget = createView(vElementConfig);
		processRCWidget(widget);
	    tViewChangeListener.onViewAdd(widget.getWidgetConfig());
	    return widget;
	}
	
	@Override
	public RCWidget initCreateAndAddView(RCWidgetConfig widgetConfig) throws Exception{
		RCWidget widget = createView(widgetConfig);
		processRCWidget(widget);
	    return widget;
	}
	
	private void processRCWidget(RCWidget widget){
		widget.setCustomizeModusListener(tCustomizeModusListener);
	    try{widget.setControlModusListener(tControlModusListener);}catch(ClassCastException e){}
	    widget.setCustomizeModus(tCustomizeModus);
	    tControlElementParentView.addView(widget);
	    if(null != tService)widget.notifyServiceReady(tService);
	}
	
	@Override
	public RCWidget createView(ViewElementConfig vElementConfig) throws Exception{
	    Class<?> c = Class.forName(vElementConfig.getClassPath());
	    Constructor<?> cons = c.getConstructor(Context.class, ViewElementConfig.class);
	    return (RCWidget)cons.newInstance(tContext, vElementConfig);
	}
	
	@Override
	public RCWidget createView(RCWidgetConfig vElementConfig) throws Exception{
	    Class<?> c = Class.forName(vElementConfig.viewElementConfig.getClassPath());
	    Constructor<?> cons = c.getConstructor(Context.class, RCWidgetConfig.class);
	    Log.d("createView", ""+vElementConfig);
	    Log.d("createView map", ""+vElementConfig.protocolMap);
	    return (RCWidget)cons.newInstance(tContext, vElementConfig);
	}
	
	@Override
	public void deleteView(View view){
		tViewChangeListener.onViewDelete(((RCWidget)view).getWidgetConfig());
		tControlElementParentView.removeView(view);
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
    	CustomizableView view = null;
    	for(int i = 0; i < size; ++i){
    		try{
    			view = (CustomizableView) tControlElementParentView.getChildAt(i);
    			view.setCustomizeModus(tCustomizeModus);
    		}catch(Exception e){
    		}
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
	public DesktopMenu getDesktopMenu(){
		return tDesktopMenu;
	}
	
	@Override
	public void startEditActivity(View targetView){
	    Intent editChannelIntent = new Intent(tContext, EditChannelActivity.class);
	    putProtocolExtras(editChannelIntent, targetView);
	    tContext.startActivityForResult(editChannelIntent, tControlElementParentView.indexOfChild(targetView));
	}
	
	private void putProtocolExtras(Intent intent, View targetView){
		RCWidget rcWidget = (RCWidget)targetView;
		Map<String, String> pm = rcWidget.getProtocolMap();
		Set<String> keySet = pm.keySet();
		for(String key : keySet){
			intent.putExtra(key, pm.get(key));
		}
	}
	
	@Override
	public void editActivityResult(int viewIndex, Intent editChannelIntent){
		Log.d(""+viewIndex, "editActivityResult");
		RCWidget rcw = (RCWidget)tControlElementParentView.getChildAt(viewIndex);
		Set<String> keySet = rcw.getProtocolMap().keySet();
		for(String key : keySet){
			Log.d(""+key, ""+editChannelIntent.getStringExtra(key));
			rcw.getProtocolMap().put(key, editChannelIntent.getStringExtra(key));
		}
		rcw.updateProtocolMap();
		tViewChangeListener.onViewChange(rcw.getWidgetConfig());
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
	
	@Override
	public void viewChanged(View view){
		try{
			tViewChangeListener.onViewChange(((RCWidget)view).getWidgetConfig());
		}catch(Exception e){
		}
	}
	
	@Override
	public void release() {
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

	public void serviceReady(Service service) {
		tService = service;
		int size = tControlElementParentView.getChildCount();
    	CustomizableView view = null;
    	for(int i = 0; i < size; ++i){
    		try{
    			view = (CustomizableView) tControlElementParentView.getChildAt(i);
    			view.notifyServiceReady(service);
    		}catch(Exception e){
    		}
    	}
	}
	
}

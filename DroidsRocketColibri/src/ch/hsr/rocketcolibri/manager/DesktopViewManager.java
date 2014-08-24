/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.manager;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.activity.DesktopActivity;
import ch.hsr.rocketcolibri.activity.EditChannelActivity;
import ch.hsr.rocketcolibri.manager.listener.CustomizeModusListener;
import ch.hsr.rocketcolibri.manager.listener.ViewChangedListener;
import ch.hsr.rocketcolibri.menu.CustomizeModusPopupMenu;
import ch.hsr.rocketcolibri.menu.desktop.DesktopMenu;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.draggable.DragController;
import ch.hsr.rocketcolibri.view.draggable.DragLayer;
import ch.hsr.rocketcolibri.view.draggable.IDragListener;
import ch.hsr.rocketcolibri.view.draggable.IDragSource;
import ch.hsr.rocketcolibri.view.resizable.IResizeDoneListener;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeController;
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
	private CustomizeModusPopupMenu tCustomizeModusPopupMenu;
	private DesktopMenu tDesktopMenu;
	private DVMViewListener dvmViewListener;
	
	public DesktopViewManager(Activity context, AbsoluteLayout rootView, AbsoluteLayout controlElementParentView,  ViewChangedListener vcListener){
		tContext = context;
		tRootView = rootView;
		tControlElementParentView = controlElementParentView;
		tViewChangeListener = vcListener;
		tResizeController = new ResizeController(context);
		tResizeController.setResizeDoneListener(createResizeDoneListener());
		tDragController = new DragController(context);
		DragLayer dragLayer = (DragLayer) tControlElementParentView;
		dragLayer.setDragController(tDragController);
	    tDragController.setDropTarget (dragLayer);
	    tDragController.setDragListener(createDragListener());
	    LayoutInflater li = LayoutInflater.from(context);
		LinearLayout ll = (LinearLayout) li.inflate(R.layout.customize_modus_popup, null, false);
		tCustomizeModusPopupMenu = new CustomizeModusPopupMenu(this, ll);
	    tCustomizeModusListener = new CustomizeModusListener(this);
	    tDesktopMenu = new DesktopMenu(tContext, this);
	}
	
	@Override
	public void setDVMViewListener(DVMViewListener dvmVl){
		dvmViewListener = dvmVl;
	}

	@Override
	public void resizeView(View resizeTarget){
		ResizeConfig rConfig = null;
		try{
			rConfig = ((ICustomizableView)resizeTarget).getViewElementConfig().getResizeConfig();
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
	public ICustomizableView createAndAddView(RCWidgetConfig vElementConfig) throws Exception{
		ICustomizableView widget = createView(vElementConfig);
		processRCWidget(widget);
	    tViewChangeListener.onViewAdd(widget.getWidgetConfig());
	    return widget;
	}
	
	@Override
	public ICustomizableView createAndAddView(ViewElementConfig vElementConfig) throws Exception{
		ICustomizableView widget = createView(vElementConfig);
		processRCWidget(widget);
	    tViewChangeListener.onViewAdd(widget.getWidgetConfig());
	    return widget;
	}
	
	@Override
	public ICustomizableView initCreateAndAddView(RCWidgetConfig widgetConfig) throws Exception{
		ICustomizableView widget = createView(widgetConfig);
		processRCWidget(widget);
	    return widget;
	}
	
	private void processRCWidget(ICustomizableView widget){
		widget.setCustomizeModusListener(tCustomizeModusListener);
		widget.setCustomizeModus(tCustomizeModus);	
		tControlElementParentView.addView((View)widget);
		onAdd((View)widget);
	}
	
	@Override
	public ICustomizableView createView(ViewElementConfig vElementConfig) throws Exception{
	    Class<?> c = Class.forName(vElementConfig.getClassPath());
	    Constructor<?> cons = c.getConstructor(Context.class, ViewElementConfig.class);
	    return (ICustomizableView)cons.newInstance(tContext, vElementConfig);
	}
	
	@Override
	public ICustomizableView createView(RCWidgetConfig vElementConfig) throws Exception{
	    Class<?> c = Class.forName(vElementConfig.viewElementConfig.getClassPath());
	    Constructor<?> cons = c.getConstructor(Context.class, RCWidgetConfig.class);
	    return (ICustomizableView)cons.newInstance(tContext, vElementConfig);
	}
	
	@Override
	public void deleteView(View view){
		tViewChangeListener.onViewDelete(((ICustomizableView)view).getWidgetConfig());
		tControlElementParentView.removeView(view);
		onDelete(view);
	}
	
	private void onAdd(View view){
		if(dvmViewListener!=null)
			dvmViewListener.onAdd(view);
	}
	
	private void onDelete(View view){
		if(dvmViewListener!=null)
			dvmViewListener.onDelete(view);
	}

	@Override
	public boolean isInCustomizeModus() {
		return tCustomizeModus;
	}

	@Override
	public void enableCustomizeModus(boolean enabled) {
		tCustomizeModus = enabled;
		updateModusOfCustomizableViews();
		updateBackground();
		if(!tCustomizeModus)
			closeSpecialThings();
	}

	private void updateBackground(){
		if(tCustomizeModus)
			tRootView.findViewById(R.id.configuration_background).setVisibility(View.VISIBLE);
		else
			tRootView.findViewById(R.id.configuration_background).setVisibility(View.INVISIBLE);
	}

	
	private void updateModusOfCustomizableViews(){
    	int size = tControlElementParentView.getChildCount();
    	ICustomizableView view = null;
    	for(int i = 0; i < size; ++i){
    		try{
    			view = (ICustomizableView) tControlElementParentView.getChildAt(i);
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
		ICustomizableView rcWidget = (ICustomizableView)targetView;
		Map<String, String> pm = rcWidget.getProtocolMap();
		Set<String> keySet = pm.keySet();
		for(String key : keySet){
			intent.putExtra(key, pm.get(key));
		}
	}
	
	@Override
	public void editActivityResult(int viewIndex, Intent editChannelIntent){
		Log.d(""+viewIndex, "editActivityResult "+tControlElementParentView.getChildCount());
		ICustomizableView rcw = (ICustomizableView)tControlElementParentView.getChildAt(viewIndex);
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
				tCustomizeModusPopupMenu.dismiss();
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
			tViewChangeListener.onViewChange(((ICustomizableView)view).getWidgetConfig());
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
}

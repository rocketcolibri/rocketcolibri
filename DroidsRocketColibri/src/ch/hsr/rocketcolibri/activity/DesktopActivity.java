/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.activity;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.db.RocketColibriDB;
import ch.hsr.rocketcolibri.db.model.Defaults;
import ch.hsr.rocketcolibri.db.model.RCModel;
import ch.hsr.rocketcolibri.manager.DesktopViewManager;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.manager.listener.ViewChangedListener;
import ch.hsr.rocketcolibri.menu.desktop.DesktopMenu;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.ui_data.input.IUiInputSource;
import ch.hsr.rocketcolibri.ui_data.output.ConnectionState;
import ch.hsr.rocketcolibri.ui_data.output.IUiOutputSinkChangeObserver;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;

/**
 * @author Artan Veliju
 */
public class DesktopActivity extends RCActivity implements IUiOutputSinkChangeObserver{
	private RCModel tModel;
	private SurfaceView surface_view;
	private RocketColibriDB tDB;
	// private Camera mCamera;
	SurfaceHolder.Callback sh_ob = null;
	SurfaceHolder surface_holder = null;
	SurfaceHolder.Callback sh_callback = null;
	private DesktopViewManager tDesktopViewManager;
	
	//sometimes when you come back to this activity and your orientation where in portrait mode
	//the views are setting up on resume while the activity is still animating to landscape
	//therefore we wait until we are in landscape and resume correctly
	private Semaphore orientationChangeLock = new Semaphore(0);
	
	public static final boolean Debugging = false;
	private boolean tIsControlling = false;
	private DesktopMenu tDesktopMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		AbsoluteLayout rootLayer = (AbsoluteLayout) findViewById(R.id.root_layer);
		AbsoluteLayout absolutLayout = (AbsoluteLayout) findViewById(R.id.drag_layer);
		tDesktopViewManager = new DesktopViewManager(this, rootLayer, absolutLayout, new ViewChangedListener() {
			@Override
			public void onViewChange(RCWidgetConfig widgetConfig) {
				rcService.getRocketColibriDB().store(widgetConfig);
			}

			@Override
			public void onViewAdd(RCWidgetConfig widgetConfig) {
				if(tModel.getWidgetConfigs()==null)
					tModel.setWidgetConfigs(new ArrayList<RCWidgetConfig>(1));
				tModel.getWidgetConfigs().add(widgetConfig);
				rcService.getRocketColibriDB().store(tModel);
			}

			@Override
			public void onViewDelete(RCWidgetConfig widgetConfig) {
				tModel.getWidgetConfigs().remove(widgetConfig);
				rcService.getRocketColibriDB().store(tModel);
			}
		});
		tDesktopMenu = tDesktopViewManager.getDesktopMenu();
		
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		surface_view = (SurfaceView) findViewById(R.id.camView);
		if (surface_holder == null) {
			surface_holder = surface_view.getHolder();
		}
		
		//this line is needed because of the SwipeInMenu,
		//there is a bug if no color is set on the surface view
		surface_view.setBackgroundColor(Color.TRANSPARENT);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(rcService!=null){
			rcService.tWifi.disconnectRocketColibriSSID(rcService);
			rcService.tProtocol.cancelOldCommandJob();
		}
		tDesktopViewManager.release();
		tDesktopViewManager = null;
	    System.gc();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
	    	try {
				orientationChangeLock.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	}
	
	boolean setupViewsOnce=true;
	/**
	 * Finds all the views we need and configure them to send click events to the activity.
	 */
	private void setupDesktop() {
		if (setupViewsOnce) {
			if(tModel==null){
				String defaultModelName = getDefaultModelName();
				if(defaultModelName!=null){
					tModel = tDB.fetchRCModelByName(defaultModelName);
					displayModelNameOnDesktopMenu(tModel.getName());
				}else{
					openModelListActivity();
					return;
				}
			}
			if (tModel != null && tModel.getWidgetConfigs()!=null){
				displayModelNameOnDesktopMenu(tModel.getName());
				for (RCWidgetConfig vec : tModel.getWidgetConfigs()) {
					try {
						Log.d(getClassName(), "initCreateAndAddView: "+vec.viewElementConfig.getClassPath());
						tDesktopViewManager.initCreateAndAddView(vec);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			setupViewsOnce = false;
		}
		int size = tDesktopViewManager.getControlElementParentView().getChildCount();
    	ICustomizableView view = null;
    	for(int i = 0; i < size; ++i){
    		try{
    			view = (ICustomizableView) tDesktopViewManager.getControlElementParentView().getChildAt(i);
    			if (view instanceof IUiOutputSinkChangeObserver)
    				rcService.tProtocol.registerUiOutputSinkChangeObserver((IUiOutputSinkChangeObserver)view);
    			if (view instanceof IUiInputSource)
    				rcService.tProtocol.registerUiInputSource((IUiInputSource)view);
    		} catch (Exception e) {e.printStackTrace();}
    	}
    	rcService.tProtocol.registerUiOutputSinkChangeObserver(this);
	}
	
	public IDesktopViewManager getDesktopViewManager(){
		return tDesktopViewManager;
	}
	
	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msg String
	 * @return void
	 */
	
	public void toast (String msg){
	    Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
	}
	/**
	 * Send a message to the debug log and display it using Toast.
	 */
	
	public void trace (String msg) {
	    if (!Debugging) return;
	    Log.d ("DesktopActivity", msg);
	    toast (msg);
	}
	
	public void openModelListActivity(){
		tDesktopMenu.onModelListOpen();
		Intent i = new Intent(this, ModelListActivity.class);
		i.putExtra(RCConstants.FLAG_ACTIVITY_RC_MODEL, getDefaultModelName());
		startActivityForResult(i, RCConstants.RC_MODEL_RESULT_CODE);

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent editChannelIntent) {
		if (requestCode == RCConstants.RC_MODEL_RESULT_CODE && editChannelIntent!=null) {
			String modelName = editChannelIntent.getStringExtra(RCConstants.FLAG_ACTIVITY_RC_MODEL);
			if(modelName!=null){
				setDefaultModelName(modelName);
				releaseDesktop();
				tDesktopViewManager.getControlElementParentView().removeAllViews();
				tModel = tDB.fetchRCModelByName(modelName);
				setupViewsOnce = true;
				setupDesktop();
				tDesktopMenu.animateClose();
				System.gc();
			}
		}else{
			Log.d("", "viewIndex:"+requestCode+" resultCode: "+resultCode);
	        if (resultCode == RESULT_OK) {
	            tDesktopViewManager.editActivityResult(requestCode, editChannelIntent);
	        }
		}
	}

	@Override
	protected void onServiceReady() {
		tDB = rcService.getRocketColibriDB();
		tDesktopMenu.setService(rcService);
		tDesktopMenu.onResume();
		setupDesktop();
	}
    
	@Override
	protected void onPause(){
		orientationChangeLock.drainPermits();
		releaseDesktop();
    	super.onPause();
	}
	
	private void releaseDesktop(){
    	try{
    		rcService.tProtocol.release();
    	}catch(Exception e){}
	}

	@Override
	protected String getClassName() {
		return DesktopActivity.class.getSimpleName();
	}
	
	@Override
	public void onBackPressed() {
		if(!tIsControlling)
			super.onBackPressed();
	}

	@Override
	public void onNotifyUiOutputSink(Object p) {
		ConnectionState data = (ConnectionState)p;
		tIsControlling = ((s.TRY_CONN == data.getState()) || (s.CONN_CONTROL == data.getState()));
	}

	@Override
	public UiOutputDataType getType(){
		return UiOutputDataType.ConnectionState;
	}
	
	public void setDefaultModelName(String name){
		Defaults def = null;
		try{def = (Defaults) tDB.fetch(Defaults.class).getFirst();}catch(Exception e){}
		if(def==null)def = new Defaults();
		def.modelName = name;
		tDB.store(def);
		uitoast(name);
	}
	
	public String getDefaultModelName(){
		Defaults def = null;
		try{def = (Defaults) tDB.fetch(Defaults.class).getFirst();}catch(Exception e){}
		if(def!=null)return def.modelName;
		return null;
	}
	
	private void displayModelNameOnDesktopMenu(final String text){
		runOnUiThread(new Runnable() {public void run() {
			tDesktopMenu.setTextOnBottom(text);	
		}});
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	orientationChangeLock.release();
	    } else {
	    	orientationChangeLock.drainPermits();
	    }
	}
}
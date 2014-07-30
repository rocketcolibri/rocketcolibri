/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import ch.futuretek.json.JsonTransformer;
import ch.futuretek.json.exception.TransformException;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.RocketColibriDefaults;
import ch.hsr.rocketcolibri.db.RocketColibriDB;
import ch.hsr.rocketcolibri.db.model.Defaults;
import ch.hsr.rocketcolibri.db.model.JsonRCModel;
import ch.hsr.rocketcolibri.db.model.RCModel;
import ch.hsr.rocketcolibri.manager.DesktopViewManager;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.manager.listener.ViewChangedListener;
import ch.hsr.rocketcolibri.menu.desktop.DesktopMenu;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.ui_data.output.ConnectionState;
import ch.hsr.rocketcolibri.ui_data.output.IUiOutputSinkChangeObserver;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.widget.IRCWidget;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;

/**
 * @author Artan Veliju
 */
public class DesktopActivity extends RCActivity implements IUiOutputSinkChangeObserver{
	private static final String TAG = "DesktopActivity";
	private RCModel tModel;
	private SurfaceView surface_view;
	private RocketColibriDB tDB;
	// private Camera mCamera;
	SurfaceHolder.Callback sh_ob = null;
	SurfaceHolder surface_holder = null;
	SurfaceHolder.Callback sh_callback = null;
	private DesktopViewManager tDesktopViewManager;
	
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
	
	private void unbindDrawables(View view) {
	       if (view.getBackground() != null) {
	       view.getBackground().setCallback(null);
	       }
	       if (view instanceof ViewGroup) {
	           for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	           unbindDrawables(((ViewGroup) view).getChildAt(i));
	           }
	       ((ViewGroup) view).removeAllViews();
	       }
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
		unbindDrawables(findViewById(R.id.root_layer));
	    System.gc();
	}
	
	//tmp var
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
				}else{
					Intent i = new Intent(this, ModelListActivity.class);
					startActivityForResult(i, RCConstants.RC_MODEL_RESULT_CODE);
					return;
				}
			}
			if (tModel != null && tModel.getWidgetConfigs()!=null)
				for (RCWidgetConfig vec : tModel.getWidgetConfigs()) {
					try {
						Log.d(getClassName(), "initCreateAndAddView: "+vec.viewElementConfig.getClassPath());
						tDesktopViewManager.initCreateAndAddView(vec);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
//			printOutJson();
			setupViewsOnce = false;
		}
		int size = tDesktopViewManager.getControlElementParentView().getChildCount();
    	IRCWidget view = null;
    	for(int i = 0; i < size; ++i){
    		try{
    			view = (IRCWidget) tDesktopViewManager.getControlElementParentView().getChildAt(i);
    			if (view instanceof IUiOutputSinkChangeObserver)
    				rcService.tProtocol.registerUiOutputSinkChangeObserver((IUiOutputSinkChangeObserver)view);
    			rcService.tProtocol.registerUiInputSource(view);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent editChannelIntent) {
		if (requestCode == RCConstants.RC_MODEL_RESULT_CODE && editChannelIntent!=null) {
			String modelName = editChannelIntent.getStringExtra(RCConstants.FLAG_ACTIVITY_RC_MODEL);
			if(modelName!=null){
				showLoading(getString(R.string.loading));
				setDefaultModelName(modelName);
				releaseDesktop();
				tModel = tDB.fetchRCModelByName(modelName);
				setupViewsOnce = true;
				setupDesktop();
				tDesktopMenu.animateClose();
				hideLoading();
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
		tDesktopMenu.setService(rcService) ;
		setupDesktop();
	}

	@Override
	protected void onPause(){
		releaseDesktop();
    	super.onPause();
	}
	
	private void releaseDesktop(){
		int size = tDesktopViewManager.getControlElementParentView().getChildCount();
    	IRCWidget view = null;
    	for(int i = 0; i < size; ++i){
    		try{
    			view = (IRCWidget) tDesktopViewManager.getControlElementParentView().getChildAt(i);
    			if (view instanceof IUiOutputSinkChangeObserver)
    				rcService.tProtocol.unregisterUiOutputSinkChangeObserver((IUiOutputSinkChangeObserver)view);
    			rcService.tProtocol.unregisterUiInputSource(view);
    		} catch (Exception e) {e.printStackTrace();}
    	}
    	tDesktopViewManager.getControlElementParentView().removeAllViews();
    	rcService.tProtocol.unregisterUiOutputSinkChangeObserver(this);
	}
	
	private void printOutJson(){
		List<JsonRCModel> jsons = new ArrayList<JsonRCModel>();
		JsonRCModel j = new JsonRCModel();
		j.model = tModel;
		for(RCWidgetConfig w : tModel.getWidgetConfigs()){
			RocketColibriDefaults.pixelToDp(this.getResources().getDisplayMetrics(), w.viewElementConfig);
		}
		j.process = "insert";
		j.timestamp = "04.07.2014 21:16:00";
		jsons.add(j);
		try {
			Log.d("", new JsonTransformer().transform(jsons));
		} catch (TransformException e) {
			e.printStackTrace();
		}
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
}

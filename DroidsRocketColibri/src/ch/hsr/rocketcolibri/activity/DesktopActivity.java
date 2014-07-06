/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import ch.hsr.rocketcolibri.db.model.JsonRCModel;
import ch.hsr.rocketcolibri.db.model.RCModel;
import ch.hsr.rocketcolibri.manager.DesktopViewManager;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.manager.listener.ViewChangedListener;
import ch.hsr.rocketcolibri.menu.desktop.DesktopMenu;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.view.widget.Circle;
import ch.hsr.rocketcolibri.view.widget.ConnectionStatusWidget;
import ch.hsr.rocketcolibri.view.widget.IRCWidget;
import ch.hsr.rocketcolibri.view.widget.OnChannelChangeListener;
import ch.hsr.rocketcolibri.view.widget.ConnectedUserInfoWidget;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;
import ch.hsr.rocketcolibri.view.widget.VideoStreamWidget;

/**
 * @author Artan Veliju
 */
public class DesktopActivity extends RCActivity{
	private static final String TAG = "DesktopActivity";
	private RCModel tModel;
	private SurfaceView surface_view;
	// private Camera mCamera;
	SurfaceHolder.Callback sh_ob = null;
	SurfaceHolder surface_holder = null;
	SurfaceHolder.Callback sh_callback = null;
	private IDesktopViewManager tDesktopViewManager;
	
	public static final boolean Debugging = false;
	private DesktopMenu tDesktopMenu;
	private OnChannelChangeListener tControlModusListener = new OnChannelChangeListener() {
		public void onChannelChange(int channel, int position) {
			rcService.updateControl(channel, position);;
		}
	};


//  TODO add video stream display here
//	SurfaceHolder.Callback my_callback() {
//		SurfaceHolder.Callback ob1 = new SurfaceHolder.Callback() {
//
//			@Override
//			public void surfaceDestroyed(SurfaceHolder holder) {
//				mCamera.stopPreview();
//				mCamera.release();
//				mCamera = null;
//			}
//
//			@Override
//			public void surfaceCreated(SurfaceHolder holder) {
//				mCamera = Camera.open();
//
//				try {
//					mCamera.setPreviewDisplay(holder);
//				} catch (IOException exception) {
//					mCamera.release();
//					mCamera = null;
//				}
//			}
//
//			@Override
//			public void surfaceChanged(SurfaceHolder holder, int format,
//					int width, int height) {
//				mCamera.startPreview();
//			}
//		};
//		return ob1;
//	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		AbsoluteLayout rootLayer = (AbsoluteLayout) findViewById(R.id.root_layer);
		AbsoluteLayout absolutLayout = (AbsoluteLayout) findViewById(R.id.drag_layer);
		tDesktopViewManager = new DesktopViewManager(this, rootLayer, absolutLayout, tControlModusListener, new ViewChangedListener() {
			@Override
			public void onViewChange(RCWidgetConfig widgetConfig) {
				rcService.getRocketColibriDB().store(widgetConfig);
			}

			@Override
			public void onViewAdd(RCWidgetConfig widgetConfig) {
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
//		TODO
//		sh_callback = my_callback();
//		surface_holder.addCallback(sh_callback);
		
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
		rcService.tWifi.disconnectRocketColibriSSID(rcService);
		rcService.tProtocol.cancelOldCommandJob();
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
	private void setupViews() {
		if (setupViewsOnce) {
			tModel = rcService.getRocketColibriDB().fetchRCModelByName(
					"Test Model");
			if (tModel != null)
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
	protected void onActivityResult(int viewIndex, int resultCode, Intent editChannelIntent) {
		Log.d("", "viewIndex:"+viewIndex+" resultCode: "+resultCode);
        if (resultCode == RESULT_OK) {
            tDesktopViewManager.editActivityResult(viewIndex, editChannelIntent);
        }
	}

	@Override
	protected void onServiceReady() {
		setupViews();
		tDesktopMenu.setService(rcService) ;
		int size = tDesktopViewManager.getControlElementParentView().getChildCount();
    	IRCWidget view = null;
    	for(int i = 0; i < size; ++i){
    		try{
    			view = (IRCWidget) tDesktopViewManager.getControlElementParentView().getChildAt(i);
    			rcService.tProtocol.registerUiOutputSinkChangeObserver(view);
    			rcService.tProtocol.registerUiInputSource(view);
    		} catch (Exception e) {e.printStackTrace();}
    	}
	}

	@Override
	protected void onPause(){
		int size = tDesktopViewManager.getControlElementParentView().getChildCount();
    	IRCWidget view = null;
    	for(int i = 0; i < size; ++i){
    		try{
    			view = (IRCWidget) tDesktopViewManager.getControlElementParentView().getChildAt(i);
    			rcService.tProtocol.unregisterUiOutputSinkChangeObserver(view);
    			rcService.tProtocol.unregisterUiInputSource(view);
    		} catch (Exception e) {e.printStackTrace();}
    	}
    	
	  super.onPause();
	}
	
	private void printOutJson(){
		List<JsonRCModel> jsons = new ArrayList<JsonRCModel>();
		JsonRCModel j = new JsonRCModel();
		j.model = tModel;
		for(RCWidgetConfig w : tModel.getWidgetConfigs()){
			RocketColibriDefaults.pixelToDp(this.getResources().getDisplayMetrics().density, w.viewElementConfig);
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
}

/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.db.model.RCModel;
import ch.hsr.rocketcolibri.manager.DesktopViewManager;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.manager.listener.ViewChangedListener;
import ch.hsr.rocketcolibri.menu.desktop.DesktopMenu;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.view.widget.Circle;
import ch.hsr.rocketcolibri.view.widget.ConnectionStatusWidget;
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

	@Override
	public void onResume() 
	{
	  super.onResume();
	}
	
	@Override
	protected void onPause()
	{
	  super.onPause();
	}
	
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
	
	@Override
	protected void onDestroy() {
		tDesktopViewManager.release();
		tDesktopViewManager = null;
		super.onDestroy();
	}
	
	//tmp var
	boolean setupViewsOnce=true;
	/**
	 * Finds all the views we need and configure them to send click events to the activity.
	 */
	private void setupViews(){
		if(setupViewsOnce){
		tModel = rcService.getRocketColibriDB().fetchRCModelByName("Test Model");
		if(tModel!=null)
		for(RCWidgetConfig vec : tModel.getWidgetConfigs()){
			try {
				tDesktopViewManager.initCreateAndAddView(vec);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		
//			try{
//				ResizeConfig rc;
//				LayoutParams lp;
//				ViewElementConfig elementConfig;
//				View view;
//			    
//			    rc = new ResizeConfig();
//			    rc.maxHeight=745;
//			    rc.minHeight=50;
//			    rc.maxWidth=400;
//			    rc.minWidth=30;
//			    lp = new LayoutParams(100, 100, 50,200);
//			    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.custimizable.CustomizableView", lp, rc);
//			    view = tDesktopViewManager.createAndAddView(elementConfig);
//			    view.setBackgroundColor(Color.CYAN);
//			    
//			    rc = new ResizeConfig();
//			    rc.keepRatio=false;
//			    rc.maxHeight=700;
//			    rc.minHeight=10;
//			    rc.maxWidth=900;
//			    rc.minWidth=10;
//			    lp = new LayoutParams(50, 50, 250, 200);
//			    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.custimizable.CustomizableView", lp, rc);
//			    view = tDesktopViewManager.createAndAddView(elementConfig);
//			    view.setBackgroundColor(Color.RED);
//			    
//			    rc = new ResizeConfig();
//			    rc.keepRatio=true;
//			    rc.maxHeight=900;
//			    rc.minHeight=90;
//			    rc.maxWidth=500;
//			    rc.minWidth=50;
//			    lp = new LayoutParams(70, 70, 400, 200);
//			    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.custimizable.CustomizableView", lp, rc);
//			    view = tDesktopViewManager.createAndAddView(elementConfig);
//			    view.setBackgroundColor(Color.LTGRAY);
//
//			    rc = new ResizeConfig();
//			    rc.keepRatio=true;
//			    rc.maxHeight=500;
//			    rc.minHeight=50;
//			    rc.maxWidth=500;
//			    rc.minWidth=50;
//			    lp = new LayoutParams(380, 380 , 100, 300);
//			    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.widget.Circle", lp, rc);
//	
//			    Circle circleView = (Circle) tDesktopViewManager.createAndAddView(elementConfig);
//			    circleView.getProtocolMap().put(RCConstants.CHANNEL_H, "3");
//			    circleView.getProtocolMap().put(RCConstants.CHANNEL_V, "2");
//			    circleView.updateProtocolMap();
//			    
//			    lp = new LayoutParams(380, 380 , 600, 300);
//			    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.widget.Circle", lp, rc);
//			    circleView = (Circle)tDesktopViewManager.createAndAddView(elementConfig);
//			    circleView.getProtocolMap().put(RCConstants.CHANNEL_H, "0");
//			    circleView.getProtocolMap().put(RCConstants.CHANNEL_V, "1");
//			    circleView.updateProtocolMap();
//	
//			}catch(Exception e){
//				e.printStackTrace();
//			}
		    
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
		tDesktopViewManager.serviceReady(rcService);
	}

	@Override
	protected String getClassName() 
	{
		return TAG;
	}
}

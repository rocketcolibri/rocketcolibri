/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.activity;

import android.graphics.Color;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.manager.DesktopViewManager;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.manager.listener.ViewChangedListener;
import ch.hsr.rocketcolibri.menu.DesktopMenu;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.view.widget.Circle;
import ch.hsr.rocketcolibri.view.widget.ConnectionStatusWidget;
import ch.hsr.rocketcolibri.view.widget.OnChannelChangeListener;
import ch.hsr.rocketcolibri.view.widget.TelemetryWidget;

/**
 * @author Artan Veliju
 */
public class DesktopActivity extends RCActivity
{
	private static final String TAG = "CircleTestActivity";
	private SurfaceView surface_view;
	// private Camera mCamera;
	SurfaceHolder.Callback sh_ob = null;
	SurfaceHolder surface_holder = null;
	SurfaceHolder.Callback sh_callback = null;
	private IDesktopViewManager tDesktopViewManager;
	
	private ConnectionStatusWidget connectionStatusWidget;
	private TelemetryWidget telemetryWidget;
		
	public static final boolean Debugging = false;
	private DesktopMenu tDesktopMenu;
	

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
		

		
		AbsoluteLayout rootLayer = (AbsoluteLayout) findViewById(R.id.root_layer);
		AbsoluteLayout absolutLayout = (AbsoluteLayout) findViewById(R.id.drag_layer);
		tDesktopViewManager = new DesktopViewManager(this, rootLayer, absolutLayout, new ViewChangedListener() {
			@Override
			public void onViewChange(ViewElementConfig viewElementConfig) {
				Log.d("changed", "changed");
				//TODO 
				//rcService.getRocketColibriDB().store(viewElementConfig);
			}
		});
		tDesktopMenu = new DesktopMenu(this, findViewById(R.id.swipeInMenu), tDesktopViewManager);
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
		/**
		RCModel model = rcService.getRocketColibriDB().fetchRCModelByName("Test Model");
		for(ViewElementConfig vec : model.getViewElementConfigs()){
			tDesktopViewManager.createView(vec);
		}
		**/
		if(setupViewsOnce){
			try{
			    ResizeConfig rc = new ResizeConfig();
			    rc.maxHeight=745;
			    rc.minHeight=50;
			    rc.maxWidth=400;
			    rc.minWidth=30;
			    LayoutParams lp = new LayoutParams(100, 100, 50,200);
			    ViewElementConfig elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.custimizable.CustomizableView", lp, rc);
			    View view = tDesktopViewManager.createView(elementConfig);
			    view.setBackgroundColor(Color.CYAN);
			    
			    rc = new ResizeConfig();
			    rc.keepRatio=false;
			    rc.maxHeight=700;
			    rc.minHeight=10;
			    rc.maxWidth=900;
			    rc.minWidth=10;
			    lp = new LayoutParams(50, 50, 250, 200);
			    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.custimizable.CustomizableView", lp, rc);
			    view = tDesktopViewManager.createView(elementConfig);
			    view.setBackgroundColor(Color.RED);
			    
			    rc = new ResizeConfig();
			    rc.keepRatio=true;
			    rc.maxHeight=900;
			    rc.minHeight=90;
			    rc.maxWidth=500;
			    rc.minWidth=50;
			    lp = new LayoutParams(70, 70, 400, 200);
			    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.custimizable.CustomizableView", lp, rc);
			    view = tDesktopViewManager.createView(elementConfig);
			    view.setBackgroundColor(Color.LTGRAY);
			    
			    rc = new ResizeConfig();
			    rc.keepRatio=true;
			    rc.maxHeight=500;
			    rc.minHeight=160;
			    rc.maxWidth=500;
			    rc.minWidth=160;
			    
			    rc = new ResizeConfig();
			    rc.maxHeight=300;
			    rc.minHeight=50;
			    rc.maxWidth=800;
			    rc.minWidth=100;
			    lp = new LayoutParams(600, 100 , 100, 0);
			    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.widget.TelemetryWidget", lp, rc);
			    this.telemetryWidget = (TelemetryWidget) tDesktopViewManager.createView(elementConfig);
			    this.telemetryWidget.setBackgroundColor(Color.CYAN);
			    this.telemetryWidget.setAlpha((float) .5);
			    
			    rc = new ResizeConfig();
			    rc.maxHeight=150;
			    rc.minHeight=50;
			    rc.maxWidth=150;
			    rc.minWidth=50;
			    lp = new LayoutParams(100, 100 , 0, 0);
			    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.widget.ConnectionStatusWidget", lp, rc);
			    this.connectionStatusWidget = (ConnectionStatusWidget) tDesktopViewManager.createView(elementConfig);
			    this.connectionStatusWidget.setAlpha(1);
			    
			    rc = new ResizeConfig();
			    rc.keepRatio=true;
			    rc.maxHeight=500;
			    rc.minHeight=50;
			    rc.maxWidth=500;
			    rc.minWidth=50;
			    lp = new LayoutParams(380, 380 , 100, 300);
			    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.widget.Circle", lp, rc);
	
			    Circle circleView = (Circle) tDesktopViewManager.createView(elementConfig);
			    circleView.setOnHChannelChangeListener(new OnChannelChangeListener ()
				{
					@Override
					public void onChannelChange(int position) 
					{
						Log.d(TAG, "received new H position from meter1:" + position);
						if (rcService != null) rcService.tChannel[3].setControl(position);
					}
				});
			    circleView.setOnVChannelChangeListener(new OnChannelChangeListener ()
				{
					@Override
					public void onChannelChange(int position) 
					{
						Log.d(TAG, "received new V position from meter1:" + position);
						if (rcService != null) rcService.tChannel[2].setControl(position);
					}
				});		
			    
			    lp = new LayoutParams(380, 380 , 600, 300);
			    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.widget.Circle", lp, rc);
			    circleView = (Circle)tDesktopViewManager.createView(elementConfig);
			    
			    circleView.setOnHChannelChangeListener(new OnChannelChangeListener ()
				{
					@Override
					public void onChannelChange(int position) 
					{
						Log.d(TAG, "received new H position from meter2:" + position);
						if (rcService != null) rcService.tChannel[0].setControl(position);
					}
				});
			    circleView.setOnVChannelChangeListener(new OnChannelChangeListener ()
				{
					@Override
					public void onChannelChange(int position) 
					{
						Log.d(TAG, "received new V position from meter2:" + position);
						if (rcService != null) rcService.tChannel[1].setControl(position);
					}
				});
	
			}catch(Exception e){
			}
		    
		    setupViewsOnce = false;
		}
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
	
	public void trace (String msg) 
	{
	    if (!Debugging) return;
	    Log.d ("DesktopActivity", msg);
	    toast (msg);
	}

	@Override
	protected void onServiceReady()
	{
		setupViews();
		tDesktopMenu.setService(rcService) ;
		rcService.registerUiOutputSinkChangeObserver(this.connectionStatusWidget);
		rcService.registerUiOutputSinkChangeObserver(this.telemetryWidget);
	}

	@Override
	protected String getClassName() 
	{
		return TAG;
	}
}

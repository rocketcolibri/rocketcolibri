package ch.hsr.rocketcolibri.activity;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocol;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.e;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.view.widget.Circle;
import ch.hsr.rocketcolibri.view.widget.OnChannelChangeListener;
import ch.hsr.rocketcolibri.view.widget.TelemetryWidget;
import ch.hsr.rocketcolibri.view.widget.ConnectionStatusWidget;

public class DesktopActivity extends RCActivity
{
	private static final String TAG = "CircleTestActivity";
	private SurfaceView surface_view;
	private Camera mCamera;
	SurfaceHolder.Callback sh_ob = null;
	SurfaceHolder surface_holder = null;
	SurfaceHolder.Callback sh_callback = null;
	private IDesktopViewManager tDesktopViewManager;
	
	private ConnectionStatusWidget connectionStatusWidget;
	private TelemetryWidget telemetryWidget;
	
	private static final int CHANGE_TOUCH_MODE_MENU_ID = Menu.FIRST;
	private static final int CONNECT_MENU_ID = Menu.FIRST+1;
	private static final int DISCONNECT_MENU_ID = Menu.FIRST+2;
	private static final int CONTROL_MENU_ID = Menu.FIRST+3;
	private static final int OBSERVE_MENU_ID = Menu.FIRST+4;
	
	public static final boolean Debugging = false;

	
	
	private void updateConnectionStateWidget()
	{
		if(rcService != null)
		{
			connectionStatusWidget.setConnectionState((s)rcService.protocolFsm.getState());
		}		
	}
	
	private void updateTelemetryWidget()
	{
		if(rcService != null)
		{
			if(null == rcService.activeuser)
			{
				telemetryWidget.setTelemetryData("no Operator");
			}
			else
			{
				telemetryWidget.setTelemetryData(rcService.activeuser.getName() +"(" +rcService.activeuser.getIpAddress() +")");
			}
		}	
	}
	
	/**
	 * handler for received Intents for the state machine changes
	 */  
	private BroadcastReceiver mProtocolStateUpdateReceiver = new BroadcastReceiver() 
	{
	  @Override
	  public void onReceive(Context context, Intent intent) 
	  {
		updateConnectionStateWidget();
		updateTelemetryWidget();
		Log.d(TAG, "online message received");
	  }
	};
	
	/**
	 * handler TelemetryUpdate
	 */  
	private BroadcastReceiver mTelemetryUpdateReceiver = new BroadcastReceiver() 
	{
	  @Override
	  public void onReceive(Context context, Intent intent) 
	  {
		updateConnectionStateWidget();
		updateTelemetryWidget();
		Log.d(TAG, "online message received");
	  }
	};

	@Override
	public void onResume() 
	{
	  super.onResume();
	  LocalBroadcastManager.getInstance(this).registerReceiver(mProtocolStateUpdateReceiver, new IntentFilter(RocketColibriProtocol.ActionStateUpdate));
	  LocalBroadcastManager.getInstance(this).registerReceiver(mTelemetryUpdateReceiver, new IntentFilter(RocketColibriProtocol.ActionTelemetryUpdate));
	}
	
	@Override
	protected void onPause()
	{
	  // Unregister since the activity is not visible
	  LocalBroadcastManager.getInstance(this).unregisterReceiver(mProtocolStateUpdateReceiver);
	  LocalBroadcastManager.getInstance(this).unregisterReceiver(mTelemetryUpdateReceiver);
	  super.onPause();
	}
	
	SurfaceHolder.Callback my_callback() {
		SurfaceHolder.Callback ob1 = new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				mCamera = Camera.open();

				try {
					mCamera.setPreviewDisplay(holder);
				} catch (IOException exception) {
					mCamera.release();
					mCamera = null;
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				mCamera.startPreview();
			}
		};
		return ob1;
	}
	
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

		sh_callback = my_callback();
		surface_holder.addCallback(sh_callback);
		

		
		AbsoluteLayout absolutLayout = (AbsoluteLayout) findViewById(R.id.drag_layer);
		tDesktopViewManager = new DesktopViewManager(this, absolutLayout, new ViewChangedListener() {
			@Override
			public void onViewChange(ViewElementConfig viewElementConfig) {
				Log.d("changed", "changed");
				//TODO service.store(viewElementConfig);
			}
		});
		new DesktopMenu(this, findViewById(R.id.swipeInMenu), tDesktopViewManager);
		setupViews();
	}
	
	@Override
	protected void onDestroy() {
		tDesktopViewManager.release();
		tDesktopViewManager = null;
		super.onDestroy();
	}
	/**
	 * Build a menu for the activity.
	 *
	 */    
	
	public boolean onCreateOptionsMenu (Menu menu){
	    super.onCreateOptionsMenu(menu);
	    
	    int order=0;
	    menu.add (0, CHANGE_TOUCH_MODE_MENU_ID, order++, "Change Touch Mode");
	    menu.add (0, CONNECT_MENU_ID, order++, "Connect to RocketColibri");
	    menu.add (0, DISCONNECT_MENU_ID, order++, "Disconnect to RocketColibri");
	    menu.add (0, CONTROL_MENU_ID, order++, "Control");
	    menu.add (0, OBSERVE_MENU_ID, order++, "Observe");
	    return true;
	}
	
	/**
	 * Perform an action in response to a menu item being clicked.
	 *
	 */
	
	public boolean onOptionsItemSelected (MenuItem item){
	    switch (item.getItemId()) {
	      case CHANGE_TOUCH_MODE_MENU_ID:
	    	  tDesktopViewManager.switchCustomieModus();
	        String message = tDesktopViewManager.isInCustomizeModus() ? "Changed touch mode. Drag now starts on long touch (click)." 
	                                              : "Changed touch mode. Drag now starts on touch (click).";
	        Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();
	        return true;

	      case CONNECT_MENU_ID:
		    Toast.makeText (getApplicationContext(), "Try Connect", Toast.LENGTH_LONG).show ();
		    if(rcService != null) rcService.wifi.Connect();
		    return true;

	      case DISCONNECT_MENU_ID:
		    Toast.makeText (getApplicationContext(), "Try Disonnect", Toast.LENGTH_LONG).show ();
		    if(rcService != null) rcService.wifi.Disconnect();
		    return true;
		    
	      case CONTROL_MENU_ID:
	    	  if(rcService != null)
	    	  {
	    		  rcService.protocolFsm.queue(e.E6_USR_CONNECT);
	    		  rcService.protocolFsm.processOutstandingEvents();
	    	  }
	    	  return true;
	      case OBSERVE_MENU_ID:
	    	  if(rcService != null)
	    	  {
	    		  rcService.protocolFsm.queue(e.E7_USR_OBSERVE);
	    		  rcService.protocolFsm.processOutstandingEvents();
	    	  }
	    	  return true;
	    	  
	    	  
	    }
	    return super.onOptionsItemSelected (item);
	}
	
	/**
	 * Finds all the views we need and configure them to send click events to the activity.
	 */
	private void setupViews(){
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
		    rc.keepRatio=false;
		    rc.maxHeight=900;
		    rc.minHeight=50;
		    rc.maxWidth=900;
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
		    this.telemetryWidget.setTelemetryData("Telemetry data");
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
					if (rcService != null) rcService.channel[3].setControl(position);
				}
			});
		    circleView.setOnVChannelChangeListener(new OnChannelChangeListener ()
			{
				@Override
				public void onChannelChange(int position) 
				{
					Log.d(TAG, "received new V position from meter1:" + position);
					if (rcService != null) rcService.channel[2].setControl(position);
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
					if (rcService != null) rcService.channel[0].setControl(position);
				}
			});
		    circleView.setOnVChannelChangeListener(new OnChannelChangeListener ()
			{
				@Override
				public void onChannelChange(int position) 
				{
					Log.d(TAG, "received new V position from meter2:" + position);
					if (rcService != null) rcService.channel[1].setControl(position);
				}
			});

		    
		}catch(Exception e){
		}
	    
	    String message = tDesktopViewManager.isInCustomizeModus() ? "Press and hold to start dragging.": "Touch a view to start dragging.";
	    Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();
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
	  updateConnectionStateWidget();
	  updateTelemetryWidget();
	}

	@Override
	protected String getClassName() {
		return TAG;
	}
}

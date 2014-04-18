package ch.hsr.rocketcolibri.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.manager.DesktopViewManager;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.draggable.DragController;
import ch.hsr.rocketcolibri.view.draggable.DragLayer;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeController;
import ch.hsr.rocketcolibri.view.widget.Circle;

public class DesktopActivity extends Activity{
	private static final String TAG = "CircleTestActivity";
	private SurfaceView surface_view;
	private Camera mCamera;
	SurfaceHolder.Callback sh_ob = null;
	SurfaceHolder surface_holder = null;
	SurfaceHolder.Callback sh_callback = null;
	private IDesktopViewManager tDesktopViewManager;
	private static final int CHANGE_TOUCH_MODE_MENU_ID = Menu.FIRST;
	private static final int CONNECT_MENU_ID = Menu.FIRST+1;
	private static final int DISCONNECT_MENU_ID = Menu.FIRST+2;
	
	public static final boolean Debugging = false;

	// handler for received Intents for the online message event 
	private BroadcastReceiver mOnlineMessageReceiver = new BroadcastReceiver() {
	  @Override
	  public void onReceive(Context context, Intent intent) {
	    // Extract data included in the Intent
		if(rcService != null) rcService.protocol.sendChannelDataCommand();
		Log.d(TAG, "online message received");
	  }
	};

	// handler for received Intents for the offline message event 
	private BroadcastReceiver mOfflineMessageReceiver = new BroadcastReceiver() {
	  @Override
	  public void onReceive(Context context, Intent intent) {
	    // Extract data included in the Intent
		if(rcService != null) rcService.protocol.cancelOldCommandJob();
	    Log.d(TAG, "offline message received");
	  }
	};

	@Override
	public void onResume() 
	{
	  super.onResume();
	  LocalBroadcastManager.getInstance(this).registerReceiver(mOnlineMessageReceiver, new IntentFilter("protocol.online"));
	  LocalBroadcastManager.getInstance(this).registerReceiver(mOfflineMessageReceiver, new IntentFilter("protocol.offline"));
	}

	@Override
	protected void onPause()
	{
	  // Unregister since the activity is not visible
	  LocalBroadcastManager.getInstance(this).unregisterReceiver(mOnlineMessageReceiver);
	  LocalBroadcastManager.getInstance(this).unregisterReceiver(mOfflineMessageReceiver);
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

	Button btnHotter;
	Button btnColder;
	Circle meter1;
	Circle meter2;
	private RocketColibriService rcService;
	private ServiceConnection mRocketColibriService = new ServiceConnection()	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder)
		{
			rcService = ((RocketColibriService.RocketColibriServiceBinder)binder).getService();
		}
		public void onServiceDisconnected(ComponentName className)
		{
			rcService = null;
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		meter1 = (Circle) findViewById(R.id.circle1);
		meter2 = (Circle) findViewById(R.id.circle2);

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

        // Start Rocket ColibriProtocol service
//		Intent intent = new Intent(this, RocketColibriService.class);
//		bindService(intent, mRocketColibriService, Context.BIND_AUTO_CREATE);
//		
//		meter1.setOnHChannelChangeListener(new OnChannelChangeListener ()
//		{
//			@Override
//			public void onChannelChange(int position) 
//			{
//				Log.d(TAG, "received new H position from meter1:" + position);
//				if (rcService != null) rcService.channel[3].setControl(position);
//			}
//		});
//		meter1.setOnVChannelChangeListener(new OnChannelChangeListener ()
//		{
//			@Override
//			public void onChannelChange(int position) 
//			{
//				Log.d(TAG, "received new V position from meter1:" + position);
//				if (rcService != null) rcService.channel[2].setControl(position);
//			}
//		});		
//		meter2.setOnHChannelChangeListener(new OnChannelChangeListener ()
//		{
//			@Override
//			public void onChannelChange(int position) 
//			{
//				Log.d(TAG, "received new H position from meter2:" + position);
//				if (rcService != null) rcService.channel[0].setControl(position);
//			}
//		});
//		meter2.setOnVChannelChangeListener(new OnChannelChangeListener ()
//		{
//			@Override
//			public void onChannelChange(int position) 
//			{
//				Log.d(TAG, "received new V position from meter2:" + position);
//				if (rcService != null) rcService.channel[1].setControl(position);
//			}
//		});
		
		tDesktopViewManager = new DesktopViewManager(this, (MyAbsoluteLayout) findViewById(R.id.drag_layer));
		setupViews();
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
		    LayoutParams lp = new LayoutParams(300, 300, 700, 300);
		    ViewElementConfig elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.custimizable.CustomizableView", lp, rc);
		    View view = tDesktopViewManager.createView(elementConfig);
		    view.setBackgroundColor(Color.CYAN);
		    
		    rc = new ResizeConfig();
		    rc.keepRatio=false;
		    rc.maxHeight=700;
		    rc.minHeight=10;
		    rc.maxWidth=900;
		    rc.minWidth=10;
		    
		    view = new CustomizableView(this);
		    lp = new LayoutParams(500, 300, 100, 200);
		    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.custimizable.CustomizableView", lp, rc);
		    view = tDesktopViewManager.createView(elementConfig);
		    view.setBackgroundColor(Color.RED);
		    
		    rc = new ResizeConfig();
		    rc.keepRatio=false;
		    rc.maxHeight=900;
		    rc.minHeight=120;
		    rc.maxWidth=900;
		    rc.minWidth=40;
		    lp = new LayoutParams(500, 300, 0, 0);
		    elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.custimizable.CustomizableView", lp, rc);
		    view = tDesktopViewManager.createView(elementConfig);
		    view.setBackgroundColor(Color.LTGRAY);
		    
		    rc = new ResizeConfig();
		    rc.keepRatio=true;
		    rc.maxHeight=500;
		    rc.minHeight=160;
		    rc.maxWidth=500;
		    rc.minWidth=160;
//		    meter1.setOnTouchListener(this);
//		    meter1.setResizeConfig(rc);
//		    meter2.setOnTouchListener(this);
//		    meter2.setResizeConfig(rc);
		}catch(Exception e){
		}
	    
	    String message = tDesktopViewManager.isInCustomizeModus() ? "Press and hold to start dragging." 
	                                          : "Touch a view to start dragging.";
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
	
	public void trace (String msg) {
	    if (!Debugging) return;
	    Log.d ("DesktopActivity", msg);
	    toast (msg);
	}
	
	
}

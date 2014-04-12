package ch.hsr.rocketcolibri.activity;

import java.io.IOException;

import org.neodatis.odb.OID;

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
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocol;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.draggable.DragController;
import ch.hsr.rocketcolibri.view.draggable.DragLayer;
import ch.hsr.rocketcolibri.view.resizable.IResizeListener;
import ch.hsr.rocketcolibri.view.resizable.ViewResizer;
import ch.hsr.rocketcolibri.widget.Circle;
import ch.hsr.rocketcolibri.widget.OnChannelChangeListener;

public class DesktopActivity extends Activity implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener {
	private static final String TAG = "CircleTestActivity";
	private SurfaceView surface_view;
	private Camera mCamera;
	SurfaceHolder.Callback sh_ob = null;
	SurfaceHolder surface_holder = null;
	SurfaceHolder.Callback sh_callback = null;

	private DragController mDragController;   // Object that sends out drag-drop events while a view is being moved.
	private DragLayer mDragLayer;             // The ViewGroup that supports drag-drop.
	private boolean customizeModeOn = true;    // If true, it takes a long click to start the drag operation.
	                                                // Otherwise, any touch event starts a drag.
	
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

//		btnHotter = (Button) findViewById(R.id.btnHotter);
//		btnColder = (Button) findViewById(R.id.btnColder);
		meter1 = (Circle) findViewById(R.id.circle1);
		meter2 = (Circle) findViewById(R.id.circle2);

//		btnHotter.setOnClickListener(this);
//		btnColder.setOnClickListener(this);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		surface_view = (SurfaceView) findViewById(R.id.camView);
		if (surface_holder == null) {
			surface_holder = surface_view.getHolder();
		}

		sh_callback = my_callback();
		surface_holder.addCallback(sh_callback);

        // Start Rocket ColibriProtocol service
		Intent intent = new Intent(this, RocketColibriService.class);
		bindService(intent, mRocketColibriService, Context.BIND_AUTO_CREATE);
		
		meter1.setOnHChannelChangeListener(new OnChannelChangeListener ()
		{
			@Override
			public void onChannelChange(int position) 
			{
				Log.d(TAG, "received new H position from meter1:" + position);
				if (rcService != null) rcService.channel[3].setControl(position);
			}
		});
		meter1.setOnVChannelChangeListener(new OnChannelChangeListener ()
		{
			@Override
			public void onChannelChange(int position) 
			{
				Log.d(TAG, "received new V position from meter1:" + position);
				if (rcService != null) rcService.channel[2].setControl(position);
			}
		});		
		meter2.setOnHChannelChangeListener(new OnChannelChangeListener ()
		{
			@Override
			public void onChannelChange(int position) 
			{
				Log.d(TAG, "received new H position from meter2:" + position);
				if (rcService != null) rcService.channel[0].setControl(position);
			}
		});
		meter2.setOnVChannelChangeListener(new OnChannelChangeListener ()
		{
			@Override
			public void onChannelChange(int position) 
			{
				Log.d(TAG, "received new V position from meter2:" + position);
				if (rcService != null) rcService.channel[1].setControl(position);
			}
		});
		
		mDragController = new DragController(this);
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
	 * Handle a click on a view. Tell the user to use a long click (press).
	 *
	 */    
	
	public void onClick(View v){
	    if (customizeModeOn) {
	       // Tell the user that it takes a long click to start dragging.
	       toast ("Press and hold to drag an image.");
	    }
	}
	
	/**
	 * Handle a long click.
	 * If mLongClick only is true, this will be the only way to start a drag operation.
	 *
	 * @param v View
	 * @return boolean - true indicates that the event was handled
	 */    
	
	public boolean onLongClick(View v){
	    if (customizeModeOn) {
	       
	        //trace ("onLongClick in view: " + v + " touchMode: " + v.isInTouchMode ());
	
	        // Make sure the drag was started by a long press as opposed to a long click.
	        // (Note: I got this from the Workspace object in the Android Launcher code. 
	        //  I think it is here to ensure that the device is still in touch mode as we start the drag operation.)
	        if (!v.isInTouchMode()) {
	           toast ("isInTouchMode returned false. Try touching the view again.");
	           return false;
	        }
	        return startDrag (v);
	    }
	
	    // If we get here, return false to indicate that we have not taken care of the event.
	    return false;
	}
	
	/**
	 * Perform an action in response to a menu item being clicked.
	 *
	 */
	
	public boolean onOptionsItemSelected (MenuItem item){
	    switch (item.getItemId()) {
	      case CHANGE_TOUCH_MODE_MENU_ID:
	        customizeModeOn = !customizeModeOn;
	        String message = customizeModeOn ? "Changed touch mode. Drag now starts on long touch (click)." 
	                                              : "Changed touch mode. Drag now starts on touch (click).";
	        Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();
	        updateModusOfCustomizableViews();
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
	
	private void updateModusOfCustomizableViews(){
    	int size = mDragLayer.getChildCount();
    	ICustomizableView view = null;
    	for(int i = 0; i < size; ++i){
    		try{
    			view = (ICustomizableView) mDragLayer.getChildAt(i);
    			view.setCustomizeModus(customizeModeOn);
    		}catch(Exception e){
    		}
    	}
	}
	
	//double tab and long click variables
	int clickCount = 0;
	long startTime;
	long endTime;
	long duration;
	static final int MAX_DURATION = 500;
		
	/**
	 * This is the starting point for a drag operation if mLongClickStartsDrag is false.
	 * It looks for the down event that gets generated when a user touches the screen.
	 * Only that initiates the drag-drop sequence.
	 *
	 */    
	
	public boolean onTouch (View v, MotionEvent ev){
		if (!customizeModeOn) return false;
        switch(ev.getAction() & MotionEvent.ACTION_MASK)
        {
        case MotionEvent.ACTION_DOWN:
        	if(System.currentTimeMillis() - startTime>MAX_DURATION){
        		clickCount=0;
        	}
            if(clickCount == 0){
            	startTime = System.currentTimeMillis();
            }
            clickCount++;
            break;
        case MotionEvent.ACTION_UP:
            if(clickCount == 2){
            	duration = System.currentTimeMillis() - startTime;
                if(duration<= MAX_DURATION)
                {
                    resizeView(v);
                }
                clickCount = 0;
                duration = 0;
                break;             
            }
        case MotionEvent.ACTION_MOVE:
        	if(clickCount==1){
            	duration = System.currentTimeMillis() - startTime;
            	if(duration>=MAX_DURATION){
            		clickCount = 0;
            		return onLongClick(v);
            	}
            }
        }
        return true;  
	}
	
	/**
	 * Start dragging a view.
	 *
	 */    
	
	public boolean startDrag (View v){
	    // Let the DragController initiate a drag-drop sequence.
	    // I use the dragInfo to pass along the object being dragged.
	    // I'm not sure how the Launcher designers do this.
	    Object dragInfo = v;
	    mDragController.startDrag (v, mDragLayer, dragInfo, DragController.DRAG_ACTION_MOVE);
	    return true;
	}
	
	/**
	 * Finds all the views we need and configure them to send click events to the activity.
	 *
	 */
	private void setupViews(){
	    DragController dragController = mDragController;
	
	    mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
	    mDragLayer.setDragController(dragController);
	    dragController.addDropTarget (mDragLayer);
	
	    CustomizableView view = new CustomizableView(this);
	    LayoutParams lp = new LayoutParams(300, 300, 700, 300);
	    view.setLayoutParams(lp);
	    view.setBackgroundColor(Color.CYAN);
	    view.setOnTouchListener(this);
	    mDragLayer.addView(view);
	    
	    view = new CustomizableView(this);
	    lp = new LayoutParams(500, 300, 100, 200);
	    view.setLayoutParams(lp);
	    view.setBackgroundColor(Color.RED);
	    view.setOnTouchListener(this);
	    mDragLayer.addView(view);
	    
	    view = new CustomizableView(this);
	    lp = new LayoutParams(500, 300, 0, 0);
	    view.setLayoutParams(lp);
	    view.setBackgroundColor(Color.LTGRAY);
	    view.setOnTouchListener(this);
	    mDragLayer.addView(view);
	    
	    meter1.setOnTouchListener(this);
	    meter2.setOnTouchListener(this);
	    
	    String message = customizeModeOn ? "Press and hold to start dragging." 
	                                          : "Touch a view to start dragging.";
	    Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();
	    updateModusOfCustomizableViews();
	}
	
	private void resizeView(View view){
	    final ViewResizer viewResizer = new ViewResizer(this, view, new IResizeListener() {
			
			@Override
			public void done(View resizedView) {
				mDragLayer.addView(resizedView);
			}
		});
	    mDragLayer.addView(viewResizer);
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
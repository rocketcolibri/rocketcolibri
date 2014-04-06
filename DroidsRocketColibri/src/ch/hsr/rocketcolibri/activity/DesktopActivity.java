package ch.hsr.rocketcolibri.activity;

import java.io.IOException;

import org.neodatis.odb.OID;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.R.id;
import ch.hsr.rocketcolibri.R.layout;
import ch.hsr.rocketcolibri.dbService.DBService;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocol;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.draggable.DragController;
import ch.hsr.rocketcolibri.view.draggable.DragLayer;
import ch.hsr.rocketcolibri.widget.Circle;
import ch.hsr.rocketcolibri.widget.OnChannelChangeListener;
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
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DesktopActivity extends Activity implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener {
	private static final String TAG = "CircleTestActivity";
	private SurfaceView surface_view;
	private Camera mCamera;
	SurfaceHolder.Callback sh_ob = null;
	SurfaceHolder surface_holder = null;
	SurfaceHolder.Callback sh_callback = null;

	private DragController mDragController;   // Object that sends out drag-drop events while a view is being moved.
	private DragLayer mDragLayer;             // The ViewGroup that supports drag-drop.
	private boolean mLongClickStartsDrag = true;    // If true, it takes a long click to start the drag operation.
	                                                // Otherwise, any touch event starts a drag.
	
	private static final int CHANGE_TOUCH_MODE_MENU_ID = Menu.FIRST;
	public static final boolean Debugging = false;

	private DBService theDB = null;
	private Boolean isConnected = false;
	
	// handler for received Intents for the online message event 
	private BroadcastReceiver mOnlineMessageReceiver = new BroadcastReceiver() {
	  @Override
	  public void onReceive(Context context, Intent intent) {
	    // Extract data included in the Intent
		if(protocolService != null) protocolService.sendChannelDataCommand();
		Log.d(TAG, "online message received");
	  }
	};

	// handler for received Intents for the offline message event 
	private BroadcastReceiver mOfflineMessageReceiver = new BroadcastReceiver() {
	  @Override
	  public void onReceive(Context context, Intent intent) {
	    // Extract data included in the Intent
		if(protocolService != null) protocolService.cancelOldCommandJob();
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
	private RocketColibriProtocol protocolService;
	private ServiceConnection mRocketColibriProtocolService = new ServiceConnection()	{
		public void onServiceConnected(ComponentName className, IBinder binder)
		{
			protocolService = ((RocketColibriProtocol.RocketColibriProtocolBinder)binder).getService();
			protocolService.ProtocolChannelData(30001, "192.168.200.1", 4);
			
		}
		public void onServiceDisconnected(ComponentName className)
		{
			protocolService = null;
		}
	};
	
	/**
	 * Test class for NeoDatis database connection, will be removed very soon..
	 * @author Haluk
	 *
	 */
	class MyString {
		String strString = null;
		OID myOID = null;
		
		public String getMyString () {
			return strString;
		}

		public void setMyString (String theString) {
			strString = theString;
		}
		
		public OID getOID () {
			return myOID;
		}
		
		public void setOID (OID theOID) {
			myOID = theOID;
		}
	}

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
		
		// Store the DB instance
		theDB = DBService.getInstance();

		// Connect to the DB
		isConnected = theDB.ConnectToDatabase(getApplicationContext());
		
    	MyString myString = new MyString();
    	MyString strStoredString = new MyString();
    	myString.setMyString("My first test");

		if (theDB != null) {
			myString.setOID(theDB.StoreToDatabase(myString));
		}

		if (theDB != null) {
			strStoredString = (MyString)theDB.ReadFromDatabase(myString.getOID());
		}

        theDB.ShowInfo(strStoredString.getMyString() + " - " + strStoredString.getOID());

        // Start Rocket ColibriProtocol service
		Intent intent = new Intent(this, RocketColibriProtocol.class);
		bindService(intent, mRocketColibriProtocolService, Context.BIND_AUTO_CREATE);
		
		meter1.setOnHChannelChangeListener(new OnChannelChangeListener ()
		{
			@Override
			public void onChannelChange(int position) 
			{
				Log.d(TAG, "received new H position from meter1:" + position);
				if (protocolService != null) protocolService.setChannel(3, position);
			}
		});
		meter1.setOnVChannelChangeListener(new OnChannelChangeListener ()
		{
			@Override
			public void onChannelChange(int position) 
			{
				Log.d(TAG, "received new V position from meter1:" + position);
				if (protocolService != null) protocolService.setChannel(2, position);
			}
		});		
		meter2.setOnHChannelChangeListener(new OnChannelChangeListener ()
		{
			@Override
			public void onChannelChange(int position) 
			{
				Log.d(TAG, "received new H position from meter2:" + position);
				if (protocolService != null) protocolService.setChannel(0, position);
			}
		});
		meter2.setOnVChannelChangeListener(new OnChannelChangeListener ()
		{
			@Override
			public void onChannelChange(int position) 
			{
				Log.d(TAG, "received new V position from meter2:" + position);
				if (protocolService != null) protocolService.setChannel(1, position);
			}
		});
		
		mDragController = new DragController(this);
		setupViews();
	}
//
//	@Override
//	public void onClick(View view) {
//		switch (view.getId()) {
//		case R.id.btnColder:
//			break;
//		case R.id.btnHotter:
//			break;
//		}
//		return;
//	}
//
//	@Override
//	public boolean onLongClick(View view) {
//		switch (view.getId()) {
//		case R.id.btnColder:
//			break;
//		case R.id.btnHotter:
//			break;
//		}
//		return true;
//	}
	
	/**
	 * Build a menu for the activity.
	 *
	 */    
	
	public boolean onCreateOptionsMenu (Menu menu){
	    super.onCreateOptionsMenu(menu);
	    
	    menu.add (0, CHANGE_TOUCH_MODE_MENU_ID, 0, "Change Touch Mode");
	    return true;
	}
	
	/**
	 * Handle a click on a view. Tell the user to use a long click (press).
	 *
	 */    
	
	public void onClick(View v){
	    if (mLongClickStartsDrag) {
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
	    if (mLongClickStartsDrag) {
	       
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
	        mLongClickStartsDrag = !mLongClickStartsDrag;
	        String message = mLongClickStartsDrag ? "Changed touch mode. Drag now starts on long touch (click)." 
	                                              : "Changed touch mode. Drag now starts on touch (click).";
	        Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();
	        return true;
	    }
	    return super.onOptionsItemSelected (item);
	}
	
	/**
	 * This is the starting point for a drag operation if mLongClickStartsDrag is false.
	 * It looks for the down event that gets generated when a user touches the screen.
	 * Only that initiates the drag-drop sequence.
	 *
	 */    
	
	public boolean onTouch (View v, MotionEvent ev){
	    // If we are configured to start only on a long click, we are not going to handle any events here.
	    if (mLongClickStartsDrag) return false;
	
	    boolean handledHere = false;
	
	    final int action = ev.getAction();
	
	    // In the situation where a long click is not needed to initiate a drag, simply start on the down event.
	    if (action == MotionEvent.ACTION_DOWN) {
	       handledHere = startDrag (v);
	    }
	    
	    return handledHere;
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
	
	    View view = new View(this);
	    LayoutParams lp = new LayoutParams(300, 300, 700, 300);
	    view.setLayoutParams(lp);
	    view.setBackgroundColor(Color.CYAN);
	    view.setOnLongClickListener(this);
	    mDragLayer.addView(view);
	    
	    view = new View(this);
	    lp = new LayoutParams(500, 300, 100, 200);
	    view.setLayoutParams(lp);
	    view.setBackgroundColor(Color.RED);
	    view.setOnLongClickListener(this);
	    mDragLayer.addView(view);
	    
	    view = new View(this);
	    lp = new LayoutParams(500, 300, 0, 0);
	    view.setLayoutParams(lp);
	    view.setBackgroundColor(Color.RED);
	    view.setOnLongClickListener(this);
	    mDragLayer.addView(view);
	    
	    meter1.setOnLongClickListener(this);
	    meter2.setOnLongClickListener(this);
	    
	    String message = mLongClickStartsDrag ? "Press and hold to start dragging." 
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
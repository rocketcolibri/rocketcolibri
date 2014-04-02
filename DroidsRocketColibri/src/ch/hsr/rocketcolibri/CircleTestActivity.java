package ch.hsr.rocketcolibri;

import java.io.IOException;

import org.neodatis.odb.OID;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.dbService.DBService;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocol;
import ch.hsr.rocketcolibri.widget.Circle;
import ch.hsr.rocketcolibri.widget.OnChannelChangeListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

public class CircleTestActivity extends Activity implements OnClickListener,
		OnLongClickListener {
	private static final String TAG = "CircleTestActivity";
	private SurfaceView surface_view;
	private Camera mCamera;
	SurfaceHolder.Callback sh_ob = null;
	SurfaceHolder surface_holder = null;
	SurfaceHolder.Callback sh_callback = null;

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

		btnHotter = (Button) findViewById(R.id.btnHotter);
		btnColder = (Button) findViewById(R.id.btnColder);
		meter1 = (Circle) findViewById(R.id.circle1);
		meter2 = (Circle) findViewById(R.id.circle2);

		btnHotter.setOnClickListener(this);
		btnColder.setOnClickListener(this);
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
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnColder:
			break;
		case R.id.btnHotter:
			break;
		}
		return;
	}

	@Override
	public boolean onLongClick(View view) {
		switch (view.getId()) {
		case R.id.btnColder:
			break;
		case R.id.btnHotter:
			break;
		}
		return true;
	}
}
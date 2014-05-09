/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.hsr.rocketcolibri.channel.Channel;
import ch.hsr.rocketcolibri.db.RocketColibriDB;
import ch.hsr.rocketcolibri.db.RocketColibriDataHandler;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocol;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.e;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolTelemetryReceiver;
import ch.hsr.rocketcolibri.protocol.WifiConnection;
import ch.hsr.rocketcolibri.view.widget.RCWidget;
import ch.hsr.rocketcolibri.widgetdirectory.IUiOutputSinkChangeObservable;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetDirectoryEntry;
import ch.hsr.rocketcolibri.widgetdirectory.uioutputdata.ConnectionState;
import ch.hsr.rocketcolibri.widgetdirectory.uioutputdata.UiOutputData;
import ch.hsr.rocketcolibri.widgetdirectory.uioutputdata.UserData;
import ch.hsr.rocketcolibri.widgetdirectory.uioutputdata.VideoUrl;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * @short Service with all components that must be available during the entire App life cycle
 * 
 * Responsibilities:
 * - Provide a binder for the activities
 * - holds protocol and wifi connection objects
 * - holds DBService object
 * - updates RCWidgets with changeds telemetry data
 */
public class RocketColibriService extends Service implements IUiOutputSinkChangeObservable {
	final String TAG = this.getClass().getName();
	public static final int NOF_CHANNEL = 8;
	// used by RCActivity
	public static volatile boolean tRunning;
	private final IBinder tBinder = new RocketColibriServiceBinder();
	private HashMap<UiOutputDataType, List<RCWidget>> tUiOutputSinkChangeObserver;

	// GUI Widget collection
	List <WidgetDirectoryEntry> tWidgetDirectory= new ArrayList<WidgetDirectoryEntry>();
	
	// reference to the protocol components
	public RocketColibriProtocolFsm tProtocolFsm;
	public RocketColibriProtocol tProtocol;
	public RocketColibriProtocolTelemetryReceiver tTelemetryReceiver;

	// database
	private RocketColibriDB tRocketColibriDB;

	
	// --- UI Input Sink
	// ..  Channels
	public Channel[] tChannel = {new Channel(), new Channel(), new Channel(), new Channel(), 
			                     new Channel(), new Channel(), new Channel(), new Channel()};
	// ..  used for Wifi connect / disconnect
	public WifiConnection tWifi;
	
	// --- Ui Output Source data
	//  .. data about the users connected to the servo controller
	public UserData tUsers;
	//  .. state of the connection between RocketColibri and Servo Controller
	public ConnectionState tConnState;
	//  .. URL containing the video stream
	public VideoUrl tVdeoUrl;


	@Override
	public IBinder onBind(Intent intent) 
	{
		return tBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "RocketColibriService started");
		RocketColibriService.tRunning = true;
		// create a protocol instance
		this.tProtocolFsm = new RocketColibriProtocolFsm(s.DISC);
		this.tProtocol = new RocketColibriProtocol(tProtocolFsm, this);
		this.tTelemetryReceiver = new RocketColibriProtocolTelemetryReceiver(this, 30001);
		this.tProtocol.setChannels(tChannel);
		this.tWifi = new WifiConnection();

		// list all available Widgets here: 
		this.tWidgetDirectory.add(new WidgetDirectoryEntry("Cross Control", "ch.hsr.rocketcolibri.widget.Circle"));
		this.tWidgetDirectory.add(new WidgetDirectoryEntry("Connection Status", "ch.hsr.rocketcolibri.widget.ConnectionStatusWidget"));
		this.tWidgetDirectory.add(new WidgetDirectoryEntry("User Info", "ch.hsr.rocketcolibri.widget.ConnectedUserInfoWidget"));

		// observer map
		tUiOutputSinkChangeObserver = new HashMap<UiOutputDataType, List<RCWidget>>();
		for (UiOutputDataType type : UiOutputDataType.values()) 
			tUiOutputSinkChangeObserver.put(type, new ArrayList<RCWidget>());

		tUsers = new UserData();
		tConnState = new ConnectionState();
		tVdeoUrl = new VideoUrl();

		// create database instance
		tRocketColibriDB = new RocketColibriDB(this);
		try {
			//read rc.db and update the users client db
			new RocketColibriDataHandler(this, tRocketColibriDB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// UiSink data notification thread
		new Thread(new Runnable() {
			private void sendToAll(UiOutputData data)	{
				if(data.getAndResetNotifyFlag()) {
	            	for(RCWidget observer : tUiOutputSinkChangeObserver.get(data.getType())) {
	        			// select the right list and object depending on the type
	        			observer.onNotifyUiOutputSink(data);
	        		}        		
	        	}
			}
		
		    @Override
		    public void run() {
	        	while(true) {
	        		try {
	        			sendToAll(tUsers);
	        			Thread.sleep(100, 0);
	        		
	        			sendToAll(tConnState);
	        			Thread.sleep(100, 0);
	        		
	        			sendToAll(tVdeoUrl);
	        			Thread.sleep(100, 0);
					} 
	        		catch (InterruptedException e) {
						e.printStackTrace();
					}
	        	}
		    }
		}).start();
   }
	 
    @Override
    public void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        
        tRocketColibriDB.close();
        tRocketColibriDB = null;
        tRunning = false;
    }

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}
    
    public RocketColibriDB getRocketColibriDB() {
    	return tRocketColibriDB;
    }

	/**
	 * Set the application to the 'Control' state
	 */
	public void setUserControl() {
		this.tProtocolFsm.queue(e.E6_USR_CONNECT);
		this.tProtocolFsm.processOutstandingEvents();
	}
	
	/**
	 * Set the application to the 'Passiv' state
	 */
	public void setUserPassive() {
		this.tProtocolFsm.queue(e.E7_USR_OBSERVE);
		this.tProtocolFsm.processOutstandingEvents();
	}
	
	/**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RocketColibriServiceBinder extends Binder {
    	public RocketColibriService getService() {
            // Return this instance of RocketColibriProtocol so clients can call public methods
            return RocketColibriService.this;
        }
    }

    /** Register a UI output sink (RCWidget) */ 
	@Override
	public void registerUiOutputSinkChangeObserver(RCWidget observer) {
		UiOutputDataType type = observer.getType();
		if(type != UiOutputDataType.None) {
			tUiOutputSinkChangeObserver.get(type).add(observer);
		}
	}

    /** Unregister a UI output sink (RCWidget) */
	@Override
	public void unregisterUiOutputSinkChangeObserver(RCWidget observer) {
		UiOutputDataType type = observer.getType();
		if(type != UiOutputDataType.None) {
			this.tUiOutputSinkChangeObserver.get(type).remove(observer);
		}
	}
}

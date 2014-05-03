package ch.hsr.rocketcolibri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ch.hsr.rocketcolibri.channel.Channel;
import ch.hsr.rocketcolibri.db.RocketColibriDB;
import ch.hsr.rocketcolibri.db.RocketColibriDataHandler;
import ch.hsr.rocketcolibri.protocol.RcOperator;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocol;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.e;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolTelemetryReceiver;
import ch.hsr.rocketcolibri.protocol.WifiConnection;
import ch.hsr.rocketcolibri.view.widget.RCWidget;
import ch.hsr.rocketcolibri.widgetdirectory.IUiSinkChangeObservable;
import ch.hsr.rocketcolibri.widgetdirectory.RCUiSinkType;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetDirectoryEntry;
import ch.hsr.rocketcolibri.widgetdirectory.uisinkdata.ConnectionState;
import ch.hsr.rocketcolibri.widgetdirectory.uisinkdata.UiSinkData;
import ch.hsr.rocketcolibri.widgetdirectory.uisinkdata.UserData;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
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
 *   
 */
public class RocketColibriService extends  Service implements IUiSinkChangeObservable
{
	final String TAG = this.getClass().getName();
	public static volatile boolean running;
	private final IBinder mBinder = new RocketColibriServiceBinder();

	private HashMap<RCUiSinkType, List<RCWidget>> uiSinkChangeObserver;
	BlockingQueue<UiSinkData> uiSinkNotifyQueue;
	public static final int NOF_CHANNEL = 8;

	// GUI Widget collection
	List <WidgetDirectoryEntry> widgetDirectory= new ArrayList<WidgetDirectoryEntry>();
	
	// reference to the protocol components
	public RocketColibriProtocolFsm protocolFsm;
	public RocketColibriProtocol protocol;
	public RocketColibriProtocolTelemetryReceiver telemetryReceiver;
	public WifiConnection wifi;
	
	// channels
	public Channel[] channel = {new Channel(), new Channel(), new Channel(), new Channel(), 
			                     new Channel(), new Channel(), new Channel(), new Channel()};
	
	// telemetry data
	
	// active users
	public UserData users;
	public ConnectionState connState;
	
	
	// video URL
	public String videoUrl = "";
	
	public RocketColibriDB tRocketColibriDB;

	@Override
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		Log.d(TAG, "RocketColibriService started");
		RocketColibriService.running = true;
		// create a protocol instance
		this.protocolFsm = new RocketColibriProtocolFsm(s.DISC);
		this.protocol = new RocketColibriProtocol(protocolFsm, this);
		this.telemetryReceiver = new RocketColibriProtocolTelemetryReceiver(this, 30001);
		this.protocol.setChannels(channel);
		this.wifi = new WifiConnection();

		// list all available Widgets here: 
		this.widgetDirectory.add(new WidgetDirectoryEntry("Cross Control", "ch.hsr.rocketcolibri.widget.Circle"));
		this.widgetDirectory.add(new WidgetDirectoryEntry("Connection Status", "ch.hsr.rocketcolibri.widget.ConnectionStatusWidget"));
		this.widgetDirectory.add(new WidgetDirectoryEntry("Telemetry Widget", "ch.hsr.rocketcolibri.widget.TelemetryWidget"));

		// observer map
		uiSinkChangeObserver = new HashMap<RCUiSinkType, List<RCWidget>>();
		for (RCUiSinkType type : RCUiSinkType.values()) 
			uiSinkChangeObserver.put(type, new ArrayList<RCWidget>());
		
		uiSinkNotifyQueue = new ArrayBlockingQueue<UiSinkData>(128);
		users = new UserData(uiSinkNotifyQueue);
		connState = new ConnectionState(uiSinkNotifyQueue);
		
		// create database instance
		tRocketColibriDB = new RocketColibriDB(this);
		try {
			//read rc.db and update the users client db
			new RocketColibriDataHandler(this, tRocketColibriDB);
		} catch (Exception e) {
			e.printStackTrace();
		}	
   }
	 
    @Override
    public void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        
        tRocketColibriDB.close();
        tRocketColibriDB = null;
        running = false;
    }

    @Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return Service.START_STICKY;
	}
    
    public RocketColibriDB getRocketColibriDB(){
    	return tRocketColibriDB;
    }

	/**
	 * Set the application to the 'Control' state
	 */
	public void setUserControl() 
	{
		this.protocolFsm.queue(e.E6_USR_CONNECT);
		this.protocolFsm.processOutstandingEvents();
	}
	
	/**
	 * Set the application to the 'Passiv' state
	 */
	public void setUserPassive() 
	{
		this.protocolFsm.queue(e.E7_USR_OBSERVE);
		this.protocolFsm.processOutstandingEvents();
	}
	
	/**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RocketColibriServiceBinder extends Binder
    {
    	public RocketColibriService getService() 
    	{
            // Return this instance of RocketColibriProtocol so clients can call public methods
            return RocketColibriService.this;
        }
    }

    // methods for the UI sink observers 
	@Override
	public void registerUiSinkChangeObserver(RCWidget observer) 
	{
		RCUiSinkType type = observer.getType();
		if(type != RCUiSinkType.None)
		{
			uiSinkChangeObserver.get(type).add(observer);
		}
	}

	@Override
	public void unregisterUiSinkChangeObserver(RCWidget observer) 
	{
		RCUiSinkType type = observer.getType();
		if(type != RCUiSinkType.None)
		{
			this.uiSinkChangeObserver.get(type).remove(observer);
		}
	}

	/**
	 * This is the notify thread from where all UiSink notifications are sent to the widgets
	 * 
	 *  TODO this may be replaced to the UI thread
	 */
	public class UiSinkNotifyConsumer implements Runnable
	{
	    @Override
	    public void run() 
	    {
	        try
	        {
	        	UiSinkData notifyData = uiSinkNotifyQueue.take();
	        	for(RCWidget observer : uiSinkChangeObserver.get(notifyData.getType()))
	    		{
	    			// select the right list and object depending on the type
	    			observer.onNotifyUiSink(notifyData);
	    		}
	        }
	        catch(InterruptedException e) 
	        {
	            e.printStackTrace();
	        }
	    }
	}

}

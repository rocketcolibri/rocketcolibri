package ch.hsr.rocketcolibri;

import java.util.ArrayList;
import java.util.List;

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
import ch.hsr.rocketcolibri.widgetdirectory.WidgetDirectoryEntry;
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
	private List <RCWidget> uiSinkChangeObserver = new ArrayList<RCWidget>();
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
	public RcOperator activeuser;
	
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
		this.wifi = new WifiConnection( (WifiManager) getSystemService(Context.WIFI_SERVICE));
		 
		// list all available Widgets here: 
		this.widgetDirectory.add(new WidgetDirectoryEntry("Cross Control", "ch.hsr.rocketcolibri.widget.Circle"));
		this.widgetDirectory.add(new WidgetDirectoryEntry("Connection Status", "ch.hsr.rocketcolibri.widget.ConnectionStatusWidget"));
		this.widgetDirectory.add(new WidgetDirectoryEntry("Telemetry Widget", "ch.hsr.rocketcolibri.widget.TelemetryWidget"));
		
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
		this.uiSinkChangeObserver.add(observer);
	}

	@Override
	public void unregisterUiSinkChangeObserver(RCWidget observer) 
	{
		this.uiSinkChangeObserver.remove(observer);
	}
	
	public void notifyAllUiSinkChangeObserver()
	{
		for(RCWidget observer : this.uiSinkChangeObserver)
		{
			// TODO
			// select the right list and object depending on the type
			observer.onNotifyUiSink(null);
		}
	}
}

package ch.hsr.rocketcolibri;

import ch.hsr.rocketcolibri.dbService.DBService;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocol;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.e;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.protocol.WifiConnection;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
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
public class RocketColibriService extends  Service 
{
	final String TAG = this.getClass().getName();
	private final IBinder mBinder = new RocketColibriServiceBinder();
	
	public static final int NOF_CHANNEL = 8;
	
	// reference to the protocol components
	public RocketColibriProtocolFsm protocolFsm;
	public RocketColibriProtocol protocol;
	public WifiConnection wifi;
	
	// channels
	public Channel[] channel = new Channel[NOF_CHANNEL];
	
	// references to the database service
	public DBService database;

	@Override
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		Log.d(TAG, "started");
		 
		// create a protocol instance
		this.protocolFsm = new RocketColibriProtocolFsm(s.DISC);
		this.protocol = new RocketColibriProtocol(protocolFsm);
		this.wifi = new WifiConnection(protocolFsm, (WifiManager) getSystemService(Context.WIFI_SERVICE));
		 
		// create database instance
		// TODO
		//this.database = new
   }
	 
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return Service.START_STICKY;
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
	public void setUserPassive() {
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
}

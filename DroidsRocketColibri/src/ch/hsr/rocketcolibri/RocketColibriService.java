/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import ch.hsr.rocketcolibri.db.RocketColibriDB;
import ch.hsr.rocketcolibri.db.RocketColibriDataHandler;
import ch.hsr.rocketcolibri.protocol.RCProtocol;
import ch.hsr.rocketcolibri.protocol.RCProtocolUdp;
import ch.hsr.rocketcolibri.protocol.WifiConnection;
import ch.hsr.rocketcolibri.view.widget.Circle;
import ch.hsr.rocketcolibri.view.widget.ConnectedUserInfoWidget;
import ch.hsr.rocketcolibri.view.widget.ConnectionStatusWidget;
import ch.hsr.rocketcolibri.view.widget.DefaultViewElementConfigRepo;
import ch.hsr.rocketcolibri.view.widget.AnalogStickWidget;
import ch.hsr.rocketcolibri.view.widget.RotaryKnobWidget;
import ch.hsr.rocketcolibri.view.widget.SwitchWidget;
import ch.hsr.rocketcolibri.view.widget.VideoStreamWidget;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetEntry;

/**
 * @short Service with all components that must be available during the entire App life cycle
 * 
 * Responsibilities:
 * - Provide a binder for the activities
 * - holds protocol and wifi connection objects
 * - holds DBService object
 */
public class RocketColibriService extends Service  {
	final String TAG = this.getClass().getName();
	public static final int NOF_CHANNEL = 8;

	public static volatile boolean tRunning;
	private final IBinder tBinder = new RocketColibriServiceBinder();

	// GUI Widget collection
	List <WidgetEntry> tWidgetDirectory= new ArrayList<WidgetEntry>();

	public RCProtocol tProtocol;

	private RocketColibriDB tRocketColibriDB;

	public WifiConnection tWifi;

	@Override
	public IBinder onBind(Intent intent) {
		return tBinder;
	}
	
	private String getUserName(){
		AccountManager accountManager = AccountManager.get(this);
		try{
	 	    Account[] accounts =  accountManager.getAccountsByType("com.google");
	 	    return accounts[0].name;
		}catch(Exception e){
			return "unknown";
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "RocketColibriService started");
		RocketColibriService.tRunning = true;
		DefaultViewElementConfigRepo.getInstance(this);
		this.tWifi = new WifiConnection();
		tProtocol = new RCProtocolUdp(getUserName()) ;
		// list all available Widgets here: 
		this.tWidgetDirectory.add(new WidgetEntry("Analog Stick", AnalogStickWidget.class.getName(), AnalogStickWidget.getDefaultViewElementConfig()));
		//this.tWidgetDirectory.add(new WidgetEntry("Cross Control", Circle.class.getName(), Circle.getDefaultViewElementConfig()));
		this.tWidgetDirectory.add(new WidgetEntry("Connection Status", ConnectionStatusWidget.class.getName(), ConnectionStatusWidget.getDefaultViewElementConfig()));
		this.tWidgetDirectory.add(new WidgetEntry("User Info", ConnectedUserInfoWidget.class.getName(), ConnectedUserInfoWidget.getDefaultViewElementConfig()));
		this.tWidgetDirectory.add(new WidgetEntry("Video Stream", VideoStreamWidget.class.getName(), VideoStreamWidget.getDefaultViewElementConfig()));
		this.tWidgetDirectory.add(new WidgetEntry("Switch", SwitchWidget.class.getName(), SwitchWidget.getDefaultViewElementConfig()));
		this.tWidgetDirectory.add(new WidgetEntry("Rotary knob", RotaryKnobWidget.class.getName(), RotaryKnobWidget.getDefaultViewElementConfig()));
		
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
        
        tProtocol.stopNotifiyUiOutputData();
        tProtocol.cancelOldCommandJob();
        
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
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RocketColibriServiceBinder extends Binder {
    	public RocketColibriService getService() {
            // Return this instance of RocketColibriProtocol so clients can call public methods
            return RocketColibriService.this;
        }
    }
	
	public List<WidgetEntry> getWidgetEntries(){
		return tWidgetDirectory;
	}
}

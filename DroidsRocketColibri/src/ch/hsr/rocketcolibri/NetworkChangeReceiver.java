/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri;

import ch.hsr.rocketcolibri.protocol.WifiConnection;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Artan Veliju
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
	final String TAG = NetworkChangeReceiver.class.getSimpleName();
	
    /**
     * Checks the connection to the ServoController which has the SSID RocketColibri
     * @return true if connected, false if not
     */
	public boolean isConnectedToRocketColibri(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo currentWifi = wifiManager.getConnectionInfo();
		boolean connected = true;
		if (currentWifi != null) {
			String ssid = currentWifi.getSSID();
			if (ssid != null)
				connected = ssid.equals(WifiConnection.networkSSID);
		}

		if (connected)
			Log.d(TAG, "RocketColibri connected");
		else
			Log.d(TAG, "RocketColibri not connected");

		return connected;
	}

	public boolean getConnectivityStatus(Context context) 
    {
    	boolean retval = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();       
        if (null != activeNetwork){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
            	retval = isConnectedToRocketColibri(context);
            }
        }
        return retval;
    }
	
	
	@Override
	public void onReceive(final Context context, final Intent intent) {
		try{
			IBinder serviceBinder = peekService(context, new Intent(context, RocketColibriService.class));
			RocketColibriService rcService = ((RocketColibriService.RocketColibriServiceBinder) serviceBinder).getService();

			if (getConnectivityStatus(context)) {
				rcService.tProtocol.eventConnectionEstablished();
			} else {
				rcService.tProtocol.eventConnectionInterrupted();
			}
		}catch(NullPointerException e){
		}

	}
}

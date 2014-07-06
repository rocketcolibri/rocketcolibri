/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.protocol;

import java.util.ConcurrentModificationException;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * @short initiates and handles to the RocketColibri Wifi network
 * 
 * Responsibilities:
 * - Creates a network configuration for the RocketColibri network,
 * - Listen to connection broadcast and checks if connection to RocketColibri is connected/disconnectd
 * - Generates events for the events for the protocol state machine
 * - provides Connect / Disconnect function to start / stop the the connection
 * 
 * @see inspired by http://viralpatel.net/blogs/android-internet-connection-status-network-change/
 * @author lorenz
 *
 */
public class WifiConnection 
{
	final static String TAG = "WifiConnection";
	public final static String networkSSID = "\"RocketColibri\"";
	private String oldSSID;
	
	/**
	 * Try to establish a connection to the RocketColibri network
	 */
	public void connectRocketColibriSSID(Context context)
	{
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		// get the currently connected Wifi 
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		oldSSID = wifiInfo.getSSID();
		
	    wifiManager.disconnect(); 
	    // create the network configuration for the RocketColibri network
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = networkSSID ;   // Please note the quotes. String should contain ssid in quotes
		conf.preSharedKey = "\""+"1234567890"+"\"";
		conf.status = WifiConfiguration.Status.ENABLED;
		conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        
		 wifiManager.addNetwork(conf);
		 int netId = wifiManager.addNetwork(conf);
	     wifiManager.enableNetwork(netId, true);
	     wifiManager.setWifiEnabled(true);
	     wifiManager.reconnect(); 
	}

	/**
	 * Try to establish a connection to the RocketColibri network
	 */
	public void disconnectRocketColibriSSID(Context context)
	{
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
		if(list==null)return;
		int disconnectId = -1;
		int reconnectId = -1;
		try {
			for( WifiConfiguration i : list ) {
				if(i.SSID != null) {
					if(i.SSID.equals( networkSSID )) 
						disconnectId = i.networkId;
					else if(i.SSID.equals( oldSSID ))
			    		reconnectId = i.networkId;
				}
			}
	        wifiManager.disconnect();
	        if(disconnectId > 0) {
	        	wifiManager.disableNetwork(disconnectId);
	        	wifiManager.removeNetwork(disconnectId);
	            Log.d(TAG, "RocketColibri network disconnected an removed");
	        }
	        if(reconnectId > 0) {
	        	wifiManager.setWifiEnabled(true);
			    wifiManager.enableNetwork(reconnectId, true);
		        Log.d(TAG, "Reconnected Wifi");
	        }
	        wifiManager.reconnect();
		} catch (ConcurrentModificationException e) {
			Log.d(TAG, "WifiManager problem occured");
		}
	}
}

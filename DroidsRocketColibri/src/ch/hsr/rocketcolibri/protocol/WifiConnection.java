package ch.hsr.rocketcolibri.protocol;

import java.util.List;

import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.e;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
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

	/**
	 * Try to establish a connection to the RocketColibri network
	 */
	public void Connect(Context context)
	{
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
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
	public void Disconnect(Context context)
	{
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
		for( WifiConfiguration i : list ) 
		{
		    if(i.SSID != null && i.SSID.equals( networkSSID )) 
		    {
		         wifiManager.disconnect();
		         wifiManager.disableNetwork(i.networkId);
		         wifiManager.removeNetwork(i.networkId);
		         wifiManager.setWifiEnabled(true);
		         Log.d(TAG, "RocketColibri network disconnected an removed");
		         break;
		    }           
		}
	}
}

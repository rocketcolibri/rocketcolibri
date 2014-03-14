package ch.hsr.rocketcolibri.protocol;

import java.io.IOException; 
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class RocketColibriProtocol extends  Service
{
	public static final int MAX_CHANNEL_VALUE = 1000;
	public static final int MIN_CHANNEL_VALUE = 0;
	private static final long CHECK_CONNECTION_INTERVAL = 3 * 1000; // 3 seconds
	private final String SSID_NAME = new String("RocketColibri");
	private final String SSID_NAME_ALT = new String("gg"); // alternative SSID
	private final IBinder mBinder = new RocketColibriProtocolBinder(); 

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RocketColibriProtocolBinder extends Binder
    {
    	public RocketColibriProtocol getService() {
            // Return this instance of RocketColibriProtocol so clients can call public methods
            return RocketColibriProtocol.this;
        }
    }

	final String TAG = this.getClass().getName();
	int port;
	InetAddress address;
	DatagramSocket channelDataSocket;
	private int sequenceNumber;
	private int[] allChannels;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private Future<?> executorFuture=null;
	private Timer mTimer = null;
	private Handler mHandler = new Handler();
	private boolean isConnected = false; 
	
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
        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new CheckRocketColibriConnection(), 0, CHECK_CONNECTION_INTERVAL);
    }
	 
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return Service.START_STICKY;
	}
	 
	// timer task, checks periodically the connection to the ServoController
    class CheckRocketColibriConnection extends TimerTask 
    {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
            	
                @Override
                public void run() 
                {
                	if(isConnected != isRocketColibriConnected())
                	{
            			isConnected = !isConnected;
            			Intent intent = new Intent();
            			if(isConnected)
            			{
            				Log.d(TAG, "RocketColibri connection changed");
            				intent.setAction("protocol.online");
            			}
            			else
            			{
            				Log.d(TAG, "RocketColibri connection offline");
            				intent.setAction("protocol.offline");
            			}
            			LocalBroadcastManager.getInstance(RocketColibriProtocol.this).sendBroadcast(intent); 
                	}                    
                }
            });
        }
    }
    
    /**
     * Checks the connection to the ServoController which has the SSID RocketColibri
     * @return true if connected, false if not
     */
	public boolean isRocketColibriConnected()
	{
		WifiManager mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    WifiInfo currentWifi = mainWifi.getConnectionInfo();
	    boolean connected = true;
	    if(currentWifi != null)
	    {
	        if(currentWifi.getSSID() != null) 
	            connected =(currentWifi.getSSID().equals(SSID_NAME) || currentWifi.getSSID().equals(SSID_NAME_ALT) );
	    }

        if (connected)
        	Log.d(TAG, "RocketColibri connected");
        else
        	Log.d(TAG, "RocketColibri not connected");

	    return connected;
	}
	
	/**
	 * opens a UDP socket for the communication with the ServoController
	 *  
	 * @param port 30001
	 * @param ia, IP address of the ServoController is normally 192.168.200.1
	 * @param numberOfChannels, how many channels must be controlled by this instance
	 */
	public void ProtocolChannelData(int port, String ia, int numberOfChannels)
	{
		// initialize unicast datagram socket
		this.port = port;
		try 
		{
			this.address = InetAddress.getByName( ia );
		}
		catch (UnknownHostException e1) 
		{
			e1.printStackTrace();
	        Log.d( TAG, "Failed to resolve ip address due to UnknownException: " + e1.getMessage() );       
		}
		try
		{
			channelDataSocket = new DatagramSocket();
		}
		catch (SocketException e) 
		{
			Log.d( TAG, "Failed to create socket due to SocketException: " + e.getMessage() );
			e.printStackTrace();
		} 

		// create & initialize channel array
		allChannels = new int[numberOfChannels];
		for(int c=0; c < numberOfChannels; c++)
			allChannels[c] = 0;
	}

	private void sendJsonMsgString(String msg)
	{
		try {
			this.channelDataSocket.send(new DatagramPacket(msg.getBytes(), msg.length(), this.address, this.port));
		} catch (IOException e) {
			Log.d( TAG, "Failed to send UDP packet due to IOException: " + e.getMessage() ); 
			e.printStackTrace();
		}
	}
	
	/**
	 * sends a ChannelDataCommand every 20ms
	 */
	public void sendChannelDataCommand() 
	{
		cancelOldCommandJob();
		final Runnable every20ms = new Runnable() {
			public void run() {
				JSONObject cdcMsg = new JSONObject();
				try {
					cdcMsg.put("v", 1);
					cdcMsg.put("cmd", "cdc");
					cdcMsg.put("sequence", sequenceNumber++);
					cdcMsg.put("name", "Lorenz");
					JSONArray channels = new JSONArray();
					for (int channel : allChannels)
						channels.put(channel);			
					cdcMsg.put("channels", channels);
				} catch (JSONException e) {
					Log.d( TAG, "Failed to compose message: " + e.getMessage() );
					e.printStackTrace();
				}
				sendJsonMsgString(cdcMsg.toString());
			}
		};
		if(null != this.executorFuture)
			this.executorFuture.cancel(true);
		this.executorFuture = scheduler.scheduleAtFixedRate(every20ms, 0, 20, TimeUnit.MILLISECONDS);
	}

	/**
	 *  cancel the running command Executor
	 */
	public void cancelOldCommandJob()
	{
		if(null != this.executorFuture)
		{
			this.executorFuture.cancel(true);
			this.executorFuture=null;
		}		
	}

	/**
	 * sends a Hello Command every 100ms
	 */
	public void sendHelloCommand() 
	{
		cancelOldCommandJob();
		final Runnable every100ms = new Runnable() 
		{
			public void run() 
			{
				JSONObject cdcMsg = new JSONObject();
				try 
				{
					cdcMsg.put("v", 1);
					cdcMsg.put("cmd", "hello");
					cdcMsg.put("sequence", sequenceNumber++);
					cdcMsg.put("name", "Lorenz");
				}
				catch (JSONException e) 
				{
					Log.d( "JSON", "Failed to compose message: " + e.getMessage() );
					e.printStackTrace();
				}
				sendJsonMsgString(cdcMsg.toString());
			}
		};
		this.executorFuture = scheduler.scheduleAtFixedRate(every100ms, 0, 100, TimeUnit.MILLISECONDS);
	}

	/**
	 * set channel
	 * @param channel channel number
	 * @param channel value 0..1000
	 */
	public void setChannel(int channel, int value)
	{
		if((channel < this.allChannels.length) && (value >= 0 && value <= 1000))
		{
			this.allChannels[channel] = value;
		}
		else
			Log.d(TAG, "invalid channel" + channel + " or value " + value);
	}
}

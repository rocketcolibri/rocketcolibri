package ch.hsr.rocketcolibri.protocol;

import java.io.IOException; 
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ProtocolChannelData {
	final String TAG = this.getClass().getName();
	
	int port;
	InetAddress address;
	DatagramSocket channelDataSocket;
	private int sequenceNumber;
	private int[] allChannels;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private Future<?> executorFuture=null;
	ProtocolChannelData(int port, String ia, int numberOfChannels)
	{
		// initialize unicast datagram socket
		this.port = port;
		try {
			this.address = InetAddress.getByName( ia );
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
	        Log.d( TAG, "Failed to resolve ip address due to UnknownException: " + e1.getMessage() );       
		}
		try {
			channelDataSocket = new DatagramSocket();
		} catch (SocketException e) {
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
	public void sendChannelDataCommand() {
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

	/** cancel the running command Executor */
	private void cancelOldCommandJob()
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
	public void sendHelloCommand() {
		cancelOldCommandJob();
		final Runnable every100ms = new Runnable() {
			public void run() {
				JSONObject cdcMsg = new JSONObject();
				try {
					cdcMsg.put("v", 1);
					cdcMsg.put("cmd", "hello");
					cdcMsg.put("sequence", sequenceNumber++);
					cdcMsg.put("name", "Lorenz");
				} catch (JSONException e) {
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
	 * @param channel value
	 */
	public void setChannel(int channel, int value)
	{
		if(channel < this.allChannels.length)
		{
			this.allChannels[channel] = value;
		}
	}
}

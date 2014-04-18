package ch.hsr.rocketcolibri.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Context;
import android.util.Log;

/**
 * 
 *
 */
public class RocketColibriProtocolTelemetryReceiver 
{	
	final String TAG = this.getClass().getName();
	DatagramSocket receiveSocket;
	private int port;
	private Context context;
	RocketColibriMessage lastTelemetryMsg;
	
	public RocketColibriProtocolTelemetryReceiver(final Context context, int port)
	{	
		this.port = port;
		this.context = context;
		
		new Thread(new Runnable()
		{
			final String TAG =  RocketColibriProtocolTelemetryReceiver.this.TAG;
			RocketColibriMessageFactory msgFactory = new RocketColibriMessageFactory();
			
			@Override
			public void run() 
			{    
				try 
				{	
					DatagramSocket serverSocket = new DatagramSocket(RocketColibriProtocolTelemetryReceiver.this.port);
					byte[] receiveData = new byte[1500];
			        Log.d(TAG, "Listening on udp " + InetAddress.getLocalHost().getHostAddress() + ":" + RocketColibriProtocolTelemetryReceiver.this.port);     
			        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			       
			        while(true)
			        {
			        	serverSocket.receive(receivePacket);
			        	RocketColibriMessage msg = msgFactory.Create(receivePacket, lastTelemetryMsg);
			        	if(! msg.equals(lastTelemetryMsg))
			        	{
			        		lastTelemetryMsg = msg;
			        		lastTelemetryMsg.sendChangeBroadcast(context);
			        	}
			        }
				}
				catch (IOException e)
				{
		              Log.e(TAG, e.toString());
				}
			}
		}).start();
	}
}

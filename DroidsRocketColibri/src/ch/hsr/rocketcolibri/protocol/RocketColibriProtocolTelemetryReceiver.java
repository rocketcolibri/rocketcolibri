package ch.hsr.rocketcolibri.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ch.hsr.rocketcolibri.RocketColibriService;
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
	private static RocketColibriService context;

	public RocketColibriProtocolTelemetryReceiver(final RocketColibriService context, int port)
	{	
		this.port = port;
		RocketColibriProtocolTelemetryReceiver.context = context;

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
			        	RocketColibriMessage msg = msgFactory.Create(receivePacket);
			        	if(null != msg)
			        	{
			        		msg.sendChangeBroadcast(RocketColibriProtocolTelemetryReceiver.context);
			        		msg.sendEvents(RocketColibriProtocolTelemetryReceiver.context);
			        	}
			        	else
			        	{
			        		Log.d(TAG, "invalid message received");
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

package ch.hsr.rocketcolibri.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import android.util.Log;

/**
 * 
 *
 */
public class RocketColibriProtocolTelemetryReceiver 
{	
	final String TAG = this.getClass().getName();
	DatagramSocket receiveSocket;
	int port;
	
	public RocketColibriProtocolTelemetryReceiver(int port)
	{	
		this.port = port;
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

/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.e;
import android.util.Log;

/**
 * Message receiver
 *
 */
public class RocketColibriProtocolTelemetryReceiver 
{	
	final String TAG = this.getClass().getName();
	final static int telemetryTimeout = 3000; // 3s
	DatagramSocket serverSocket;
	private int port;
	private static RocketColibriService context;
	private RocketColibriProtocolFsm tFsm;
	public RocketColibriProtocolTelemetryReceiver(final RocketColibriService context, int port, RocketColibriProtocolFsm fsm)
	{	
		this.port = port;
		tFsm = fsm;
		RocketColibriProtocolTelemetryReceiver.context = context;
	}
				

	public void startReceiveTelemetry()
	{
		// AsyncTask
		new Thread(new Runnable()
		{
			final String TAG =  RocketColibriProtocolTelemetryReceiver.this.TAG;
			RocketColibriMessageFactory msgFactory = new RocketColibriMessageFactory();
			
			@Override
			public void run() 
			{    
				try 
				{	
					serverSocket = new DatagramSocket(RocketColibriProtocolTelemetryReceiver.this.port);
					byte[] receiveData = new byte[1500];
			        Log.d(TAG, "Listening on udp " + InetAddress.getLocalHost().getHostAddress() + ":" + RocketColibriProtocolTelemetryReceiver.this.port);     
			        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			        serverSocket.setSoTimeout(telemetryTimeout);   // set the timeout in millisecounds.
			        while(true)
			        {
			        	try 
			        	{
				        	serverSocket.receive(receivePacket);
				        	RocketColibriMessage msg = msgFactory.Create(receivePacket);
				        	if(null != msg)
				        	{
				        		msg.sendUpdateUiSinkAndSendEvents(RocketColibriProtocolTelemetryReceiver.context, tFsm);
				        	}
				        	else
				        	{
				        		Log.d(TAG, "invalid message received");
				        	}
			        	}
			        	catch (SocketTimeoutException te) 
			        	{
			        		handleTimeout();	        		
			            }
			        }
				}
				catch (IOException e)
				{
					Log.e(TAG, "socket closed");
		            return;
				}
			}
		}).start();
	}
		
	public void stopReceiveTelemetry()
	{
		setTelemetryOffline();
		serverSocket.close();  
	}


	private void setTelemetryOffline() {
		

	}
	
	/**
	 * this actions must be performed if a Telemetry Receive Timeout occurs
	 */
	private void handleTimeout() 
	{
		if (null != RocketColibriProtocolTelemetryReceiver.context.tUsers.getActiveUser())
		{	
			RocketColibriProtocolTelemetryReceiver.context.tUsers.removeAllUsers();
			tFsm.queue(e.E8_TIMEOUT);
			tFsm.processNextEvent();
		}
		RocketColibriProtocolTelemetryReceiver.context.tVdeoUrl.setVideoUrl("");
	}
}

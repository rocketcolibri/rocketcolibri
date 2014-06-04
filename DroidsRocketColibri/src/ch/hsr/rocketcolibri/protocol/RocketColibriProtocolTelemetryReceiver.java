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
	DatagramSocket tServerSocket;
	private int tPort;
	private RocketColibriProtocolFsm tFsm;
	private RCProtocol tProtocol;
	public RocketColibriProtocolTelemetryReceiver(int port, RocketColibriProtocolFsm fsm, RCProtocol proto)
	{	
		tPort = port;
		tProtocol = proto;
		tFsm = fsm;
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
					tServerSocket = new DatagramSocket(RocketColibriProtocolTelemetryReceiver.this.tPort);
					byte[] receiveData = new byte[1500];
			        Log.d(TAG, "Listening on udp " + InetAddress.getLocalHost().getHostAddress() + ":" + RocketColibriProtocolTelemetryReceiver.this.tPort);     
			        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			        tServerSocket.setSoTimeout(telemetryTimeout);   // set the timeout in millisecounds.
			        while(true)
			        {
			        	try 
			        	{
				        	tServerSocket.receive(receivePacket);
				        	RocketColibriMessage msg = msgFactory.Create(receivePacket);
				        	if(null != msg)
				        	{
				        		msg.sendUpdateUiSinkAndSendEvents(tFsm, tProtocol);
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
		tServerSocket.close();  
	}


	private void setTelemetryOffline() {
		

	}
	
	/**
	 * this actions must be performed if a Telemetry Receive Timeout occurs
	 */
	private void handleTimeout() 
	{
		if (null != tProtocol.tUsers.getActiveUser())
		{	
			tProtocol.tUsers.removeAllUsers();
			tFsm.queue(e.E8_TIMEOUT);
			tFsm.processNextEvent();
		}
		tProtocol.tVdeoUrl.setVideoUrl("");
	}
}

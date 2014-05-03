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

import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.channel.Channel;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.protocol.fsm.Action;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

/**
 * @short implementation of the RocketColibri protocol 
 */
public class RocketColibriProtocol 
{
	public static final int MAX_CHANNEL_VALUE = 1000;
	public static final int MIN_CHANNEL_VALUE = 0;
	public static final String ActionStateUpdate = "protocol.updatestate";
	public static final String ActionTelemetryUpdate = "protocol.updatetelemetry";

	
	private RocketColibriProtocolFsm fsm;
	private RocketColibriService service;
	final String TAG = this.getClass().getName();
	int port;
	InetAddress address;
	
	DatagramSocket channelDataSocket;
	private int sequenceNumber;
	private Channel[] allChannels;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private Future<?> executorFuture=null;
	
	private String user;
	
	private String getUserName(Context context)
	{
		AccountManager accountManager = AccountManager.get(context);
 	    Account[] accounts =  accountManager.getAccountsByType("com.google");
 	    return accounts[0].name;	
	}
	
	public RocketColibriProtocol(RocketColibriProtocolFsm fsm, RocketColibriService service) 
	{
		this.service = service;
		this.fsm = fsm;
		// don't send any message
		this.fsm.getStateMachinePlan().entryAction(s.DISC, stopSendMessage);
				
		// send hello message
		this.fsm.getStateMachinePlan().entryAction(s.TRY_CONN, startSendHelloMessage);
		this.fsm.getStateMachinePlan().entryAction(s.CONN_LCK_OUT, startSendHelloMessage);
		this.fsm.getStateMachinePlan().entryAction(s.CONN_OBSERVE, startSendHelloMessage);
		
		// send channel message
		this.fsm.getStateMachinePlan().entryAction(s.CONN_TRY_CONTROL, startSendChannelMessage);
		this.fsm.getStateMachinePlan().entryAction(s.CONN_CONTROL, startSendChannelMessage);
		
		
		// send Broadcast on every state change
		this.fsm.getStateMachinePlan().leaveAction(s.DISC, updateState);
		this.fsm.getStateMachinePlan().leaveAction(s.TRY_CONN, updateState); 
		this.fsm.getStateMachinePlan().leaveAction(s.CONN_OBSERVE, updateState); 
		this.fsm.getStateMachinePlan().leaveAction(s.CONN_LCK_OUT,  updateState);
		this.fsm.getStateMachinePlan().leaveAction(s.CONN_TRY_CONTROL,  updateState);
		this.fsm.getStateMachinePlan().leaveAction(s.CONN_CONTROL, updateState);
		
		user = getUserName(service);
		
		InitSocket();
	}
    
	/**
	 * opens a UDP socket for the communication with the ServoController
	 *  
	 * @param port 30001
	 * @param ia, IP address of the ServoController is normally 192.168.200.1
	 */
	private void InitSocket()
	{
		// initialize unicast datagram socket
		this.port = 30001;
		try 
		{
			this.address = InetAddress.getByName( "192.168.200.1");
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
	}

	public void setChannels(Channel[] channels)
	{
		this.allChannels = channels;
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
		final Runnable every20ms = new Runnable() 
		{
			public void run() 
			{
				JSONObject cdcMsg = new JSONObject();
				try 
				{
					cdcMsg.put("v", 1);
					cdcMsg.put("cmd", "cdc");
					cdcMsg.put("sequence", sequenceNumber++);
					cdcMsg.put("user", user);
					JSONArray channels = new JSONArray();
					for (Channel channel : allChannels)
						channels.put(channel.getChannelValue());			
					cdcMsg.put("channels", channels);
				} 
				catch (JSONException e) 
				{
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
					cdcMsg.put("name", user);
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

	// action from the state machine
	Action<RocketColibriProtocolFsm> startSendHelloMessage = new Action<RocketColibriProtocolFsm>() {
		public void apply(RocketColibriProtocolFsm fsm, Object event,
				Object nextState) 
		{
			Log.d(TAG, "execute action startSendHelloMessage");
			service.telemetryReceiver.startReceiveTelemetry();
			sendHelloCommand();			
		}
	};
	
	Action<RocketColibriProtocolFsm> startSendChannelMessage = new Action<RocketColibriProtocolFsm>() {
		public void apply(RocketColibriProtocolFsm fsm, Object event,
				Object nextState) 
		{
			Log.d(TAG, "execute action startSendChannelMessage");
			sendChannelDataCommand();
		}
	};
	
	Action<RocketColibriProtocolFsm> stopSendMessage = new Action<RocketColibriProtocolFsm>() {
		public void apply(RocketColibriProtocolFsm fsm, Object event,
				Object nextState) 
		{
			Log.d(TAG, "execute action stopSendMessage");
			service.telemetryReceiver.stopReceiveTelemetry();
			cancelOldCommandJob();
		}
	};
	
	Action<RocketColibriProtocolFsm> updateState = new Action<RocketColibriProtocolFsm>() {
		public void apply(RocketColibriProtocolFsm fsm, Object event,	Object nextState) 
		{
			Log.d(TAG, "execute action updateState");
			service.connState.setState((s)nextState);
		}
	};
}

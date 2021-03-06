/**
 * Rocket Colibri © 2014
 */
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

import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.e;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.ui_data.input.UiInputSourceChannel;
import ch.hsr.rocketcolibri.fsm.Action;
import android.util.Log;

/**
 * @short implementation of the RocketColibri protocol 
 */
public class RCProtocolUdp extends RCProtocol{
	public static final int MAX_CHANNEL_VALUE = 1000;
	public static final int MIN_CHANNEL_VALUE = 0;
	public static final int NOF_CHANNELS = 8;
	public static final String ActionStateUpdate = "protocol.updatestate";
	public static final String ActionTelemetryUpdate = "protocol.updatetelemetry";

	
	
	private RocketColibriProtocolFsm tFsm;
	private RocketColibriProtocolTelemetryReceiver tTelemetryReceiver;
	
	final String TAG = this.getClass().getName();
	int port;
	InetAddress address;
	
	DatagramSocket channelDataSocket;
	private int sequenceNumber;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private Future<?> executorFuture=null;
	
	private String tUsername;
	
	
	
	public RCProtocolUdp( String username) 
	{
		this.tUsername = username;
		this.tFsm = new RocketColibriProtocolFsm(s.DISC);
		this.tTelemetryReceiver = new RocketColibriProtocolTelemetryReceiver( tProtcolConfig.getPort(), tFsm, this);
		
		// don't send any message
		this.tFsm.getStateMachinePlan().leaveAction(s.DISC, createSocketAndUpdateSate);
		
		// don't send any message
		this.tFsm.getStateMachinePlan().entryAction(s.DISC, stopSendMessage);
				
		// send hello message
		this.tFsm.getStateMachinePlan().entryAction(s.TRY_CONN, startSendHelloMessage);
		this.tFsm.getStateMachinePlan().entryAction(s.CONN_LCK_OUT, startSendHelloMessage);
		this.tFsm.getStateMachinePlan().entryAction(s.CONN_OBSERVE, startSendHelloMessage);
		
		// send channel message
		this.tFsm.getStateMachinePlan().entryAction(s.CONN_TRY_CONTROL, startSendChannelMessage);
		this.tFsm.getStateMachinePlan().entryAction(s.CONN_CONTROL, startSendChannelMessage);
		
		
		// send Broadcast on every state change
//		this.tFsm.getStateMachinePlan().leaveAction(s.DISC, updateState);
		this.tFsm.getStateMachinePlan().leaveAction(s.TRY_CONN, updateState); 
		this.tFsm.getStateMachinePlan().leaveAction(s.CONN_OBSERVE, updateState); 
		this.tFsm.getStateMachinePlan().leaveAction(s.CONN_LCK_OUT,  updateState);
		this.tFsm.getStateMachinePlan().leaveAction(s.CONN_TRY_CONTROL,  updateState);
		this.tFsm.getStateMachinePlan().leaveAction(s.CONN_CONTROL, updateState);

	}
    
	/**
	 * opens a UDP socket for the communication with the ServoController
	 *  
	 */
	private void InitSocket()
	{
		// initialize unicast datagram socket
		this.port = tProtcolConfig.getPort();
		try 
		{
			this.address = InetAddress.getByName( tProtcolConfig.getIpAddress());
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
	private void CloseSocket()
	{
		channelDataSocket = null;
	}
	
	private void sendJsonMsgString(String msg)
	{
		try {
			Log.d("sent" + this.address, msg);
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
					cdcMsg.put("user", tUsername);
					
					int[] allChannels = {0, 0, 0, 0, 0, 0, 0, 0};
					int[] allChannelsFailsafe = {0, 0, 0, 0, 0, 0, 0, 0};
					for(UiInputSourceChannel c :tChannelList) {
						if(c.getChannelAssignment() < NOF_CHANNELS && 
						   c.getChannelAssignment() >= 0 && 
						   c.getChannelValue() >= MIN_CHANNEL_VALUE &&
						   c.getChannelValue() <= MAX_CHANNEL_VALUE) {
							allChannels[c.getChannelAssignment()] = c.getChannelValue();
							allChannelsFailsafe[c.getChannelAssignment()] = c.getChannelFailsafePosition();
						}
					}
					
					
					JSONArray channels = new JSONArray();
					for (int channel : allChannels)
					{
						channels.put(channel);
					}

					JSONArray failsafe = new JSONArray();
					for (int channelFailsafe : allChannelsFailsafe)
					{
						failsafe.put(channelFailsafe);
					}
					cdcMsg.put("channels", channels);
					cdcMsg.put("failsafe", failsafe);
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
	@Override
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
					cdcMsg.put("user"
							+ "", tUsername);
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
			tTelemetryReceiver.startReceiveTelemetry();
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
			tTelemetryReceiver.setTelemetryOffline();
			cancelOldCommandJob();
			CloseSocket();
			
		}
	};
	
	Action<RocketColibriProtocolFsm> createSocketAndUpdateSate = new Action<RocketColibriProtocolFsm>() {
		public void apply(RocketColibriProtocolFsm fsm, Object event,	Object nextState) 
		{
			InitSocket();
			Log.d(TAG, "execute action createSocketAndUpdateSate");
			tConnState.setState((s)nextState);
		}
	};
	
	Action<RocketColibriProtocolFsm> updateState = new Action<RocketColibriProtocolFsm>() {
		public void apply(RocketColibriProtocolFsm fsm, Object event,	Object nextState) 
		{
			Log.d(TAG, "execute action updateState");
			tConnState.setState((s)nextState);
		}
	};
	
	/**
	 * physical connection established (e.g. Wifi connected)
	 */
	@Override
	public void eventConnectionEstablished() {
		tFsm.queue(e.E1_CONN_SSID);
		tFsm.processOutstandingEvents();
		super.eventConnectionEstablished();
	}
	
	/**
	 * physical connection interrupted
	 */
	@Override
	public void eventConnectionInterrupted() {
		cancelOldCommandJob();
		tFsm.queue(e.E2_DISC_SSID);
		tFsm.processOutstandingEvents();
		super.eventConnectionInterrupted();
	}
	
	/**
	 * User input: start control
	 * @return true if the event is processed
	 */
	@Override
	public boolean eventUserStartControl(){
		tFsm.queue(e.E6_USR_CONNECT);
		tFsm.processOutstandingEvents();
		return true;
	}
	
	/**
	 * User input stop control
	 * @return true if the event is processed
	 */
	@Override
	public boolean eventUserStopControl(){
		tFsm.queue(e.E7_USR_OBSERVE);
		tFsm.processOutstandingEvents();
		return true;
	}
	
	public String getUserName(){
		return tUsername;
	}
}

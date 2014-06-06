package ch.hsr.rocketcolibri.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.view.View;
import ch.hsr.rocketcolibri.ui_data.output.ConnectionState;
import ch.hsr.rocketcolibri.ui_data.output.IUiOutputSinkChangeObservable;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputData;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.ui_data.output.UserData;
import ch.hsr.rocketcolibri.ui_data.output.VideoUrl;
import ch.hsr.rocketcolibri.view.widget.IRCWidget;



public class RCProtocol implements IUiOutputSinkChangeObservable{
	static final int MAX_CHANNEL = 8;
	Channel[] tChannelArray = new Channel[MAX_CHANNEL];
	
	private HashMap<UiOutputDataType, List<IRCWidget>> tUiOutputSinkChangeObserver;
	// --- Ui Output Source data
	//  .. data about the users connected to the servo controller
	public UserData tUsers;
	//  .. state of the connection between RocketColibri and Servo Controller
	public ConnectionState tConnState;
	//  .. URL containing the video stream
	public VideoUrl tVdeoUrl;

	public RCProtocol() {
		
		// UI sink data
		tUsers = new UserData();
		tConnState = new ConnectionState();
		tVdeoUrl = new VideoUrl();
		
		// observer map
		tUiOutputSinkChangeObserver = new HashMap<UiOutputDataType, List<IRCWidget>>();
		for (UiOutputDataType type : UiOutputDataType.values()) 
			tUiOutputSinkChangeObserver.put(type, new ArrayList<IRCWidget>());
		
		// UiSink data notification thread
		new Thread(new Runnable() {
			private void sendToAll(UiOutputData data)	{
				if(data.getAndResetNotifyFlag()) {
	            	for(IRCWidget observer : tUiOutputSinkChangeObserver.get(data.getType())) {
	        			// select the right list and object depending on the type
	        			observer.onNotifyUiOutputSink(data);
	        		}        		
	        	}
			}
		
		    @Override
		    public void run() {
	        	while(true) {
	        		try {
	        			sendToAll(tUsers);
	        			Thread.sleep(100, 0);
	        		
	        			sendToAll(tConnState);
	        			Thread.sleep(100, 0);
	        		
	        			sendToAll(tVdeoUrl);
	        			Thread.sleep(100, 0);
					} 
	        		catch (InterruptedException e) {
						e.printStackTrace();
					}
	        	}
		    }
		}).start();
	}
	
	/**
	 * physical connection established (e.g. Wifi connected)
	 */
	public void eventConnectionEstablished() {
		
	}
	
	/**
	 * physical connection interrupted
	 */
	public void eventConnectionInterrupted() {
		
	}
	
	/**
	 * User input: start control
	 * @return true if the event is processed
	 */
	public boolean eventUserStartControl(){
		return true;
	}
	
	/**
	 * User input stop control
	 * @return true if the event is processed
	 */
	public boolean eventUserStopControl(){
		return true;
	}

    /** Register a UI output sink (RCWidget) */ 
	@Override
	public void registerUiOutputSinkChangeObserver(IRCWidget customizableView) {
		UiOutputDataType type = customizableView.getType();
		if(type != UiOutputDataType.None) {
			tUiOutputSinkChangeObserver.get(type).add(customizableView);
		}

		if(tUsers.getType() == customizableView.getType()) tUsers.notifyThis();
		if(tConnState.getType()== customizableView.getType()) tConnState.notifyThis();
		if(tVdeoUrl.getType() ==  customizableView.getType()) tVdeoUrl.notifyThis();
	}

    /** Unregister a UI output sink (RCWidget) */
	@Override
	public void unregisterUiOutputSinkChangeObserver(IRCWidget observer) {
		UiOutputDataType type = observer.getType();
		if(type != UiOutputDataType.None) {
			this.tUiOutputSinkChangeObserver.get(type).remove(observer);
		}
	}
	
	/**
	 * register an UiInputSource ()
	 * @param channel channel object
	 * @param channelnumber
	 * @return true if channel has been registered successfully
	 */
	public boolean registerUiInputSource(Channel channel , int channelNumber){
		if(null == tChannelArray[channelNumber] || channel == tChannelArray[channelNumber]) {
			tChannelArray[channelNumber] = channel;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * unregister an UiInputSource ()
	 * @param channelnumber
	 * @return true if channel has been unregistered successfully
	 */
	public boolean unregisterUiInputSource(int channelNumber){
		tChannelArray[channelNumber] = null;
		return true;
	}

}

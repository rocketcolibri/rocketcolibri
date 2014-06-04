package ch.hsr.rocketcolibri.protocol;

import android.net.wifi.p2p.WifiP2pManager.Channel;
import ch.hsr.rocketcolibri.view.widget.RCWidget;
import ch.hsr.rocketcolibri.widgetdirectory.IUiOutputSinkChangeObservable;


public class RCProtocol implements IUiOutputSinkChangeObservable {
	static final int MAX_CHANNEL = 8;
	Channel[] tChannel = new Channel[MAX_CHANNEL];

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
	public void registerUiOutputSinkChangeObserver(RCWidget observer) {
		
	}
	
    /** Unregister a UI output sink (RCWidget) */
	@Override	
	public void unregisterUiOutputSinkChangeObserver(RCWidget observer){
		
	}

	
	/**
	 * register an UiInputSource ()
	 * @param channel channel object
	 * @param channelnumber
	 */
	public void registerUiInputSource(Channel channel , int channelnumber){
		
	}
	
	/**
	 * unregister an UiInputSource ()
	 * @param channelnumber
	 */
	public void unregisterUiInputSource(int channelnumber){
		
	}
}

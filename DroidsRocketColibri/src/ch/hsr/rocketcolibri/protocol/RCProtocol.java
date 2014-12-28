package ch.hsr.rocketcolibri.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.hsr.rocketcolibri.ui_data.input.IUiInputSource;
import ch.hsr.rocketcolibri.ui_data.input.UiInputData;
import ch.hsr.rocketcolibri.ui_data.input.UiInputProtocol;
import ch.hsr.rocketcolibri.ui_data.input.UiInputSourceChannel;
import ch.hsr.rocketcolibri.ui_data.output.ConnectionState;
import ch.hsr.rocketcolibri.ui_data.output.IUiOutputSinkChangeObservable;
import ch.hsr.rocketcolibri.ui_data.output.IUiOutputSinkChangeObserver;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputData;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.ui_data.output.UserData;
import ch.hsr.rocketcolibri.ui_data.output.VideoUrl;


public class RCProtocol implements IUiOutputSinkChangeObservable{
	static final int MAX_CHANNEL = 8;
	boolean tIsEnabled;
	protected List<UiInputSourceChannel> tChannelList = new ArrayList<UiInputSourceChannel>();
	public UiInputProtocol tProtcolConfig = new UiInputProtocol();
	
	protected Lock tUiOutputSinkChangeObserverMutex;
	private HashMap<UiOutputDataType, List<IUiOutputSinkChangeObserver>> tUiOutputSinkChangeObserver;
	// --- Ui Output Source data
	//  .. data about the users connected to the servo controller
	public UserData tUsers;
	//  .. state of the connection between RocketColibri and Servo Controller
	public ConnectionState tConnState;
	//  .. URL containing the video stream
	public VideoUrl tVdeoUrl;

	private final ScheduledExecutorService tNotificationScheduler = Executors.newScheduledThreadPool(1);
	ScheduledFuture<?> tNotificationSchedulerFuture;
	
	public RCProtocol() {
		
		// UI sink data
		tUsers = new UserData();
		tConnState = new ConnectionState();
		tVdeoUrl = new VideoUrl();
		
		tUiOutputSinkChangeObserverMutex = new ReentrantLock(true);
		// observer map
		tUiOutputSinkChangeObserverMutex.lock();
		tUiOutputSinkChangeObserver = new HashMap<UiOutputDataType, List<IUiOutputSinkChangeObserver>>();
		initUiOutputChildList();
		tUiOutputSinkChangeObserverMutex.unlock();
	}

	public void startNotifiyUiOutputData() {
		stopNotifiyUiOutputData();
		tNotificationSchedulerFuture = tNotificationScheduler.scheduleAtFixedRate(new Runnable() {
			private void sendToAll(UiOutputData data)	{
				tUiOutputSinkChangeObserverMutex.lock();
				if(data.getAndResetNotifyFlag()) {
	            	for(IUiOutputSinkChangeObserver observer : tUiOutputSinkChangeObserver.get(data.getType())) {
	        			// select the right list and object depending on the type
	        			observer.onNotifyUiOutputSink(data);
	        		}
	        	}
            	tUiOutputSinkChangeObserverMutex.unlock();
			}
		
		    @Override
		    public void run() {
	        	while(true) {
	        		sendToAll(tUsers);
					sendToAll(tConnState);
					sendToAll(tVdeoUrl);
	        	}
		    }
		
		}, 1, 1, TimeUnit.SECONDS);
	}
	
	public void stopNotifiyUiOutputData() {
		try {
			tNotificationSchedulerFuture.cancel(true);	
		} catch (NullPointerException e) {
		    // do something other
		}
	}
	
	/**
	 * physical connection established (e.g. Wifi connected)
	 */
	public void eventConnectionEstablished() {
		startNotifiyUiOutputData();	
	}
	
	/**
	 * physical connection interrupted
	 */
	public void eventConnectionInterrupted() {
		stopNotifiyUiOutputData();
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
	public void registerUiOutputSinkChangeObserver(IUiOutputSinkChangeObserver customizableView) {
		tUiOutputSinkChangeObserverMutex.lock();
		UiOutputDataType type = customizableView.getType();
		
		if(type != UiOutputDataType.None) {
			tUiOutputSinkChangeObserver.get(type).add(customizableView);
		}

		if(tUsers.getType() == customizableView.getType()) tUsers.notifyThis();
		if(tConnState.getType()== customizableView.getType()) tConnState.notifyThis();
		if(tVdeoUrl.getType() ==  customizableView.getType()) tVdeoUrl.notifyThis();
		tUiOutputSinkChangeObserverMutex.unlock();
	}

    /** Unregister a UI output sink (RCWidget) */
	@Override
	public void unregisterUiOutputSinkChangeObserver(IUiOutputSinkChangeObserver observer) {
		tUiOutputSinkChangeObserverMutex.lock();
		UiOutputDataType type = observer.getType();
		if(type != UiOutputDataType.None) {
			this.tUiOutputSinkChangeObserver.get(type).remove(observer);
		}
		tUiOutputSinkChangeObserverMutex.unlock();
	}
	
	/**
	 * register an UiInputSource ()
	 * @param channel channel object
	 * @param channelnumber
	 * @return true if channel has been registered successfully
	 */
	public boolean registerUiInputSource(IUiInputSource uiInputSource) {
		List<UiInputData> list = uiInputSource.getUiInputSourceList();
		if(null != list) {
			
			for(UiInputData c : list)
			{
				 if (c instanceof UiInputSourceChannel) 
				     tChannelList.add((UiInputSourceChannel)c);
				 
				 if (c instanceof UiInputProtocol) 
				     tProtcolConfig =(UiInputProtocol)c; // replace with the values from the widget
			}
		}
		return true;
	}

	/**
	 * unregister an UiInputSource ()
	 * @param channelnumber
	 * @return true if channel has been unregistered successfully
	 */
	public boolean unregisterUiInputSource(IUiInputSource uiInputData){
		List<UiInputData> list = uiInputData.getUiInputSourceList();
		if(null != list) {
			for(UiInputData c : list)
			{
				 if (c instanceof UiInputSourceChannel) 
					 tChannelList.remove(c);

				 if (c instanceof UiInputProtocol) 
				     tProtcolConfig =new UiInputProtocol(); // create a new Object with defaults
			}
		}
		return true;
	}

	public void cancelOldCommandJob() {
		// TODO Auto-generated method stub
		
	}
	
	public void release(){
		tUiOutputSinkChangeObserverMutex.lock();
		tUiOutputSinkChangeObserver.clear();
		initUiOutputChildList();
		tUiOutputSinkChangeObserverMutex.unlock();
		tChannelList.clear();
	}
	
	private void initUiOutputChildList(){
		for (UiOutputDataType type : UiOutputDataType.values()) 
			tUiOutputSinkChangeObserver.put(type, new ArrayList<IUiOutputSinkChangeObserver>());
	}

	public void setIsEnabled(boolean state)
	{
		tIsEnabled = state;
	}
	
	public boolean getIsEnabled()
	{
		return tIsEnabled;
	}
	
}

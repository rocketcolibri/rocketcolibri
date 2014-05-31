/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.widget;

import java.util.Map;

import android.app.Service;
import android.content.Context;
import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;

/**
 * Describes the interface between a widget and the RocketColibriService
 */
public abstract class RCWidget extends CustomizableView {
	protected RCWidgetConfig tWidgetConfig;
	protected OnTouchListener tCustomizeModusListener;
	
	/**This Constructor will be called on loading the whole Model at startup*/
	public RCWidget(Context context, RCWidgetConfig rcWidgetConfig){
		this(context, rcWidgetConfig.viewElementConfig);
		tWidgetConfig = rcWidgetConfig;
		updateProtocolMap();
	}
	
	/**This Constructor will be called on runtime in customize Mode by creating a new Widget*/
    public RCWidget(Context context, ViewElementConfig vElementConfig) {
		super(context, vElementConfig);
		tWidgetConfig = new RCWidgetConfig(null, vElementConfig);
	}

	/** should be overridden from the child */
	public void setControlModusListener(OnChannelChangeListener channelListener) {}
	
	
	@Override
	public void setCustomizeModusListener(OnTouchListener customizeModusListener){
		tCustomizeModusListener = customizeModusListener;
		setOnTouchListener(tCustomizeModusListener);
	}
	
	public RCWidgetConfig getWidgetConfig(){
		tWidgetConfig.viewElementConfig = getViewElementConfig();
		return tWidgetConfig;
	}
	
	public Map<String, String> getProtocolMap(){
		return tWidgetConfig.protocolMap;
	}
	
	public void setProtocolMap(Map<String, String> protocolMap){
		tWidgetConfig.protocolMap = protocolMap;
		updateProtocolMap();
	}
	
	/** Will be called bei the RCWidget itself if the RCWidgetConfig Constuctor is called
	 *  or if setProtocolMap is called.
	 *  If the protocolMap will change by calling getProtocolMap then updateProtocolMap should
	 *  called manually */
	public abstract void updateProtocolMap();
	
	/**
	 * RocketColibriService sends UiSink change notification with this methods
	 * The Object class depends on the return value of getType
	 * @param data
	 */
	public void onNotifyUiOutputSink(Object data){}
	
	/**
	 * Override this function with the Ui Output data type the widget wants to receive with the onNotifyUiOutputSink method. 
	 * @return type
	 */
	public UiOutputDataType getType() {
		return UiOutputDataType.None;
	}
	
	/**
	 * Override the methode if the widget serves as a UI input source for RC channels.
	 * 
	 * @return Return the number of channels (ChannelListernes) that may be attached to the widget.
	 */
	public int getNumberOfChannelListener() {
		return 0;
	}
	
	@Override
	public void notifyServiceReady(Service rcService) {
		try {
			((RocketColibriService) rcService).registerUiOutputSinkChangeObserver(this);
		}catch(Exception e){
		}
	}
}

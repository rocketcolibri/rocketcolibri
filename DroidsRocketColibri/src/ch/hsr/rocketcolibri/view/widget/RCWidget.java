package ch.hsr.rocketcolibri.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.widgetdirectory.RCUiSinkType;

/**
 * Describes the interface between a widget and the RocketColibriService
 */
public class RCWidget extends CustomizableView 
{
	
	public RCWidget(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	}
	
    public RCWidget(Context context, ViewElementConfig cElementConfig) {
		super(context, cElementConfig);
	}


	public RCWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	/**
	 * RocketColibriService sends UiSink change notification with this methods
	 * The Object class depends on the return value of getType
	 * @param data
	 */
	public void onNotifyUiSink(Object data)	{}
	
	/**
	 * Override this function with the UiSink type the widget wants to receive with the onNotifyUiSink method. 
	 * @return type
	 */
	public RCUiSinkType getType() 
	{
		return RCUiSinkType.None;
	}
	
	/**
	 * Override the methode if the widget serves as a UI source for RC channels.
	 * 
	 * @return Return the number of channels (ChannelListernes) that may be attached to the widget.
	 */
	public int getNumberOfChannelListener() 
	{
		return 0;
	}
}

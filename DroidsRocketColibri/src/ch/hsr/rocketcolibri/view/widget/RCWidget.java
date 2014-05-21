/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;

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
	public void onNotifyUiOutputSink(Object data)	{}
	
	/**
	 * Override this function with the Ui Output data type the widget wants to receive with the onNotifyUiOutputSink method. 
	 * @return type
	 */
	public UiOutputDataType getType() 
	{
		return UiOutputDataType.None;
	}
	
	/**
	 * Override the methode if the widget serves as a UI input source for RC channels.
	 * 
	 * @return Return the number of channels (ChannelListernes) that may be attached to the widget.
	 */
	public int getNumberOfChannelListener() 
	{
		return 0;
	}

}

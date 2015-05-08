/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.ui_data.input;

import android.util.Log;

/**
 * @short modell of an RC channel
 * 
 * Responsibilities
 * Calculates the channel value from the input value of widget control.
 */
public class UiInputSourceChannel extends UiInputData
{
	public static final int CHANNEL_UNASSIGNED = -1;
	public static final int MIN_WIDGET_VALUE = 0;
	public static final int MAX_WIDGET_VALUE = 999;

	public static final int MIN_CHANNEL_VALUE = 1;
	public static final int MAX_CHANNEL_VALUE = 1000;
	
	
	/* attributes according to the requirements */
	int assignment;
	int defaultPosition;
	int failsafePosition;
	boolean inverted;
	int minRange;
	int maxRange;
	int trimm;
	boolean expo;
	boolean sticky;
	
	/* widget attributes */
	int tWidgetPosition;
	int tWidgetMinPosition;
	int tWidgetMaxPosition;

	/* current value transmitted for this channel */
	int currentChannelValue;
	
	public UiInputSourceChannel()
	{
		assignment = CHANNEL_UNASSIGNED; // unassigned
		minRange = MIN_CHANNEL_VALUE;
		maxRange = MAX_CHANNEL_VALUE;
		
		tWidgetMinPosition = MIN_WIDGET_VALUE;
		tWidgetMaxPosition = MAX_WIDGET_VALUE;
		tWidgetPosition = 0;
		
		trimm = 0;
		inverted = false;
		failsafePosition = currentChannelValue = defaultPosition = 0;
		expo = false;
	}
	
	/** setter */
	public synchronized void setChannelAssignment(int a) { assignment = a; }
	public synchronized void setChannelMinRange(int r)	{
		if (r <=  MAX_CHANNEL_VALUE && r >= MIN_CHANNEL_VALUE ) {
			minRange = r;
			updateChannelValue();
		}
	}
	public synchronized void setChannelMaxRange(int r)	{
		if (r <=  MAX_CHANNEL_VALUE && r >= MIN_CHANNEL_VALUE )  {
			maxRange = r;
			updateChannelValue();
		}
	}
	
	public synchronized void setChannelTrimm(int t){ 
		trimm = t;
		updateChannelValue();
	}
	
	public synchronized void setChannelInverted(boolean i){ 
		inverted = i;
		updateChannelValue();
	}
	
	public synchronized void setChannelDefaultPosition(int d)	{
		defaultPosition = d;
		updateChannelValue();
	}
	
	
	public synchronized void setWidgetSticky(boolean d){ sticky = d;}
	
	public synchronized void setWidgetExpo(boolean d){ expo = d;}

	public synchronized void setChannelFailsafePosition(int d)	{
		this.failsafePosition = d;
	}

	
	public synchronized void setWidgetRange(int min, int max) {
		assert min < max : "widget range error " + min +".." + max;
		tWidgetMaxPosition = max;
		tWidgetMinPosition = min;
		updateChannelValue();
	}
	
	public synchronized void setWidgetPosition(int position) {
		tWidgetPosition = position;
		updateChannelValue();
		Log.d("Channel", "Channel" + assignment + " widgetpos:" + tWidgetMaxPosition + "("+position+")"+ " channel:" + this.currentChannelValue);
	}
	
	/**
	 * 
	 * @return the widget position between tWidgetMinPosition an tWidgetMaxPosition
	 */
	public synchronized int setWidgetToDefault() {
		// adapt widget ranges to channel range
		tWidgetPosition = (defaultPosition - minRange) * (tWidgetMaxPosition - tWidgetMinPosition)  / (maxRange - minRange) + tWidgetMinPosition; 
		updateChannelValue();
		return tWidgetPosition;
	}
	
	
	/** getter */
	public synchronized int getChannelAssignment() { return assignment; }
	public synchronized int getChannelMinRange()	{ return minRange;	}
	public synchronized int getChannelMaxRange()	{ return maxRange;	}
	public synchronized int getChannelTrimm()	{ return trimm;	}
	public synchronized boolean getChannelInverted()	{ return inverted;	}
	public synchronized int getChannelDefaultPosition() { return defaultPosition;	}
	public synchronized boolean getWidgetSticky() { return sticky;	}
	public synchronized boolean getWidgetExpo() { return expo;	}
	public synchronized int getChannelFailsafePosition() { return failsafePosition;	}		

	/**
	 * get the value to be transmit in the CDC message
	 * @return channel value
	 */
	public synchronized int getChannelValue()
	{
		return currentChannelValue;
	}
	
	int getExpo(int channelInt)
	{
		double channelRange = MAX_CHANNEL_VALUE-MIN_CHANNEL_VALUE;
		Double c = new Double(channelInt);
		double base = (c-(channelRange/2))/(channelRange/4);	
		c =  Math.pow(base, 3.0)  /16*channelRange+channelRange/2-2;
		
		return c.intValue();		
	}
	
	/**
	 * this function is called when an attribute has been changed
	 */
	private void updateChannelValue() {
		if(assignment != CHANNEL_UNASSIGNED) {
			int channel = 0;
			if(tWidgetMinPosition < tWidgetMaxPosition)
			{
				// adjust to range
				if(tWidgetPosition < tWidgetMinPosition)
					tWidgetPosition = tWidgetMinPosition;
				
				if(tWidgetPosition > tWidgetMaxPosition)
					tWidgetPosition = tWidgetMaxPosition;
				
				// adapt widget ranges to channel range
				channel = (tWidgetPosition - tWidgetMinPosition) * (maxRange-minRange) / (tWidgetMaxPosition - tWidgetMinPosition)  + minRange;
			}
			else
			{
				// adjust to range
				if(tWidgetPosition < tWidgetMaxPosition)
					tWidgetPosition = tWidgetMaxPosition;
				
				if(tWidgetPosition > tWidgetMinPosition)
					tWidgetPosition = tWidgetMinPosition;
				
				// adapt widget ranges to channel range
				channel = (tWidgetPosition - tWidgetMaxPosition) * (maxRange-minRange) / ( tWidgetMinPosition- tWidgetMaxPosition)  + minRange;
			}
			
			channel = channel + trimm;
						
			if(channel <  MIN_CHANNEL_VALUE)
				channel = MIN_CHANNEL_VALUE;
			
			if(channel >  MAX_CHANNEL_VALUE)
				channel = MAX_CHANNEL_VALUE;
			
			// check if epox
			if(expo) {
				channel = getExpo(channel);
			}
				
			// check if inverted
			if(inverted) {
				channel  = MAX_CHANNEL_VALUE-channel; 
			}
			// adjust range
			
			currentChannelValue = channel;
		}
		else
			currentChannelValue = 0;
	}
}

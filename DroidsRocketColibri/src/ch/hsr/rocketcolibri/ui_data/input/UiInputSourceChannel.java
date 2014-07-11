/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.ui_data.input;

/**
 * @short modell of an RC channel
 * 
 * Responsibilities
 * Calculates the channel value from the input value of widget control.
 */
public class UiInputSourceChannel
{
	public static final int CHANNEL_UNASSIGNED = -1;
	public static final int MIN_WIDGET_VALUE = 0;
	public static final int MAX_WIDGET_VALUE = 999;

	public static final int MIN_CHANNEL_VALUE = 1;
	public static final int MAX_CHANNEL_VALUE = 1000;
	
	
	/* attributes according to the requirements */
	int assignment;
	int defaultPosition;
	boolean inverted;
	int minRange;
	int maxRange;
	int trimm;
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
		currentChannelValue = defaultPosition = 0;
	}
	
	/** setter */
	public synchronized void setAssignment(int a) { assignment = a; }
	public synchronized void setMinRange(int r)	{ minRange = r;	}
	public synchronized void setMaxRange(int r)	{ maxRange = r;	}
	public synchronized void setTrimm(int t)	{ if(t>-1)trimm = t;	}
	public synchronized void setInverted(boolean i)	{ inverted = i;	}
	public synchronized void setDefaultPosition(int d)	{ defaultPosition = d;	}
	public synchronized void setSticky(boolean d)	{ sticky = d;	}
	
	
	public synchronized void setWidgetRange(int min, int max) {
		assert min < max : "widget range error " + min +".." + max;
		tWidgetMaxPosition = max;
		tWidgetMinPosition = min;
	}
	
	public synchronized void setWidgetPosition(int position) {
		assert position >= tWidgetMinPosition && position <= tWidgetMaxPosition: "widget position " + position +" out of range" + tWidgetMinPosition +".." + tWidgetMaxPosition;
		tWidgetMaxPosition = position;
		updateChannelValue();
	}
	
	/**
	 * 
	 * @return the widget position between tWidgetMinPosition an tWidgetMaxPosition
	 */
	public synchronized int setWidgetToDefault() {
		// TODO
		tWidgetPosition = (tWidgetMinPosition + tWidgetMaxPosition) / 2;
		updateChannelValue();
		return tWidgetPosition;
	}
	
	
	/** getter */
	public synchronized int getAssignment() { return assignment; }
	public synchronized int getMinRange()	{ return minRange;	}
	public synchronized int getMaxRange()	{ return maxRange;	}
	public synchronized int getTrimm()	{ return trimm;	}
	public synchronized boolean getInverted()	{ return inverted;	}
	public synchronized int getDefaultPosition() { return defaultPosition;	}
	public synchronized boolean getSticky() { return sticky;	}
		
	private void updateChannelValue() {
		if(assignment != CHANNEL_UNASSIGNED) {
			int channel = tWidgetPosition;
			// adjust to range
			if(channel < tWidgetMinPosition)
				channel = tWidgetMinPosition;
			
			if(channel > tWidgetMaxPosition)
				channel = tWidgetMaxPosition;
			
			// move to 0
			channel -= tWidgetMinPosition;
			
			// calculate range
			int range = tWidgetMaxPosition - tWidgetMinPosition;
			
			channel = Math.round(MAX_WIDGET_VALUE * (float)channel / (float)range);
			
			channel = channel + trimm;
			if(channel <  MIN_CHANNEL_VALUE)
				channel = MIN_CHANNEL_VALUE;
			
			if(channel >  MAX_CHANNEL_VALUE)
				channel = MAX_CHANNEL_VALUE;
			
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
	
	/**
	 * set the value to the default
	 */
	public synchronized int setToDefaultPosition() {
		return currentChannelValue = defaultPosition;
	}
	
	/**
	 * get the value to be transimte in the cdc message
	 * @return channel value
	 */
	public synchronized int getChannelValue()
	{
		return currentChannelValue;
	}
}

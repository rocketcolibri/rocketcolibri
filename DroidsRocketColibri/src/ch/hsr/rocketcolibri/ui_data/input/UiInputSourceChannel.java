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
	public static final int MIN_VALUE = 0;
	public static final int MAX_VALUE = 1000;

	/* attributes according to the requirements */
	int assignment;
	int defaultPosition;
	boolean inverted;
	int minRange;
	int maxRange;
	int trimm;
	boolean sticky;

	/* current value transmitted for this channel */
	int currentChannelValue;
	
	public UiInputSourceChannel()
	{
		assignment = -1; // unassigned
		minRange = MIN_VALUE;
		maxRange = MAX_VALUE;
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
	/** getter */
	public synchronized int getAssignment() { return assignment; }
	public synchronized int getMinRange()	{ return minRange;	}
	public synchronized int getMaxRange()	{ return maxRange;	}
	public synchronized int getTrimm()	{ return trimm;	}
	public synchronized boolean getInverted()	{ return inverted;	}
	public synchronized int getDefaultPosition() { return defaultPosition;	}
	public synchronized boolean getSticky() { return sticky;	}
	
	/**
	 * set the value from the control widget
	 */
	public synchronized int calculateChannelValue(int inputFromWidget) {
		inputFromWidget = inputFromWidget + trimm;
		if(inputFromWidget <  MIN_VALUE)
			inputFromWidget = MIN_VALUE;
		
		if(inputFromWidget >  MAX_VALUE)
			inputFromWidget = MAX_VALUE;
		
		// check if inverted
		if(inverted) {
			inputFromWidget  = MAX_VALUE-inputFromWidget; 
		}
		// adjust range
		currentChannelValue = inputFromWidget;
		return currentChannelValue;
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

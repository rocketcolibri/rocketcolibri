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
	public void setAssignment(int a) { assignment = a; }
	public void setMinRange(int r)	{ minRange = r;	}
	public void setMaxRange(int r)	{ maxRange = r;	}
	public void setTrimm(int t)	{ if(t>-1)trimm = t;	}
	public void setInverted(boolean i)	{ inverted = i;	}
	public void setDefaultPosition(int d)	{ defaultPosition = d;	}
	public void setSticky(boolean d)	{ sticky = d;	}
	/** getter */
	public int getAssignment() { return assignment; }
	public int getMinRange()	{ return minRange;	}
	public int getMaxRange()	{ return maxRange;	}
	public int getTrimm()	{ return trimm;	}
	public boolean getInverted()	{ return inverted;	}
	public int getDefaultPosition() { return defaultPosition;	}
	public boolean getSticky() { return sticky;	}
	
	/**
	 * set the value from the control widget
	 */
	public int calculateChannelValue(int inputFromWidget) {
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
	public int setToDefaultPosition() {
		return currentChannelValue = defaultPosition;
	}
	
	/**
	 * get the value to be transimte in the cdc message
	 * @return channel value
	 */
	public int getChannelValue()
	{
		return currentChannelValue;
	}
}

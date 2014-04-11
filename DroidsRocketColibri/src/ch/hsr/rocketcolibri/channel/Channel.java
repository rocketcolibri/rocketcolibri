package ch.hsr.rocketcolibri.channel;

/**
 * @short modell of an RC channel
 * 
 * Responsibilities
 * Calculates the channel value from the input value of widget control.
 */
public class Channel
{
	public static final int MIN_VALUE = 0;
	public static final int MAX_VALUE = 1000;

	/* attributes according to the requirements */
	int defaultChannelValue;
	boolean inverted;
	int minRange;
	int maxRange;
	int trimm;

	/* current value transmitted for this channel */
	int currentChannelValue;
	
	public Channel()
	{
		minRange = MIN_VALUE;
		maxRange = MAX_VALUE;
		trimm = 0;
		inverted = false;
		defaultChannelValue = 0;
	}
	
	/** setter */
	void setMinRange(int r)	{ minRange = r;	}
	void setMaxRange(int r)	{ maxRange = r;	}
	void setTrimm(int t)	{ trimm = t;	}
	void setInverted(boolean i)	{ inverted = i;	}
	void setDefaultChannelValue(int d)	{ defaultChannelValue = d;	}
	/** getter */
	int getMinRange()	{ return minRange;	}
	int getMaxRange()	{ return maxRange;	}
	int getTrimm()	{ return trimm;	}
	boolean getInverted()	{ return inverted;	}
	int getDefaultChannelValue() { return defaultChannelValue;	}
	
	/**
	 * set the value from the control widget
	 */
	void setControl(int inputFromWidget)
	{
		inputFromWidget = inputFromWidget + trimm;
		
		if(inputFromWidget <  MIN_VALUE)
			inputFromWidget = MIN_VALUE;
		
		if(inputFromWidget >  MAX_VALUE)
			inputFromWidget = MAX_VALUE;
		

		// check if inverted
		if(inverted)
		{
			inputFromWidget  = MAX_VALUE-inputFromWidget; 
		}

		
		// adjust range
		currentChannelValue = inputFromWidget;
	}
	
	/**
	 * get the value to be transimte in the cdc message
	 * @return channel value
	 */
	int getChannelValue()
	{
		return currentChannelValue;
	}
}

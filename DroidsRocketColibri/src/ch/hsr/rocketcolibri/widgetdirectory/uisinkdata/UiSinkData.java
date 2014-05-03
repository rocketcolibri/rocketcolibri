package ch.hsr.rocketcolibri.widgetdirectory.uisinkdata;
import ch.hsr.rocketcolibri.widgetdirectory.RCUiSinkType;

public class UiSinkData
{
	protected boolean doNotify;
	protected RCUiSinkType type = RCUiSinkType.None;
		
	protected void notifyThis()
	{
		doNotify = true;
	}
	
	public RCUiSinkType getType()
	{
		return this.type;
	}
	
	public boolean getAndResetNotifyFlag()
	{
		boolean retval = doNotify; 
		doNotify = false;
		return retval;
	}
}

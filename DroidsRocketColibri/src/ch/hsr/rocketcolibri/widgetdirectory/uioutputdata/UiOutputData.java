/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.widgetdirectory.uioutputdata;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;

public class UiOutputData
{
	protected boolean doNotify;
	protected UiOutputDataType type = UiOutputDataType.None;
		
	protected void notifyThis()
	{
		doNotify = true;
	}
	
	public UiOutputDataType getType()
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

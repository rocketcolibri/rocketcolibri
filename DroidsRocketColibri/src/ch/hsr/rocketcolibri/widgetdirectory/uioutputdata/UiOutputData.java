/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.widgetdirectory.uioutputdata;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;

/**
 * parent class of all objects that are provided from
 * the RocketColibriService (which acts as a user interface output source).
 * 
 * A RCWidget that wants to display such an object can be registered
 * to the RocketColibriService and will be notified by the RocketColibriService
 * as soon as the objects changes its content.
 */
public class UiOutputData
{
	protected boolean doNotify;
	protected UiOutputDataType type = UiOutputDataType.None;
		
	public void notifyThis()
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

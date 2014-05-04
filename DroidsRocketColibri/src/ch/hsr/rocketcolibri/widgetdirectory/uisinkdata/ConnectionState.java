/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.widgetdirectory.uisinkdata;

import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.widgetdirectory.RCUiSinkType;

public class ConnectionState extends UiSinkData {

	private s state;
	
	public ConnectionState() 
	{
		type = RCUiSinkType.ConnectionState;
	}

	public boolean setState(s newstate)
	{
		if (newstate != this.state)
		{
			this.state = newstate;
			this.notifyThis();
			return true;
		}
		else
			return false;
	}
	
	public s getState()
	{
		return this.state;
	}
	
}

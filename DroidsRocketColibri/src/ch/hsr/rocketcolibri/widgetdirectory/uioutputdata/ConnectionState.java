/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.widgetdirectory.uioutputdata;

import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;

/**
 * Contains the information about the state of the connection
 * between the RocketColibri and the ServoControler 
 */
public class ConnectionState extends UiOutputData {

	private s state;
	
	public ConnectionState() 
	{
		type = UiOutputDataType.ConnectionState;
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

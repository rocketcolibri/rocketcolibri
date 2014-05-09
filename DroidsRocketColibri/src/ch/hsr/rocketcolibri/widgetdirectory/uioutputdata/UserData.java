/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.widgetdirectory.uioutputdata;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.rocketcolibri.protocol.RcOperator;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;

public class UserData extends UiOutputData
{
	private RcOperator activeUser;
	private List<RcOperator>passivUser = new ArrayList<RcOperator>();
	
	public UserData() 
	{
		type = UiOutputDataType.ConnectedUsers;
	}

	public boolean setConnectedUsers(RcOperator activeUser, List<RcOperator>passivUser)
	{
		boolean doNotify = false;
		if(!activeUser.equals(this.activeUser))
		{
			this.activeUser = activeUser;
			doNotify = true;
		}
		
		if(!passivUser.equals(this.passivUser))
		{
			this.passivUser = passivUser;
			doNotify = true;
		}
		
		if (doNotify)
			notifyThis();
		
		return doNotify;
	}
	
	public boolean setActiveUser(RcOperator activeUser)
	{
		if(!activeUser.equals(this.activeUser))
		{
			this.activeUser = activeUser;
			notifyThis();
			return true;
		}
		else
			return false;
	}
	
	public void removeAllUsers()
	{
		this.activeUser = null;
		this.passivUser.clear();
		notifyThis();
	}
	
	public RcOperator getActiveUser()
	{
		return this.activeUser;
	}
	
	public List<RcOperator> getPassivUsers()
	{
		return this.passivUser;
	}
}

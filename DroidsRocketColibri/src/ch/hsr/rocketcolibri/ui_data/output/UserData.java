/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.ui_data.output;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.rocketcolibri.protocol.RcOperator;

/**
 * Contains the name and the IP address of all 
 * connected active and passive users 
 */
public class UserData extends UiOutputData
{
	private RcOperator activeUser;
	private List<RcOperator>passivUser = new ArrayList<RcOperator>();
	
	public UserData() 
	{
		type = UiOutputDataType.ConnectedUsers;
	}

	public  synchronized boolean setConnectedUsers(RcOperator activeUser, List<RcOperator>passivUser)
	{
		boolean doNotify = false;
		if(null != activeUser) {
			if(!activeUser.equals(this.activeUser)) 	{
				this.activeUser = activeUser;
				doNotify = true;
			}
		} else {
			if(null != this.activeUser) {
				this.activeUser = activeUser;
				doNotify = true;
			}
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
	
	public  synchronized boolean setActiveUser(RcOperator activeUser)
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
	
	public  synchronized void removeAllUsers()
	{
		this.activeUser = null;
		this.passivUser.clear();
		notifyThis();
	}
	
	public RcOperator getActiveUser()
	{
		return this.activeUser;
	}
	
	public  synchronized List<RcOperator> getPassivUsers()
	{
		return this.passivUser;
	}
}

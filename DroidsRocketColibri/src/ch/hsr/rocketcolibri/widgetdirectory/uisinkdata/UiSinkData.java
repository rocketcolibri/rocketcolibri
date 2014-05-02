package ch.hsr.rocketcolibri.widgetdirectory.uisinkdata;

import java.util.concurrent.BlockingQueue;

import ch.hsr.rocketcolibri.widgetdirectory.RCUiSinkType;

public class UiSinkData
{
	protected RCUiSinkType type = RCUiSinkType.None;
	
	BlockingQueue<UiSinkData> queue;
	public UiSinkData(BlockingQueue<UiSinkData>queue)
	{
		this.queue = queue;
		type = RCUiSinkType.ConnectedUsers;
	}
		
	public void notifyThis()
	{
		if(null != this.queue)
		{
			try 
			{	
				this.queue.put(this);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public RCUiSinkType getType()
	{
		return this.type;
	}
}
